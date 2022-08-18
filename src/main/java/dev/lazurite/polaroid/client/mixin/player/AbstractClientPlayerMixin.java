package dev.lazurite.polaroid.client.mixin.player;

import com.mojang.authlib.GameProfile;
import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.item.CameraItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.ProfilePublicKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin adds an FOV modifier for the {@link CameraItem}.
 */
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level level, BlockPos blockPos, float f, GameProfile gameProfile, @Nullable ProfilePublicKey profilePublicKey) {
        super(level, blockPos, f, gameProfile, profilePublicKey);
    }

    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    public void getFieldOfViewModifier(CallbackInfoReturnable<Float> info) {
        if (this.getUseItem().is(Polaroid.CAMERA_ITEM) && Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            info.setReturnValue(0.75f);
        }
    }
}
