package dev.lazurite.polaroid.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.PhotoItemRenderer;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {
    @Inject(
            method = "renderArmWithItem(Lnet/minecraft/client/player/AbstractClientPlayer;FFLnet/minecraft/world/InteractionHand;FLnet/minecraft/world/item/ItemStack;FLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/client/player/AbstractClientPlayer;isScoping()Z"
            ),
            cancellable = true
    )
    private void renderArmWithItem(AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, ItemStack item, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo info) {
        if (item.getItem().equals(Polaroid.USED_PHOTO_ITEM)) {
            matrices.pushPose();
            PhotoItemRenderer.render((ItemInHandRenderer) (Object) this, item, player, tickDelta, pitch, hand, swingProgress, equipProgress, matrices, vertexConsumers, light);
            matrices.popPose();
            info.cancel();
        }
    }
}
