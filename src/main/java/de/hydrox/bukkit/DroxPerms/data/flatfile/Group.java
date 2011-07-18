package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;

public class Group {
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
		output.put("inherites", heritages);
		output.put("permissions", permissions);
		return output;
	}
}
