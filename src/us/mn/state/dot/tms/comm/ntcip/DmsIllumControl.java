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
 * Ntcip DmsIllumControl object
 *
 * @author Douglas Lau
 */
public class DmsIllumControl extends Illum implements ASN1Integer {

	/** Brightness level control methods */
	static public final int UNDEFINED = 0;
	static public final int OTHER = 1;
	static public final int PHOTOCELL = 2;
	static public final int TIMER = 3;
	static public final int MANUAL = 4;

	/** Control method descriptions */
	static protected final String[] CONTROL = {
		"???", "other", "photocell", "timer", "manual"
	};

	/** Create a new DmsIllumControl object */
	public DmsIllumControl(int c) {
		super(1);
		setInteger(c);
	}

	/** Create a new DmsIllumControl object */
	public DmsIllumControl() {
		this(UNDEFINED);
	}

	/** Get the object name */
	protected String getName() { return "dmsIllumControl"; }

	/** Brightness level control method */
	protected int control;

	/** Set the integer value */
	public void setInteger(int value) {
		control = value;
		if(control < 0 || control >= CONTROL.length)
			control = UNDEFINED;
	}

	/** Get the integer value */
	public int getInteger() { return control; }

	/** Test if the brightness level control is "manual" */
	public boolean isManual() { return control == MANUAL; }

	/** Test if the brightness level control is "photocell" */
	public boolean isPhotocell() { return control == PHOTOCELL; }

	/** Get the object value */
	public String getValue() { return CONTROL[control]; }
}
