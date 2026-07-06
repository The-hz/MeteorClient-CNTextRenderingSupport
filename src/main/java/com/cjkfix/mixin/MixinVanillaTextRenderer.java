package com.cjkfix.mixin;

import com.cjkfix.util.QueuedText;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.text.VanillaTextRenderer;
import meteordevelopment.meteorclient.utils.render.color.Color;
import net.minecraft.client.font.TextRenderer.TextLayerType;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = VanillaTextRenderer.class, remap = false)
public abstract class MixinVanillaTextRenderer {

    @Shadow
    @Final
    private BufferAllocator buffer;

    @Shadow
    @Final
    private VertexConsumerProvider.Immediate immediate;

    @Shadow
    public double scale;

    @Shadow
    private boolean building;

    @Shadow
    private double alpha;

    @Unique
    private final List<QueuedText> cjkTextQueue = new ArrayList<>();

    /**
     * @author The-hz
     * @reason 队列化渲染，begin 时清空队列
     */
    @Overwrite
    public void begin(double scale, boolean scaleOnly, boolean big) {
        if (building) throw new RuntimeException("VanillaTextRenderer.begin() called twice");

        this.scale = scale * 2;
        this.building = true;
        cjkTextQueue.clear();
    }

    /**
     * @author The-hz
     * @reason 不立即绘制，将文字请求加入队列，等 end() 时统一绘制
     */
    @Overwrite
    public double render(String text, double x, double y, Color color, boolean shadow) {
        boolean wasBuilding = building;
        if (!wasBuilding) begin(1, false, false);

        x += 0.5 * scale;
        y += 0.5 * scale;

        // 计算带透明度的颜色，放入队列
        int packedColor = new Color(
            color.r, color.g, color.b,
            (int) (((double) color.a / 255 * alpha) * 255)
        ).getPacked();

        cjkTextQueue.add(new QueuedText(
            text,
            (float) (x / scale),
            (float) (y / scale),
            packedColor,
            shadow
        ));

        double width = mc.textRenderer.getWidth(text) + (shadow ? 1 : 0);

        if (!wasBuilding) end(null);
        return (x / scale + width - 1) * scale;
    }

    /**
     * @author The-hz
     * @reason 从队列取出所有文字，用正确矩阵统一绘制
     */
    @Overwrite
    public void end(MatrixStack matrices) {
        if (!building) throw new RuntimeException("VanillaTextRenderer.end() called without calling begin()");

        Matrix4f baseMatrix = matrices != null ? matrices.peek().getPositionMatrix() : new Matrix4f();

        Matrix4f finalMatrix = new Matrix4f(baseMatrix);
        finalMatrix.scale((float) scale, (float) scale, 1);

        RenderSystem.disableDepthTest();

        for (QueuedText qt : cjkTextQueue) {
            mc.textRenderer.draw(
                qt.text(), qt.x(), qt.y(), qt.color(), qt.shadow(),
                finalMatrix, immediate,
                TextLayerType.NORMAL, 0,
                LightmapTextureManager.MAX_LIGHT_COORDINATE
            );
        }

        cjkTextQueue.clear();
        immediate.draw();

        RenderSystem.enableDepthTest();

        this.scale = 2;
        this.building = false;
    }
}
