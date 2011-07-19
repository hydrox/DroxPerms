package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.io.File;
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
	
	private Configuration groupsConfig;
	private Configuration usersConfig;
	
	public FlatFilePermissions(Plugin plugin) {
        // Write some default configuration
		
		groupsConfig = new Configuration(new File(plugin.getDataFolder(), "groups.yml"));
		usersConfig = new Configuration(new File(plugin.getDataFolder(), "users.yml"));
		if (!new File(plugin.getDataFolder(), "groups.yml").exists()) {
			plugin.getServer().getLogger().info("[DroxPerms] Generating default groups.yml");
			HashMap<String,Object> tmp = new HashMap<String,Object>();
			tmp.put("default", new Group("default").toConfigurationNode());

			groupsConfig.setProperty("Groups", tmp);
			groupsConfig.save();
		}
		if (!new File(plugin.getDataFolder(), "users.yml").exists()) {
			plugin.getServer().getLogger().info("[DroxPerms] Generating default users.yml");
			HashMap<String,Object> tmp = new HashMap<String,Object>();
			tmp.put("mydrox", new User().toConfigurationNode());
			tmp.put("tehbeard", new User().toConfigurationNode());

			usersConfig.setProperty("Users", tmp);
			usersConfig.save();
		}
    }
	
	public static final String NODE = "FlatFile";

	public String getGroup(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setPlayerGroup(String player, String group) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getPlayerSubgroups(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void addPlayerSubgroup(String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void removePlayerSubgroup(String player, String subgroup) {
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

	public String getPlayerGroup(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void setGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getGroupSubgroups(String group) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void addGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public void removeGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
