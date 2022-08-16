package dev.lazurite.polaroid.item;

import dev.lazurite.polaroid.client.PhotoCapture;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CameraItem extends Item {
    public CameraItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        final var itemStack = user.getItemInHand(hand);

        if (level.isClientSide()) {
            PhotoCapture.sendPhoto();
        }

        return InteractionResultHolder.success(itemStack);
    }
}