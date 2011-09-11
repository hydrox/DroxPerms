package de.hydrox.bukkit.DroxPerms.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import de.hydrox.bukkit.DroxPerms.data.flatfile.FlatFilePermissions;

public class Config {
	private static String dataProvider = null;
	private static String defaultWorld = null;
	private static int saveInterval;
	private static HashMap<String, ArrayList<String>> worldMirrors= null;
	private Configuration configuration = null;
	private Logger logger;
	
	
	public Config(Plugin plugin) {
		logger = plugin.getServer().getLogger();
		logger.info("[DroxPerms] Setting up configuration");
		configuration = plugin.getConfiguration();
        // Write some default configuration
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
        	plugin.getServer().getLogger().info("[DroxPerms] Generating default configuration");
            HashMap<String, ArrayList<String>> mirrors = new HashMap<String, ArrayList<String>>();
            ArrayList<String> defaultMirror = new ArrayList<String>();
            defaultMirror.add("nether");
            mirrors.put("world", defaultMirror);
            mirrors.put("hub", null);
            configuration.setProperty("DataProvider", FlatFilePermissions.NODE);
            configuration.setProperty("DefaultWorld", "world");
            configuration.setProperty("SaveInterval", 5);
            configuration.setProperty("Mirrors", mirrors);
            configuration.save();
        }
		logger.info("[DroxPerms] loading configuration");
		configuration.load();

		dataProvider = configuration.getString("DataProvider");
		logger.info("[DroxPerms] Using DataProvider: " + dataProvider);
		defaultWorld = configuration.getString("DefaultWorld");
		logger.info("[DroxPerms] Setting DefaultWorld: " + defaultWorld);
		saveInterval = configuration.getInt("SaveInterval", 5);
		logger.info("[DroxPerms] Setting SaveInterval: " + saveInterval + " minutes");
		logger.info("[DroxPerms] Loading World-Mirrors");
		worldMirrors = new HashMap<String, ArrayList<String>>();
		ConfigurationNode tmp = configuration.getNode("Mirrors");
		Iterator<String> iter = tmp.getKeys().iterator();
		while (iter.hasNext()) {
			String world = iter.next();
			worldMirrors.put(world, (ArrayList<String>) tmp.getStringList(world, new ArrayList<String>()));
			logger.fine("mirrors for world "+world+": " + worldMirrors.get(world).size());
		}
		
		// TODO Needs config-file reader
	}
	
	public static String getDataProvider() {
		return dataProvider;
	}

	public static String getDefaultWorld() {
		return defaultWorld;
	}

	public static String getRealWorld(String world) {
		if (worldMirrors.containsKey(world)) {
			return world;
		}
		for (String key : worldMirrors.keySet()) {
			Iterator<String> iter = worldMirrors.get(key).iterator();
			while (iter.hasNext()) {
				String string = (String) iter.next();
				if (string.equals(world)) {
					return key;
				}
			}
		}
		return defaultWorld;
	}

	public static int getSaveInterval() {
		return saveInterval;
	}
}
