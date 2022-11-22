package dev.lazurite.polaroid.client.util;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.polaroid.Polaroid;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.network.FriendlyByteBuf;
import org.quiltmc.qsl.networking.api.client.ClientPlayNetworking;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class PhotoUtil {
    private static boolean captureQueued = false;

    public static void queuePhotoCapture() {
        captureQueued = true;
    }

    public static boolean isCaptureQueued() {
        return captureQueued;
    }

    public static void captureAndSend() {
        captureQueued = false;

        if (!RenderSystem.isOnRenderThread()) {
            CompletableFuture.runAsync(PhotoUtil::captureAndSend);
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

            try (var squareImage = new NativeImage(96, 96, false)) {
                nativeImage.resizeSubRectTo(k, l, i, j, squareImage);

                /* Send the byte information to the server */
                final var buf = new FriendlyByteBuf(Unpooled.buffer());
                buf.writeByteArray(squareImage.asByteArray());
                ClientPlayNetworking.send(Polaroid.PHOTO_C2S, buf);

            } catch (IOException e) {
                Polaroid.LOGGER.warn("Failed to serialize image.", e);
            } finally {
                nativeImage.close();
            }
        }
    }
}
