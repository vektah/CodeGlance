package net.vektah.codeglance.render;

import com.intellij.openapi.diagnostic.Logger;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Runs tasks sequentially in a queue. Thread safe.
 */
public class TaskRunner implements Runnable {
	private boolean stop = false;
	private ArrayBlockingQueue<Runnable> taskQueue = new ArrayBlockingQueue<Runnable>(10);
	private Logger logger = Logger.getInstance(getClass());

	public void add(Runnable task) {
		logger.debug("Added new task");
		taskQueue.add(task);
	}

	public void stop() {
		stop = true;
	}

	@Override public void run() {
		while(!stop) {
			try {
				logger.debug("Starting task");
				taskQueue.take().run();
				logger.debug("Task completed");
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
