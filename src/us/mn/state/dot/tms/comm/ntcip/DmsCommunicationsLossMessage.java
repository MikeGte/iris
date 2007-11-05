/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2006  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm.ntcip;

/**
 * Ntcip DmsCommunicationsLossMessage object
 *
 * @author Douglas Lau
 */
public class DmsCommunicationsLossMessage extends MessageIDCode {

	/** Create a new DMS communications loss message object
	  * @param m memory type
	  * @param n message number
	  * @param c message CRC */
	public DmsCommunicationsLossMessage(int m, int n, int c) {
		super(12);
		memory = m;
		number = n;
		crc = c;
	}

	/** Get the object name */
	protected String getName() { return "dmsCommunicationsLossMessage"; }
}
