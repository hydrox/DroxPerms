package de.hydrox.bukkit.DroxPerms;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import de.hydrox.bukkit.DroxPerms.data.IDataProvider;

public class DroxPlayerCommands implements CommandExecutor {

	private DroxPerms plugin;
	private IDataProvider dp;

	protected int playerInfoSet = 0;
	protected int playerPermAdd = 0;
	protected int playerPermRem = 0;
	protected int playerGroupSet = 0;
	protected int playerAddSub = 0;
	protected int playerRemSub = 0;
	protected int playerPromote = 0;
	protected int playerDemote = 0;
	protected int playerListPerms = 0;

	public DroxPlayerCommands(DroxPerms plugin) {
		this.plugin = plugin;
		dp = plugin.dataProvider;
	}
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (!(sender.hasPermission("droxperms.players"))) {
			sender.sendMessage("You don't have permission to modify Players.");
			return true;
		}
		dp = plugin.dataProvider;
		Player caller = null;
		if (sender instanceof Player) {
			caller = (Player) sender;
		}
		boolean result = false;
		if (split.length == 0) {
			return false;
		} else if (caller != null && caller.getName().equalsIgnoreCase(split[1])
				&& !(sender.hasPermission("droxperms.players.self"))) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to modify your Permissions.");
			return true;			
		}
		// add permission
		if (split[0].equalsIgnoreCase("addperm")) {
			playerPermAdd++;
			if (split.length == 3) {
				// add global permission
				result = dp.addPlayerPermission(sender, split[1], null, split[2]);
			} else if (split.length == 4) {
				// add world permission
				result = dp.addPlayerPermission(sender, split[1], split[3], split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		// remove permission
		if (split[0].equalsIgnoreCase("remperm")) {
			playerPermRem++;
			if (split.length == 3) {
				// remove global permission
				result = dp.removePlayerPermission(sender, split[1], null, split[2]);
			} else if (split.length == 4) {
				// remove world permission
				result = dp.removePlayerPermission(sender, split[1], split[3], split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		// add subgroup
		if (split[0].equalsIgnoreCase("addsub")) {
			playerAddSub++;
			if (split.length == 3) {
				result = dp.addPlayerSubgroup(sender, split[1], split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		// remove subgroup
		if (split[0].equalsIgnoreCase("remsub")) {
			playerRemSub++;
			if (split.length == 3) {
				result = dp.removePlayerSubgroup(sender, split[1],split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		// set group
		if (split[0].equalsIgnoreCase("setgroup")) {
			playerGroupSet++;
			if (split.length == 3) {
				result = dp.setPlayerGroup(sender, split[1],split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		if (split[0].equalsIgnoreCase("has") && (split.length == 3)) {
			Player player = plugin.getServer().getPlayer(split[1]);
			if (player == null) {
				sender.sendMessage(split[1] + " is not online");
			} else {
				result = player.hasPermission(split[2]);
				if (result) {
					sender.sendMessage(split[1] + " has permission for " + split[2]);
				} else {
					sender.sendMessage(split[1] + " doesn't have permission for " + split[2]);
				}
			}
		}

		if (split[0].equalsIgnoreCase("listperms") && split.length >= 2) {
			playerListPerms++;
			Map<String, Map<String, Boolean>> permissions = null;
			if (split.length == 3) {
				permissions = dp.getPlayerPermissions(split[1], split[2], true);
			} else if (split.length == 2) {
				permissions = dp.getPlayerPermissions(split[1], null, true);
			} else {
				return false;
			}
			if (permissions == null) {
				sender.sendMessage(ChatColor.RED + "Could not find user matching input or found more then one user matching");
				return true;
			}
			String player = dp.getUserNameFromPart(split[1]); 
			sender.sendMessage(player + " has permission from group: " + dp.getPlayerGroup(player));
			List<String> subgroupssimple = dp.getPlayerSubgroupsSimple(player);
			if (subgroupssimple != null && subgroupssimple.size() > 0) {
				StringBuilder string = new StringBuilder();
				string.append(player + " has permission from subgroups:");
				for (String subgroupstring : subgroupssimple) {
					string.append(" " + subgroupstring);
				}
				sender.sendMessage(string.toString());
			}
			List<String> subgroups = dp.getPlayerSubgroups(player);
			subgroups.removeAll(subgroupssimple);
			if (subgroups != null && subgroups.size() > 0) {
				StringBuilder string = new StringBuilder();
				string.append(player + " has permission from inherited subgroups:");
				for (String subgroupstring : subgroups) {
					string.append(" " + subgroupstring);
				}
				sender.sendMessage(string.toString());
			}
			Map<String, Boolean> globalperms = permissions.get("global");
			if (globalperms != null && globalperms.size() > 0) {
				sender.sendMessage(player + " has permission globalpermissions:");
				for (String globalstring : globalperms.keySet()) {
					ChatColor color = (globalperms.get(globalstring)) ? ChatColor.GREEN : ChatColor.RED;
					sender.sendMessage(" " + globalstring + ": " + color + globalperms.get(globalstring));
				}
			}
			Map<String, Boolean> worldperms = permissions.get("world");
			if (worldperms != null && worldperms.size() > 0) {
				sender.sendMessage(player + " has permission worldpermissions:");
				for (String worldstring : worldperms.keySet()) {
					ChatColor color = (worldperms.get(worldstring)) ? ChatColor.GREEN : ChatColor.RED;
					sender.sendMessage(" " + worldstring + ": " + color + worldperms.get(worldstring));
				}
			}
			Map<String, String> infos = dp.getPlayerInfoComplete(player);
			if (infos != null) {
				StringBuilder string = new StringBuilder();
				string.append(player + " has info-nodes:\n");
				for (String infonode : infos.keySet()) {
					string.append("-" + infonode + ": " + infos.get(infonode) + "\n");
				}
				sender.sendMessage(string.toString());
			}
		}

		// promote
		if (split[0].equalsIgnoreCase("promote")) {
			playerPromote++;
			if (split.length == 3) {
				result = dp.promotePlayer(sender, split[1], split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		// demote
		if (split[0].equalsIgnoreCase("demote")) {
			playerDemote++;
			if (split.length == 3) {
				result = dp.demotePlayer(sender, split[1], split[2]);
			}
			plugin.refreshPlayer(plugin.getServer().getPlayer(split[1]));
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		// set info-node
		if (split[0].equalsIgnoreCase("setinfo")) {
			playerInfoSet++;
			if (split.length == 4) {
				String data = split[3].replace("_", " ");
				result = dp.setPlayerInfo(sender, split[1], split[2], data);
			}
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		if (split[0].equalsIgnoreCase("unsetinfo")) {
			playerInfoSet++;
			if (split.length == 3) {
				result = dp.setPlayerInfo(sender, split[1], split[2], null);
			}
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		if (split[0].equalsIgnoreCase("delete")) {
			if (split.length == 2) {
				result = dp.deletePlayer(sender, split[1]);
			}
			if (!result) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique/non-existant username given?");
			}
			return true;
		}

		if (split[0].equalsIgnoreCase("debug") && split.length >= 2) {
			Player player = plugin.getServer().getPlayer(split[1]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique username given or player not online?");
				return true;
			}
			Set<PermissionAttachmentInfo> tmp = player.getEffectivePermissions();
			for (PermissionAttachmentInfo permissionAttachmentInfo : tmp) {
				if (split.length == 3 && !permissionAttachmentInfo.getPermission().startsWith(split[2])) {
					continue;
				}
				sender.sendMessage(permissionAttachmentInfo.getPermission() + " " + permissionAttachmentInfo.getValue());
			}
		}




		return true;
	}
}
