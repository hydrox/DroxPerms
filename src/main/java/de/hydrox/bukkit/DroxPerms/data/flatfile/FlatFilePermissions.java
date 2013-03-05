package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.data.AGroup;
import de.hydrox.bukkit.DroxPerms.data.APermissions;
import de.hydrox.bukkit.DroxPerms.data.AUser;
import de.hydrox.bukkit.DroxPerms.data.TransactionLogger;

/**
 * 
 * @author Matthias Söhnholz
 *
 */
public class FlatFilePermissions extends APermissions{

	public static final String NODE = "FlatFile";

	private YamlConfiguration groupsConfig;
	private YamlConfiguration usersConfig;
	private YamlConfiguration tracksConfig;

	//Tehbeard start
	private Logger logger = Logger.getLogger("DroxPerms");
	//Tehbeard End

	public FlatFilePermissions() {
		groupsConfig = YamlConfiguration.loadConfiguration(new File("groups.yml"));
		usersConfig = YamlConfiguration.loadConfiguration(new File("users.yml"));
		tracksConfig = YamlConfiguration.loadConfiguration(new File("tracks.yml"));
	}

	public FlatFilePermissions(DroxPerms plugin)  {
		FlatFilePermissions.plugin = plugin;
		// Write some default configuration

		//Tehbeard Start
		//Add transaction logger
		File f = new File(plugin.getDataFolder(),"transaction.log");
		try {
			Handler handler = new TransactionLogger(f);
			handler.setLevel(Level.INFO);
			logger.addHandler(handler);
			logger.setLevel(Level.INFO);
			logger.setUseParentHandlers(false);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Tehbeard End

		groupsConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "groups.yml"));
		YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(plugin.getResource("groups.yml"));
		groupsConfig.setDefaults(defConfig);
		usersConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "users.yml"));
		defConfig = YamlConfiguration.loadConfiguration(plugin.getResource("users.yml"));
		usersConfig.setDefaults(defConfig);
		tracksConfig = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "tracks.yml"));

		Set<String> groups = groupsConfig.getConfigurationSection("groups.").getKeys(false);
		for (String group : groups) {
			plugin.getServer().getLogger().fine("load group: " + group);
			ConfigurationSection conf = groupsConfig.getConfigurationSection("groups." + group);
			Group newGroup = new Group(group, conf);
			Group.addGroup(newGroup);
		}

		ConfigurationSection tracksSection = tracksConfig.getConfigurationSection("tracks.");
		if (tracksSection != null) {
			Set<String> tracks = tracksConfig.getConfigurationSection("tracks.").getKeys(false);
			for (String track : tracks) {
				plugin.getServer().getLogger().fine("load track: " + track);
				ConfigurationSection conf = tracksConfig.getConfigurationSection("tracks." + track);
				Track newTrack = new Track(track, conf);
				Track.addTrack(newTrack);
			}
		}
	}

	public void save() {
		HashMap<String,Object> tmp = new HashMap<String,Object>();
		Iterator<AGroup> iter = AGroup.iter();
		while (iter.hasNext()) {
			Group group = (Group) iter.next();
			tmp.put(group.getName().toLowerCase(), group.toConfigurationNode());
			groupsConfig.createSection("groups." + group.getName(), group.toConfigurationNode());
		}

		try {
			groupsConfig.save(new File(plugin.getDataFolder(),"groups.yml"));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to groups.yml", e);
		}

		tmp = new HashMap<String,Object>();
		Iterator<AUser> iter2 = AUser.iter();
		while (iter2.hasNext()) {
			User user = (User) iter2.next();
			if (user.isDirty()) {
				usersConfig.createSection("users." + user.getName(), user.toConfigurationNode());
				user.clean();
			}
		}

		try {
			usersConfig.save(new File(plugin.getDataFolder(), "users.yml"));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to users.yml", e);
		}
	}

	public boolean createPlayer(String name) {
		if (User.existUser(name)) {
			return false;
		} else {
			User.addUser(new User(name));
			return true;
		}
	}

	public boolean deletePlayer(CommandSender sender, String name) {
		Player player = plugin.getServer().getPlayerExact(name);
		if (player != null) {
			sender.sendMessage(ChatColor.RED + "Can't delete online Player.");
			return false;
		}
		User user = (User) getExactUser(name);
		if (user != null) {
			User.removeUser(name);
			usersConfig.set("users." + name, null);
			sender.sendMessage(ChatColor.GREEN + "Deleted Player " + name + ".");
			return true;
		}
		sender.sendMessage(ChatColor.RED + "No Player with this exact name found.");

		return false;
	}

	public boolean createGroup(CommandSender sender, String name) {
		if (Group.existGroup(name)) {
			return false;
		} else {
			Group.addGroup(new Group(name));
			return true;
		}
	}

	protected AUser getExactUser(String name) {
		AUser user = null;
		if (User.existUser(name)) {
			user = User.getUser(name);
			return user;
		} else {
			ConfigurationSection node = usersConfig.getConfigurationSection("users." + name);
			if (node != null) {
				user = new User(name, node);
				User.addUser(user);
				return user;
			}
		}
		return null;
	}

	protected AUser getPartialUser(String name) {
		boolean unsureOnline = false;
		AUser user = null;
		String onlineplayer = null;
		Player player = plugin.getServer().getPlayer(name);
		if (player != null) {
			onlineplayer = player.getName();
		}
		if (onlineplayer != null && User.existUser(onlineplayer)) {
			user = User.getUser(onlineplayer);
		} else if (User.existUser(name)) {
			user = User.getUser(name);
		} else {
			Iterator<AUser> iter = User.iter();
			while (iter.hasNext()) {
				AUser user2 = iter.next();
				if (user2.getName().toLowerCase().contains(name.toLowerCase())) {
					if (user == null) {
						user = user2;
					} else {
						unsureOnline = true;
					}
				}
			}
			if (user == null || unsureOnline) {
				ConfigurationSection node = usersConfig.getConfigurationSection("users." + name);
				if (node != null) {
					user = new User(name, node);
					User.addUser(user);
				} else {
					Set<String> users = usersConfig.getConfigurationSection("users").getKeys(false);
					for (String potentialUser : users) {
						if(potentialUser.toLowerCase().contains(name.toLowerCase())) {
							if (user == null) {
								node = usersConfig.getConfigurationSection("users." + potentialUser);
								user = new User(potentialUser, node);
								User.addUser(user);
							} else {
								return null;
							}
						}
					}
				}
			}
		}
		if (user == null) {
			return null;
		}
		return user;
	}

	@Override
	public Set<String> getAllUserNames() {
		return usersConfig.getConfigurationSection("users.").getKeys(false);
	}
}
