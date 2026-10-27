# Big Color Change — Color Evolution

Client-side Fabric mod for Minecraft **1.21.2**.

## Intended behavior
- Starts in grayscale when a world is entered.
- Every 60 seconds, the mod advances its color evolution and chooses an independent random hue for a category: sky, terrain, entities, or HUD/UI.
- Color hues are random rather than a fixed red/blue/green sequence.
- Color fades in smoothly over approximately five seconds.
- This is a client-side visual effect; it does not change world data.

## Rendering limitation
The effect is a full-frame post-process using approximate screen-space masks. It cannot perfectly identify every block, mob, sky pixel, or individual UI element: entities overlap terrain, and HUD elements are not all positioned at screen edges. Proper semantic separation needs deeper render-layer integration or dedicated masks.

## Build
Requires Java 21. On Windows run `gradlew.bat build`; on macOS/Linux run `./gradlew build`. The JAR should appear in `build/libs/`.

## Verification
A successful CI build confirms compilation and packaging only. Launch Minecraft 1.21.2 with Fabric Loader, Fabric API, and Satin to verify shader loading and actual in-game appearance.
