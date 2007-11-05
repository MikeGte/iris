/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2002  Minnesota Department of Transportation
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
 * Ntcip ModuleModel object
 *
 * @author Douglas Lau
 */
public class ModuleModel extends GlobalModuleTable implements ASN1OctetString {

	/** Create a new ModuleModel object */
	public ModuleModel(int i) {
		super(i);
	}

	/** Get the object name */
	protected String getName() { return "moduleModel"; }

	/** Get the module table item (for moduleModel objects) */
	protected int getTableItem() { return 4; }

	/** Actual module model */
	protected String model;

	/** Set the octet string value */
	public void setOctetString(byte[] value) { model = new String(value); }

	/** Get the octet string value */
	public byte[] getOctetString() { return model.getBytes(); }

	/** Get the object value */
	public String getValue() { return model; }
}
