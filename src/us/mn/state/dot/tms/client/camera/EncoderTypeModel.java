/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2017-2019  Minnesota Department of Transportation
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

import java.util.ArrayList;
import us.mn.state.dot.tms.EncoderType;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyDescriptor;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for encoder types.
 *
 * @author Douglas Lau
 */
public class EncoderTypeModel extends ProxyTableModel<EncoderType> {

	/** Create a proxy descriptor */
	static public ProxyDescriptor<EncoderType> descriptor(Session s) {
		return new ProxyDescriptor<EncoderType>(
			s.getSonarState().getCamCache().getEncoderTypes(),
			false,	/* has_properties */
			true,	/* has_create_delete */
			false	/* has_name */
		);
	}

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<EncoderType>> createColumns() {
		ArrayList<ProxyColumn<EncoderType>> cols =
			new ArrayList<ProxyColumn<EncoderType>>(4);
		cols.add(new ProxyColumn<EncoderType>("encoder.type", 100) {
			public Object getValueAt(EncoderType et) {
				return et.getName();
			}
		});
		cols.add(new ProxyColumn<EncoderType>("encoder.type.make",
			100)
		{
			public Object getValueAt(EncoderType et) {
				return et.getMake();
			}
			public boolean isEditable(EncoderType et) {
				return canWrite(et, "make");
			}
			public void setValueAt(EncoderType et, Object value) {
				et.setMake(value.toString());
			}
		});
		cols.add(new ProxyColumn<EncoderType>("encoder.type.model", 100)
		{
			public Object getValueAt(EncoderType et) {
				return et.getModel();
			}
			public boolean isEditable(EncoderType et) {
				return canWrite(et, "model");
			}
			public void setValueAt(EncoderType et, Object value) {
				et.setModel(value.toString());
			}
		});
		cols.add(new ProxyColumn<EncoderType>("encoder.type.config", 80)
		{
			public Object getValueAt(EncoderType et) {
				return et.getConfig();
			}
			public boolean isEditable(EncoderType et) {
				return canWrite(et, "config");
			}
			public void setValueAt(EncoderType et, Object value) {
				et.setConfig(value.toString());
			}
		});
		return cols;
	}

	/** Create a new encoder type table model */
	public EncoderTypeModel(Session s) {
		super(s, descriptor(s), 12);
	}
}
