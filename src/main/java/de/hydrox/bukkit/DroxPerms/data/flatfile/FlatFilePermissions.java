package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.data.AGroup;
import de.hydrox.bukkit.DroxPerms.data.APermissions;
import de.hydrox.bukkit.DroxPerms.data.ATrack;
import de.hydrox.bukkit.DroxPerms.data.AUser;
import de.hydrox.bukkit.DroxPerms.utils.uuid.UUIDFetcher;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.configuration.file.FileConfiguration;

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

	public FlatFilePermissions() {
		groupsConfig = YamlConfiguration.loadConfiguration(new File("groups.yml"));
		usersConfig = YamlConfiguration.loadConfiguration(new File("users.yml"));
		tracksConfig = YamlConfiguration.loadConfiguration(new File("tracks.yml"));
	}

	public FlatFilePermissions(DroxPerms plugin)  {
		FlatFilePermissions.plugin = plugin;
		// Write some default configuration

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

	@Override
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
				usersConfig.createSection("users." + user.getUUID().toString(), user.toConfigurationNode());
				user.clean();
			}
		}

		try {
			usersConfig.save(new File(plugin.getDataFolder(), "users.yml"));
		} catch (IOException e) {
			plugin.getLogger().log(Level.SEVERE, "Could not save config to users.yml", e);
		}
	}

	@Override
	public boolean createPlayer(UUID uuid) {
		if (User.existUser(uuid)) {
			return false;
		} else {
			User.addUser(new User(uuid));
			return true;
		}
	}

	@Override
	public boolean deletePlayer(CommandSender sender, String name) {
		AUser user = getExactUserByName(name);
		if (user != null) {
			return deletePlayer(sender, user);
		}
		return false;
	}
	@Override
	public boolean deletePlayer(CommandSender sender, AUser user) {
		boolean num = false;
		Player player = plugin.getServer().getPlayer(user.getUUID());
		if (player != null) {
			sender.sendMessage(ChatColor.RED + "Can't delete online Player.");
			return false;
		}
		
		if (user != null) {
		        num = User.removeUser(user.getUUID());
		}
		
		if (num) {
		        sender.sendMessage(ChatColor.GREEN + "Deleted Player " + user.getName() + ".");
			return true;
		} else {
		        sender.sendMessage(ChatColor.RED + "No Player with this exact name found.");
		        return false;
		}
	}

	@Override
	public boolean createGroup(CommandSender sender, String name) {
		if (Group.existGroup(name)) {
			return false;
		} else {
			Group.addGroup(new Group(name));
			return true;
		}
	}

	@Override
	public AUser getUserByUUID(UUID uuid) {
		AUser user = null;
		if (User.existUser(uuid)) {
			user = User.getUser(uuid);
			return user;
		} else {
			ConfigurationSection node = null;
			node = usersConfig.getConfigurationSection("users." + uuid.toString());
			if (node != null) {
				user = new User(uuid, node);
				User.addUser(user);
				return user;
			}
		}
		return null;
	}
	
	@Override
	public AUser getExactUserByName(String name) {
		UUID uuid;
		try {
			uuid = UUIDFetcher.getUUIDOf(name);
		} catch (Exception ex) {
			plugin.getLogger().info("Could not retrieve UUID for player: " + name + "!");
			plugin.getLogger().info("Stacktrace: ");
			ex.printStackTrace();
			uuid = null;
		}
		return getUserByUUID(uuid);
	}
	
	@Override
	public AUser getPartialUserByName(String name) {
		Player[] players = plugin.getServer().getOnlinePlayers();
		UUID uuid;
		
		for (int i = 0; i < players.length; i++) {
			if (players[i].getName().contains(name)) {
				name = players[i].getName();
				break;
			}
		}
		
		try {
			uuid = UUIDFetcher.getUUIDOf(name);
		} catch (Exception ex) {
			plugin.getLogger().info("Could not retrieve UUID for player: " + name + "!");
			plugin.getLogger().info("Stacktrace: ");
			ex.printStackTrace();
			uuid = null;
		}
		return getUserByUUID(uuid);
	}

	@Override
	public Set<String> getAllUserNames() {
		return usersConfig.getConfigurationSection("users.").getKeys(false);
	}

	@Override
	public ATrack getTrack(String track) {
		return Track.getTrack(track);
	}

	@Override
	public boolean migrateToNewerVersion() {
		FileConfiguration configuration = plugin.getConfig();
		String dbVersion = configuration.getString("DatabaseVersion", "0.5.1-flatfile");
		
		if ("0.5.1-flatfile".equals(dbVersion)) {
			plugin.getLogger().info("detected Storage version 0.5.1, update to 1.0.0");
			
			Set<String> userSet = usersConfig.getConfigurationSection("users.").getKeys(false);
			List users = new ArrayList();
			users.addAll(userSet);
			
			UUIDFetcher fetcher = new UUIDFetcher(users);
			Map<String, UUID> response = null;
			try {
				response = fetcher.call();
			} catch(Exception ex) {
				plugin.getLogger().warning("Could not retreve UUIDS! Abandoning migration.");
				return false;
			}
			
			if (response == null) {
				plugin.getLogger().warning("No users found. Abandoning migration.");
				return false;
			} else {
				Iterator<String> iterator = userSet.iterator();
				while (iterator.hasNext()) {
					String player = iterator.next();
					if (response.containsKey(player)) {
						String newKey = response.get(player).toString();
						usersConfig.getConfigurationSection("users.").set(player, null);
						usersConfig.getConfigurationSection("users.").set(player, newKey);
					}
				}
			}

			dbVersion = "1.0.0-flatfile";
			plugin.getLogger().info("updated Storage to 1.0.0-flatfile");
			configuration.set("DatabaseVersion", dbVersion);
			try {
				configuration.save(new File(plugin.getDataFolder() + File.separator + "config.yml"));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		plugin.getLogger().info("Storage Migration complete");
		return true;
	}
}
