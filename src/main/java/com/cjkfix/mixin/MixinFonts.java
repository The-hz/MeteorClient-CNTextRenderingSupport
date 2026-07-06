package com.cjkfix.mixin;

import com.cjkfix.util.CJKFontSupport;
import meteordevelopment.meteorclient.renderer.Fonts;
import meteordevelopment.meteorclient.renderer.text.CustomTextRenderer;
import meteordevelopment.meteorclient.renderer.text.FontFace;
import meteordevelopment.meteorclient.renderer.text.FontFamily;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(value = Fonts.class, remap = false)
public abstract class MixinFonts {

    @Shadow
    @Final
    @Mutable
    public static String[] BUILTIN_FONTS;

    @Shadow
    public static String DEFAULT_FONT_FAMILY;

    @Shadow
    public static FontFace DEFAULT_FONT;

    @Shadow
    @Final
    public static List<FontFamily> FONT_FAMILIES;

    @Shadow
    public static CustomTextRenderer RENDERER;

    @Shadow
    public static native void load(FontFace fontFace);

    @Shadow
    public static native FontFamily getFamily(String name);

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        BUILTIN_FONTS = new String[]{"SarasaGothicSC"};
    }

    @Inject(method = "refresh", at = @At("HEAD"))
    private static void onRefreshHead(CallbackInfo ci) {
        CJKFontSupport.loadCharset();
    }

    @ModifyConstant(method = "refresh", constant = @Constant(intValue = 1))
    private static int changeFontIndex(int original) {
        return 0;
    }
}
