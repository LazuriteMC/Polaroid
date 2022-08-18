package dev.lazurite.polaroid.client.mixin.player;

import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.item.CameraItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * This mixin adds another condition to {@link Player#isScoping()} to allow for scoping using the {@link CameraItem}.
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "isScoping", at = @At("RETURN"), cancellable = true)
    public void isScoping(CallbackInfoReturnable<Boolean> info) {
        info.setReturnValue(info.getReturnValue() || this.getUseItem().is(Polaroid.CAMERA_ITEM));
    }
}
