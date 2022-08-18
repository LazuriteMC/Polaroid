package dev.lazurite.polaroid.client.mixin.render.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.PolaroidClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin is responsible for rendering a {@link Polaroid#PHOTO_ITEM} on an {@link ItemFrame} entity.
 */
@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin {
    @Shadow protected abstract <T extends ItemFrame> int getLightVal(T itemFrame, int glowLight, int regularLight);
    @Shadow @Final private static ModelResourceLocation MAP_FRAME_LOCATION;

    @Inject(
            method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            ),
            cancellable = true
    )
    public <T extends ItemFrame> void render(T itemFrame, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo info) {
        if (itemFrame.getItem().is(Polaroid.PHOTO_ITEM)) {
            final var item = itemFrame.getItem();

            poseStack.translate(0.0, 0.0, 0.4375);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);

            boolean isBig = itemFrame.getRotation() % 2 != 0 && item.hasTag();

            if (isBig) {
                poseStack.translate(-64, -64, -1.0);
            } else {
                poseStack.translate(-25.6, -28.8, -0.25);
                poseStack.scale(0.4f, 0.4f, 0.4f);
            }

            int k = this.getLightVal(itemFrame, 15728850, i);
            PolaroidClient.PHOTO_RENDERER.render(itemFrame.getItem(), poseStack, multiBufferSource, k, true, !isBig);
            poseStack.popPose();
            info.cancel();
        }
    }

    @Inject(method = "getFrameModelResourceLoc", at = @At("HEAD"), cancellable = true)
    private <T extends ItemFrame> void getFrameModelResourceLoc(T entity, ItemStack stack, CallbackInfoReturnable<ModelResourceLocation> info) {
        if (stack.is(Polaroid.PHOTO_ITEM) && entity.getRotation() % 2 != 0 && stack.hasTag()) {
            info.setReturnValue(MAP_FRAME_LOCATION);
        }
    }
}
