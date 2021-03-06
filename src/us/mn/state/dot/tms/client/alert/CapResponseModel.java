/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2020  SRF Consulting Group
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
package us.mn.state.dot.tms.client.alert;

import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import us.mn.state.dot.tms.CapResponse;
import us.mn.state.dot.tms.CapResponseEnum;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.ProxyColumn;
import us.mn.state.dot.tms.client.proxy.ProxyDescriptor;
import us.mn.state.dot.tms.client.proxy.ProxyTableModel;

/**
 * Table model for CAP response type substitution values.
 *
 * @author Gordon Parikh
 */
@SuppressWarnings("serial")
public class CapResponseModel extends ProxyTableModel<CapResponse> {

	/** Create a proxy descriptor */
	static public ProxyDescriptor<CapResponse> descriptor(Session s) {
		return new ProxyDescriptor<CapResponse>(
				s.getSonarState().getCapResponseCache(), true);
	}

	/** Create the columns in the model */
	@Override
	protected ArrayList<ProxyColumn<CapResponse>> createColumns() {
		ArrayList<ProxyColumn<CapResponse>> cols =
				new ArrayList<ProxyColumn<CapResponse>>(3);
		cols.add(new ProxyColumn<CapResponse>("alert.cap.event", 300) {
			public Object getValueAt(CapResponse crt) {
				return crt.getEvent();
			}
			public boolean isEditable(CapResponse crt) {
				return canWrite(crt);
			}
			public void setValueAt(CapResponse crt, Object value) {
				String ev = value.toString();
				if (ev == null || ev.isEmpty())
					crt.setEvent(CapResponse.DEFAULT_EVENT);
				else
					crt.setEvent(ev);
			}
		});
		cols.add(new ProxyColumn<CapResponse>("alert.cap.response",300){
			public Object getValueAt(CapResponse crt) {
				return crt.getResponseType();
			}
			public boolean isEditable(CapResponse crt) {
				return canWrite(crt);
			}
			public void setValueAt(CapResponse crt, Object value) {
				crt.setResponseType(value.toString());
			}
			protected TableCellEditor createCellEditor() {
				JComboBox<String> cbx = new JComboBox<String>(
						CapResponseEnum.stringValues());
				return new DefaultCellEditor(cbx);
			}
		});
		cols.add(new ProxyColumn<CapResponse>("alert.cap.multi", 300) {
			public Object getValueAt(CapResponse crt) {
				return crt.getMulti();
			}
			public boolean isEditable(CapResponse crt) {
				return canWrite(crt);
			}
			public void setValueAt(CapResponse crt, Object value) {
				// TODO validate MULTI
				crt.setMulti(value.toString());
			}
		});
		return cols;
	}

	public CapResponseModel(Session s) {
		super(s, descriptor(s), 12);
	}
}
