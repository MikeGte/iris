/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2020  SRF Consulting Group, Inc.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import us.mn.state.dot.tms.utils.UniqueNameCreator;

/**
 * Helper class for CAP response type substitution values.  Used on the client
 * and server.
 *
 * @author Gordon Parikh
 */
public class CapUrgencyFieldHelper extends BaseHelper {

	/** Don't instantiate */
	private CapUrgencyFieldHelper() {
		assert false;
	}

	/** Lookup the urgency substitution value with the specified name */
	static public CapUrgencyField lookup(String name) {
		return (CapUrgencyField) namespace.lookupObject(
			CapUrgencyField.SONAR_TYPE, name);
	}

	/** Lookup the urgency substitution value corresponding to the given
	 *  event and urgency value. */
	static public CapUrgencyField lookupFor(String event, String urg) {
		if (event == null || urg == null)
			return null;
		Iterator<CapUrgencyField> it = iterator();
		while (it.hasNext()) {
			CapUrgencyField cu = it.next();
			if (event.equals(cu.getEvent()) &&
			    urg.equals(cu.getUrgency()))
				return cu;
		}
		return null;
	}

	/** Get an CapUrgencyField object iterator */
	static public Iterator<CapUrgencyField> iterator() {
		return new IteratorWrapper<CapUrgencyField>(namespace.iterator(
			CapUrgencyField.SONAR_TYPE));
	}

	/** All known urgency substitution MULTI strings that match the given
	 *  urgency values (or any urgency values if uvals is empty).
	 */
	static public ArrayList<String> getMaxLen(String[] uvals) {
		// make a HashSet of the urgency values to evaluate inclusion
		HashSet<String> uvs = null;
		if (uvals.length > 0)
			uvs = new HashSet<String>(Arrays.asList(uvals));

		// go through all urgency value substitution MULTI strings
		ArrayList<String> multiStrs = new ArrayList<String>();
		Iterator<CapUrgencyField> it = iterator();
		while (it.hasNext()) {
			CapUrgencyField cu = it.next();
			if (uvs == null || uvs.contains(cu.getUrgency())) {
				String multi = cu.getMulti();
				if (multi != null)
					multiStrs.add(multi);
			}
		}
		return multiStrs;
	}

	/** Name creator */
	static private final UniqueNameCreator UNC = new UniqueNameCreator(
		"cap_urg_field_%d", 24, (n)->lookup(n));

	/** Create a unique CapUrgencyField record name */
	static public String createUniqueName() {
		return UNC.createUniqueName();
	}
}
