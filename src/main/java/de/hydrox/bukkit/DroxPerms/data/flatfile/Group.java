package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.util.config.ConfigurationNode;

public class Group {
	private static HashMap<String, Group> groups = new HashMap<String, Group>();
	
	private String name;
	private HashMap<String, ArrayList<String>> permissions;
	private ArrayList<String> globalPermissions;
	private ArrayList<String> subgroups;

	public Group() {
		this("default");
	}

	public Group(String name) {
		this.name = name;
		this.subgroups = new ArrayList<String>();
		this.globalPermissions = new ArrayList<String>();
		this.permissions = new HashMap<String, ArrayList<String>>();
	}

	public Group(String name, ConfigurationNode node) {
		this.name = name;
		System.out.println("groups" + node.getKeys().toString());
		System.out.println("groups.subgroups" + node.getStringList("subgroups", new ArrayList<String>()));
		this.subgroups = (ArrayList<String>) node.getStringList("subgroups", new ArrayList<String>());
		System.out.println("subgroups: " + subgroups.size());
		this.globalPermissions = (ArrayList<String>) node.getStringList("globalpermissions", new ArrayList<String>());
		System.out.println("globalpermissions: " + globalPermissions.size());
		this.permissions = new HashMap<String, ArrayList<String>>();
		ConfigurationNode tmp = node.getNode("permissions");
		Iterator<String> iter = tmp.getKeys().iterator();
		while (iter.hasNext()) {
			String world = iter.next();
			permissions.put(world, (ArrayList<String>) tmp.getStringList(world, new ArrayList<String>()));
			System.out.println("permissions "+world+": " + permissions.get(world).size());
		}
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Object> toConfigurationNode() {
		HashMap<String, Object> output = new HashMap<String, Object>();
		output.put("subgroups", subgroups);
		output.put("permissions", permissions);
		output.put("globalpermissions", globalPermissions);
		return output;
	}

	public boolean addPermission(String world, String permission) {
		ArrayList<String> permArray = permissions.get(world.toLowerCase());
		if (permArray != null) {
			if (permArray.contains(permission)) {
				return false;
			}
			permArray.add(permission);
			return true;
		}
		return false;
	}

	public boolean removePermission(String world, String permission) {
		ArrayList<String> permArray = permissions.get(world.toLowerCase());
		if (permArray != null) {
			if (permArray.contains(permission)) {
				permArray.remove(permission);
				return true;
			}
			return false;
		}
		return false;
	}

	public boolean addSubgroup(String subgroup) {
		if(Group.existGroup(subgroup.toLowerCase())) {
			if(!subgroups.contains(subgroup.toLowerCase())) {
				subgroups.add(subgroup.toLowerCase());
				return true;
			}
			
		} 
		return false;
	}

	public boolean removeSubgroup(String subgroup) {
		if(subgroups.contains(subgroup.toLowerCase())) {
			subgroups.remove(subgroup.toLowerCase());
			return true;
		}
		return false;
	}

	public boolean hasPermission(String world, String permission) {
		ArrayList<String> permArray = permissions.get(world.toLowerCase());
		if (permArray != null) {
			if (permArray.contains(permission)) {
				return true;
			}
		}

		for (String subgroup : subgroups) {
			if (Group.getGroup(subgroup) != null) {
				if (Group.getGroup(subgroup).hasPermission(world.toLowerCase(), permission)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean addWorld(String world) {
		if (permissions.containsKey(world.toLowerCase())) {
			return false;
		}
		permissions.put(world.toLowerCase(), new ArrayList<String>());
		return true;
	}

	public static boolean addGroup(Group group) {
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

	public static Group getGroup(String name) {
		return groups.get(name.toLowerCase());
	}

	public static boolean existGroup(String name) {
		if (groups.containsKey(name.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static void clearGroups() {
		groups.clear();
	}
	
	public static Iterator<Group> iter() {
		return groups.values().iterator();
	}
}
