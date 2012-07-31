/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2012  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.dms;

import us.mn.state.dot.sonar.User;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.GeoLocHelper;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;
import us.mn.state.dot.tms.client.proxy.SonarObjectForm;
import us.mn.state.dot.tms.utils.I18N;

/**
 * Table model for DMS table form 2.
 *
 * @author Michael Darter
 * @author Douglas Lau
 * @see DMSForm, DMSForm2
 */
public class DMSModel2 extends ProxyTableModel<DMS> {

	/** Create the columns in the model */
	protected ProxyColumn[] createColumns() {
	    // NOTE: half-indent to declare array
	    return new ProxyColumn[] {
		new ProxyColumn<DMS>(I18N.get("dms.abbreviation"), 40) {
			public Object getValueAt(DMS d) {
				return d.getName();
			}
			public boolean isEditable(DMS d) {
				return (d == null) && canAdd();
			}
			public void setValueAt(DMS d, Object value) {
				String v = value.toString().trim();
				if(v.length() > 0)
					cache.createObject(v);
			}
		},
		new ProxyColumn<DMS>(I18N.get("location"), 200) {
			public Object getValueAt(DMS d) {
				return GeoLocHelper.getDescription(
					d.getGeoLoc());
			}
		},
		new ProxyColumn<DMS>(I18N.get("location.dir"), 30) {
			public Object getValueAt(DMS d) {
				return DMSHelper.getRoadDir(d);
			}
		},
		new ProxyColumn<DMS>(I18N.get("dms.aws.allowed"), 80,
			Boolean.class)
		{
			public Object getValueAt(DMS d) {
				return d.getAwsAllowed();
			}
		},
		new ProxyColumn<DMS>(I18N.get("device.style.aws.controlled"),
			80, Boolean.class)
		{
			public Object getValueAt(DMS d) {
				return d.getAwsControlled();
			}
		},
		new ProxyColumn<DMS>(I18N.get("dms.owner"), 60) {
			public Object getValueAt(DMS d) {
				User u = d.getOwnerCurrent();
				String name = (u == null ? "" : u.getName());
				return (name == null ? "" : name);
			}
		},
		new ProxyColumn<DMS>(I18N.get("device.status"), 100) {
			public Object getValueAt(DMS d) {
				return DMSHelper.getAllStyles(d);
			}
		},
		new ProxyColumn<DMS>(I18N.get("dms.model"), 40) {
			public Object getValueAt(DMS d) {
				return d.getModel();
			}
		},
		new ProxyColumn<DMS>(I18N.get("dms.access"), 140) {
			public Object getValueAt(DMS d) {
				return d.getSignAccess();
			}
		}
	    };
	}

	/** Create a new DMS table model */
	public DMSModel2(Session s) {
		super(s, s.getSonarState().getDmsCache().getDMSs());
	}

	/** Determine if a properties form is available */
	public boolean hasProperties() {
		return true;
	}

	/** Create a properties form for one proxy */
	protected SonarObjectForm<DMS> createPropertiesForm(DMS proxy) {
		return new DMSProperties(session, proxy);
	}

	/** Get the SONAR type name */
	protected String getSonarType() {
		return DMS.SONAR_TYPE;
	}
}
