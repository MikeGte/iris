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
package us.mn.state.dot.tms.server.comm.pelcop;

import java.nio.ByteBuffer;
import static us.mn.state.dot.tms.DeviceRequest.*;
import us.mn.state.dot.tms.server.CameraImpl;
import us.mn.state.dot.tms.server.comm.Operation;
import us.mn.state.dot.tms.server.comm.ParsingException;

/**
 * Camera control property.  This message is sent by the keyboard to control
 * the selected camera.
 *
 * @author Douglas Lau
 */
public class CamControlProp extends MonStatusProp {

	/** Camera control request code */
	static public final int REQ_CODE = 0xC0;

	/** Focus far bit flag */
	static private final int BIT_FOCUS_FAR = 1 << 0;

	/** Focus near bit flag */
	static private final int BIT_FOCUS_NEAR = 1 << 1;

	/** Iris open bit flag */
	static private final int BIT_IRIS_OPEN = 1 << 2;

	/** Iris close bit flag */
	static private final int BIT_IRIS_CLOSE = 1 << 3;

	/** Extended command flag */
	static private final int BIT_EXTENDED = 1 << 0;

	/** Pan right flag */
	static private final int BIT_PAN_RIGHT = 1 << 1;

	/** Pan left flag */
	static private final int BIT_PAN_LEFT = 1 << 2;

	/** Tilt up flag */
	static private final int BIT_TILT_UP = 1 << 3;

	/** Tilt down flag */
	static private final int BIT_TILT_DOWN = 1 << 4;

	/** Zoom in flag */
	static private final int BIT_ZOOM_IN = 1 << 5;

	/** Zoom out flag */
	static private final int BIT_ZOOM_OUT = 1 << 6;

	/** Dead zone for joystick slop */
	static private final int DEAD_ZONE = 6;

	/** Maximum pan value */
	static private final int MAX_PAN = 64;

	/** Maximum tilt value */
	static private final int MAX_TILT = 63;

	/** Map a pan value to [0, 1] range */
	static private float pan_range(int p) {
		return (p > DEAD_ZONE)
		      ? (p - DEAD_ZONE) / (float) (MAX_PAN - DEAD_ZONE)
		      : 0;
	}

	/** Map a tilt value to [0, 1] range */
	static private float tilt_range(int p) {
		return (p > DEAD_ZONE)
		      ? (p - DEAD_ZONE) / (float) (MAX_TILT - DEAD_ZONE)
		      : 0;
	}

	/** Mask for valid extended packets */
	static private final int EXT_MASK = 0xC1;

	/** Extended store preset code */
	static private final int EXT_STORE_PRESET = 3;

	/** Extended clear preset code */
	static private final int EXT_CLEAR_PRESET = 5;

	/** Extended recall preset code */
	static private final int EXT_RECALL_PRESET = 7;

	/** Special preset number for menu open */
	static private final int MENU_OPEN_PRESET = 77;

	/** Special preset number for menu enter */
	static private final int MENU_ENTER_PRESET = 78;

	/** Special preset number for menu cancel */
	static private final int MENU_CANCEL_PRESET = 79;

	/** Create a new camera control property */
	public CamControlProp(boolean l, int mn) {
		super(l, mn);
	}

	/** Decode a QUERY request from keyboard */
	@Override
	public void decodeQuery(Operation op, ByteBuffer rx_buf)
		throws ParsingException
	{
		int mlo = parseBCD2(rx_buf);
		int cam = parseBCD4(rx_buf);
		int c0 = parse8(rx_buf);
		int c1 = parse8(rx_buf);
		int c2 = parse8(rx_buf);
		int c3 = parse8(rx_buf);
		int mhi = parseBCD2(rx_buf);
		if (parse8(rx_buf) != 0)
			throw new ParsingException("PTZ");
		setMonNumber((100 * mhi) + mlo);
		sendControl(cam, c0, c1, c2, c3);
	}

	/** Send control to camera */
	private void sendControl(int cam, int c0, int c1, int c2, int c3)
		throws ParsingException
	{
		CameraImpl c = findCamera(cam);
		if (c != null) {
			if (c0 != 0) {
				sendFocusControl(c, c0);
				sendIrisControl(c, c0);
			} else {
				if ((c1 & BIT_EXTENDED) == 0)
					sendPTZ(c, c1, c2, c3);
				else
					sendExtended(c, c1, c3);
			}
		}
	}

	/** Send focus control */
	private void sendFocusControl(CameraImpl c, int c0)
		throws ParsingException
	{
		switch (c0 & (BIT_FOCUS_FAR | BIT_FOCUS_NEAR)) {
		case 0:
			break;
		case BIT_FOCUS_FAR:
			c.setDeviceRequest(CAMERA_FOCUS_FAR.ordinal());
			break;
		case BIT_FOCUS_NEAR:
			c.setDeviceRequest(CAMERA_FOCUS_NEAR.ordinal());
			break;
		default:
			throw new ParsingException("FOCUS");
		}
	}

	/** Send iris control */
	private void sendIrisControl(CameraImpl c, int c0)
		throws ParsingException
	{
		switch (c0 & (BIT_IRIS_OPEN | BIT_IRIS_CLOSE)) {
		case 0:
			break;
		case BIT_IRIS_OPEN:
			c.setDeviceRequest(CAMERA_IRIS_OPEN.ordinal());
			break;
		case BIT_IRIS_CLOSE:
			c.setDeviceRequest(CAMERA_IRIS_CLOSE.ordinal());
			break;
		default:
			throw new ParsingException("IRIS");
		}
	}

	/** Send PTZ commands */
	private void sendPTZ(CameraImpl c, int c1, int c2, int c3)
		throws ParsingException
	{
		float pan = parsePan(c1, c2);
		float tilt = parseTilt(c1, c3);
		float zoom = parseZoom(c1);
		c.sendPTZ(pan, tilt, zoom);
	}

	/** Parse pan value */
	private float parsePan(int c1, int c2) throws ParsingException {
		switch (c1 & (BIT_PAN_RIGHT | BIT_PAN_LEFT)) {
		case 0:
			return 0;
		case BIT_PAN_RIGHT:
			return pan_range(c2);
		case BIT_PAN_LEFT:
			return -pan_range(c2);
		default:
			throw new ParsingException("PAN");
		}
	}

	/** Parse tilt value */
	private float parseTilt(int c1, int c3) throws ParsingException {
		switch (c1 & (BIT_TILT_UP | BIT_TILT_DOWN)) {
		case 0:
			return 0;
		case BIT_TILT_UP:
			return tilt_range(c3);
		case BIT_TILT_DOWN:
			return -tilt_range(c3);
		default:
			throw new ParsingException("TILT");
		}
	}

	/** Parse zoom value */
	private float parseZoom(int c1) throws ParsingException {
		switch (c1 & (BIT_ZOOM_IN | BIT_ZOOM_OUT)) {
		case 0:
			return 0;
		case BIT_ZOOM_IN:
			return 1;
		case BIT_ZOOM_OUT:
			return -1;
		default:
			throw new ParsingException("ZOOM");
		}
	}

	/** Send extended commands */
	private void sendExtended(CameraImpl c, int c1, int c3)
		throws ParsingException
	{
		if ((c1 & EXT_MASK) != BIT_EXTENDED)
			throw new ParsingException("EXT");
		int preset = parseBCD2(c3);
		switch (c1) {
		case EXT_STORE_PRESET:
			storePreset(c, preset);
			break;
		case EXT_RECALL_PRESET:
			c.setRecallPreset(preset);
			break;
		}
	}

	/** Store a preset */
	private void storePreset(CameraImpl c, int preset) {
		switch (preset) {
		case MENU_OPEN_PRESET:
			c.setDeviceRequest(CAMERA_MENU_OPEN.ordinal());
			break;
		case MENU_ENTER_PRESET:
			c.setDeviceRequest(CAMERA_MENU_ENTER.ordinal());
			break;
		case MENU_CANCEL_PRESET:
			c.setDeviceRequest(CAMERA_MENU_CANCEL.ordinal());
			break;
		default:
			c.setStorePreset(preset);
			break;
		}
	}
}
