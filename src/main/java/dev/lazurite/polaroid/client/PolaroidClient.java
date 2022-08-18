package dev.lazurite.polaroid.client;

import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.render.PolaroidPhotoRenderer;
import dev.lazurite.polaroid.client.util.PhotoUtil;
import dev.lazurite.polaroid.item.CameraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientLifecycleEvents;
import org.quiltmc.qsl.lifecycle.api.client.event.ClientTickEvents;

public class PolaroidClient implements ClientModInitializer {
    public static final ResourceLocation CAMERA_SCOPE = new ResourceLocation(Polaroid.MODID, "textures/misc/polaroid_camera_scope.png");
    public static PolaroidPhotoRenderer PHOTO_RENDERER;

    @Override
    public void onInitializeClient(ModContainer mod) {
        ClientLifecycleEvents.READY.register(this::onClientReady);
        ClientTickEvents.START.register(this::onClientTick);
    }

    protected void onClientReady(Minecraft minecraft) {
        PHOTO_RENDERER = new PolaroidPhotoRenderer(Minecraft.getInstance().getTextureManager());
    }

    /**
     * This event is responsible for checking if the player is attacking while holding a {@link CameraItem}.
     * If so, a photo capture is queued for the renderer.
     * @param minecraft
     */
    protected void onClientTick(Minecraft minecraft) {
        final var player = minecraft.player;

        if (player != null &&
            player.isScoping() &&
            player.getMainHandItem().is(Polaroid.CAMERA_ITEM) &&
            minecraft.options.keyAttack.consumeClick()
        ) PhotoUtil.queuePhotoCapture();
    }
}
