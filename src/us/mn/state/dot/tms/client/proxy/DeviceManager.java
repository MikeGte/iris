/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2016-2019  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client.proxy;

import us.mn.state.dot.tms.Device;
import us.mn.state.dot.tms.GeoLoc;
import us.mn.state.dot.tms.ItemStyle;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.client.proxy.GeoLocManager;
import us.mn.state.dot.tms.client.proxy.ProxyManager;

/**
 * A device manager is a container for SONAR device objects.
 *
 * @author Douglas Lau
 */
abstract public class DeviceManager<T extends Device>
	extends ProxyManager<T>
{
	/** Create a new device manager */
	protected DeviceManager(Session s, GeoLocManager lm,
		ProxyDescriptor<T> pd, int zt, ItemStyle ds)
	{
		super(s, lm, pd, zt, ds);
	}

	/** Create a new device manager */
	protected DeviceManager(Session s, GeoLocManager lm,
		ProxyDescriptor<T> pd, int zt)
	{
		super(s, lm, pd, zt);
	}

	/** Check the style of the specified proxy */
	@Override
	public boolean checkStyle(ItemStyle is, T proxy) {
		return is.checkBit(proxy.getStyles());
	}
}
