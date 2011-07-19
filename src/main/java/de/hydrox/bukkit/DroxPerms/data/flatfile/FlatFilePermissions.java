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
	
	public boolean createPlayer(String name) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean createGroup(String name) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String getPlayerGroup(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean setPlayerGroup(String player, String group) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getPlayerSubgroups(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addPlayerSubgroup(String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removePlayerSubgroup(String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addPlayerPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removePlayerPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getPlayerPermissions(String player, String world) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addGroupPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removeGroupPermission(String player, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean setGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getGroupSubgroups(String group) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removeGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getGroupPermissions(String player, String world) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
