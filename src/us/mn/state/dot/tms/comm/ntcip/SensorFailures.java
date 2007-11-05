/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2002  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.comm.ntcip;

/**
 * Ntcip SensorFailures object
 *
 * @author Douglas Lau
 */
public class SensorFailures extends SkylineDmsStatus implements ASN1OctetString
{
	/** Create a new SensorFailures object */
	public SensorFailures() {
		super(2);
		oid[node++] = 17;
		oid[node++] = 0;
	}

	/** Get the object name */
	protected String getName() { return "sensorFailures"; }

	/** Sensor failures bitmap */
	protected byte[] failures = new byte[0];

	/** Set the octet string value */
	public void setOctetString(byte[] value) { failures = value; }

	/** Get the octet string value */
	public byte[] getOctetString() { return failures; }

	/** Get the object value */
	public String getValue() {
		StringBuffer buffer = new StringBuffer();
		int f = 1;
		for(int i = 0; i < failures.length; i++) {
			for(int b = 0; b < 8; b++, f++) {
				if((failures[i] & 1 << b) != 0) {
					if(buffer.length() > 0)
						buffer.append(", ");
					buffer.append("#");
					buffer.append(f);
				}
			}
		}
		if(buffer.length() == 0) buffer.append("None");
		return buffer.toString();
	}
}
