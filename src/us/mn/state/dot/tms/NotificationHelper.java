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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Iterator;
import us.mn.state.dot.sonar.SonarObject;
import us.mn.state.dot.tms.client.Session;
import us.mn.state.dot.tms.utils.UniqueNameCreator;

/**
 * Helper class for Push Notifications.  Used on the client and server.
 *
 * @author Gordon Parikh
 */
public class NotificationHelper extends BaseHelper {

	/** Don't instantiate */
	private NotificationHelper() {
		assert false;
	}

	/** Name creator */
	static private final UniqueNameCreator UNC = new UniqueNameCreator(
		"notif_%d", 30, (n)->lookup(n));

	/** Create a unique Notification record name */
	static public String createUniqueName() {
		return UNC.createUniqueName();
	}

	/** Lookup the Notification with the specified name */
	static public Notification lookup(String name) {
		return (Notification) namespace.lookupObject(
			Notification.SONAR_TYPE, name);
	}

	/** Address all outstanding Notification objects associated with the
	 *  given reference object.
	 */
	static public void addressAllRef(SonarObject refObject, Session s) {
		if (refObject == null)
			return;
		Iterator<Notification> it = iterator();
		boolean changed = false;
		while (it.hasNext()) {
			Notification pn = it.next();
			if (refObject.getTypeName().equals(pn.getRefObjectType())
					&& refObject.getName().equals(pn.getRefObjectName())
					&& pn.getAddressedTime() == null) {
				pn.setAddressedBy(s.getUser().getName());
				pn.setAddressedTime(new Date());
				changed = true;
			}
		}
		if (changed)
			s.getNotificationManager().checkStopBlinkBG();
	}

	/** Address all outstanding Notification objects. */
	static public void addressAll(Session s) {
		Iterator<Notification> it = iterator();
		int n = 0;
		while (it.hasNext()) {
			Notification pn = it.next();
			// make sure the user can see this notification and it
			// hasn't been addressed
			if (checkPrivileges(s, pn) && checkAddressed(pn, false)) {
				pn.setAddressedBy(s.getUser().getName());
				pn.setAddressedTime(new Date());
				++n;
			}
		}
		System.out.println("Addressed " + n + " notifications");
		s.getNotificationManager().checkStopBlinkBG();
	}

	/** Find a Notification object associated with the given reference
	 *  object. Only returns one object.
	 */
	static public Notification lookup(SonarObject refObject) {
		if (refObject == null)
			return null;
		Iterator<Notification> it = iterator();
		while (it.hasNext()) {
			Notification pn = it.next();
			if (refObject.getTypeName().equals(pn.getRefObjectType())
					&& refObject.getName().equals(pn.getRefObjectName())) {
				return pn;
			}
		}
		return null;
	}

	/** Get a Notification object iterator */
	static public Iterator<Notification> iterator() {
		return new IteratorWrapper<Notification>(namespace.iterator(
			Notification.SONAR_TYPE));
	}

	/** Check if the user can see this notification based on their privileges
	 *  and whether the notification has been addressed. If pastOk is True,
	 *  recently-addressed notifications are also included.
	 */
	static public boolean check(Session s, Notification pn, boolean pastOk) {
		return checkPrivileges(s, pn) && checkAddressed(pn, pastOk);
	}

	/** Check if the user can see this notification based on their privileges.
	 *  If needs_write is true, the user must be able to write objects of this
	 *  type, otherwise they must be able to read them. Note that this
	 *  overrides edit mode.
	  */
	static public boolean checkPrivileges(Session s, Notification pn) {
		String tname = (pn != null) ? pn.getRefObjectType() : null;
		if (tname != null) {
			return pn.getNeedsWrite() ? s.canWrite(tname, true)
					: s.canRead(tname);
		}
		return false;
	}

	/** Check if this notification has not yet been addressed. If pastOk is
	 *  true, the the time since this notification has been addressed is
	 *  checked against a system attribute, otherwise this only checks if the
	 *  addressed_time attribute has been set (if so returning false).
	 */
	static public boolean checkAddressed(Notification pn, boolean pastOk) {
		if (pn != null) {
			Date addrTime = pn.getAddressedTime();
			if (!pastOk)
				return addrTime == null;
			if (addrTime != null) {
				LocalDateTime at = addrTime.toInstant().atZone(
						ZoneId.systemDefault()).toLocalDateTime();
				return Duration.between(at, LocalDateTime.now()).getSeconds()
					<= SystemAttrEnum.NOTIFICATION_TIMEOUT_SECS.getInt();
			} else
				// if hasn't been addressed, show it
				return true;
		}
		return false;
	}

	/** Make a human-readable duration string from a Date object and the
	 *  current date/time. Effectively calls Duration.between(d, now),
	 *  but works with LocalDateTime objects (using the system's time zone to
	 *  convert from the Date object). This truncates to seconds (no
	 *  milliseconds are shown).
	 */
	static public String getDurationString(Date d) {
		if (d != null) {
			LocalDateTime dt = d.toInstant().truncatedTo(
					ChronoUnit.SECONDS).atZone(ZoneId.systemDefault())
					.toLocalDateTime();
			LocalDateTime now = LocalDateTime.now()
					.truncatedTo(ChronoUnit.SECONDS);
			return getDurationString(Duration.between(dt, now));
		}
		return "";
	}

	/** Make a human-readable duration string from a Duration object. This
	 *  truncates to seconds (no milliseconds are shown).
	 */
	static public String getDurationString(Duration d) {
		if (d != null) {
			return d.toString().substring(2).replaceAll(
					"(\\d[HMS])(?!$)", "$1 ").toLowerCase();
		}
		return "";
	}
}
