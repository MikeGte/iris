/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2017-2018  Minnesota Department of Transportation
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
import us.mn.state.dot.tms.server.comm.Operation;

/**
 * A property to setup a monitor.
 *
 * @author Douglas Lau
 */
public class MonitorProp extends MonProp {

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

	/** Get the monitor title bar */
	static private boolean getTitleBar(VideoMonitorImpl mon) {
		MonitorStyle ms = monitorStyle(mon);
		return (ms != null) && ms.getTitleBar();
	}

	/** Get the horizontal gap */
	static private int getHGap(VideoMonitorImpl mon) {
		MonitorStyle ms = monitorStyle(mon);
		return (ms != null) ? ms.getHGap() : 0;
	}

	/** Get the vertical gap */
	static private int getVGap(VideoMonitorImpl mon) {
		MonitorStyle ms = monitorStyle(mon);
		return (ms != null) ? ms.getVGap() : 0;
	}

	/** Controller pin */
	private final int pin;

	/** Extra monitor numbers (full-screen) */
	private final String extra;

	/** Create a new monitor prop */
	public MonitorProp(int p, String ex) {
		pin = p;
		extra = ex;
	}

	/** Create a new monitor prop */
	public MonitorProp(int p) {
		this(p, "");
	}

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
		sb.append(UNIT_SEP);
		sb.append("AAAA");	// FIXME
		sb.append(UNIT_SEP);
		sb.append(Integer.toString(getHGap(mon)));
		sb.append(UNIT_SEP);
		sb.append(Integer.toString(getVGap(mon)));
		sb.append(UNIT_SEP);
		sb.append(extra);
		sb.append(RECORD_SEP);
		return sb.toString();
	}

	/** Get monitor label as a string */
	private String getMonLabel(VideoMonitorImpl mon) {
		if (getTitleBar(mon)) {
			assert mon != null;
			int n = mon.getMonNum();
			if (n > 0)
				return Integer.toString(n);
			else
				return mon.getName();
		} else
			return "";
	}

	/** Get a string representation of the property */
	@Override
	public String toString() {
		return "monitor: " + pin;
	}
}
