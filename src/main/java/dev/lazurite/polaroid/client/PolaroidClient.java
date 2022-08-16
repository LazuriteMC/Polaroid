package dev.lazurite.polaroid.client;

import dev.lazurite.polaroid.client.render.PolaroidPhotoRenderer;
import net.minecraft.client.Minecraft;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;

public class PolaroidClient implements ClientModInitializer {
    public static PolaroidPhotoRenderer PHOTO_RENDERER;

    @Override
    public void onInitializeClient(ModContainer mod) {
        ClientLifecycleEvents.READY.register(this::onClientReady);
    }

    protected void onClientReady(Minecraft minecraft) {
        PHOTO_RENDERER = new PolaroidPhotoRenderer(Minecraft.getInstance().getTextureManager());
    }
}
