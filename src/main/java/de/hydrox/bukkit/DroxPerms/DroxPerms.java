package de.hydrox.bukkit.DroxPerms;

import org.bukkit.plugin.java.JavaPlugin;

import de.hydrox.bukkit.DroxPerms.data.Configuration;
import de.hydrox.bukkit.DroxPerms.data.FlatFilePermissions;
import de.hydrox.bukkit.DroxPerms.data.IDataProvider;

/**
 * Base Class of DroxPerms
 * @author Matthias Söhnholz
 */
public class DroxPerms extends JavaPlugin
{
	protected Configuration config;
	protected IDataProvider dataProvider;

	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public void onEnable() {
		config = new Configuration();
		if(config.getDataProvider().equals(FlatFilePermissions.NODE)) {
			dataProvider = new FlatFilePermissions();
		}
	}

}
