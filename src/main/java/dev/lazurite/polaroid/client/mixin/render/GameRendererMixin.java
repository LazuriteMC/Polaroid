package dev.lazurite.polaroid.client.mixin.render;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.polaroid.client.util.PhotoUtil;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin uses an injection point just before the user interface is rendered,
 * making it the ideal target for taking a screenshot for the polaroid image.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLevel(Lcom/mojang/blaze3d/vertex/PoseStack;FJZLnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/GameRenderer;Lnet/minecraft/client/renderer/LightTexture;Lcom/mojang/math/Matrix4f;)V",
                    shift = At.Shift.AFTER
            )
    )
    public void renderLevel(float tickDelta, long limitTime, PoseStack matrix, CallbackInfo info) {
        if (PhotoUtil.isCaptureQueued()) {
            PhotoUtil.captureAndSend();
        }
    }
}
