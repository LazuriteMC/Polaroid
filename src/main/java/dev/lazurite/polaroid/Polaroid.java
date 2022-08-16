package dev.lazurite.polaroid;

import dev.lazurite.polaroid.item.CameraItem;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.quiltmc.qsl.networking.api.PacketSender;
import org.quiltmc.qsl.networking.api.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Polaroid implements ModInitializer {
    public static final String MODID = "polaroid";
    public static final Logger LOGGER = LoggerFactory.getLogger("Polaroid");

    public static final CameraItem CAMERA_ITEM = Registry.register(Registry.ITEM, new ResourceLocation(MODID, "camera_item"), new CameraItem(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final Item USED_PHOTO_ITEM = Registry.register(Registry.ITEM, new ResourceLocation(MODID, "used_photo_item"), new Item(new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_MISC)));
    public static final Item BLANK_PHOTO_ITEM = Registry.register(Registry.ITEM, new ResourceLocation(MODID, "blank_photo_item"), new Item(new Item.Properties().stacksTo(64).tab(CreativeModeTab.TAB_MISC)));

    public static final ResourceLocation PHOTO_C2S = new ResourceLocation(MODID, "photo_c2s");

    public static final ResourceLocation POLAROID_CAMERA_SHUTTER_ID = new ResourceLocation(MODID, "polaroid_camera_shutter");
    public static SoundEvent POLAROID_CAMERA_SHUTTER_EVENT = new SoundEvent(POLAROID_CAMERA_SHUTTER_ID);

    @Override
    public void onInitialize(ModContainer mod) {
        LOGGER.info("Say cheese!");
        ServerPlayNetworking.registerGlobalReceiver(PHOTO_C2S, this::onPhotoReceived);
    }

    protected void onPhotoReceived(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        final var resourceLocation = buf.readResourceLocation();
        final var bytes = buf.readByteArray();

        server.execute(() -> {
            /* Create a photo item with the necessary image bytes, then give to the player. */
            final var photoItem = new ItemStack(USED_PHOTO_ITEM);
            final var tag = photoItem.getOrCreateTag();
            tag.putByteArray("imageData", bytes);
            tag.putString("resource", resourceLocation.toString());
            player.addItem(photoItem);

            /* Play the shutter sound for the player */
            player.level.playSound(
                    null,
                    player.blockPosition(),
                    POLAROID_CAMERA_SHUTTER_EVENT,
                    SoundSource.PLAYERS,
                    1.0f,
                    1.0f
            );
        });
    }
}
