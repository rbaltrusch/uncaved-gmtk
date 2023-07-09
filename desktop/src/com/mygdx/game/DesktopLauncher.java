package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Uncaved");
		config.setForegroundFPS(60);
		config.setWindowedMode(GameLoop.SCREEN_WIDTH, GameLoop.SCREEN_HEIGHT);
		config.useVsync(true);
		config.setResizable(false);
		config.setWindowIcon("block2.jpg");
		new Lwjgl3Application(new GameLoop(), config);
	}
}
