package dev.brothalucious.bigcolorchange;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.MathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client-side controller for Big Color Change.
 * The initial palette stays vanilla until its delay expires.
 */
public final class BigColorChangeClient implements ClientModInitializer {
    public static final String MOD_ID = "bigcolorchange";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static final long START_DELAY_TICKS = 20L * 60L;
    private static final long TRANSITION_TICKS = 20L * 20L;
    private static final long STAGE_TICKS = START_DELAY_TICKS + TRANSITION_TICKS;

    private static long ticks;
    private static float hueShift;
    private static int stage;
    private static boolean running;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Big Color Change initialized; world colors begin normal.");
        ClientTickEvents.END_CLIENT_TICK.register(BigColorChangeClient::tick);
    }

    private static void tick(MinecraftClient client) {
        if (client.world == null || client.player == null) return;
        ticks++;
        long elapsed = Math.max(0L, ticks - START_DELAY_TICKS);
        stage = (int) ((elapsed / STAGE_TICKS) % 7L);
        long withinStage = elapsed % STAGE_TICKS;
        running = ticks >= START_DELAY_TICKS;
        float progress = running
                ? MathHelper.clamp((float) withinStage / (float) TRANSITION_TICKS, 0.0F, 1.0F)
                : 0.0F;
        hueShift = running ? (stage + progress) / 7.0F : 0.0F;
    }

    /** Returns the palette shift factor (0 before the initial delay). */
    public static float getHueShift() { return hueShift; }
    public static int getStage() { return stage; }
    public static boolean isRunning() { return running; }
}
