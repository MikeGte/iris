/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2010-2015  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.ntcip.mib1203;

import us.mn.state.dot.tms.server.comm.snmp.ASN1Integer;

/**
 * The DmsLightSensorNumRows object indicates the number of rows in the light
 * sensor status table.  This object was added in NTCIP 1203 v2.
 *
 * @author Douglas Lau
 */
public class DmsLightSensorNumRows extends ASN1Integer {

	/** Create a new DmsLightSensorNumRows object */
	public DmsLightSensorNumRows() {
		super(MIB1203.statError.child(new int[] {29, 0}));
	}
}
