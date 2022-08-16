package dev.lazurite.polaroid.mixin;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.PolaroidClient;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;

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
        if (item.getItem().equals(Polaroid.USED_PHOTO_ITEM) && item.hasTag()) {
            matrices.pushPose();

            try {
                final var id = item.getTag().getInt("id");
                final var data = item.getTag().getByteArray("data");
                final var nativeImage = NativeImage.read(new ByteArrayInputStream(data));
                PolaroidClient.PHOTO_RENDERER.render(matrices, vertexConsumers, id, nativeImage, light);
            } catch (IOException e) {
                Polaroid.LOGGER.warn("Unable to render polaroid image.", e);
            }

            matrices.popPose();
            info.cancel();
        }
    }
}
