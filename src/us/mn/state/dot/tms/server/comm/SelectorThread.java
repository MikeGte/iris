/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016-2017  Minnesota Department of Transportation
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
package us.mn.state.dot.tms.server.comm;

import us.mn.state.dot.sched.TimeSteward;

/**
 * The selector thread performs non-blocking I/O on a set of channels.
 *
 * @author Douglas Lau
 */
public final class SelectorThread {

	/** Singleton comm selector */
	static private CommSelector TASK;

	/** Thread group for selector thread */
	static private final ThreadGroup GROUP = new ThreadGroup("Selector");

	/** Create the selector thread */
	static {
		new SelectorThread();
	}

	/** Thread to run select loop */
	private final Thread thread;

	/** Create a new selector thread */
	public SelectorThread() {
 		thread = new Thread(GROUP, "selector") {
			@Override public void run() {
				doRun();
			}
		};
		thread.setDaemon(true);
		thread.start();
	}

	/** Run the thread */
	private void doRun() {
		try (CommSelector task = new CommSelector()) {
			TASK = task;
			task.selectLoop();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		// CommSelector will auto-close, so don't use it
		finally {
			TASK = null;
		}
	}

	/** Get the comm selector */
	static public CommSelector getSelector() {
		// Loop for 4 seconds to allow for race at startup
		for (int i = 0; i < 20; i++) {
			CommSelector task = TASK;
			if (task != null)
				return task;
			TimeSteward.sleep_well(200);
		}
		return TASK;
	}
}
