# Big Color Change / Color Evolution

A client-side Fabric mod for Minecraft 1.21.2 that starts with normal Minecraft colors and gradually shifts world colors over time.

## Features
- Starts with the normal world appearance.
- Cycles color families through hue shifts over time.
- Configurable defaults in the mod source.

## Build
Requires Java 21. From the project root run:

- Windows: `gradlew.bat build`
- macOS/Linux: `./gradlew build`

The built mod JAR will appear in `build/libs/`.

## Install
Put the built JAR in your Minecraft 1.21.2 `mods` folder alongside Fabric Loader. Install Fabric API only if required by the selected project dependencies.

> This repository is a starting implementation; verify the build and in-game shader behavior on your exact Minecraft/Fabric setup before using it in a world.
