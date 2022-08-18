package dev.lazurite.polaroid.client.mixin.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.PolaroidClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * This mixin is responsible for rendering a {@link Polaroid#PHOTO_ITEM} in the player's hand. This is done very similarly to map rendering.
 */
@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {
    @Shadow @Final private Minecraft minecraft;

    @Shadow protected abstract void renderPlayerArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm);

    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;isScoping()Z"
            ),
            cancellable = true
    )
    private void renderArmWithItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo info) {
        if (item.getItem().equals(Polaroid.PHOTO_ITEM)) {
            final var arm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() : player.getMainArm().getOpposite();

            /* Borrowed from ItemInHandRenderer - render the player's hand */
            float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
            matrices.translate(f * 0.125F, -0.125, 0.0);
            if (!this.minecraft.player.isInvisible()) {
                matrices.pushPose();
                matrices.mulPose(Vector3f.ZP.rotationDegrees(f * 10.0F));
                this.renderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
                matrices.popPose();
            }

            /* Some more borrowed setup */
            matrices.pushPose();
            matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75);
            float g = Mth.sqrt(swingProgress);
            float h = Mth.sin(g * (float) Math.PI);
            float i = -0.5F * h;
            float j = 0.4F * Mth.sin(g * (float) (Math.PI * 2));
            float k = -0.3F * Mth.sin(swingProgress * (float) Math.PI);
            matrices.translate(f * i, j - 0.3F * h, k);
            matrices.mulPose(Vector3f.XP.rotationDegrees(h * -45.0F));
            matrices.mulPose(Vector3f.YP.rotationDegrees(f * h * -30.0F));

            // TODO make this better
            matrices.translate(0, 0.05f, 0);

            PolaroidClient.PHOTO_RENDERER.render(item, matrices, vertexConsumers, light, false, true);
            matrices.popPose();
            info.cancel();
        }
    }
}
