/*
 * IRIS -- Intelligent Roadway Information System
 * Copyright (C) 2000-2010  Minnesota Department of Transportation
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
package us.mn.state.dot.tms.client;

import java.awt.Cursor;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import us.mn.state.dot.log.TmsLogFactory;
import us.mn.state.dot.map.Layer;
import us.mn.state.dot.map.LayerState;
import us.mn.state.dot.map.MapModel;
import us.mn.state.dot.sched.AbstractJob;
import us.mn.state.dot.sched.Scheduler;
import us.mn.state.dot.sonar.ConfigurationError;
import us.mn.state.dot.sonar.SonarException;
import us.mn.state.dot.sonar.User;
import us.mn.state.dot.trafmap.BaseLayers;
import us.mn.state.dot.tms.SystemAttrEnum;
import us.mn.state.dot.tms.utils.PropertyFile;
import us.mn.state.dot.tms.client.system.LoginForm;
import us.mn.state.dot.tms.client.toast.SmartDesktop;
import us.mn.state.dot.tms.client.widget.Screen;
import us.mn.state.dot.tms.client.widget.ScreenLayout;
import us.mn.state.dot.tms.utils.I18N;

/**
 * The Main class for IrisClient.
 *
 * @author Erik Engstrom
 * @author Douglas Lau
 */
public class IrisClient extends JFrame {

	/** Login scheduler */
	static protected final Scheduler LOGIN = new Scheduler("LOGIN");

	/** Window title login message */
	static protected final String WINDOW_TITLE_LOGIN = "Login to Start";

	/** Get window title.
	 * @param u User, may be null. */
	static protected String getWindowTitle(User u) {
		return SystemAttrEnum.WINDOW_TITLE.getString() +
		       getWindowTitleSuffix(u);
	}

	/** Get the window title suffix */
	static protected String getWindowTitleSuffix(User u) {
		if(u != null)
			return u.getName() + " (" + u.getFullName() + ")";
		else
			return WINDOW_TITLE_LOGIN;
	}

	/** Array of screens to display client */
	protected final Screen[] screens;

	/** Array of screen panes */
	protected final ScreenPane[] s_panes;

	/** Base layers */
	protected final List<Layer> baseLayers;

	/** Desktop pane */
	protected final SmartDesktop desktop;

	/** Screen layout for desktop pane */
	protected final ScreenLayout layout;

	/** Message logger */
	protected final Logger logger;

	/** Client properties */
	protected final Properties props;

	/** Session menu */
	protected final SessionMenu session_menu;

	/** View menu */
	protected JMenu view_menu;

	/** Help menu */
	protected final HelpMenu help_menu;

	/** Login session information */
	protected Session session;

	/** Create a new Iris client */
	public IrisClient(Properties props) throws IOException {
		super(getWindowTitle(null));
		this.props = props;
		logger = TmsLogFactory.createLogger("IRIS", Level.WARNING,
			null);
		I18N.initialize(props);
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		screens = Screen.getAllScreens();
		s_panes = new ScreenPane[screens.length];
		desktop = new SmartDesktop(screens[0]);
		baseLayers = new BaseLayers().getLayers();
		initializeScreenPanes();
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				quit();
			}
		});
		layout = new ScreenLayout(desktop);
		getContentPane().add(desktop);
		session_menu = new SessionMenu(this);
		help_menu = new HelpMenu(desktop);
		buildMenus();
		autoLogin();
	}

	/** Log out the current session and quit the client */
	public void quit() {
		logout();
		new AbstractJob(LOGIN) {
			public void perform() {
				System.exit(0);
			}
		}.addToScheduler();
	}

	/** Initialize the screen panes */
	protected void initializeScreenPanes() {
		for(int s = 0; s < s_panes.length; s++)
			s_panes[s] = new ScreenPane();
		for(ScreenPane sp: s_panes) {
			sp.addComponentListener(new ComponentAdapter() {
				public void componentHidden(ComponentEvent e) {
					arrangeTabs();
				}
				public void componentShown(ComponentEvent e) {
					arrangeTabs();
				}
			});
			desktop.add(sp, JLayeredPane.DEFAULT_LAYER);
			MapModel mm = new MapModel();
			for(Layer l: baseLayers) {
				LayerState ls = l.createState();
				mm.addLayer(ls);
				mm.setHomeLayer(ls);
			}
			sp.getMap().setModel(mm);
		}
	}

	/** Make the frame displayable (called by window toolkit) */
	public void addNotify() {
		super.addNotify();
		setBounds(Screen.getMaximizedBounds());
		if(screens.length < 2)
			setExtendedState(MAXIMIZED_BOTH);
	}

	/** Build all the menus */
	protected void buildMenus() {
		JMenuBar m_bar = new JMenuBar();
		m_bar.add(session_menu);
		m_bar.add(help_menu);
		setJMenuBar(m_bar);
	}

	/** Auto-login the user if enabled */
	protected void autoLogin() {
		String un = PropertyFile.get(props, "autologin.username");
		String pws = PropertyFile.get(props, "autologin.password");
		if(un == null || pws == null)
			return;
		char[] pw = pws.toCharArray();
		pws = null;
		if(un.length() > 0 && pw.length > 0) {
			try {
				doLogin(un, pw);
			}
			catch(Exception ex) {
				System.err.println("Auto-login failed.");
				ex.printStackTrace();
			}
		}
	}

	/** Get a list of all visible screen panes. Will return an empty
	 *  list if IRIS is minimized. */
	protected LinkedList<ScreenPane> getVisiblePanes() {
		LinkedList<ScreenPane> visible = new LinkedList<ScreenPane>();
		for(ScreenPane s: s_panes)
			if(s.isVisible())
				visible.add(s);
		return visible;
	}

	/** Arrange the tabs on the visible screen panes */
	protected void arrangeTabs() {
		removeTabs();
		Session s = session;
		if(s != null) {
			LinkedList<ScreenPane> visible = getVisiblePanes();
			if(visible.isEmpty())
				return;
			for(MapTab tab: s.getTabs()) {
				int p = tab.getNumber() % visible.size();
				ScreenPane pane = visible.get(p);
				pane.addTab(tab);
				tab.setMap(pane.getMap());
			}
			for(ScreenPane sp: visible)
				sp.createToolPanels(s);
		}
	}

	/** Show the login form */
	public void login() {
		if(session == null)
			desktop.show(new LoginForm(this, desktop));
	}

	/** Login a user */
	public void login(final String user, final char[] pwd) {
		new AbstractJob(LOGIN) {
			public void perform() throws Exception {
				doLogin(user, pwd);
			}
		}.addToScheduler();
	}

	/** Login a user */
	protected void doLogin(String user, char[] pwd) throws Exception {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		closeSession();
		try {
			session = createSession(user, pwd);
		}
		finally {
			for(int i = 0; i < pwd.length; ++i)
				pwd[i] = ' ';
			updateMenus(session);
			arrangeTabs();
			setCursor(null);
			validate();
		}
	}

	/** Create a new session */
	protected Session createSession(String user, char[] pwd)
		throws IOException, ConfigurationError, SonarException,
		NoSuchFieldException, IllegalAccessException
	{
		SonarState state = createSonarState();
		state.login(user, new String(pwd));
		state.populateCaches();
		setTitle(getWindowTitle(state.getUser()));
		return new Session(state, desktop, props, logger, baseLayers);
	}

	/** Create a new SONAR state */
	protected SonarState createSonarState() throws IOException,
		ConfigurationError, NoSuchFieldException, IllegalAccessException
	{
		return new SonarState(props, new SimpleHandler());
	}

	/** Update the menus for a session */
	protected void updateMenus(Session s) {
		JMenu vm = view_menu;
		if(vm != null)
			getJMenuBar().remove(vm);
		if(s != null) {
			view_menu = new ViewMenu(s);
			getJMenuBar().add(view_menu, 1);
			help_menu.add(desktop);
		} else {
			view_menu = null;
		}
		session_menu.setLoggedIn(s != null);
	}

	/** Logout of the current session */
	public void logout() {
		new AbstractJob(LOGIN) {
			public void perform() {
				doLogout();
			}
		}.addToScheduler();
	}

	/** Clean up when the user logs out */
	protected void doLogout() {
		updateMenus(null);
		removeTabs();
		closeSession();
		setTitle(getWindowTitle(null));
		validate();
	}

	/** Close the session */
	protected void closeSession() {
		Session s = session;
		if(s != null)
			s.dispose();
		session = null;
	}

	/** Removed all the tabs */
	protected void removeTabs() {
		for(ScreenPane sp: s_panes)
			sp.removeTabs();
	}
}
