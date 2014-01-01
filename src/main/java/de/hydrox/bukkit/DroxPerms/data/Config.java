package de.hydrox.bukkit.DroxPerms.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

public class Config {
	private static String dataProvider = null;
	private static String defaultWorld = null;
	private static int saveInterval;
	private static Map<String, List<String>> worldMirrors= null;
	
	private static ConfigurationSection mysql = null;
	
	public Config(Plugin plugin) {
		Logger logger = plugin.getServer().getLogger();
		logger.info("[DroxPerms] Setting up configuration");
		FileConfiguration configuration = plugin.getConfig();
        logger.info("[DroxPerms] loading configuration");

		dataProvider = configuration.getString("DataProvider");
		logger.info("[DroxPerms] Using DataProvider: " + dataProvider);
		defaultWorld = configuration.getString("DefaultWorld");
		logger.info("[DroxPerms] Setting DefaultWorld: " + defaultWorld);
		saveInterval = configuration.getInt("SaveInterval", 5);
		logger.info("[DroxPerms] Setting SaveInterval: " + saveInterval + " minutes");
		logger.info("[DroxPerms] Loading World-Mirrors");
		worldMirrors = new HashMap<String, List<String>>();
		ConfigurationSection mirrorConf = configuration.getConfigurationSection("Mirrors.");
		if (mirrorConf != null) {
			Set<String> worlds = mirrorConf.getKeys(false);
			for (String world : worlds) {
				List<String> worldList = configuration.getStringList("Mirrors." + world);
				worldMirrors.put(world, worldList);
				logger.fine("mirrors for world "+world+": " + worldMirrors.get(world).size());
			}
		}

		mysql = configuration.getConfigurationSection("MySQL");
	}
	
	public static String getDataProvider() {
		return dataProvider;
	}

	public static String getDefaultWorld() {
		return defaultWorld;
	}

	public static Set<String> getWorlds() {
		return worldMirrors.keySet();
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

	public static ConfigurationSection getMySQLConfig() {
		return mysql;
	}
}
