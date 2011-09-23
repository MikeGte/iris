/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2008-2011  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.server.comm;

import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import us.mn.state.dot.tms.Modem;

/**
 * A Modem Messenger provides modem dialup support on top of another messenger
 * (such as StreamMessenger).
 *
 * @author Douglas Lau
 */
public class ModemMessenger extends Messenger {

	/** Wrapped messenger */
	protected final Messenger wrapped;

	/** Modem to dial */
	protected final Modem modem;

	/** Phone number to dial */
	protected final String phone_number;

	/** Read timeout (ms) */
	protected int timeout = 750;

	/** Create a new modem messenger */
	public ModemMessenger(Messenger m, Modem mdm, String phone) {
		wrapped = m;
		modem = mdm;
		phone_number = phone;
	}

	/** Set the messenger timeout */
	public void setTimeout(int t) throws IOException {
		wrapped.setTimeout(t);
		timeout = t;
	}

	/** Open the messenger */
	public void open() throws IOException {
		wrapped.open();
		output = wrapped.getOutputStream();
		input = wrapped.getInputStream();
		connectModem();
	}

	/** Close the messenger */
	public void close() {
		wrapped.close();
		output = null;
		input = null;
	}

    	/** Connect the modem to the specified phone number */
	protected void connectModem() throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(output,
			"US-ASCII");
		PrintWriter pw = new PrintWriter(osw, true);
		InputStreamReader isr = new InputStreamReader(input,"US-ASCII");
		String config = modem.getConfig();
		if(config != null && config.length() > 0)
			configureModem(pw, isr, config);
		dialModem(pw, isr);
		try {
			wrapped.setTimeout(modem.getTimeout());
			waitForConnect(isr);
		}
		finally {
			wrapped.setTimeout(timeout);
		}
    	}

	/** Configure the modem */
	protected void configureModem(PrintWriter pw, InputStreamReader isr,
		String config) throws IOException
	{
		pw.println("\r\n\r\n\r\n" + config + "\r\n");
		String resp = readResponse(isr);
		if(!resp.toUpperCase().contains("OK"))
			throw new IOException("Modem config error: " + resp);
	}

	/** Dial the modem */
	protected void dialModem(PrintWriter pw, InputStreamReader isr)
		throws IOException
	{
		pw.println("ATDT" + phone_number + "\r\n");
		readResponse(isr);
	}

	/** Wait for successful connection */
	protected void waitForConnect(InputStreamReader isr)
		throws IOException
	{
		String resp = readResponse(isr);
		if(!resp.toUpperCase().contains("CONNECT"))
			throw new IOException("Modem connect error: " + resp);
	}

	/** Read a reaponse from the modem */
	protected String readResponse(InputStreamReader isr) throws IOException{
		char[] buf = new char[64];
		int n_chars = isr.read(buf, 0, 64);
		if(n_chars < 0)
			throw new EOFException("END OF STREAM");
		return new String(buf, 0, n_chars);
	}
}
