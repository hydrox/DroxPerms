package de.hydrox.bukkit.DroxPerms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DroxStatsCommands implements CommandExecutor {
	private DroxPerms plugin;

	public DroxStatsCommands(DroxPerms plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender.hasPermission("droxperms.stats"))) {
			sender.sendMessage("You don't have permission to run the TestSuite.");
			return true;
		}
		
		if (args[0].equalsIgnoreCase("groupnumbers")) {
			HashMap<String, ArrayList<String>> groupmembers = plugin.dataProvider.getGroupMembers();
			plugin.logger.info("[DroxPerms] Group Stats:");
			Set<String> keys = groupmembers.keySet();
			for (String group : keys) {
				int count = groupmembers.get(group).size();
				plugin.logger.info("[DroxPerms] " + group + " has " + count + " members.");
			}
		}

		if (args[0].equalsIgnoreCase("subgroupnumbers")) {
			HashMap<String, ArrayList<String>> subgroupmembers = plugin.dataProvider.getSubgroupMembers();
			plugin.logger.info("[DroxPerms] Subgroup Stats:");
			Set<String> keys = subgroupmembers.keySet();
			for (String group : keys) {
				int count = subgroupmembers.get(group).size();
				plugin.logger.info("[DroxPerms] " + group + " has " + count + " members.");
			}
		}

		if (args[0].equalsIgnoreCase("groupmembers")) {
			HashMap<String, ArrayList<String>> groupmembers = plugin.dataProvider.getGroupMembers();
			plugin.logger.info("[DroxPerms] Group members:");
			Set<String> keys = groupmembers.keySet();
			for (String group : keys) {
				ArrayList<String> members = groupmembers.get(group);
				plugin.logger.info("[DroxPerms] Group: " + group + ":");
				StringBuilder memberstring = new StringBuilder();
				for (String member : members) {
					memberstring.append(member).append(" ");
				}
				plugin.logger.info("[DroxPerms] " + memberstring.toString());
			}
		}

		if (args[0].equalsIgnoreCase("subgroupmembers")) {
			HashMap<String, ArrayList<String>> subgroupmembers = plugin.dataProvider.getSubgroupMembers();
			plugin.logger.info("[DroxPerms] Group members:");
			Set<String> keys = subgroupmembers.keySet();
			for (String subgroup : keys) {
				ArrayList<String> members = subgroupmembers.get(subgroup);
				plugin.logger.info("[DroxPerms] Group: " + subgroup + ":");
				StringBuilder memberstring = new StringBuilder();
				for (String member : members) {
					memberstring.append(member).append(" ");
				}
				plugin.logger.info("[DroxPerms] " + memberstring.toString());
			}
		}

		return true;
	}
}
