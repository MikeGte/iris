/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2005-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.camera;

import java.awt.BorderLayout;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.utils.I18N;

/**
 * Provides a GUI for the camera tab on the operator interface for IRIS.
 *
 * @author Douglas Lau
 */
public class CameraTab extends MapTab {

	/** Create a new camera tab for the IRIS client */
	public CameraTab(Session session, CameraManager manager) {
		super(manager.getProxyType(), I18N.get("camera.summary"));
		add(new CameraViewer(session, manager), BorderLayout.NORTH);
		add(manager.createStyleSummary(), BorderLayout.CENTER);
	}

	/** Get the tab number */
	public int getNumber() {
		return 2;
	}
}
