package com.cjkfix.mixin;

import meteordevelopment.meteorclient.gui.WidgetScreen;
import meteordevelopment.meteorclient.gui.themes.meteor.MeteorGuiTheme;
import meteordevelopment.meteorclient.renderer.text.TextRenderer;
import meteordevelopment.meteorclient.renderer.text.VanillaTextRenderer;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = MeteorGuiTheme.class, remap = false)
public abstract class MixinMeteorGuiTheme {

    @Shadow
    private SettingGroup sgGeneral;

    @Unique
    private Setting<Boolean> cjkVanillaFont;

    /**
     * 在构造函数结束后添加 vanillaFont 设置项
     */
    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        cjkVanillaFont = sgGeneral.add(new BoolSetting.Builder()
            .name("vanilla-font")
            .description("Use Minecraft's vanilla font renderer instead of the custom one. "
                + "Enables full CJK support without modifying font files.")
            .defaultValue(false)
            .onChanged(aBoolean -> {
                if (mc.currentScreen instanceof WidgetScreen) {
                    ((WidgetScreen) mc.currentScreen).invalidate();
                }
            })
            .build()
        );
    }

    /**
     * 覆写 textRenderer()，当 vanillaFont 开启时返回原版渲染器
     */
    @Inject(method = "textRenderer", at = @At("HEAD"), cancellable = true)
    private void onTextRenderer(CallbackInfoReturnable<TextRenderer> cir) {
        if (cjkVanillaFont != null && cjkVanillaFont.get()) {
            cir.setReturnValue(VanillaTextRenderer.INSTANCE);
        }
    }
}
