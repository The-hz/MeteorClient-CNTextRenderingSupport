package com.cjkfix.mixin;

import com.cjkfix.util.CJKFontSupport;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import meteordevelopment.meteorclient.renderer.text.Font;
import meteordevelopment.meteorclient.utils.render.ByteTexture;
import net.minecraft.client.texture.AbstractTexture;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Constructor;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

@Mixin(value = Font.class, remap = false)
public abstract class MixinFont {

    @Unique
    private static final int CJK_SIZE = 8192;

    @Shadow
    @Final
    @Mutable
    public AbstractTexture texture;

    // 关键修改：加上 @Mutable 和初始化值，泛型使用 Object 避免类型冲突
    @Shadow
    @Final
    @Mutable
    private Int2ObjectOpenHashMap<Object> charMap = new Int2ObjectOpenHashMap<>();

    @Unique
    private static Constructor<?> cjkCharDataCtor;

    @Inject(method = "<init>(Ljava/nio/ByteBuffer;I)V", at = @At("RETURN"))
    private void onInit(ByteBuffer buffer, int height, CallbackInfo ci) {
        // 确保字符集已加载
        CJKFontSupport.loadCharset();
        int[] extraCPs = CJKFontSupport.getExtraCodepoints();

        // 双重保险：如果原版初始化依然丢失，这里强制初始化
        if (this.charMap == null) {
            this.charMap = new Int2ObjectOpenHashMap<>();
        }
        charMap.clear();

        // 释放旧纹理
        if (texture != null) {
            texture.close();
        }

        // ---- 重新烘焙 8192 纹理 ----
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        STBTruetype.stbtt_InitFont(fontInfo, buffer);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(CJK_SIZE * CJK_SIZE);

        STBTTPackedchar.Buffer[] cdata = {
            STBTTPackedchar.create(95),   // Basic Latin
            STBTTPackedchar.create(96),   // Latin 1 Supplement
            STBTTPackedchar.create(128),  // Latin Extended-A
            STBTTPackedchar.create(144),  // Greek and Coptic
            STBTTPackedchar.create(256),  // Cyrillic
            STBTTPackedchar.create(1),    // infinity symbol
            STBTTPackedchar.create(extraCPs.length) // CJK
        };

        IntBuffer cpBuf = null;
        if (extraCPs.length > 0) {
            cpBuf = BufferUtils.createIntBuffer(extraCPs.length);
            cpBuf.put(extraCPs).flip();
        }

        STBTTPackContext packContext = STBTTPackContext.create();
        STBTruetype.stbtt_PackBegin(packContext, bitmap, CJK_SIZE, CJK_SIZE, 0, 1);

        STBTTPackRange.Buffer packRange = STBTTPackRange.create(cdata.length);
        packRange.put(STBTTPackRange.create().set(height, 32,   null, 95,  cdata[0], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 160,  null, 96,  cdata[1], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 256,  null, 128, cdata[2], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 880,  null, 144, cdata[3], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 1024, null, 256, cdata[4], (byte) 2, (byte) 2));
        packRange.put(STBTTPackRange.create().set(height, 8734, null, 1,   cdata[5], (byte) 2, (byte) 2));
        if (cpBuf != null) {
            packRange.put(STBTTPackRange.create().set(height, 0, cpBuf,
                extraCPs.length, cdata[6], (byte) 2, (byte) 2));
        } else {
            packRange.put(STBTTPackRange.create().set(height, 0, null, 0, cdata[6], (byte) 2, (byte) 2));
        }
        packRange.flip();

        STBTruetype.stbtt_PackFontRanges(packContext, buffer, 0, packRange);
        STBTruetype.stbtt_PackEnd(packContext);

        // 创建新纹理
        texture = new ByteTexture(CJK_SIZE, CJK_SIZE, bitmap,
            ByteTexture.Format.A, ByteTexture.Filter.Linear, ByteTexture.Filter.Linear);

        // 填充 charMap — 前 6 个范围
        for (int i = 0; i < 6; i++) {
            STBTTPackedchar.Buffer cbuf = cdata[i];
            int offset = packRange.get(i).first_unicode_codepoint_in_range();
            for (int j = 0; j < cbuf.capacity(); j++) {
                charMap.put(j + offset, cjkToCharData(cbuf.get(j), CJK_SIZE));
            }
        }

        // 填充 charMap — CJK 范围
        if (cpBuf != null && cdata[6].capacity() > 0) {
            STBTTPackedchar.Buffer cbuf = cdata[6];
            cpBuf.rewind();
            for (int j = 0; j < cbuf.capacity(); j++) {
                int cp = cpBuf.get(j);
                charMap.put(cp, cjkToCharData(cbuf.get(j), CJK_SIZE));
            }
        }
    }

    @Unique
    private static Object cjkToCharData(STBTTPackedchar pc, int size) {
        float ipw = 1f / size;
        float iph = 1f / size;
        try {
            if (cjkCharDataCtor == null) {
                Class<?> cdClass = Class.forName("meteordevelopment.meteorclient.renderer.text.Font$CharData");
                cjkCharDataCtor = cdClass.getDeclaredConstructor(
                    float.class, float.class, float.class, float.class,
                    float.class, float.class, float.class, float.class, float.class);
                cjkCharDataCtor.setAccessible(true);
            }
            return cjkCharDataCtor.newInstance(
                pc.xoff(), pc.yoff(), pc.xoff2(), pc.yoff2(),
                pc.x0() * ipw, pc.y0() * iph, pc.x1() * ipw, pc.y1() * iph,
                pc.xadvance()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to create CharData", e);
        }
    }
}
