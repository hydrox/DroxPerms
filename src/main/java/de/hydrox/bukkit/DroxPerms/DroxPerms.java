package de.hydrox.bukkit.DroxPerms;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import de.hydrox.bukkit.DroxPerms.data.Config;
import de.hydrox.bukkit.DroxPerms.data.IDataProvider;
import de.hydrox.bukkit.DroxPerms.data.flatfile.FlatFilePermissions;
import de.hydrox.bukkit.DroxPerms.data.sql.SQLPermissions;

/**
 * Base Class of DroxPerms
 * 
 * @author Matthias Söhnholz
 */
public class DroxPerms extends JavaPlugin {
	protected IDataProvider dataProvider;

	private DroxPlayerListener playerListener = new DroxPlayerListener(this);
	private DroxGroupCommands groupCommandExecutor = new DroxGroupCommands(this);
	private DroxPlayerCommands playerCommandExecutor = new DroxPlayerCommands(this);
	private DroxTestCommands testCommandExecutor = new DroxTestCommands();
	private DroxStatsCommands statsCommandExecutor = new DroxStatsCommands(this);
	private Map<Player, Map<String, PermissionAttachment>> permissions = new HashMap<Player, Map<String, PermissionAttachment>>();
	private DroxPermsAPI API = null;

	private Runnable commiter;
	private BukkitTask task;
	public Logger logger = Logger.getLogger("Minecraft");

	public void onDisable() {
		long time = System.currentTimeMillis();
		logger.info("[DroxPerms] shutting down");
		// Unregister everyone
		logger.info("[DroxPerms] unregister Players");
		for (Player p : getServer().getOnlinePlayers()) {
			unregisterPlayer(p);
		}
		disableScheduler();

		// Safe data
		logger.info("[DroxPerms] save configs");
		dataProvider.save();
		logger.info("[DroxPerms] Plugin unloaded in " + (System.currentTimeMillis() - time) + "ms.");
	}

	public void onEnable() {
		long time = System.currentTimeMillis();
		logger.info("[DroxPerms] Activating Plugin.");
		getConfig().options().copyDefaults(true);
		saveConfig();
		new Config(this);
		logger.info("[DroxPerms] Loading DataProvider");
		if (Config.getDataProvider().equalsIgnoreCase(FlatFilePermissions.NODE)) {
			dataProvider = new FlatFilePermissions(this);
		} else if (Config.getDataProvider().equalsIgnoreCase(SQLPermissions.NODE)) {
			try {
				dataProvider = new SQLPermissions(Config.getMySQLConfig(), this);
			} catch (SQLException e) {
				SQLPermissions.mysqlError(e);
			}
		} else {
			logger.warning("No DataProvider named \""+Config.getDataProvider()+ "\" available. Falling back to " + FlatFilePermissions.NODE);
			dataProvider = new FlatFilePermissions(this);
		}

		API = new DroxPermsAPI(this);

		// Commands
		logger.info("[DroxPerms] Setting CommandExecutors");
		getCommand("changegroup").setExecutor(groupCommandExecutor);
		getCommand("changeplayer").setExecutor(playerCommandExecutor);
		getCommand("testdroxperms").setExecutor(testCommandExecutor);
		getCommand("droxstats").setExecutor(statsCommandExecutor);

		// Events
		logger.info("[DroxPerms] Registering Events");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(playerListener, this);

		// Register everyone online right now
		logger.info("[DroxPerms] Register online players");
		for (Player p : getServer().getOnlinePlayers()) {
			registerPlayer(p);
		}

		enableScheduler();

		logger.info("[DroxPerms] Plugin activated in " + (System.currentTimeMillis() - time) + "ms.");
	}

	public DroxPermsAPI getAPI() {
		return API;
	}

	private String getPrefix(String player) {
		String prefix = API.getPlayerInfo(player, "display_prefix");
		if (prefix == null) {
			String group = API.getPlayerGroup(player);
			prefix = API.getGroupInfo(group, "display_prefix");
		}
		if (prefix != null) {
			return prefix.replace("&", "\247");
		}
		return "";
	}

	protected void registerPlayer(Player player) {
		permissions.remove(player);
		registerPlayer(player, player.getWorld());
		String displayName = getPrefix(player.getName()) + player.getDisplayName();
		if (displayName.length()>16) {
			displayName = displayName.substring(0, 16);
		}
		player.setPlayerListName(displayName);
	}

	protected void registerPlayer(Player player, World world) {
		Map<String, PermissionAttachment> attachments = new LinkedHashMap<String, PermissionAttachment>();

		PermissionAttachment attachment = player.addAttachment(this);
		attachments.put("subgroups", attachment);
		attachment = player.addAttachment(this);
		attachments.put("group", attachment);
		attachment = player.addAttachment(this);
		attachments.put("global", attachment);
		attachment = player.addAttachment(this);
		attachments.put("world", attachment);

		permissions.put(player, attachments);
		calculateAttachment(player, world);
	}

	protected void unregisterPlayer(Player player) {
		Map<String, PermissionAttachment> attachments = permissions.get(player);
		if (attachments != null) {
			for (PermissionAttachment attachment : attachments.values()) {
				player.removeAttachment(attachment);
			}
		}
		permissions.remove(player);
	}

	protected void refreshPermissions() {
		for (Player player : permissions.keySet()) {
			refreshPlayer(player);
		}
	}

	protected void refreshPlayer(Player player) {
		if (player == null) {
			return;
		}
		refreshPlayer(player, player.getWorld());
	}

	protected void refreshPlayer(Player player, World world) {
		if (player == null) {
			return;
		}
		Map<String, PermissionAttachment> attachments = permissions.get(player);
		if (attachments == null) {
			getLogger().severe("Attachments for Player " + player.getName() + " are null. THIS SHOULD NOT BE. Returning without updating Permissions.");
			return;
		}
		for (PermissionAttachment attachment : attachments.values()) {
			for (String key : attachment.getPermissions().keySet()) {
				attachment.unsetPermission(key);
			}
		}
		calculateAttachment(player, world);
	}

	private void calculateAttachment(Player player, World world) {
		Map<String, PermissionAttachment> attachments = permissions
				.get(player);

		PermissionAttachment attachment = attachments.get("group");
		Map<String, Map<String, Boolean>> playerPermissions = dataProvider
				.getPlayerPermissions(player.getName(), world.getName(), false);
		Map<String, Boolean> perms = playerPermissions.get("group");
		Map<String, Boolean> negPerms = new HashMap<String, Boolean>();
		if (perms != null) {
			for (String entry : perms.keySet()) {
				if(!perms.get(entry)) {
					negPerms.put(entry, perms.get(entry));
					continue;
				}
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
			for (String entry : negPerms.keySet()) {
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
		}
		attachment = attachments.get("subgroups");
		perms = playerPermissions.get("subgroups");
		negPerms.clear();
		if (perms != null) {
			for (String entry : perms.keySet()) {
				if(!perms.get(entry)) {
					negPerms.put(entry, perms.get(entry));
					continue;
				}
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
			for (String entry : negPerms.keySet()) {
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
		}
		attachment = attachments.get("global");
		perms = playerPermissions.get("global");
		negPerms.clear();
		if (perms != null) {
			for (String entry : perms.keySet()) {
				if(!perms.get(entry)) {
					negPerms.put(entry, perms.get(entry));
					continue;
				}
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
			for (String entry : negPerms.keySet()) {
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
		}

		attachment = attachments.get("world");
		perms = playerPermissions.get("world");
		negPerms.clear();
		if (perms != null) {
			for (String entry : perms.keySet()) {
				if(!perms.get(entry)) {
					negPerms.put(entry, perms.get(entry));
					continue;
				}
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
			for (String entry : negPerms.keySet()) {
				attachment.setPermission(entry, perms.get(entry));
				logger.fine("[DroxPerms] Setting " + entry
						+ " to " + perms.get(entry) + " for player " + player.getName());
			}
		}
		player.recalculatePermissions();
	}

	private void enableScheduler() {
		disableScheduler();
		commiter = new DroxSaveThread(this);
		int minutes = Config.getSaveInterval();
		task = getServer().getScheduler().runTaskTimer(this, commiter, 1200, minutes * 1200);
		logger.info("[DroxPerms] Saving changes every " + minutes + " minutes!");
	}

	private void disableScheduler() {
		if (task != null) {
			getServer().getScheduler().cancelTask(task.getTaskId());
			task = null;
			logger.info("[DroxPerms] Deactivated Save-Thread.");
		}
	}
}
