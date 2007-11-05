/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000,2001  Minnesota Department of Transportation
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
package us.mn.state.dot.tms;

import java.rmi.*;

/**
 * RemoteList
 *
 * @author Douglas Lau
 */
public interface RemoteList extends Remote, java.io.Serializable {

	/** Add an element to the list */
	public void add( int index, Object element ) throws RemoteException;

	/** Remove an element from the list */
	public void remove( int index ) throws RemoteException;

	/** Set an element in the list */
	public void set( int index, Object element ) throws RemoteException;
}
