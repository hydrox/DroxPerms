package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import de.hydrox.bukkit.DroxPerms.data.IDataProvider;

/**
 * 
 * @author Matthias Söhnholz
 *
 */
public class FlatFilePermissions implements IDataProvider {

	public static final String NODE = "FlatFile";

	private Configuration groupsConfig;
	private Configuration usersConfig;
	

	public FlatFilePermissions() {
		groupsConfig = new Configuration(new File("groupsConfig.yml"));
		usersConfig = new Configuration(new File("users.yml"));
	}

	public FlatFilePermissions(Plugin plugin) {
        // Write some default configuration
		
		groupsConfig = new Configuration(new File(plugin.getDataFolder(), "groups.yml"));
		usersConfig = new Configuration(new File(plugin.getDataFolder(), "users.yml"));
		if (!new File(plugin.getDataFolder(), "groups.yml").exists()) {
			plugin.getServer().getLogger().info("[DroxPerms] Generating default groups.yml");
			HashMap<String,Object> tmp = new HashMap<String,Object>();
			tmp.put("default", new Group("default").toConfigurationNode());

			groupsConfig.setProperty("groups", tmp);
			groupsConfig.save();
		}
		groupsConfig.load();
		System.out.println(groupsConfig.getKeys().toString());
		Map<String, ConfigurationNode> groups = groupsConfig.getNodes("groups");
		Iterator<String> iter = groups.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			plugin.getServer().getLogger().fine("load group: " + key);
			ConfigurationNode conf = groups.get(key);
			Group newGroup = new Group(key, conf);
			Group.addGroup(newGroup);
		}

		if (!new File(plugin.getDataFolder(), "users.yml").exists()) {
			plugin.getServer().getLogger().info("[DroxPerms] Generating default users.yml");
			HashMap<String,Object> tmp = new HashMap<String,Object>();
			tmp.put("mydrox", new User().toConfigurationNode());
			tmp.put("tehbeard", new User().toConfigurationNode());

			usersConfig.setProperty("users", tmp);
			usersConfig.save();
		}
    }

	public void save() {
		HashMap<String,Object> tmp = new HashMap<String,Object>();
		Iterator<Group> iter = Group.iter();
		while (iter.hasNext()) {
			Group group = iter.next();
			tmp.put(group.getName().toLowerCase(), group.toConfigurationNode());
		}

		groupsConfig.setProperty("groups", tmp);
		groupsConfig.save();
	}

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

	public boolean addGroupPermission(String group, String world, String node) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removeGroupPermission(String group, String world, String node) {
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

	public String[] getGroupPermissions(String group, String world) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
