package dev.lazurite.polaroid.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.lazurite.polaroid.Polaroid;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PolaroidPhotoRenderer implements AutoCloseable {
    private static final ResourceLocation POLAROID_PHOTO_BACKGROUND = new ResourceLocation(Polaroid.MODID, "textures/item/polaroid_photo_background.png");
    private final Int2ObjectMap<PolaroidPhotoInstance> photos = new Int2ObjectOpenHashMap<>();
    private final TextureManager textureManager;

    public PolaroidPhotoRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int id, NativeImage photoData, int light) {
        this.getOrCreateMapInstance(id, photoData).draw(matrices, vertexConsumers, light);
    }

    private PolaroidPhotoInstance getOrCreateMapInstance(int id, NativeImage photoData) {
        return this.photos.compute(id, (idx, instance) -> {
            if (instance == null) {
                return new PolaroidPhotoInstance(idx, photoData);
            }

            return instance;
        });
    }

    public void resetData() {
        for(PolaroidPhotoInstance mapInstance : this.photos.values()) {
            mapInstance.close();
        }

        this.photos.clear();
    }

    public void close() {
        this.resetData();
    }

    /**
     * Draws the signature polaroid border/background.
     * @param matrices
     * @param vertexConsumers
     * @param light
     */
    public void renderBackground(PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
        matrices.mulPose(Vector3f.YP.rotationDegrees(180.0F));
        matrices.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        matrices.translate(-0.5, -0.5, 0.0);
        matrices.scale(0.0078125F, 0.0078125F, 0.0078125F);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.text(POLAROID_PHOTO_BACKGROUND));
        Matrix4f matrix4f = matrices.last().pose();
        vertexConsumer.vertex(matrix4f, -7.0F, 155.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
        vertexConsumer.vertex(matrix4f, 135.0F, 155.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
        vertexConsumer.vertex(matrix4f, 135.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
        vertexConsumer.vertex(matrix4f, -7.0F, -7.0F, 0.0F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
    }

    class PolaroidPhotoInstance implements AutoCloseable {
        private final DynamicTexture texture;
        private final ResourceLocation resourceLocation;
        private boolean requiresUpload = true;

        PolaroidPhotoInstance(int i, NativeImage photoData) {
            this.texture = new DynamicTexture(photoData);
            this.resourceLocation = PolaroidPhotoRenderer.this.textureManager.register("polaroid/" + i, this.texture);
        }

        public void draw(PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
            doUpload();
            drawImage(matrices, vertexConsumers, light);
        }

        /**
         * If the texture hasn't been uploaded yet, do it.
         */
        private void doUpload() {
            if (this.requiresUpload) {
                this.texture.upload();
                this.requiresUpload = false;
            }
        }



        /**
         * Draws the image itself on top of the polaroid background.
         * @param matrices
         * @param vertexConsumers
         * @param light
         */
        private void drawImage(PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
            final var matrix4f = matrices.last().pose();
            final var vertexConsumer = vertexConsumers.getBuffer(RenderType.text(this.resourceLocation));
            vertexConsumer.vertex(matrix4f, 0.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
            vertexConsumer.vertex(matrix4f, 128.0F, 128.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
            vertexConsumer.vertex(matrix4f, 128.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
            vertexConsumer.vertex(matrix4f, 0.0F, 0.0F, -0.01F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
        }

        public void close() {
            this.texture.close();
        }
    }
}