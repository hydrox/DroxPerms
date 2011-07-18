package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class User {
	private String name;
	private String group;
	private ArrayList<String> subgroups;
	private HashMap<String, ArrayList<String>> permissions;
	
	public User() {
		this("mydrox");
	}
	public User(String name) {
		this.name = name;
		this.group = "default";
		this.subgroups = new ArrayList<String>();
		this.permissions = new HashMap<String, ArrayList<String>>();
	}
	
	public String getName() {
		return name;
	}
	
	public HashMap<String, Object> toConfigurationNode() {
		LinkedHashMap<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("group", group);
		output.put("subgroups", subgroups);
		output.put("permissions", permissions);
		return output;
	}
}
