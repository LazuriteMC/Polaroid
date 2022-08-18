package dev.lazurite.polaroid.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.PolaroidClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public abstract class ItemFrameRendererMixin {
    @Shadow protected abstract <T extends ItemFrame> int getLightVal(T itemFrame, int glowLight, int regularLight);

    @Inject(
            method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z"
            ),
            cancellable = true
    )
    public <T extends ItemFrame> void render(T itemFrame, float f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, CallbackInfo info) {
        if (itemFrame.getItem().getItem() == Polaroid.PHOTO_ITEM) {
            poseStack.translate(0.0, 0.0, 0.4375);

            int j =  itemFrame.getRotation() % 4 * 2;
            poseStack.mulPose(Vector3f.ZP.rotationDegrees((float)j * 360.0F / 8.0F));

            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            poseStack.scale(0.0078125F, 0.0078125F, 0.0078125F);
            poseStack.translate(-25.6, -28.8, -0.5);
            int k = this.getLightVal(itemFrame, 15728850, i);

            poseStack.scale(0.4f, 0.4f, 0.4f);

            PolaroidClient.PHOTO_RENDERER.render(itemFrame.getItem(), poseStack, multiBufferSource, k, true);
            poseStack.popPose();
            info.cancel();
        }
    }
}
