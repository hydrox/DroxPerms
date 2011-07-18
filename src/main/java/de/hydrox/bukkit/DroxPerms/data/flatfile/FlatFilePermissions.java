package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;

import de.hydrox.bukkit.DroxPerms.data.IDataProvider;

/**
 * 
 * @author Matthias Söhnholz
 *
 */
public class FlatFilePermissions implements IDataProvider {
	
	private Configuration groups;
	private Configuration users;
	
	public FlatFilePermissions(Plugin plugin) {
        // Write some default configuration
		
		groups = new Configuration(new File(plugin.getDataFolder(), "groups.yml"));
		users = new Configuration(new File(plugin.getDataFolder(), "users.yml"));
        if (!new File(plugin.getDataFolder(), "groups.yml").exists()) {
        	plugin.getServer().getLogger().info("[DroxPerms] Generating default groups.yml");
        	groups.setProperty("Groups", new Group().toConfigurationNode());
        	groups.save();
        }
        if (!new File(plugin.getDataFolder(), "users.yml").exists()) {
        	plugin.getServer().getLogger().info("[DroxPerms] Generating default users.yml");
        	HashMap<String,Object> tmp = new HashMap<String,Object>();
        	tmp.put("mydrox", new User().toConfigurationNode());
        	tmp.put("tehbeard", new User().toConfigurationNode());
        
        	users.setProperty("Users", tmp);
        	users.save();
        }
    }
	
	public static final String NODE = "FlatFile";

	public String getGroup(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setGroup(String player, String group) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getSubgroups(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void addSubgroup(String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void removeSubgroup(String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void addPlayerPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void removePlayerPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void addGroupPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void removeGroupPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getPlayerPermissions(String player, String world) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
