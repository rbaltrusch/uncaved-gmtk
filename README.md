# Uncaved

This is my game jam entry for the 48 hour GMTK game jam in 2023 (see [here](https://itch.io/jam/gmtk-2023)). The game is written in Java with libGDX (first time!).

The game requires at least Java 1.8 to run or build and needs to be run from within the assets folder (read the official documentation [here](https://libgdx.com/wiki/start/import-and-running) for more information).

## Package as Executable

download packr from https://github.com/libgdx/packr
download OpenJDK JRE zip to be bundled with the game

java -jar packr-all-4.0.0.jar  --jdk OpenJDK17U-jre_x64_windows_hotspot_17.0.7_7.zip --useZgcIfSupportedOs --executable uncaved --classpath=desktop/build/libs/desktop-1.0.jar --mainclass com.mygdx.game.DesktopLauncher --vmargs Xmx1G --output build --platform windows64
