/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005  Minnesota Department of Transportation
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
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package us.mn.state.dot.tms.comm.mndot;

/**
 * Meter rates
 *
 * @author Douglas Lau
 */
public class MeterRate {

	/** Flash (non-metering) rate */
	static public int FLASH = 0;

	/** Central mode metering rate */
	static public int CENTRAL = 1;

	/** TOD mode metering rate */
	static public int TOD = 2;

	/** Forced flash (metering disabled) rate */
	static public int FORCED_FLASH = 7;

	static public boolean isValid(int r) {
		return r >= FLASH && r <= FORCED_FLASH;
	}

	static public boolean isMetering(int r) {
		return r > FLASH && r < FORCED_FLASH;
	}

	static public boolean isCentralControl(int r) {
		return r == FORCED_FLASH || r == CENTRAL;
	}
}
