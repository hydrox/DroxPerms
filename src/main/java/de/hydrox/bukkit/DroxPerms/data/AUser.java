package de.hydrox.bukkit.DroxPerms.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AUser {
	protected static Map<String, AUser> users = new HashMap<String, AUser>();
	protected static Map<String, AUser> backupUsers = new HashMap<String, AUser>();
	protected static boolean testmode = false;

	public static boolean removeUser(String name) {
		if (existUser(name.toLowerCase())) {
			users.remove(name.toLowerCase());
			return true;
		}
		return false;
	}

	public abstract boolean addPermission(String world, String permission);
	public abstract boolean addSubgroup(String subgroup);
	
	public abstract String getGroup();
	public abstract String getInfo(String node);
	public abstract Map<String, String> getInfoComplete();
	public abstract String getName();
	public abstract Map<String, Map<String, Boolean>> getPermissions(String world);
	public abstract List<String> getSubgroups();
	public abstract long getTimedSubgroupExpires(String subgroup);
	public abstract Map<String,Long> getTimedSubgroups();
	public abstract String getTimedTrack();
	public abstract long getTimedTrackExpires();
	public abstract boolean hasTimedSubgroup(String subgroup);
	public abstract boolean removePermission(String world, String permission);
	public abstract boolean removeSubgroup(String subgroup);
	public abstract boolean setGroup(String newGroup);
	public abstract boolean setInfo(String node, String data);
	public abstract boolean setTimedSubgroup(String subgroup,long expires);
	public abstract boolean setTimedTrack(String track,long expires);

	public static boolean addUser(AUser user) {
		if (existUser(user.getName().toLowerCase())) {
			return false;
		}
		users.put(user.getName().toLowerCase(), user);
		return true;
	}

	public static AUser getUser(String name) {
		return users.get(name.toLowerCase());
	}

	public static boolean existUser(String name) {
		if (users.containsKey(name.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static void clearUsers() {
		users.clear();
	}

	public static Iterator<AUser> iter() {
		return users.values().iterator();
	}

	public static void setTestMode() {
		if (!testmode) {
			backupUsers = users;
			users = new HashMap<String, AUser>();
			testmode = true;
		}
	}

	public static void setNormalMode() {
		if (testmode) {
			users = backupUsers;
			testmode = false;
		}
	}

}
