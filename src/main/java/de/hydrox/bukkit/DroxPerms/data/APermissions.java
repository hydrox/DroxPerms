package de.hydrox.bukkit.DroxPerms.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

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

	//Tehbeard start
	private Logger logger = Logger.getLogger("DroxPerms");
	//Tehbeard End

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

	//Tehbeard Start


	public boolean setTimedTrack(CommandSender sender, String player,
			String track, long time) {
		AUser user = getUser(player,true);
		logger.info("===BEGIN TIMED TRACK BLOCK===");
		if(user == null){ sender.sendMessage("USER_NOT_FOUND"); logger.severe("User not found! " + player);return false;}
		if(!ATrack.existTrack(track)){ sender.sendMessage("TRACK_NOT_FOUND"); logger.severe("No Track found! " + track + " when promoting " + player);return false;}

		if(user.getTimedTrack() != null){
			if(!ATrack.existTrack(user.getTimedTrack())){sender.sendMessage("ON_BAD_TRACK");logger.severe(player + " on bad track " + user.getTimedTrack());return false;}
			if(ATrack.getTrack(user.getTimedTrack()).getDemoteGroup(user.getGroup()) == null){sender.sendMessage("CANT_DEMOTE"); logger.severe("Users track does not exist " + player + " " + user.getTimedTrack());return false;}
			//check demote->promote
			if(ATrack.getTrack(track).getPromoteGroup(ATrack.getTrack(user.getTimedTrack()).getDemoteGroup(user.getGroup())) ==null){sender.sendMessage("CANT_USE_TRACK"); logger.severe("No way to promote player " + player + " along " + track + " from group " + ATrack.getTrack(user.getTimedTrack()).getDemoteGroup(user.getGroup()));return false;}
		}
		else
		{
			//just check promote
			if(ATrack.getTrack(track).getPromoteGroup(user.getGroup()) == null){ sender.sendMessage("CANT_PROMOTE");logger.severe("No way to promote player " + player + " along " + track + " from group " + user.getGroup());return false;}
		}

		//if user currently has a timed Track
		if(user.getTimedTrack() != null){
			//if it's the same one, add the time
			if(user.getTimedTrack().equals(track)){

				if(user.setTimedTrack(track,
						user.getTimedTrackExpires() + time
						)){
					logger.info("Extended time of player " + player + " on track " + track + " by " + time + "seconds.");
					sender.sendMessage("SUCCESS");
					return true;
				}
				else
				{
					logger.severe("Could not extend time of player " + player + " on track " + track + " by " + time + "seconds.");
					sender.sendMessage("CANT_EXTEND_TIME");
					return false;
				}
			}
			else
			{
				//Tell sender how much to recredit the user
				String endedTrack = user.getTimedTrack();
				long timeLeft = user.getTimedTrackExpires() - (System.currentTimeMillis()/1000L);


				//demote user
				if(!user.setGroup(ATrack.getTrack(endedTrack).getDemoteGroup(user.getGroup()))){
					sender.sendMessage("COULD_NOT_DEMOTE");
					logger.severe("Could not demote " + player + " from group " + user.getGroup() + " using track " + endedTrack);
					return false;
				}
				//send recredit message only if demoted
				if(timeLeft > 0){
					sender.sendMessage("CREDIT " + endedTrack + " " + (timeLeft/86400));
					logger.info("Recredit " + player + " " + endedTrack + " " + timeLeft);
				}
			}
		}

		//promote user
		if(!user.setGroup(ATrack.getTrack(track).getPromoteGroup(user.getGroup()))){
			sender.sendMessage("COULD_NOT_PROMOTE");
			logger.severe("Could not promote " + player + " from group " + user.getGroup() + " using track " + track);
			return false;
		}
		else
		{
			logger.info(player + " promoted to group " + user.getGroup() + " using track " + track + " for " + time + " seconds, expires " + new SimpleDateFormat().format(new Date(System.currentTimeMillis() + (time * 1000L))));
		}
		//set data
		if(user.setTimedTrack(track, (System.currentTimeMillis()/1000L) + time)){
			sender.sendMessage("SUCCESS");
			logger.info(player + " updated timed track data");
			return true;
		}
		else
		{
			sender.sendMessage("COULD_NOT_STORE");
			logger.severe("Could not store player track information! " + player + " from group " + user.getGroup() + " using track " + track);
			return false;
		}

	}

	@Override
	public boolean addTimedSubgroup(CommandSender sender, String player,
			String subgroup, long time) {
		AUser user = getUser(player,true);
		logger.info("===BEGIN TIMED SUBGROUP BLOCK===");
		if(user == null){ logger.severe("User not found! " + player);return false;}
		if(!AGroup.existGroup(subgroup)){logger.severe("Group not found! " + subgroup);return false;}

		long t = time;
		if(user.hasTimedSubgroup(subgroup)){
			logger.info("Extending " + subgroup + " time by " + time + " seconds for " + player);
			t+= user.getTimedSubgroupExpires(subgroup);
		}
		else
		{
			t+= (System.currentTimeMillis() / 1000L);
		}

		if(!user.getSubgroups().contains(subgroup)){
			if(!user.addSubgroup(subgroup)){
				logger.severe("Could not add subgroup " + subgroup + " to " + player);
				return false;
			}
		}

		if(user.setTimedSubgroup(subgroup, t)){

			logger.info("Updated timed subgroup information for " + player + " " + subgroup + " " + time + " seconds.");
			sender.sendMessage("SUCCESS");
			return true;
		}
		else
		{
			logger.severe("Could not update information for " + player + " " + subgroup + " " + time + " seconds.");
			sender.sendMessage("ERROR");
			return false;
		}
	}

	@Override
	public String getTimedTrack(CommandSender sender, String player) {
		AUser user = getUser(player,true);
		if(user==null){return null;}
		return user.getTimedTrack();
	}

	@Override
	public long getTimedTrackExpires(CommandSender sender, String player) {
		AUser user = getUser(player,true);
		if(user==null){return 0L;}
		return user.getTimedTrackExpires();
	}

	@Override
	public Map<String, Long> getTimedSubgroups(CommandSender sender,
			String player) {
		AUser user = getUser(player,true);
		if(user==null){return null;}

		return user.getTimedSubgroups();
	}

	@Override
	public abstract Set<String> getAllUserNames();

	@Override
	public boolean processTimes(CommandSender sender, String player) {

		//check track
		AUser user = getUser(player,true);
		if(user==null){return false;}
		String track = user.getTimedTrack();
		if(track != null){
			long expires = user.getTimedTrackExpires();
			long time = System.currentTimeMillis() / 1000L;
			if(time > expires){
				logger.info("===BEGIN DEMOTE TRACK BLOCK===");
				if(!ATrack.existTrack(track)){logger.severe("Could not demote, track " + track + " not found for " + player);return false;}

				String demoteTo = ATrack.getTrack(track).getDemoteGroup(user.getGroup());

				if(demoteTo == null){logger.severe("Could not demote " + player + ", no demote path for " + user.getGroup());return  false;}

				if(!user.setGroup(demoteTo)){logger.severe("Could not demote " + player + " to group " + demoteTo);return false;}

				if(user.setTimedTrack(null, 0L)){
					logger.info("Track data reset for " + player);
				}
				else
				{
					logger.severe("Player " + player + " demoted, but could not update track data!");
					return false;
				}
			}
		}
		boolean res = true;
		//check subgroup
		Iterator<Entry<String, Long>> it = user.getTimedSubgroups().entrySet().iterator();
		while(it.hasNext()){
			Entry<String, Long> e = it.next();
			String sg = e.getKey();
			long expires = e.getValue();
			long time = System.currentTimeMillis() / 1000L;
			if(time > expires){
				logger.info(sg + " expired for " + player);

						if(!user.removeSubgroup(sg)){
							logger.severe("Could not remove subgroup " + sg + " for " + player);
							res = false;
						}

						it.remove();
			}
		}


		return res;
	}

	public boolean cancelTimed(CommandSender sender,String player,String group){
		AUser user = getUser(player,true);
		long recredit = 0;
		if(user==null){logger.severe("Could not cancel for user " + player + ", not found");return false;}

		if(group == null){
			logger.info("===BEGIN CANCEL TRACK BLOCK===");
			if(user.getTimedTrack()==null){logger.severe("Could not cancel for " + player + ", does not have a group active");return false;}

			if(user.getTimedTrackExpires() > (System.currentTimeMillis() / 1000L)){
				recredit = user.getTimedTrackExpires() - (System.currentTimeMillis() / 1000L);
			}
			group = user.getTimedTrack();

			if(!ATrack.existTrack(group)){logger.severe("Cannot find track " + group);return false;}
			if(ATrack.getTrack(group).getDemoteGroup(user.getGroup()) == null){logger.severe("Cannot find a demote path for track " + group + " group " + user.getGroup());return false;}

			if(!user.setGroup(ATrack.getTrack(group).getDemoteGroup(user.getGroup()))){
				logger.severe("Error occured while demoting " + player + " on track " + group + " from " + user.getGroup());
				return false;
			}
			else{
				user.setTimedTrack(null, 0L);
				logger.info("Cancelled track, recredit " + group + " "  + recredit + " to " + player);

			}

		}
		else
		{
			logger.info("===BEGIN CANCEL SUBGROUP BLOCK===");
			if(user.getTimedSubgroupExpires(group) == 0L){logger.severe("subgroup " + group + " not active for " + player);return false;}

			if(user.getTimedSubgroupExpires(group) > (System.currentTimeMillis() / 1000L)){
				recredit = user.getTimedSubgroupExpires(group) - (System.currentTimeMillis() / 1000L);
			}

			if(!user.removeSubgroup(group)){
				logger.severe("Could not remove subgroup " + group + " from " + player);
				return false;
			}
			else
			{
				user.setTimedSubgroup(group, 0L);
				logger.info("Cancelled subgroup, recredit " + group + " "  + recredit + " to " + player);
			}
		}

		if(recredit > 0){
			sender.sendMessage("CREDIT " + group + " " + (recredit/86400));
		}

		return true;
	}

	//Tehbeard End
}
