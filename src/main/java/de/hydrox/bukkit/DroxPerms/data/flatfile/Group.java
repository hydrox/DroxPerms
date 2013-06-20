package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import de.hydrox.bukkit.DroxPerms.data.AGroup;
import de.hydrox.bukkit.DroxPerms.data.Config;

public class Group extends AGroup{
	private Map<String, Map<String, Boolean>> permissions;
	private Map<String, String> info;
	private Map<String, Boolean> globalPermissions;
	private List<String> subgroups;

	private Map<String, Permission> bukkitPermissions;

	public Group() {
		this("default");
	}

	public Group(String name) {
		this.name = name;
		this.subgroups = new ArrayList<String>();
		this.globalPermissions = new LinkedHashMap<String, Boolean>();
		this.permissions = new LinkedHashMap<String, Map<String, Boolean>>();
	}

	public Group(String name, ConfigurationSection node) {
		this.name = name;
		this.subgroups = node.getStringList("subgroups");
		this.globalPermissions = fileFormatToInternal(node.getStringList("globalpermissions"));
		if (this.globalPermissions == null) {
			this.globalPermissions = new LinkedHashMap<String, Boolean>();
		}
		this.permissions = new LinkedHashMap<String, Map<String, Boolean>>();
		if (node.contains("permissions")) {
			Set<String> worlds = node.getConfigurationSection("permissions.").getKeys(false);
			for (String world : worlds) {
				permissions.put(world, fileFormatToInternal(node.getStringList("permissions." + world)));
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

	private Map<String, Boolean> fileFormatToInternal(List<String> list){
		if (list == null) {
			return null;
		}
		Map<String, Boolean> result = new LinkedHashMap<String, Boolean>();
		for (String string : list) {
			boolean value = true;
			if (string.startsWith("-")) {
				string = string.substring(1);
				value = false;
			}
			result.put(string, value);
		}
		return result;
	}

	private List<String> internalFormatToFile(Map<String, Boolean> map){
		if (map == null) {
			return null;
		}
		List<String> result = new ArrayList<String>();
		for (String string : map.keySet()) {
			if(!map.get(string)){
				string = "-"+string;
			}
			result.add(string);
		}
		return result;
	}

	public Map<String, Object> toConfigurationNode() {
		Map<String, Object> output = new HashMap<String, Object>();
		if (subgroups != null && subgroups.size() != 0) {
			output.put("subgroups", subgroups);
		}
		if (permissions != null && permissions.size() != 0) {
			Map<String, List<String>> tmp = new LinkedHashMap<String, List<String>>(); 
			for (String world : permissions.keySet()) {
				tmp.put(world, internalFormatToFile(permissions.get(world)));
			}
			output.put("permissions", tmp);
		}
		if (info != null && info.size() != 0) {
			output.put("info", info);
		}
		if (globalPermissions != null && globalPermissions.size() != 0) {
			output.put("globalpermissions", internalFormatToFile(globalPermissions));
		}
		return output;
	}

	public Map<String, Map<String, Boolean>> getPermissions(String world) {
		Map<String, Map<String, Boolean>> result = new LinkedHashMap<String, Map<String, Boolean>>();
		Map<String, Boolean> groupperms = new LinkedHashMap<String, Boolean>();
		//add group permissions
		groupperms.put("droxperms.meta.group." + name, true);
		if (world != null) {
			groupperms.put("droxperms.meta.group." + name + "." + Config.getRealWorld(world), true);
		}
		result.put("group", groupperms);
		//add subgroup permissions
		if (subgroups != null) {
			Map<String, Boolean> subgroupperms = new LinkedHashMap<String, Boolean>();
			for (Iterator<String> iterator = subgroups.iterator(); iterator.hasNext();) {
				String subgroup = iterator.next();
				subgroupperms.put("droxperms.meta.group." + subgroup, true);
				if (world != null) {
					subgroupperms.put("droxperms.meta.group." + subgroup + "." + Config.getRealWorld(world), true);
				}
			}
			result.put("subgroups", subgroupperms);
		}
		//add global permissions
		if (globalPermissions != null) {
			result.put("global", globalPermissions);
		}
		//add world permissions
		if (world != null && permissions != null) {
			Map<String, Boolean> worldperms = new LinkedHashMap<String, Boolean>();
			if (permissions.get(Config.getRealWorld(world)) != null) {
				worldperms.putAll(permissions.get(Config.getRealWorld(world)));
			}
			result.put("world", worldperms);
		}
		return result;
	}

	public boolean addPermission(String world, String permission) {
		boolean value = true;
		if (permission.startsWith("-")) {
			permission = permission.substring(1);
			value = false;
		}
		if (world == null) {
			if (globalPermissions == null) {
				globalPermissions = new LinkedHashMap<String, Boolean>();
			}
			if (globalPermissions.containsKey(permission)) {
				return false;
			}
			globalPermissions.put(permission, value);
			updatePermissions();
			return true;
		}

		Map<String, Boolean> permArray = permissions.get(Config.getRealWorld(world).toLowerCase());
		if (permArray == null) {
			permArray = new LinkedHashMap<String, Boolean>();
			permissions.put(Config.getRealWorld(world).toLowerCase(), permArray);
		}
		if (permArray.containsKey(permission)) {
			return false;
		}
		permArray.put(permission, value);
		updatePermissions();
		return true;
	}

	public boolean removePermission(String world, String permission) {
		if (world == null) {
			if (globalPermissions.containsKey(permission)) {
				globalPermissions.remove(permission);
				updatePermissions();
				return true;
			}
			return false;
		}

		Map<String, Boolean> permArray = permissions.get(Config.getRealWorld(world).toLowerCase());
		if (permArray == null) {
			permArray = new LinkedHashMap<String, Boolean>();
			permissions.put(Config.getRealWorld(world).toLowerCase(), permArray);
		}
		if (permArray.containsKey(permission)) {
			permArray.remove(permission);
			updatePermissions();
			return true;
		}
		return false;
	}

	public boolean addSubgroup(String subgroup) {
		if(Group.existGroup(subgroup.toLowerCase())) {
			if (subgroups == null) {
				subgroups = new ArrayList<String>();
			}
			if(!subgroups.contains(subgroup.toLowerCase())) {
				subgroups.add(subgroup.toLowerCase());
				updatePermissions();
				return true;
			}
		}
		return false;
	}

	public boolean removeSubgroup(String subgroup) {
		if(subgroups != null && subgroups.contains(subgroup.toLowerCase())) {
			subgroups.remove(subgroup.toLowerCase());
			updatePermissions();
			return true;
		}
		return false;
	}

	public boolean hasPermission(String world, String permission) {
		Map<String, Boolean> permArray = permissions.get(world.toLowerCase());
		if (permArray != null && permArray.containsKey(permission)) {
			return permArray.get(permission);
		}

		for (String subgroup : subgroups) {
			if (Group.getGroup(subgroup) != null &&
					Group.getGroup(subgroup).hasPermission(world.toLowerCase(), permission)) {
				return true;
			}
		}
		return false;
	}

	public boolean setInfo(String node, String data) {
		if (info == null) {
			info = new HashMap<String, String>();
		}
		if(data == null) {
			info.remove(node);
			return true;
		}
		info.put(node, data);
		return true;
	}

	public String getInfo(String node) {
		if (info == null) {
			return null;
		}
		return info.get(node);
	}

	public Map<String, String> getInfoComplete() {
		if (info == null) {
			return null;
		}
		return new HashMap<String, String>(info);
	}

	public boolean addWorld(String world) {
		if (permissions.containsKey(world.toLowerCase())) {
			return false;
		}
		permissions.put(world.toLowerCase(), new LinkedHashMap<String, Boolean>());
		updatePermissions();
		return true;
	}

	public List<String> getSubgroups() {
		if (subgroups == null) {
			subgroups = new ArrayList<String>();
		}
		return subgroups;
	}

	private void updatePermissions() {
		bukkitPermissions = new LinkedHashMap<String, Permission>();
		//create Permission for default world
		if (subgroups == null) {
			subgroups = new ArrayList<String>();
		}
		if (!permissions.containsKey(Config.getDefaultWorld())) {
			Map<String, Boolean> children = new LinkedHashMap<String, Boolean>();
			for (String subgroup : subgroups) {
				children.put("droxperms.meta.group." + subgroup + "." + Config.getDefaultWorld(), true);
			}
			children.put("droxperms.meta.group." + name, true);

			Permission permission = new Permission("droxperms.meta.group." + name + "." + Config.getDefaultWorld(), "Group-Permissions for group " + name + " on world " + Config.getDefaultWorld(), PermissionDefault.FALSE, children);
			Bukkit.getPluginManager().removePermission(permission);
			Bukkit.getPluginManager().addPermission(permission);
			bukkitPermissions.put(Config.getDefaultWorld(), permission);
		}

		//create Permissions for other worlds
		for (String world : Config.getWorlds()) {
			Map<String, Boolean> children = new LinkedHashMap<String, Boolean>();
			for (String subgroup : subgroups) {
				children.put("droxperms.meta.group." + subgroup + "." + world, true);
			}

			children.put("droxperms.meta.group." + name, true);

			if(permissions.get(world) != null) {
				children.putAll(permissions.get(world));
			}

			Permission permission = new Permission("droxperms.meta.group." + name + "." + world, "Group-Permissions for group " + name + " on world " + world, PermissionDefault.FALSE, children);
			Bukkit.getPluginManager().removePermission(permission);
			Bukkit.getPluginManager().addPermission(permission);
			bukkitPermissions.put(world, permission);
		}

		Map<String, Boolean> children = new LinkedHashMap<String, Boolean>();
		for (String subgroup : subgroups) {
			children.put("droxperms.meta.group." + subgroup, true);
		}

		children.putAll(globalPermissions);

		//create Permission for global grouppermissions
		Permission permission = new Permission("droxperms.meta.group." + name, "Group-Permissions for group " + name, PermissionDefault.FALSE, children);
		Bukkit.getPluginManager().removePermission(permission);
		Bukkit.getPluginManager().addPermission(permission);
	}
}
