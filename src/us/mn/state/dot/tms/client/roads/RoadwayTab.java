/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2006-2009  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.roads;

import java.awt.BorderLayout;
import java.util.List;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapBean;
import us.mn.state.dot.tms.client.MapTab;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.SonarState;

/**
 * The RoadwayTab class provides the GUI for editing roadway nodes.
 *
 * @author Douglas Lau
 */
public class RoadwayTab extends MapTab {

	/** Corridor chooser component */
	protected final CorridorChooser chooser;

	/** Selected corridor list */
	protected final CorridorList clist;

	/** Create a new roadway node tab */
	public RoadwayTab(Session session, R_NodeManager m,
		List<LayerState> lstates)
	{
		super("Roadway", "View / edit roadway nodes");
		SonarState st = session.getSonarState();
		R_NodeCreator creator = new R_NodeCreator(st,session.getUser());
		clist = new CorridorList(m, creator);
		chooser = new CorridorChooser(m, clist);
		for(LayerState ls: lstates)
			map_model.addLayer(ls);
		LayerState ls = m.getLayer().createState();
		map_model.addLayer(ls);
		map_model.setHomeLayer(ls);
		add(chooser, BorderLayout.NORTH);
		add(clist, BorderLayout.CENTER);
	}

	/** Set the map */
	public void setMap(MapBean map) {
		chooser.setMap(map);
		// FIXME: this is ugly
		R_NodeProperties.map = map;
	}

	/** Get the tab number */
	public int getNumber() {
		return 5;
	}

	/** Dispose of the DMS tab */
	public void dispose() {
		super.dispose();
		chooser.dispose();
	}
}
