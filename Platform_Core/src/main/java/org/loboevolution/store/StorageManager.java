/*
    GNU GENERAL LICENSE
    Copyright (C) 2014 - 2018 Lobo Evolution

    This program is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    verion 3 of the License, or (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    General License for more details.

    You should have received a copy of the GNU General Public
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    

    Contact info: ivan.difrancesco@yahoo.it
 */
/*
 * Created on Mar 12, 2005
 */
package org.loboevolution.store;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.loboevolution.security.GenericLocalPermission;
import org.loboevolution.security.LocalSecurityPolicy;

import com.loboevolution.store.SQLiteCommon;

/**
 * * @author J. H. S.
 */
public class StorageManager implements Runnable {

	/** The Constant logger. */
	private static final Logger logger = LogManager.getLogger(StorageManager.class);

	/** The Constant HOST_STORE_QUOTA. */
	private static final long HOST_STORE_QUOTA = 200 * 1024;

	/** The Constant HOST_STORE_DIR. */
	private static final String HOST_STORE_DIR = "HostStore";

	/** The Constant CACHE_DIR. */
	private static final String CACHE_DIR = "cache";

	/** The Constant CONTENT_DIR. */
	private static final String CONTENT_DIR = "content";

	/** The Constant LOOK_AND_FEEL. */
	private static final String LOOK_AND_FEEL = "CREATE TABLE LOOK_AND_FEEL (acryl integer, aero integer, aluminium integer, bernstein integer, fast integer, graphite integer, "
			+ "hiFi integer,luna integer, mcWin integer, mint integer, noire integer, smart integer, texture integer, "
			+ "bold integer, italic integer, underline integer, strikethrough integer, subscript integer, "
			+ "superscript integer, fontSize text, font text, color text)";
	
	/** The Constant SEARCH. */
	private static final String SEARCH = "CREATE TABLE SEARCH (name text, description text, baseUrl text, queryParameter text)";
	
	/** The Constant SEARCH_SELECTED. */
	private static final String SEARCH_SELECTED = "CREATE TABLE SEARCH_SELECTED (name text, description text, baseUrl text, queryParameter text, selected integer, type text)";

	private static final StorageManager instance = new StorageManager();

	/** The store directory. */
	private final File storeDirectory;

	/** The Constant NO_HOST. */
	private static final String NO_HOST = "$NO_HOST$";

	/** The restricted store cache. */
	private final Map<String, RestrictedStore> restrictedStoreCache = new HashMap<String, RestrictedStore>();

	/** The Constant MANAGED_STORE_UPDATE_DELAY. */
	private static final int MANAGED_STORE_UPDATE_DELAY = 1000 * 60 * 5;

	/** The thread started. */
	private boolean threadStarted = false;

	/**
	 * Gets the Constant instance.
	 *
	 * @return the Constant instance
	 */
	public static StorageManager getInstance() throws IOException {
		return instance;
	}

	/**
	 * Instantiates a new storage manager.
	 */
	private StorageManager() {
		this.storeDirectory = LocalSecurityPolicy.STORE_DIRECTORY;
		if (!this.storeDirectory.exists()) {
			this.storeDirectory.mkdirs();
			File dbDir = new File(storeDirectory, "store");
			dbDir.mkdirs();
		}
	}

	/**
	 * Ensure thread started.
	 */
	private void ensureThreadStarted() {
		if (!this.threadStarted) {
			synchronized (this) {
				if (!this.threadStarted) {
					Thread t = new Thread(this, "StorageManager");
					t.setDaemon(true);
					t.setPriority(Thread.MIN_PRIORITY);
					t.start();
					this.threadStarted = true;
				}
			}
		}
	}

	/**
	 * Gets the app home.
	 *
	 * @return the app home
	 */
	public File getAppHome() {
		return this.storeDirectory;
	}

	/**
	 * Gets the cache host directory.
	 *
	 * @param hostName
	 *            the host name
	 * @return the cache host directory
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public File getCacheHostDirectory(String hostName) throws IOException {
		CacheManager.getInstance();
		File cacheDir = this.getCacheRoot();
		if (hostName == null || "".equals(hostName)) {
			hostName = NO_HOST;
		}
		return new File(cacheDir, hostName);
	}

	/**
	 * Gets the content cache file.
	 *
	 * @param hostName
	 *            the host name
	 * @param fileName
	 *            the file name
	 * @return the content cache file
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public File getContentCacheFile(String hostName, String fileName) throws IOException {
		File domainDir = this.getCacheHostDirectory(hostName);
		File xamjDir = new File(domainDir, CONTENT_DIR);
		return new File(xamjDir, fileName);
	}

	/**
	 * Gets the cache root.
	 *
	 * @return the cache root
	 */
	public File getCacheRoot() {
		return new File(this.storeDirectory, CACHE_DIR);
	}

	/**
	 * Gets the restricted store.
	 *
	 * @param hostName
	 *            the host name
	 * @param createIfNotExists
	 *            the create if not exists
	 * @return the restricted store
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public RestrictedStore getRestrictedStore(String hostName, final boolean createIfNotExists) throws IOException {
		SecurityManager sm = System.getSecurityManager();
		if (sm != null) {
			sm.checkPermission(GenericLocalPermission.EXT_GENERIC);
		}
		if (hostName == null || "".equals(hostName)) {
			hostName = NO_HOST;
		}
		final String normHost = hostName;
		RestrictedStore store;
		synchronized (this) {
			store = this.restrictedStoreCache.get(normHost);
			if (store == null) {
				store = AccessController.doPrivileged((PrivilegedAction<RestrictedStore>) () -> {
					File hostStoreDir = new File(storeDirectory, HOST_STORE_DIR);
					File domainDir = new File(hostStoreDir, normHost);
					if (!createIfNotExists && !domainDir.exists()) {
						return null;
					}
					try {
						return new RestrictedStore(domainDir, HOST_STORE_QUOTA);
					} catch (IOException ioe) {
						throw new IllegalStateException(ioe);
					}
				});
				if (store != null) {
					this.restrictedStoreCache.put(normHost, store);
				}
			}
		}
		if (store != null) {
			this.ensureThreadStarted();
		}
		return store;
	}

	public void saveSettings(String name, ClassLoader classLoader) throws IOException {
		// TODO
	}

	public void saveSettings(String name, Serializable classLoader) throws IOException {
		// TODO
	}

	public Serializable retrieveSettings(String name, ClassLoader classLoader) throws IOException {
		return null;
		// TODO
	}

	public Serializable retrieveSettings(String name, Serializable classLoader) throws IOException {
		return null;
		// TODO
	}

	public void createDatabase() {
		String urlDatabase = SQLiteCommon.getSettingsDirectory();
		File f = new File(SQLiteCommon.getDirectory());
		if (!f.exists()) {
			try (Connection conn = DriverManager.getConnection(urlDatabase); Statement stmt = conn.createStatement()) {
				createTable(urlDatabase, LOOK_AND_FEEL);
				createTable(urlDatabase, SEARCH);
				createTable(urlDatabase, SEARCH_SELECTED);
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	private void createTable(String urlDatabase, String table) {
		try (Connection conn = DriverManager.getConnection(urlDatabase); Statement stmt = conn.createStatement()) {
        	stmt.execute(table);
        } catch (Exception e) {
        	logger.error(e.getMessage());
        }
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				Thread.sleep(MANAGED_STORE_UPDATE_DELAY);
				RestrictedStore[] stores;
				synchronized (this) {
					stores = this.restrictedStoreCache.values().toArray(new RestrictedStore[0]);
				}
				for (RestrictedStore store : stores) {
					Thread.yield();
					store.updateSizeFile();
				}
			} catch (Throwable err) {
				logger.error("run()", err);
				try {
					Thread.sleep(MANAGED_STORE_UPDATE_DELAY);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			}
		}
	}
}
