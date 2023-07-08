package com.mygdx.game.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.utils.TimeUtils;

public class DelayedRunnableHandler {

	private List<DelayedRunnable> runnables;
	private List<DelayedRunnable> queue;

	public DelayedRunnableHandler() {
		runnables = new ArrayList<>();
		queue = new ArrayList<>();
	}

	public void update() {
		long time = TimeUtils.millis();
		runnables.addAll(queue);
		queue.clear();

		Iterator<DelayedRunnable> runnables = this.runnables.iterator();
		List<DelayedRunnable> expiredRunnables = new ArrayList<>();
		while (runnables.hasNext()) {
			DelayedRunnable runnable = runnables.next();
			if (runnable.isDelayOver(time)) {
				runnable.run();
				expiredRunnables.add(runnable);
			}
		}
		expiredRunnables.forEach(this.runnables::remove);
	}

	public void add(Runnable runnable, long msDelay) {
		// adding to queue instead of directly to runnables to avoid concurrent
		// modification exceptions
		queue.add(new DelayedRunnable(runnable, msDelay));
	}

	private class DelayedRunnable implements Runnable {

		private Runnable runnable;
		private long msDelay;
		private long startTime;

		public DelayedRunnable(Runnable runnable, long msDelay) {
			this.runnable = runnable;
			this.msDelay = msDelay;
			startTime = TimeUtils.millis();
		}

		@Override
		public void run() {
			runnable.run();
		}

		public boolean isDelayOver(long time) {
			return time - startTime > msDelay;
		}
	}
}
