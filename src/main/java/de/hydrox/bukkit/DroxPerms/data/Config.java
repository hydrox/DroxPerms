package de.hydrox.bukkit.DroxPerms.data;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import de.hydrox.bukkit.DroxPerms.data.flatfile.FlatFilePermissions;

public class Config {
	private String dataProvider = null;
	private Configuration configuration = null;
	private Logger logger;
	
	
	public Config(Plugin plugin) {
		logger = plugin.getServer().getLogger();
		logger.info("[DroxPerms] Setting up configuration");
		configuration = plugin.getConfiguration();
        // Write some default configuration
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
        	plugin.getServer().getLogger().info("[DroxPerms] Generating default configuration");
            configuration.setProperty("DataProvider", FlatFilePermissions.NODE);
            configuration.save();
        }
		dataProvider = configuration.getString("DataProvider");
		logger.info("[DroxPerms] Using DataProvider: " + dataProvider);
		
		// TODO Needs config-file reader
	}
	
	public String getDataProvider() {
		return dataProvider;
	}
}
