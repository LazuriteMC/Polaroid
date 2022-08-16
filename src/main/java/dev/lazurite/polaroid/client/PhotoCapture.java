package dev.lazurite.polaroid.client;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.polaroid.Polaroid;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PhotoCapture {
    static void sendPhoto() {
        if (!RenderSystem.isOnRenderThread()) {
            CompletableFuture.runAsync(PhotoCapture::sendPhoto);
        } else {
            final var nativeImage = Screenshot.takeScreenshot(Minecraft.getInstance().getMainRenderTarget());

            int i = nativeImage.getWidth();
            int j = nativeImage.getHeight();
            int k = 0;
            int l = 0;

            if (i > j) {
                k = (i - j) / 2;
                i = j;
            } else {
                l = (j - i) / 2;
                j = i;
            }

            try (var squareImage = new NativeImage(64, 64, false)) {

                /* Resize the image to 64x64 */
                nativeImage.resizeSubRectTo(k, l, i, j, squareImage);

                /* Send the byte information to the server */
                final var bytes = squareImage.asByteArray();
                final var buf = new FriendlyByteBuf(Unpooled.buffer());
                final var uniqueID = Math.abs(new Random().nextInt());

                buf.writeResourceLocation(new ResourceLocation(Polaroid.MODID, "image_" + uniqueID));
                buf.writeByteArray(bytes);
                ClientPlayNetworking.send(Polaroid.PHOTO_C2S, buf);

            } catch (IOException e) {
                Polaroid.LOGGER.warn("Failed to serialize image.", e);
            } finally {
                nativeImage.close();
            }
        }
    }
}
