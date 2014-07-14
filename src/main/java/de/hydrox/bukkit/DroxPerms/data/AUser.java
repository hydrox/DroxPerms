package de.hydrox.bukkit.DroxPerms.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class AUser {
	protected static Map<UUID, AUser> users = new HashMap<UUID, AUser>();
	protected static Map<UUID, AUser> backupUsers = new HashMap<UUID, AUser>();
	protected static boolean testmode = false;

	public static boolean removeUser(UUID uuid) {
		if (existUser(uuid)) {
			users.remove(uuid);
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
	public abstract UUID getUUID();
	public abstract Map<String, Map<String, Boolean>> getPermissions(String world);
	public abstract List<String> getSubgroups();
	public abstract boolean removePermission(String world, String permission);
	public abstract boolean removeSubgroup(String subgroup);
	public abstract boolean setGroup(String newGroup);
	public abstract boolean setInfo(String node, String data);

	public static boolean addUser(AUser user) {
		if (existUser(user.getUUID())) {
			return false;
		}
		users.put(user.getUUID(), user);
		return true;
	}

	public static AUser getUser(UUID uuid) {
		return users.get(uuid);
	}

	public static boolean existUser(UUID uuid) {
		if (users.containsKey(uuid)) {
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
			users = new HashMap<UUID, AUser>();
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
