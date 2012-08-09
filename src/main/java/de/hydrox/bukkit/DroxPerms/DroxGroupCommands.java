package de.hydrox.bukkit.DroxPerms;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.hydrox.bukkit.DroxPerms.data.IDataProvider;

public class DroxGroupCommands implements CommandExecutor {
    
	private DroxPerms plugin;
	private IDataProvider dp;

	protected int groupInfoSet = 0;
	protected int groupPermAdd = 0;
	protected int groupPermRem = 0;
	protected int groupGroupSet = 0;
	protected int groupAddSub = 0;
	protected int groupRemSub = 0;
	protected int groupNew = 0;
	protected int groupListPerms = 0;

    public DroxGroupCommands(DroxPerms plugin) {
        this.plugin = plugin;
        this.dp = plugin.dataProvider;
    }
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (!(sender.hasPermission("droxperms.groups"))) {
			sender.sendMessage("You don't have permission to modify Groups.");
			return true;
		}
        this.dp = plugin.dataProvider;
		boolean result = false;
		if (split.length == 0) {
			// display help
			return false;
		}
		// add permission
		if (split[0].equalsIgnoreCase("addperm")) {
			groupPermAdd++;
			if (split.length == 3) {
				// add global permission
				result = dp.addGroupPermission(sender, split[1], null, split[2]);
			} else if (split.length == 4) {
				// add world permission
				result = dp.addGroupPermission(sender, split[1], split[3], split[2]);
			}
			plugin.refreshPermissions();
			return result;
		}

		// remove permission
		if (split[0].equalsIgnoreCase("remperm")) {
			groupPermRem++;
			if (split.length == 3) {
				// remove global permission
				result = dp.removeGroupPermission(sender, split[1], null, split[2]);
			} else if (split.length == 4) {
				// remove world permission
				result = dp.removeGroupPermission(sender, split[1], split[3], split[2]);
			}
			plugin.refreshPermissions();
			return result;
		}

		// add subgroup
		if (split[0].equalsIgnoreCase("addsub")) {
			groupAddSub++;
			if (split.length == 3) {
				result = dp.addGroupSubgroup(sender, split[1], split[2]);
			}
			plugin.refreshPermissions();
			return result;
		}

		// remove subgroup
		if (split[0].equalsIgnoreCase("remsub")) {
			groupRemSub++;
			if (split.length == 3) {
				result = dp.removeGroupSubgroup(sender, split[1],split[2]);
			}
			plugin.refreshPermissions();
			return result;
		}

		// add new group
		if (split[0].equalsIgnoreCase("new")) {
			groupNew++;
			if (split.length == 2) {
				result = dp.createGroup(sender, split[1]);
				if (!result) {
					sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. Group already exists!");
				} else {
					sender.sendMessage(ChatColor.GREEN + "New Group created!");
				}
				return true;
			} else {
				return false;
			}
		}

		if (split[0].equalsIgnoreCase("listperms") && split.length >= 2) {
			groupListPerms++;
			Map<String, List<String>> permissions = null;
			if (split.length == 3) {
				permissions = dp.getGroupPermissions(split[1], split[2]);
			} else if (split.length == 2) {
				permissions = dp.getGroupPermissions(split[1], null);
			} else {
				return false;
			}
			List<String> subgroups = dp.getGroupSubgroups(split[1]);
			if (subgroups != null && subgroups.size() > 0) {
				StringBuilder string = new StringBuilder();
				string.append(split[1] + " has permission from subgroups:");
				for (String subgroupstring : subgroups) {
					string.append(" " + subgroupstring);
				}
				sender.sendMessage(string.toString());
			}
			List<String> globalperms = permissions.get("global");
			if (globalperms != null && globalperms.size() > 0) {
				StringBuilder string = new StringBuilder();
				string.append(split[1] + " has permission globalpermissions:");
				for (String globalstring : globalperms) {
					string.append(" " + globalstring);
				}
				sender.sendMessage(string.toString());
			}
			List<String> worldperms = permissions.get("world");
			if (worldperms != null && worldperms.size() > 0) {
				StringBuilder string = new StringBuilder();
				string.append(split[1] + " has permission worldpermissions:");
				for (String globalstring : worldperms) {
					string.append(" " + globalstring);
				}
				sender.sendMessage(string.toString());
			}
		}

		// set info-node
		if (split[0].equalsIgnoreCase("setinfo")) {
			groupInfoSet++;
			if (split.length == 4) {
				String data = split[3].replace("_", " ");
				result = dp.setGroupInfo(sender, split[1], split[2], data);
			}
			return result;
		}

		// unset info-node
		if (split[0].equalsIgnoreCase("unsetinfo")) {
			groupInfoSet++;
			if (split.length == 3) {
				result = dp.setGroupInfo(sender, split[1], split[2], null);
			}
			return result;
		}

		return true;
	}

}
