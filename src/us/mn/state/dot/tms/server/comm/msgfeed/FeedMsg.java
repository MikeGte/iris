/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2011  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm.msgfeed;

import java.util.Date;
import us.mn.state.dot.tms.DMS;
import us.mn.state.dot.tms.DMSHelper;
import us.mn.state.dot.tms.MultiString;
import us.mn.state.dot.tms.server.DMSImpl;

/**
 * Feed sign message.
 *
 * @author Douglas Lau
 */
public class FeedMsg {

	/** DMS to send message */
	private final DMSImpl dms;

	/** MULTI string */
	private final MultiString multi;

	/** Expire time */
	private final Date expire;

	/** Create a new feed message */
	public FeedMsg(String line) {
		String[] msg = line.split("\t", 3);
		dms = parseDms(msg[0]);
		multi = (msg.length > 1) ? new MultiString(msg[1]) : null;
		expire = (msg.length > 2) ? parseTime(msg[2]) : null;
	}

	/** Return the DMS or null if it doesn't exist */
	private DMSImpl parseDms(String txt) {
		DMS dms = DMSHelper.lookup(txt.trim());
		if(dms instanceof DMSImpl)
			return (DMSImpl)dms;
		else
			return null;
	}

	/** Parse a time stamp */
	private Date parseTime(String time) {
		/* FIXME */
		return new Date();
	}

	/** Get a string representation of the feed message */
	public String toString() {
		return "dms: " + dms + ", multi: " + multi +
			", expire: " + expire;
	}

	/** Activate the message */
	public void activate() {
		if(isValid()) {
			dms.setFeedMessage(multi);
			MsgFeedPoller.log("VALID " + toString());
		} else {
			dms.setFeedMessage(new MultiString());
			MsgFeedPoller.log("INVALID " + toString());
		}
	}

	/** Check if the feed message is valid */
	private boolean isValid() {
		return dms != null && isMultiValid() && !hasExpired();
	}

	/** Check if the multi string is valid */
	private boolean isMultiValid() {
		return multi != null && multi.isValid();
	}

	/** Check if the feed message has expired */
	private boolean hasExpired() {
		return expire == null || expire.after(new Date());
	}
}
