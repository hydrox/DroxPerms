package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

import de.hydrox.bukkit.DroxPerms.data.AGroup;

public class Group extends AGroup{

	public Group() {
		this("default");
	}

	public Group(String name) {
		this.name = name;
		this.subgroups = new ArrayList<String>();
		this.globalPermissions = new ArrayList<String>();
		this.permissions = new LinkedHashMap<String, List<String>>();
	}

	public Group(String name, ConfigurationSection node) {
		this.name = name;
		this.subgroups = node.getStringList("subgroups");
		this.globalPermissions = node.getStringList("globalpermissions");
		this.permissions = new LinkedHashMap<String, List<String>>();
		if(node.contains("permissions")) {
			Set<String> worlds = node.getConfigurationSection("permissions.").getKeys(false);
			for (String world : worlds) {
				permissions.put(world, node.getStringList("permissions." + world));
			}
		}
		if(node.contains("info")) {
			this.info = new HashMap<String, String>();
			Set<String> infoNodes = node.getConfigurationSection("info.").getKeys(false);
			for (String infoNode : infoNodes) {
				info.put(infoNode, node.getString("info." + infoNode));
			}
		}

		updatePermissions();
	}
        
	public Map<String, Object> toConfigurationNode() {
		Map<String, Object> output = new HashMap<String, Object>();
		if (subgroups != null && subgroups.size() != 0) {
			output.put("subgroups", subgroups);
		}
		if (permissions != null && permissions.size() != 0) {
			output.put("permissions", permissions);
		}
		if (info != null && info.size() != 0) {
			output.put("info", info);
		}
		if (globalPermissions != null && globalPermissions.size() != 0) {
			output.put("globalpermissions", globalPermissions);
		}
		return output;
	}
}
