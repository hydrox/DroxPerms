package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import de.hydrox.bukkit.DroxPerms.data.Config;
import de.hydrox.bukkit.DroxPerms.data.IDataProvider;

/**
 * 
 * @author Matthias Söhnholz
 *
 */
public class FlatFilePermissions implements IDataProvider {

	public static final String NODE = "FlatFile";
	protected static Plugin plugin = null;

	private Configuration groupsConfig;
	private Configuration usersConfig;
	

	public FlatFilePermissions() {
		groupsConfig = new Configuration(new File("groupsConfig.yml"));
		usersConfig = new Configuration(new File("users.yml"));
	}

	public FlatFilePermissions(Plugin plugin) {
		FlatFilePermissions.plugin = plugin;
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
//		System.out.println(groupsConfig.getKeys().toString());
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

		usersConfig.load();
//		System.out.println(usersConfig.getKeys().toString());
		Map<String, ConfigurationNode> users = usersConfig.getNodes("users");
		iter = users.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			plugin.getServer().getLogger().fine("load user: " + key);
			ConfigurationNode conf = users.get(key);
			User newUser = new User(key, conf);
			User.addUser(newUser);
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

		tmp = new HashMap<String,Object>();
		Iterator<User> iter2 = User.iter(); 
		while (iter2.hasNext()) {
			User user = iter2.next();
			tmp.put(user.getName().toLowerCase(), user.toConfigurationNode());
		}

		usersConfig.setProperty("users", tmp);
		usersConfig.save();
	}

	public boolean createPlayer(String name) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean createGroup(CommandSender sender, String name) {
		if (Group.existGroup(name)) {
			return false;
		} else {
			Group.addGroup(new Group(name));
			return true;
		}
	}

	public String getPlayerGroup(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean setPlayerGroup(CommandSender sender, String player, String group) {
		if (User.existUser(player)) {
			boolean result = User.getUser(player).setGroup(group);
			if (result) {
				sender.sendMessage("Set group of player " + player + " to " + group);
				return true;
			} else {
				sender.sendMessage("Couldn't set group of player " + player);
				return false;
			}
		} else {
			return false;
		}
	}

	public String[] getPlayerSubgroups(String player) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addPlayerSubgroup(CommandSender sender, String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removePlayerSubgroup(CommandSender sender, String player, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addPlayerPermission(CommandSender sender, String player, String world, String node) {
		if (User.existUser(player)) {
			boolean result = User.getUser(player).addPermission(world, node);
			if (result) {
				sender.sendMessage("Added " + node + " to permissionslist of player " + player);
				return true;
			} else {
				sender.sendMessage("Couldn't add permission to player " + player);
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean removePlayerPermission(CommandSender sender, String player, String world, String node) {
		if (User.existUser(player)) {
			boolean result = User.getUser(player).removePermission(world, node);
			if (result) {
				sender.sendMessage("removed " + node + " from permissionslist of player " + player);
				return true;
			} else {
				sender.sendMessage("Couldn't remove permission from player " + player);
				return false;
			}
		} else {
			return false;
		}
	}

	public String[] getPlayerPermissions(String player, String world) {
		if (!User.existUser(player)) {
			plugin.getServer().getLogger().info("[DroxPerms] User " + player + " doesn't exist yet. Creating ...");
			User.addUser(new User(player));
			return User.getUser(player).getPermissions(Config.getRealWorld(world));
		}
		return User.getUser(player).getPermissions(Config.getRealWorld(world));
	}

	public boolean addGroupPermission(CommandSender sender, String group, String world, String node) {
		if (Group.existGroup(group)) {
			boolean result = Group.getGroup(group).addPermission(world, node);
			if (result) {
				sender.sendMessage("Added " + node + " to permissionslist of group " + group);
				return true;
			} else {
				sender.sendMessage("Couldn't add permission to group " + group);
				return false;
			}
		} else {
			sender.sendMessage("Group " + group + " doesn't exist.");
			return false;
		}
	}

	public boolean removeGroupPermission(CommandSender sender, String group, String world, String node) {
		if (Group.existGroup(group)) {
			boolean result = Group.getGroup(group).removePermission(world, node);
			if (result) {
				sender.sendMessage("removed " + node + " from permissionslist of group " + group);
				return true;
			} else {
				sender.sendMessage("Couldn't remove permission from group " + group);
				return false;
			}
		} else {
			sender.sendMessage("Group " + group + " doesn't exist.");
			return false;
		}
	}

	public boolean setGroupSubgroup(String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getGroupSubgroups(String group) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean addGroupSubgroup(CommandSender sender, String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public boolean removeGroupSubgroup(CommandSender sender, String group, String subgroup) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public String[] getGroupPermissions(String group, String world) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
}
