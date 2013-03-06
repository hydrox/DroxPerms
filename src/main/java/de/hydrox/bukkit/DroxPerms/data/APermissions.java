package de.hydrox.bukkit.DroxPerms.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import de.hydrox.bukkit.DroxPerms.DroxPerms;

/**
 * 
 * @author Matthias Söhnholz
 *
 */
public abstract class APermissions implements IDataProvider {

	protected static DroxPerms plugin = null;

	public abstract boolean createPlayer(String name);

	public abstract boolean deletePlayer(CommandSender sender, String name);

	public abstract boolean createGroup(CommandSender sender, String name);

	public String getPlayerGroup(String player) {
		AUser user = getUser(player, true);
		if (user != null) {
			return user.getGroup();
		} else {
			return "";
		}
	}

	public boolean setPlayerGroup(CommandSender sender, String player, String group) {
		AUser user = getUser(player, true);
		if (user != null) {
			boolean result = user.setGroup(group);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "Set group of player " + user.getName() + " to " + group);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't set group of player " + user.getName());
				return false;
			}
		} else {
			return false;
		}
	}

	public List<String> getPlayerSubgroups(String player) {
		AUser user = getUser(player, true);
		if (user != null) {
			List<String> result = new ArrayList<String>(user.getSubgroups());
			result.add(user.getGroup());
			result = calculateSubgroups(result);
			result.remove(user.getGroup());
			return result;
		} else {
			return null;
		}
	}

	public List<String> getPlayerSubgroupsSimple(String player) {
		AUser user = getUser(player, true);
		if (user != null) {
			return new ArrayList<String>(user.getSubgroups());
		} else {
			return null;
		}
	}

	private List<String> calculateSubgroups(List<String> input) {
		List<String> result = new ArrayList<String>(input);
		List<String> toTest = new ArrayList<String>(input);

		while (toTest.size()!=0) {
			String string = toTest.get(0);
			AGroup group = AGroup.getGroup(string);
			if (group != null) {
				List<String> subgroups = group.getSubgroups();
				for (String string2 : subgroups) {
					if (!result.contains(string2)) {
						result.add(string2);
						toTest.add(string2);
					}
				}
			}
			toTest.remove(string);
		}
		return result;
	}

	public boolean addPlayerSubgroup(CommandSender sender, String player, String subgroup) {
		AUser user = getUser(player, true);
		if (user != null) {
			boolean result = user.addSubgroup(subgroup);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "Added " + subgroup + " to subgrouplist of player " + user.getName());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't add subgroup to player " + user.getName());
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean removePlayerSubgroup(CommandSender sender, String player, String subgroup) {
		AUser user = getUser(player, true);
		if (user != null) {
			boolean result = user.removeSubgroup(subgroup);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "removed " + subgroup + " from subgrouplist of player " + user.getName());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't remove subgroup from player " + user.getName());
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean addPlayerPermission(CommandSender sender, String player, String world, String node) {
		AUser user = getUser(player, true);
		if (user != null) {
			boolean result = user.addPermission(world, node);
			if (result) {
				if(world != null && !Config.getRealWorld(world).equalsIgnoreCase(world)) {
					sender.sendMessage(ChatColor.RED + "World " + world + " inherits Permission from " + Config.getRealWorld(world));
					sender.sendMessage(ChatColor.RED + "Permission were set on " + Config.getRealWorld(world));
				}
				sender.sendMessage(ChatColor.GREEN + "Added " + node + " to permissionslist of player " + user.getName());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't add permission to player " + user.getName());
				return false;
			}
		} else {
			return false;
		}
	}

	public boolean removePlayerPermission(CommandSender sender, String player, String world, String node) {
		AUser user = getUser(player, true);
		if (user != null) {
			boolean result = user.removePermission(world, node);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "removed " + node + " from permissionslist of player " + user.getName());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't remove permission from player " + user.getName());
				return false;
			}
		} else {
			return false;
		}
	}

	public Map<String, Map<String, Boolean>> getPlayerPermissions(String player, String world, boolean partialMatch) {
		AUser user = getUser(player, partialMatch);
		if (user == null) {
			if (partialMatch) {
				return null;
			}
			plugin.getServer().getLogger().info("[DroxPerms] User " + player + " doesn't exist yet. Creating ...");
			createPlayer(player);
			AUser.addUser(user);
			return getUser(player, false).getPermissions(world);
		}
		return user.getPermissions(world);
	}

	public boolean setPlayerInfo(CommandSender sender, String player, String node, String data) {
		AUser user = getUser(player, true);
		if (user != null) {
			boolean result = user.setInfo(node, data);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "set info-node " + node + " of player " + user.getName());
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't set info-node of player " + user.getName());
				return false;
			}
		} else {
			return false;
		}
	}

	public String getPlayerInfo(String player, String node) {
		AUser user = getUser(player, true);
		if (user != null) {
			return user.getInfo(node);
		} else {
			return null;
		}
	}

	public Map<String, String> getPlayerInfoComplete(String player) {
		AUser user = getUser(player, true);
		if (user != null) {
			return user.getInfoComplete();
		} else {
			return null;
		}
	}

	public boolean addGroupPermission(CommandSender sender, String group, String world, String node) {
		if (AGroup.existGroup(group)) {
			boolean result = AGroup.getGroup(group).addPermission(world, node);
			if (result) {
				if(world != null && !Config.getRealWorld(world).equalsIgnoreCase(world)) {
					sender.sendMessage(ChatColor.RED + "World " + world + " inherits Permission from " + Config.getRealWorld(world));
					sender.sendMessage(ChatColor.RED + "Permission were set on " + Config.getRealWorld(world));
				}
				sender.sendMessage(ChatColor.GREEN + "Added " + node + " to permissionslist of group " + group);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't add permission to group " + group);
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Group " + group + " doesn't exist.");
			return false;
		}
	}

	public boolean removeGroupPermission(CommandSender sender, String group, String world, String node) {
		if (AGroup.existGroup(group)) {
			boolean result = AGroup.getGroup(group).removePermission(world, node);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "removed " + node + " from permissionslist of group " + group);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't remove permission from group " + group);
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Group " + group + " doesn't exist.");
			return false;
		}
	}

	public List<String> getGroupSubgroups(String groupName) {
		AGroup group = AGroup.getGroup(groupName);
		if (group != null) {
			return calculateSubgroups(group.getSubgroups());
		} else {
			return null;
		}
	}

	public boolean addGroupSubgroup(CommandSender sender, String group, String subgroup) {
		if (AGroup.existGroup(group)) {
			boolean result = AGroup.getGroup(group).addSubgroup(subgroup);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "Added " + subgroup + " to subgrouplist of group " + group);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't add subgroup to group " + group);
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Group " + group + " doesn't exist.");
			return false;
		}
	}

	public boolean removeGroupSubgroup(CommandSender sender, String group, String subgroup) {
		if (AGroup.existGroup(group)) {
			boolean result = AGroup.getGroup(group).removeSubgroup(subgroup);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "removed " + subgroup + " from subgrouplist of group " + group);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't remove subgroup from group " + group);
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Group " + group + " doesn't exist.");
			return false;
		}
	}

	public Map<String, Map<String, Boolean>> getGroupPermissions(String groupName, String world) {
		AGroup group = AGroup.getGroup(groupName);
		if (group == null) {
			return null;
		}
		return group.getPermissions(world);
	}

	public boolean setGroupInfo(CommandSender sender, String group, String node, String data) {
		if (AGroup.existGroup(group)) {
			boolean result = AGroup.getGroup(group).setInfo(node, data);
			if (result) {
				sender.sendMessage(ChatColor.GREEN + "set info-node " + node + " for group " + group);
				return true;
			} else {
				sender.sendMessage(ChatColor.RED + "Couldn't set info-node for group " + group);
				return false;
			}
		} else {
			sender.sendMessage(ChatColor.RED + "Group " + group + " doesn't exist.");
			return false;
		}
	}

	public String getGroupInfo(String group, String node) {
		if (AGroup.existGroup(group)) {
			return AGroup.getGroup(group).getInfo(node);
		} else {
			return null;
		}
	}

	public Map<String, String> getGroupInfoComplete(String group) {
		if (AGroup.existGroup(group)) {
			return AGroup.getGroup(group).getInfoComplete();
		} else {
			return null;
		}
	}

	private AUser getUser(String name, boolean checkPartial) {
		if (checkPartial) {
			return getPartialUser(name);
		}
		return getExactUser(name);
	}
	protected abstract AUser getExactUser(String name);

	protected abstract AUser getPartialUser(String name);

	@Override
	public boolean promotePlayer(CommandSender sender, String player,
			String track) {
		ATrack selectedTrack = ATrack.getTrack(track);
		if (selectedTrack == null) {
			sender.sendMessage(ChatColor.RED + "Could not find Track " + track + ".");
			return false;
		}
		AUser user = getUser(player, true);
		if (user != null) {
			String newGroup = selectedTrack.getPromoteGroup(user.getGroup());
			if (newGroup == null) {
				sender.sendMessage(ChatColor.RED + "Could not promote on Track " + track + ".");
				return false;
			}
			return setPlayerGroup(sender, player, newGroup);
		} else {
			sender.sendMessage(ChatColor.RED + "Could not find User " + player + ".");
			return false;
		}
	}

	@Override
	public boolean demotePlayer(CommandSender sender, String player,
			String track) {
		ATrack selectedTrack = ATrack.getTrack(track);
		if (selectedTrack == null) {
			sender.sendMessage(ChatColor.RED + "Could not find Track " + track + ".");
			return false;
		}
		AUser user = getUser(player, true);
		if (user != null) {
			String newGroup = selectedTrack.getDemoteGroup(user.getGroup());
			if (newGroup == null) {
				sender.sendMessage(ChatColor.RED + "Could not demote on Track " + track + ".");
				return false;
			}
			return setPlayerGroup(sender, player, newGroup);
		} else {
			sender.sendMessage(ChatColor.RED + "Could not find User " + player + ".");
			return false;
		}
	}

	@Override
	public Map<String, List<String>> getGroupMembers() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		Iterator<AGroup> groups = AGroup.iter();
		while (groups.hasNext()) {
			AGroup group = (AGroup) groups.next();
			result.put(group.getName(), new ArrayList<String>());
		}

		Set<String> users = getAllUserNames();
		for (String key : users) {
			AUser user = getExactUser(key);
			String userGroup = user.getGroup();
			if (userGroup == null) {
				plugin.logger.warning("Group for User " + user.getName() + " is not set");
				continue;
			}
			List<String> resultGroup = result.get(user.getGroup());
			if (resultGroup == null) {
				plugin.logger.warning("Group " + userGroup + " for User " + user.getName() + " does not exist");
				continue;
			}
			resultGroup.add(user.getName());
		}
		return result;
	}

	@Override
	public Map<String, List<String>> getSubgroupMembers() {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		Iterator<AGroup> groups = AGroup.iter();
		while (groups.hasNext()) {
			AGroup group = groups.next();
			result.put(group.getName(), new ArrayList<String>());
		}

		Set<String> users = getAllUserNames();
		for (String key : users) {
			AUser user = getExactUser(key);
			List<String> subgroups = user.getSubgroups();
			for (String subgroup : subgroups) {
				List<String> resultGroup = result.get(subgroup);
				if (resultGroup == null) {
					plugin.logger.warning("SubGroup " + subgroup + " for User " + user.getName() + " does not exist");
					continue;
				}
				resultGroup.add(user.getName());
			}
		}
		return result;
	}

	public String getUserNameFromPart(String partialName) {
		AUser user = getUser(partialName, true);
		if (user != null) {
			return user.getName();
		}
		return null;
	}

	@Override
	public Set<String> getGroupNames() {
		Set<String> groupNames = AGroup.getGroups();
		return groupNames;
	}

	@Override
	public abstract Set<String> getAllUserNames();
}
