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
package us.mn.state.dot.tms.server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.tms.ColorScheme;
import us.mn.state.dot.tms.DmsColor;
import us.mn.state.dot.tms.Font;
import us.mn.state.dot.tms.FontHelper;
import us.mn.state.dot.tms.SignConfig;
import us.mn.state.dot.tms.SignConfigHelper;
import us.mn.state.dot.tms.TMSException;

/**
 * A sign configuration defines the type and dimensions of a sign.
 *
 * @author Douglas Lau
 */
public class SignConfigImpl extends BaseObjectImpl implements SignConfig {

	/** Find existing or create a new sign config.
	 * @param fw Face width (mm).
	 * @param fh Face height (mm).
	 * @param bh Border -- horizontal (mm).
	 * @param bv Border -- vertical (mm).
	 * @param ph Pitch -- horizontal (mm).
	 * @param pv Pitch -- vertical (mm).
	 * @param pxw Pixel width.
	 * @param pxh Pixel height.
	 * @param cw Character width (0 means variable).
	 * @param ch Character height (0 means variable).
	 * @param mf Monochrome foreground color (24-bit).
	 * @param mb Monochrome background color (24-bit).
	 * @param cs Color scheme ordinal.
	 * @return Matching existing, or new sign config.
	 */
	static public SignConfigImpl findOrCreate(int fw, int fh, int bh, int bv,
		int ph, int pv, int pxw, int pxh, int cw, int ch, int mf, int mb,
		int cs)
	{
		if (fw <= 0 || fh <= 0 || bh < 0 || bv < 0 || ph <= 0 ||
		    pv <= 0 || pxw <= 0 || pxh <= 0 || cw < 0 || ch < 0)
			return null;
		SignConfig sc = SignConfigHelper.find(fw, fh, bh, bv, ph, pv,
			pxw, pxh, cw, ch, mf, mb, cs);
		if (sc instanceof SignConfigImpl)
			return (SignConfigImpl) sc;
		else {
			String n = createUniqueName();
			SignConfigImpl sci = new SignConfigImpl(n, fw, fh, bh,
				bv, ph, pv, pxw, pxh, cw, ch, mf, mb, cs, "");
			return createNotify(sci);
		}
	}

	/** Notify clients of the new sign config */
	static private SignConfigImpl createNotify(SignConfigImpl sc) {
		try {
			sc.notifyCreate();
			return sc;
		}
		catch (SonarException e) {
			System.err.println("createNotify: " + e.getMessage());
			return null;
		}
	}

	/** Find or create LCS sign config */
	static public SignConfigImpl findOrCreateLCS() {
		return findOrCreate(600, 600, 1, 1, 1, 1, 1, 1, 0, 0,
			ColorScheme.MONOCHROME_1_BIT.ordinal(),
			DmsColor.AMBER.rgb(), DmsColor.BLACK.rgb());
	}

	/** Last allocated sign config ID */
	static private int last_id = 0;

	/** Create a unique sign config name */
	static private synchronized String createUniqueName() {
		String n = createNextName();
		while (namespace.lookupObject(SONAR_TYPE, n) != null)
			n = createNextName();
		return n;
	}

	/** Create the next system config name */
	static private String createNextName() {
		last_id++;
		// Check if the ID has rolled over to negative numbers
		if (last_id < 0)
			last_id = 0;
		return "cfg_" + last_id;
	}

	/** Load all the sign configs */
	static protected void loadAll() throws TMSException {
		namespace.registerType(SONAR_TYPE, SignConfigImpl.class);
		store.query("SELECT name, face_width, face_height, " +
			"border_horiz, border_vert, pitch_horiz, pitch_vert, " +
			"pixel_width, pixel_height, char_width, char_height, " +
			"monochrome_foreground, monochrome_background, " +
			"color_scheme, default_font FROM iris." +
			SONAR_TYPE + ";", new ResultFactory()
		{
			public void create(ResultSet row) throws Exception {
				namespace.addObject(new SignConfigImpl(row));
			}
		});
	}

	/** Get a mapping of the columns */
	@Override
	public Map<String, Object> getColumns() {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("name", name);
		map.put("face_width", face_width);
		map.put("face_height", face_height);
		map.put("border_horiz", border_horiz);
		map.put("border_vert", border_vert);
		map.put("pitch_horiz", pitch_horiz);
		map.put("pitch_vert", pitch_vert);
		map.put("pixel_width", pixel_width);
		map.put("pixel_height", pixel_height);
		map.put("char_width", char_width);
		map.put("char_height", char_height);
		map.put("monochrome_foreground", monochrome_foreground);
		map.put("monochrome_background", monochrome_background);
		map.put("color_scheme", color_scheme.ordinal());
		map.put("default_font", default_font);
		return map;
	}

	/** Get the database table name */
	@Override
	public String getTable() {
		return "iris." + SONAR_TYPE;
	}

	/** Get the SONAR type name */
	@Override
	public String getTypeName() {
		return SONAR_TYPE;
	}

	/** Create a sign config */
	private SignConfigImpl(ResultSet row) throws SQLException {
		this(row.getString(1),   // name
		     row.getInt(2),      // face_width
		     row.getInt(3),      // face_height
		     row.getInt(4),      // border_horiz
		     row.getInt(5),      // border_vert
		     row.getInt(6),      // pitch_horiz
		     row.getInt(7),      // pitch_vert
		     row.getInt(8),      // pixel_width
		     row.getInt(9),      // pixel_height
		     row.getInt(10),     // char_width
		     row.getInt(11),     // char_height
		     row.getInt(12),     // monochrome_foreground
		     row.getInt(13),     // monochrome_background
		     row.getInt(14),     // color_scheme
		     row.getString(15)   // default_font
		);
	}

	/** Create a sign config */
	private SignConfigImpl(String n, int fw, int fh, int bh, int bv, int ph,
		int pv, int pxw, int pxh, int cw, int ch, int mf, int mb,
		int cs, String df)
	{
		super(n);
		face_width = fw;
		face_height = fh;
		border_horiz = bh;
		border_vert = bv;
		pitch_horiz = ph;
		pitch_vert = pv;
		pixel_width = pxw;
		pixel_height = pxh;
		char_width = cw;
		char_height = ch;
		monochrome_foreground = mf;
		monochrome_background = mb;
		color_scheme = ColorScheme.fromOrdinal(cs);
		default_font = FontHelper.lookup(df);
	}

	/** Width of the sign face (mm) */
	private final int face_width;

	/** Get width of the sign face (mm) */
	@Override
	public int getFaceWidth() {
		return face_width;
	}

	/** Height of sign face (mm) */
	private final int face_height;

	/** Get height of the sign face (mm) */
	@Override
	public int getFaceHeight() {
		return face_height;
	}

	/** Horizontal border (mm) */
	private final int border_horiz;

	/** Get horizontal border (mm) */
	@Override
	public int getBorderHoriz() {
		return border_horiz;
	}

	/** Vertical border (mm) */
	private final int border_vert;

	/** Get vertical border (mm) */
	@Override
	public int getBorderVert() {
		return border_vert;
	}

	/** Horizontal pitch (mm) */
	private final int pitch_horiz;

	/** Get horizontal pitch (mm) */
	@Override
	public int getPitchHoriz() {
		return pitch_horiz;
	}

	/** Vertical pitch (mm) */
	private final int pitch_vert;

	/** Get vertical pitch (mm) */
	@Override
	public int getPitchVert() {
		return pitch_vert;
	}

	/** Sign width in pixels */
	private final int pixel_width;

	/** Get sign width (pixels) */
	@Override
	public int getPixelWidth() {
		return pixel_width;
	}

	/** Sign height (pixels) */
	private final int pixel_height;

	/** Get sign height (pixels) */
	@Override
	public int getPixelHeight() {
		return pixel_height;
	}

	/** Character width (pixels; 0 means variable) */
	private final int char_width;

	/** Get character width (pixels) */
	@Override
	public int getCharWidth() {
		return char_width;
	}

	/** Character height (pixels; 0 means variable) */
	private final int char_height;

	/** Get character height (pixels) */
	@Override
	public int getCharHeight() {
		return char_height;
	}

	/** Monochrome scheme foreground color (24-bit). */
	private final int monochrome_foreground;

	/** Get monochrome scheme foreground color (24-bit). */
	@Override
	public int getMonochromeForeground() {
		return monochrome_foreground;
	}

	/** Monochrome scheme background color (24-bit). */
	private final int monochrome_background;

	/** Get monochrome scheme background color (24-bit). */
	@Override
	public int getMonochromeBackground() {
		return monochrome_background;
	}

	/** DMS Color scheme */
	private final ColorScheme color_scheme;

	/** Get the color scheme (ordinal of ColorScheme) */
	@Override
	public int getColorScheme() {
		return color_scheme.ordinal();
	}

	/** Default font */
	private Font default_font;

	/** Set the default font */
	@Override
	public void setDefaultFont(Font f) {
		default_font = f;
	}

	/** Set the default font */
	public void doSetDefaultFont(Font f) throws TMSException {
		if (f != default_font) {
			store.update(this, "default_font", f);
			setDefaultFont(f);
		}
	}

	/** Get the default font */
	@Override
	public Font getDefaultFont() {
		return default_font;
	}
}
