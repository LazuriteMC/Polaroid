package dev.lazurite.polaroid.client.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

public class PolaroidPhotoRenderer implements AutoCloseable {
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

    class PolaroidPhotoInstance implements AutoCloseable {
        private final DynamicTexture texture;
        private final RenderType renderType;
        private boolean requiresUpload = true;

        PolaroidPhotoInstance(int i, NativeImage photoData) {
            this.texture = new DynamicTexture(photoData);
            ResourceLocation resourceLocation = PolaroidPhotoRenderer.this.textureManager.register("polaroid/" + i, this.texture);
            this.renderType = RenderType.text(resourceLocation);
        }

        private void updateTexture() {
            this.texture.upload();
        }

        public void draw(PoseStack matrices, MultiBufferSource vertexConsumers, int light) {
            if (this.requiresUpload) {
                this.updateTexture();
                this.requiresUpload = false;
            }

//            RenderSystem.setShaderTexture(0, this.texture.getId());

            Matrix4f matrix4f = matrices.last().pose();
            VertexConsumer vertexConsumer = vertexConsumers.getBuffer(this.renderType);
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