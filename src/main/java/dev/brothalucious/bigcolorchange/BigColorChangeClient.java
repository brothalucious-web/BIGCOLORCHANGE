package dev.brothalucious.bigcolorchange;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.ladysnake.satin.api.event.ShaderEffectRenderCallback;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BigColorChangeClient implements ClientModInitializer {
    public static final String MOD_ID = "bigcolorchange";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final long START_DELAY_TICKS = 20L * 60L;
    private static final long TRANSITION_TICKS = 20L * 20L;
    private static final long STAGE_TICKS = START_DELAY_TICKS + TRANSITION_TICKS;

    private static long ticks;
    private static float hueShift;
    private static ManagedShaderEffect effect;
    private static Uniform1f hueUniform;

    @Override
    public void onInitializeClient() {
        effect = ShaderEffectManager.getInstance().manage(Identifier.of(MOD_ID, "color_shift"));
        hueUniform = effect.findUniform1f("HueShift");

        ClientTickEvents.END_CLIENT_TICK.register(BigColorChangeClient::tick);
        ShaderEffectRenderCallback.EVENT.register(tickDelta -> {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.world != null && client.player != null && effect != null) {
                if (hueUniform != null) hueUniform.set(hueShift);
                effect.render(tickDelta);
            }
        });

        LOGGER.info("Big Color Change loaded: normal colors for 60 seconds, then a gradual hue cycle.");
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            ticks = 0L;
            hueShift = 0.0F;
            return;
        }

        ticks++;
        if (ticks < START_DELAY_TICKS) {
            hueShift = 0.0F;
            return;
        }

        long elapsed = ticks - START_DELAY_TICKS;
        long stage = (elapsed / STAGE_TICKS) % 7L;
        long withinStage = elapsed % STAGE_TICKS;
        float progress = MathHelper.clamp((float) withinStage / (float) TRANSITION_TICKS, 0.0F, 1.0F);
        hueShift = ((stage + progress) / 7.0F) % 1.0F;
    }
}
