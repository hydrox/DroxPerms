package de.hydrox.bukkit.DroxPerms;

import org.bukkit.plugin.java.JavaPlugin;

import de.hydrox.bukkit.DroxPerms.data.Config;
import de.hydrox.bukkit.DroxPerms.data.IDataProvider;
import de.hydrox.bukkit.DroxPerms.data.flatfile.FlatFilePermissions;

/**
 * Base Class of DroxPerms
 * @author Matthias Söhnholz
 */
public class DroxPerms extends JavaPlugin
{
	protected Config config;
	protected IDataProvider dataProvider;

	public void onDisable() {
		// TODO Auto-generated method stub
		
	}

	public void onEnable() {
		config = new Config(this);
		if(config.getDataProvider().equals(FlatFilePermissions.NODE)) {
			dataProvider = new FlatFilePermissions(this);
		}
	}

}
