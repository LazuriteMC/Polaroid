package dev.lazurite.polaroid.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.mixin.ItemInHandRendererAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class PhotoItemRenderer {
    public static void render(ItemInHandRenderer itemInHandRenderer, ItemStack stack, AbstractClientPlayer player, float tickDelta, float pitch, InteractionHand hand, float swingProgress, float equipProgress, PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        final var tag = stack.getTag();

        if (tag != null && !tag.isEmpty()) {
            final var resource = new ResourceLocation(tag.getString("resource"));
            final var textureManager = Minecraft.getInstance().getTextureManager();

            if (textureManager.getTexture(resource, null) == null) {
                try {
                    final var bytes = tag.getByteArray("imageData");
                    final var dynamicTexture = new DynamicTexture(NativeImage.read(new ByteArrayInputStream(bytes)));
                    textureManager.register(resource, dynamicTexture);
                } catch (IOException e) {
                    Polaroid.LOGGER.warn("Failed to load polaroid image", e);
                }
            }

            final var humanoidArm = InteractionHand.MAIN_HAND == hand ? player.getMainArm() : player.getMainArm().getOpposite();
            setupHand(itemInHandRenderer, humanoidArm, matrices, swingProgress, equipProgress, light, vertexConsumers, stack);

//            RenderSystem.setShader(GameRenderer::getPositionTexShader);
//            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
//            RenderSystem.setShaderTexture(0, resource);
//            RenderSystem.enableBlend();
//            RenderSystem.disableBlend();
        }
    }

    private static void setupHand(ItemInHandRenderer itemInHandRenderer, HumanoidArm arm, PoseStack matrices, float swingProgress, float equipProgress, int light, MultiBufferSource vertexConsumers, ItemStack stack) {
//        float f = arm == HumanoidArm.RIGHT ? 1.0F : -1.0F;
//        matrices.translate((f * 0.125F), -0.125, 0.0);
//        if (!Minecraft.getInstance().player.isInvisible()) {
//            matrices.pushPose();
//            matrices.mulPose(Vector3f.ZP.rotationDegrees(f * 10.0F));
//            ((ItemInHandRendererAccess) itemInHandRenderer).invokeRenderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
//            matrices.popPose();
//        }
//
//        matrices.pushPose();
//        matrices.translate(f * 0.51F, -0.08F + equipProgress * -1.2F, -0.75);
//        float g = Mth.sqrt(swingProgress);
//        float h = Mth.sin(g * (float) Math.PI);
//        float i = -0.5F * h;
//        float j = 0.4F * Mth.sin(g * (float) (Math.PI * 2));
//        float k = -0.3F * Mth.sin(swingProgress * (float) Math.PI);
//        matrices.translate(f * i, j - 0.3F * h, k);
//        matrices.mulPose(Vector3f.XP.rotationDegrees(h * -45.0F));
//        matrices.mulPose(Vector3f.YP.rotationDegrees(f * h * -30.0F));
//
//        matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
//        matrices.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
//        matrices.scale(0.38F, 0.38F, 0.38F);
//        matrices.translate(-0.5, -0.5, 0.0);
//        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
//        Integer integer = MapItem.getMapId(stack);
//        MapItemSavedData mapItemSavedData = MapItem.getSavedData(integer, Minecraft.getInstance().level);
//        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mapItemSavedData == null ? MAP_BACKGROUND : MAP_BACKGROUND_CHECKERBOARD);
//        Matrix4f matrix4f = matrices.last().pose();
//        vertexConsumer.vertex(matrix4f, -7.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(swingProgress).endVertex();
//        vertexConsumer.vertex(matrix4f, 135.0F, 135.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(swingProgress).endVertex();
//        vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(swingProgress).endVertex();
//        vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(swingProgress).endVertex();
//
//        matrices.popPose();
    }
}
