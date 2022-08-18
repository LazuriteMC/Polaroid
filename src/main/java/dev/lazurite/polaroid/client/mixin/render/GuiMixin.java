package dev.lazurite.polaroid.client.mixin.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.lazurite.polaroid.client.PolaroidClient;
import net.minecraft.client.gui.Gui;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Gui.class)
public class GuiMixin {
    @Redirect(
            method = "renderSpyglassOverlay",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V"
            )
    )
    private void setShaderTexture(int id, ResourceLocation resourceLocation) {
        RenderSystem.setShaderTexture(0, PolaroidClient.CAMERA_SCOPE);
    }
}
