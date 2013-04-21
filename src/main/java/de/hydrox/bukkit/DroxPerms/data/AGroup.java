package de.hydrox.bukkit.DroxPerms.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.permissions.Permission;

public abstract class AGroup {
	private static Map<String, AGroup> groups = new HashMap<String, AGroup>();
	private static Map<String, AGroup> backupGroups = new HashMap<String, AGroup>();
	private static boolean testmode = false;

	protected String name;

	protected Map<String, Permission> bukkitPermissions;

	public String getName() {
		return name;
	}

	public abstract Map<String, Map<String, Boolean>> getPermissions(String world);

	public abstract boolean addPermission(String world, String permission);

	public abstract boolean removePermission(String world, String permission);

	public abstract boolean addSubgroup(String subgroup);

	public abstract boolean removeSubgroup(String subgroup);

	public abstract boolean hasPermission(String world, String permission);

	public abstract boolean setInfo(String node, String data);

	public abstract String getInfo(String node);

	public abstract Map<String, String> getInfoComplete();

	public abstract List<String> getSubgroups();

	public static boolean addGroup(AGroup group) {
		if (existGroup(group.name.toLowerCase())) {
			return false;
		}
		groups.put(group.name.toLowerCase(), group);
		return true;
	}

	public static boolean removeGroup(String name) {
		if (existGroup(name.toLowerCase())) {
			groups.remove(name.toLowerCase());
			return true;
		}
		return false;
	}

	public static AGroup getGroup(String name) {
		return groups.get(name.toLowerCase());
	}

	public static boolean existGroup(String name) {
		if (groups.containsKey(name.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static Set<String> getGroups() {
		return groups.keySet();
	}

	public static void clearGroups() {
		groups.clear();
	}
	
	public static Iterator<AGroup> iter() {
		return groups.values().iterator();
	}

	public static void setTestMode() {
		if (!testmode) {
			backupGroups = groups;
			groups = new HashMap<String, AGroup>();
			testmode = true;
		}
	}

	public static void setNormalMode() {
		if (testmode) {
			groups = backupGroups;
			testmode = false;
		}
	}
}
