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
package us.mn.state.dot.tms.utils;

import java.io.IOException;
import java.net.URL;

/**
 * Simple class to open a web browser in another process
 *
 * @author Douglas Lau
 */
public class WebBrowser {

	static protected final String WINDOWS_EXECUTABLE =
		"\"\\Program Files\\Internet Explorer\\IExplore\" ";

	static protected final String OTHER_EXECUTABLE = "firefox ";

	static protected final String EXECUTABLE;
	static {
		String osName = System.getProperties().getProperty(
			"os.name").toLowerCase();
		if(osName.indexOf("windows") >= 0)
			EXECUTABLE = WINDOWS_EXECUTABLE;
		else
			EXECUTABLE = OTHER_EXECUTABLE;
	}

	/** Execute a subprocess with a web browser at the given URL */
	static public void open(URL url) throws IOException {
		Runtime.getRuntime().exec(EXECUTABLE + url.toString());
	}
}
