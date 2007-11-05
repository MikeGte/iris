/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2006-2007  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.monitor;

import java.rmi.RemoteException;
import us.mn.state.dot.tms.client.TmsConnection;
import us.mn.state.dot.tms.client.toast.AddForm;
import us.mn.state.dot.tms.client.toast.Icons;
import us.mn.state.dot.tms.client.toast.SortedListForm;

/**
 * MonitorListForm
 *
 * @author    <a href="mailto:timothy.a.johnson@dot.state.mn.us">Tim Johnson</a>
 */
public class MonitorListForm extends SortedListForm {

	/** Frame title */
	static private final String TITLE = "Monitors";

	/** Add title */
	static private final String ADD_TITLE = "Add Monitors";

	/** Create a new monitor list form */
	public MonitorListForm(TmsConnection tc) {
		super(TITLE, tc, tc.getProxy().getMonitorList(),
			Icons.getIcon("monitor"));
	}

	/** Initializze the widgets in the form */
	protected void initialize() throws RemoteException {
		add(createListPanel());
		super.initialize();
	}

	/** Add an item to the list */
	protected void addItem() throws Exception {
		connection.getDesktop().show(new AddForm(ADD_TITLE, sList));
		updateButtons();
	}

	/** Edit an item in the list */
	protected void editItem() throws RemoteException {
		String id = getSelectedItem();
		if(id != null) {
			connection.getDesktop().show(
				new MonitorProperties(connection, id));
		}
	}

	/** Get the prototype cell value */
	protected String getPrototypeCellValue() {
		return "123";
	}
}
