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

/** Client-side, grayscale-first color evolution. */
public final class BigColorChangeClient implements ClientModInitializer {
    public static final String MOD_ID = "bigcolorchange";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final Random RANDOM = new Random();
    private static final int EVOLUTION_TICKS = 20 * 60;
    private static final int TRANSITION_TICKS = 20 * 5;

    private static ManagedShaderEffect effect;
    private static Uniform1f transitionUniform, colorCountUniform, skyHueUniform, terrainHueUniform, entitiesHueUniform, uiHueUniform;
    private static int ticksSinceJoin, lastEvolutionStep = -1, colorCount;
    private static float transitionProgress, skyHue, terrainHue, entitiesHue, uiHue;

    @Override
    public void onInitializeClient() {
        try {
            effect = ShaderEffectManager.getInstance().manage(Identifier.of(MOD_ID, "color_shift"));
            transitionUniform = effect.findUniform1f("Transition");
            colorCountUniform = effect.findUniform1f("ColorCount");
            skyHueUniform = effect.findUniform1f("SkyHue");
            terrainHueUniform = effect.findUniform1f("TerrainHue");
            entitiesHueUniform = effect.findUniform1f("EntitiesHue");
            uiHueUniform = effect.findUniform1f("UiHue");
        } catch (RuntimeException ex) {
            LOGGER.error("Could not initialize Big Color Change post effect. Verify Satin and Minecraft versions.", ex);
            effect = null;
        }
        ClientTickEvents.END_CLIENT_TICK.register(BigColorChangeClient::tick);
        LOGGER.info("Big Color Change initialized: grayscale start, then random color evolution every minute.");
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) { resetEvolution(); return; }
        ticksSinceJoin++;
        int step = ticksSinceJoin / EVOLUTION_TICKS;
        while (lastEvolutionStep < step && colorCount < 4) {
            lastEvolutionStep++;
            chooseRandomCategoryHue();
        }
        transitionProgress = MathHelper.clamp((ticksSinceJoin % EVOLUTION_TICKS) / (float) TRANSITION_TICKS, 0.0F, 1.0F);
        transitionProgress = transitionProgress * transitionProgress * (3.0F - 2.0F * transitionProgress);
    }

    private static void chooseRandomCategoryHue() {
        int category = RANDOM.nextInt(4);
        float hue = RANDOM.nextFloat();
        switch (category) {
            case 0 -> skyHue = hue;
            case 1 -> terrainHue = hue;
            case 2 -> entitiesHue = hue;
            case 3 -> uiHue = hue;
            default -> throw new IllegalStateException("Unexpected category");
        }
        colorCount = Math.min(4, colorCount + 1);
    }

    private static void resetEvolution() {
        ticksSinceJoin = 0; lastEvolutionStep = -1; colorCount = 0; transitionProgress = 0.0F;
        skyHue = terrainHue = entitiesHue = uiHue = 0.0F;
    }

    /** Called by the renderer mixin after the game and HUD frame is drawn. */
    public static void renderFullFrame() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null || effect == null) return;
        if (transitionUniform != null) transitionUniform.set(transitionProgress);
        if (colorCountUniform != null) colorCountUniform.set(colorCount);
        if (skyHueUniform != null) skyHueUniform.set(skyHue);
        if (terrainHueUniform != null) terrainHueUniform.set(terrainHue);
        if (entitiesHueUniform != null) entitiesHueUniform.set(entitiesHue);
        if (uiHueUniform != null) uiHueUniform.set(uiHue);
        effect.render(0.0F);
    }
}