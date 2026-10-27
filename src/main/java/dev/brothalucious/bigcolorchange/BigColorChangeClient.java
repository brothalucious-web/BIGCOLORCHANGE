package dev.brothalucious.bigcolorchange;

import java.util.Random;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.ladysnake.satin.api.managed.ManagedShaderEffect;
import org.ladysnake.satin.api.managed.ShaderEffectManager;
import org.ladysnake.satin.api.managed.uniform.Uniform1f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BigColorChangeClient implements ClientModInitializer {
    public static final String MOD_ID = "bigcolorchange";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static final Random RANDOM = new Random();
    private static final int MIN_TRANSITION_TICKS = 80;
    private static final int MAX_TRANSITION_TICKS = 180;

    private static ManagedShaderEffect effect;
    private static Uniform1f hueUniform;
    private static int transitionTicks;
    private static int elapsedTicks;
    private static float startHue;
    private static float targetHue;
    private static float hueShift;

    @Override
    public void onInitializeClient() {
        effect = ShaderEffectManager.getInstance().manage(Identifier.of(MOD_ID, "color_shift"));
        hueUniform = effect.findUniform1f("HueShift");
        chooseNextTarget(0.0F);

        ClientTickEvents.END_CLIENT_TICK.register(BigColorChangeClient::tick);
        LOGGER.info("Big Color Change loaded: continuously shifting screen colors through smooth, randomized hue transitions.");
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) {
            elapsedTicks = 0;
            hueShift = 0.0F;
            startHue = 0.0F;
            chooseNextTarget(0.0F);
            return;
        }

        elapsedTicks++;
        if (elapsedTicks >= transitionTicks) {
            chooseNextTarget(hueShift);
        }

        float progress = MathHelper.clamp((float) elapsedTicks / (float) transitionTicks, 0.0F, 1.0F);
        // Smoothstep avoids abrupt changes at either end of each random transition.
        progress = progress * progress * (3.0F - 2.0F * progress);

        float delta = targetHue - startHue;
        if (delta > 0.5F) delta -= 1.0F;
        if (delta < -0.5F) delta += 1.0F;
        hueShift = (startHue + delta * progress) % 1.0F;
        if (hueShift < 0.0F) hueShift += 1.0F;
    }

    private static void chooseNextTarget(float currentHue) {
        startHue = currentHue;
        targetHue = RANDOM.nextFloat();
        transitionTicks = MIN_TRANSITION_TICKS + RANDOM.nextInt(MAX_TRANSITION_TICKS - MIN_TRANSITION_TICKS + 1);
        elapsedTicks = 0;
    }

    /**
     * Called at the end of GameRenderer.render, after both the 3D world and HUD
     * have been drawn, so the post effect can tint the complete frame.
     */
    public static void renderFullFrame() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || effect == null) {
            return;
        }
        if (hueUniform != null) {
            hueUniform.set(hueShift);
        }
        effect.render(0.0F);
    }
}
