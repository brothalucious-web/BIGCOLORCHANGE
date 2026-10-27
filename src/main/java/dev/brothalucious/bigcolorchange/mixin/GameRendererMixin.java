package dev.brothalucious.bigcolorchange.mixin;

import dev.brothalucious.bigcolorchange.BigColorChangeClient;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "render(Lnet/minecraft/client/render/RenderTickCounter;Z)V", at = @At("TAIL"))
    private void bigcolorchange$applyFullFrameColorShift(RenderTickCounter tickCounter, boolean tick, CallbackInfo ci) {
        BigColorChangeClient.renderFullFrame();
    }
}
