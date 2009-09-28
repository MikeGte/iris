/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2009  Minnesota Department of Transportation
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

package us.mn.state.dot.tms.server.comm.dmslite;

import java.io.IOException;

/**
 * Pair class. A Pair is a LISP like pair, with a car and cdr.
 * @author      Michael Darter
 * @company     AHMCT, University of California, Davis
 * @created     03/12/08
 */
public class Pair
{
	/** Fields */
	private final Comparable m_car;
	private final Comparable m_cdr;

	/** Constructor */
	Pair(Comparable car, Comparable cdr) {
		m_car = car;
		m_cdr = cdr;
	}

	/** Return car */
	public Comparable car() {
		return m_car;
	}

	/** Return cdr */
	public Comparable cdr() {
		return m_cdr;
	}

	/** find a Pair in a list by comparing the car of each list 
	 *  element with the argument.
	 * @returns the first item in the list that matches else null 
	 *          if not found.
	 * @throws IOException if the specified car is not found. */
	public static Pair findCar(Pair[] list, Comparable argcar)
		throws IOException {
		for(Pair i : list)
			if(i.car().compareTo(argcar) == 0) {
				return i;
			}
		throw new IOException("Pair.findCar(" + argcar.toString()
			+ ") not found.");
	}
}
