package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
	private static HashMap<String, Group> groups = new HashMap<String, Group>();
	
	private String name;
	private HashMap<String, ArrayList<String>> permissions;
	private ArrayList<String> heritages;

	public Group() {
		this("default");
	}

	public Group(String name) {
		this.name = name;
		this.heritages = new ArrayList<String>();
		this.permissions = new HashMap<String, ArrayList<String>>();
	}

	public String getName() {
		return name;
	}

	public HashMap<String, Object> toConfigurationNode() {
		HashMap<String, Object> output = new HashMap<String, Object>();
		output.put("subgroups", heritages);
		output.put("permissions", permissions);
		return output;
	}

	public boolean addPermission(String world, String Permission) {
		throw new UnsupportedOperationException();
	}

	public boolean removePermission(String world, String Permission) {
		throw new UnsupportedOperationException();
	}

	public boolean addSubgroup(String subgroup) {
		throw new UnsupportedOperationException();
	}

	public boolean removeSubgroup(String subgroup) {
		throw new UnsupportedOperationException();
	}

	public boolean hasPermission(String world, String Permission) {
		throw new UnsupportedOperationException();
	}

	public static boolean addGroup(Group group) {
		throw new UnsupportedOperationException();
	}

	public static boolean removeGroup(String name) {
		throw new UnsupportedOperationException();
	}

	public static Group getGroup(String name) {
		throw new UnsupportedOperationException();
	}

	public static boolean existGroup(String name) {
		throw new UnsupportedOperationException();
	}

	public static void clearGroups() {
		groups.clear();
	}
}
