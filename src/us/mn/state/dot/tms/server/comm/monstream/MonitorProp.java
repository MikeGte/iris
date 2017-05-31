/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2017  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.monstream;

import java.io.IOException;
import java.nio.ByteBuffer;
import us.mn.state.dot.tms.ControllerIO;
import us.mn.state.dot.tms.MonitorStyle;
import us.mn.state.dot.tms.server.VideoMonitorImpl;
import us.mn.state.dot.tms.server.comm.ControllerProp;
import us.mn.state.dot.tms.server.comm.Operation;

/**
 * A property to setup a monitor.
 *
 * @author Douglas Lau
 */
public class MonitorProp extends ControllerProp {

	/** Get a monitor style */
	static private MonitorStyle monitorStyle(VideoMonitorImpl mon) {
		return (mon != null)
		      ? mon.getMonitorStyle()
		      : null;
	}

	/** Get force-aspect as a string */
	static private String getForceAspect(VideoMonitorImpl mon) {
		MonitorStyle ms = monitorStyle(mon);
		return (ms != null && ms.getForceAspect()) ? "1" : "0";
	}

	/** Get monitor accent color */
	static private String getAccent(VideoMonitorImpl mon) {
		MonitorStyle ms = monitorStyle(mon);
		return (ms != null)
		      ? ms.getAccent()
		      : MonitorStyle.DEFAULT_ACCENT;
	}

	/** Get the monitor font size */
	static private int getFontSz(VideoMonitorImpl mon) {
		MonitorStyle ms = monitorStyle(mon);
		return (ms != null)
		      ? ms.getFontSz()
		      : MonitorStyle.DEFAULT_FONT_SZ;
	}

	/** ASCII record separator */
	static private final char RECORD_SEP = 30;

	/** ASCII unit separator */
	static private final char UNIT_SEP = 31;

	/** Current controller pin */
	private int pin = 1;

	/** Encode a STORE request */
	@Override
	public void encodeStore(Operation op, ByteBuffer tx_buf)
		throws IOException
	{
		tx_buf.put(formatReq(getMonitor(op)).getBytes("UTF8"));
	}

	/** Get current monitor */
	private VideoMonitorImpl getMonitor(Operation op) {
		ControllerIO cio = op.getController().getIO(pin);
		return (cio instanceof VideoMonitorImpl)
		      ? (VideoMonitorImpl) cio
		      :	null;
	}

	/** Advance to the next controller pin */
	public void advancePin(Operation op) {
		if (pin < op.getController().getMaxPin())
			pin++;
		else
			pin = 0;
	}

	/** Format a config request */
	private String formatReq(VideoMonitorImpl mon) {
		StringBuilder sb = new StringBuilder();
		sb.append("monitor");
		sb.append(UNIT_SEP);
		sb.append(pin - 1);
		sb.append(UNIT_SEP);
		sb.append(getMonLabel(mon));
		sb.append(UNIT_SEP);
		sb.append(getAccent(mon));
		sb.append(UNIT_SEP);
		sb.append(getForceAspect(mon));
		sb.append(UNIT_SEP);
		sb.append(Integer.toString(getFontSz(mon)));
		sb.append(RECORD_SEP);
		return sb.toString();
	}

	/** Get monitor label as a string */
	private String getMonLabel(VideoMonitorImpl mon) {
		if (mon != null) {
			int n = mon.getMonNum();
			if (n > 0)
				return Integer.toString(n);
			else
				return mon.getName();
		}
		return "";
	}

	/** Get a string representation of the property */
	@Override
	public String toString() {
		return "monitor: " + pin;
	}

	/** Does the controller have more monitors? */
	public boolean hasMore() {
		return pin > 0;
	}
}