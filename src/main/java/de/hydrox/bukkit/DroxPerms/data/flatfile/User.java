package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.bukkit.util.config.ConfigurationNode;

import de.hydrox.bukkit.DroxPerms.data.Config;

public class User {
	private static HashMap<String, User> users = new HashMap<String, User>();

	private String name;
	private String group;
	private ArrayList<String> subgroups;
	private ArrayList<String> globalPermissions;
	private HashMap<String, ArrayList<String>> permissions;

	public User() {
		this("mydrox");
	}

	public User(String name) {
		this.name = name;
		this.group = "default";
		this.subgroups = new ArrayList<String>();
		this.globalPermissions = new ArrayList<String>();
		this.permissions = new HashMap<String, ArrayList<String>>();
	}

	public User(String name, ConfigurationNode node) {
		this.name = name;
		this.group = node.getString("group");
		this.subgroups = (ArrayList<String>) node.getStringList("subgroups", new ArrayList<String>());
		this.globalPermissions = (ArrayList<String>) node.getStringList("globalpermissions", new ArrayList<String>());
		ConfigurationNode tmp = node.getNode("permissions");
		if(tmp != null) {
			this.permissions = new HashMap<String, ArrayList<String>>();
			Iterator<String> iter = tmp.getKeys().iterator();
			while (iter.hasNext()) {
				String world = iter.next();
				permissions.put(world, (ArrayList<String>) tmp.getStringList(world, new ArrayList<String>()));
			}
		}
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Object> toConfigurationNode() {
		LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("group", group);
		if (subgroups != null && subgroups.size() != 0) {
			output.put("subgroups", subgroups);
		}
		if (permissions != null && permissions.size() != 0) {
			output.put("permissions", permissions);
		}
		if (globalPermissions != null && globalPermissions.size() != 0) {
			output.put("globalpermissions", globalPermissions);
		}
		return output;
	}
	
	public String[] getPermissions(String world) {
		ArrayList<String> perms = new ArrayList<String>();
		//add group permissions
		perms.add("droxperms.meta.group." + group);
		//add subgroup permissions
		if (subgroups != null) {
			for (Iterator<String> iterator = subgroups.iterator(); iterator.hasNext();) {
				String subgroup = iterator.next();
				perms.add("droxperms.meta.group." + subgroup);
			}
		}
		//add global permissions
		if (globalPermissions != null) {
			perms.addAll(globalPermissions);
		}
		//add world permissions
		if (permissions != null) {
			perms.addAll(permissions.get(world));
		}
		return perms.toArray(new String[0]);
	}

	public boolean setGroup(String newGroup) {
		if (Group.existGroup(newGroup)) {
			group = newGroup;
			return true;
		}
		return false;
	}

	public boolean addPermission(String world, String permission) {
		if (world == null) {
			if (globalPermissions == null) {
				globalPermissions = new ArrayList<String>();
			}
			if (globalPermissions.contains(permission)) {
				return false;
			}
			globalPermissions.add(permission);
			return true;
		}

		if (permissions == null) {
			permissions = new HashMap<String, ArrayList<String>>();
		}
		ArrayList<String> permArray = permissions.get(Config.getRealWorld(world).toLowerCase());
		if (permArray == null) {
			permArray = new ArrayList<String>();
			permissions.put(Config.getRealWorld(world).toLowerCase(), permArray);
		}
		if (permArray.contains(permission)) {
			return false;
		}
		permArray.add(permission);
		return true;
	}

	public boolean removePermission(String world, String permission) {
		if (world == null) {
			if (globalPermissions != null && globalPermissions.contains(permission)) {
				globalPermissions.remove(permission);
				return true;
			}
			return false;
		}

		if (permissions == null) {
			return false;
		}
		ArrayList<String> permArray = permissions.get(Config.getRealWorld(world).toLowerCase());
		if (permArray == null) {
			permArray = new ArrayList<String>();
			permissions.put(Config.getRealWorld(world).toLowerCase(), permArray);
		}
		if (permArray.contains(permission)) {
			permArray.remove(permission);
			return true;
		}
		return false;
	}

	public boolean addSubgroup(String subgroup) {
		if(Group.existGroup(subgroup.toLowerCase())) {
			if (subgroups == null) {
				subgroups = new ArrayList<String>();
			}
			if (!subgroups.contains(subgroup.toLowerCase())) {
				subgroups.add(subgroup.toLowerCase());
				return true;
			}
		}
		return false;
	}

	public boolean removeSubgroup(String subgroup) {
		if(subgroups != null && subgroups.contains(subgroup.toLowerCase())) {
			subgroups.remove(subgroup.toLowerCase());
			return true;
		}
		return false;
	}

	public static boolean addUser(User user) {
		if (existUser(user.name.toLowerCase())) {
			return false;
		}
		users.put(user.name.toLowerCase(), user);
		return true;
	}

	public static boolean removeUser(String name) {
		if (existUser(name.toLowerCase())) {
			users.remove(name.toLowerCase());
			return true;
		}
		return false;
	}

	public static User getUser(String name) {
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
	
	public static Iterator<User> iter() {
		return users.values().iterator();
	}
}
