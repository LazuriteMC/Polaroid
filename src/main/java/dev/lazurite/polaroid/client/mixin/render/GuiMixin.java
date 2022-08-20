package dev.lazurite.polaroid.client.mixin.render;

import dev.lazurite.polaroid.Polaroid;
import dev.lazurite.polaroid.client.PolaroidClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(Gui.class)
public class GuiMixin {
    @Shadow @Final private Minecraft minecraft;

    @ModifyArgs(
            method = "renderSpyglassOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"
            )
    )
    private void setShaderTexture(Args args) {
        if (this.minecraft.player != null && this.minecraft.player.getUseItem().is(Polaroid.CAMERA_ITEM)) {
            args.set(1, PolaroidClient.CAMERA_SCOPE);
        }
    }
}
