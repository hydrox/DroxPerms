package de.hydrox.bukkit.DroxPerms.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import de.hydrox.bukkit.DroxPerms.data.flatfile.FlatFilePermissions;

public class Config {
	private static String dataProvider = null;
	private static String defaultWorld = null;
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
            configuration.setProperty("DataProvider", FlatFilePermissions.NODE);
            configuration.setProperty("DefaultWorld", "world");
            configuration.setProperty("Mirrors", mirrors);
            configuration.save();
        }
		dataProvider = configuration.getString("DataProvider");
		defaultWorld = configuration.getString("DefaultWorld");
		logger.info("[DroxPerms] Using DataProvider: " + dataProvider);
		
		// TODO Needs config-file reader
	}
	
	public static String getDataProvider() {
		return dataProvider;
	}

	public static String getDefaultWorld() {
		return defaultWorld;
	}
}
