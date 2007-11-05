/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2003-2006  Minnesota Department of Transportation
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
package us.mn.state.dot.tms;

/**
 * SignAlert is a class which encapsulates all the properties of a single
 * alert on a dynamic message sign (DMS).
 *
 * @author Douglas Lau
 */
public class SignAlert extends SignMessage {

	/** Create a new sign alert */
	public SignAlert(String o, MultiString m, BitmapGraphic b, int d) {
		super(o, m, b, d);
	}
}
