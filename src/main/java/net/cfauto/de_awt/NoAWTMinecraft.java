package net.cfauto.de_awt;

import net.cfauto.de_awt.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.Window;
import net.minecraft.util.crash.CrashReport;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

/**
 * Most of this stuff was ported from <a href="https://modrinth.com/mod/gambac">Gambac</a>
 * Credits to DanyGames2014
 */
public class NoAWTMinecraft extends Minecraft {

	private final int previousWidth;
	private final int previousHeight;

	public NoAWTMinecraft(int w, int h, boolean fullscreen) {
		super(null, null, null, w, h, fullscreen);
		this.previousWidth = w;
		this.previousHeight = h;
	}

	@Override
	public void handleCrash(CrashReport crashSummary) {
		this.shutdown();
		System.exit(0);
	}

	@Override
	public void tick() {
		if (GL11.glGetString(GL11.GL_RENDERER).contains("Apple M")) {
			GL11.glEnable(GL30.GL_FRAMEBUFFER_SRGB);
		}
		if (Display.getWidth() != this.width || Display.getHeight() != this.height) {
			this.scale(Display.getWidth(), Display.getHeight());
		}

		super.tick();
	}

	@Override
	public void toggleFullscreen() {
		boolean isFullscreen = ((MinecraftAccessor)this).isFullscreen();
		try {
			isFullscreen = !isFullscreen;
			if (isFullscreen) {
				this.width = Display.getWidth();
				this.height = Display.getHeight();

				Display.setDisplayMode(Display.getDesktopDisplayMode());
				this.width = Display.getDisplayMode().getWidth();
				this.height = Display.getDisplayMode().getHeight();
			} else {
				this.width = this.previousWidth;
				this.height = this.previousHeight;
				Display.setDisplayMode(new DisplayMode(this.width, this.height));
			}
			if (this.width <= 0) {
				this.width = 1;
			}
			if (this.height <= 0) {
				this.height = 1;
			}

			if (this.screen != null) {
				this.scale(this.width, this.height);
			}

			Display.setFullscreen(isFullscreen);
			Display.update();
		} catch (Exception ignored) {}

		((MinecraftAccessor)this).setFullscreen(isFullscreen);
	}

	private void scale(int w, int h) {
		if (w <= 0) {
			w = 1;
		}
		if (h <= 0) {
			h = 1;
		}
		this.width = w;
		this.height = h;
		if (this.screen != null) {
			Window scaled = new Window(w, h);
			int scaledWidth = scaled.getWidth();
			int scaledHeight = scaled.getHeight();
			this.screen.init(this, scaledWidth, scaledHeight);
		}
	}

	@Override
	public void run() {
		try {
			super.run();
		} catch (Exception ex){
			this.shutdown();
			System.exit(0);
		}
	}
}
