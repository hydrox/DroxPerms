package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import de.hydrox.bukkit.DroxPerms.data.AUser;

public class User extends AUser{

	public User() {
		this("mydrox");
	}

	public User(String name) {
		this.name = name;
		this.group = "default";
		this.subgroups = new ArrayList<String>();
		this.globalPermissions = new ArrayList<String>();
		this.permissions = new LinkedHashMap<String, List<String>>();
		this.dirty = true;

		//Tehbeard Start
		this.timedTrack = null;
		this.timedTrackExpires = 0L;
		this.timedSubgroups = new HashMap<String, Long>();
		//Tehbeard End
	}

	public User(String name, ConfigurationSection node) {
		this.name = name;
		this.group = node.getString("group", "default");
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

		//Tehbeard Start
		this.timedTrack = node.getString("timedTrack",null);
		this.timedTrackExpires = node.getLong("timedTrackExpires", 0L);
		this.timedSubgroups = new HashMap<String, Long>();
		if(node.contains("timedSubgroups")){
			for(String e : node.getStringList("timedSubgroups")){
				String[] tg = e.split("\\:");
				if(tg.length != 2){
					throw new IllegalStateException(name + " Has an invalid timed Group entry [" + e + "] does not contain two values");
				}
				String subgroup = tg[0];
				long subgroupExpires = Long.parseLong(tg[1]);
				this.timedSubgroups.put(subgroup, subgroupExpires);
			}
		}
		//Tehbeard End


		this.dirty = false;
	}

	public Map<String, Object> toConfigurationNode() {
		Map<String, Object> output = new LinkedHashMap<String, Object>();
		output.put("group", group);
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


		//Tehbeard Start

		if(this.timedTrack !=null){output.put("timedTrack", timedTrack);}

		if(this.timedTrackExpires != 0L){output.put("timedTrackExpires", this.timedTrackExpires);}
		if(this.timedSubgroups.size() > 0){
			List<String> sg = new ArrayList<String>();
			for(Entry<String, Long> e : this.timedSubgroups.entrySet()){
				sg.add(e.getKey() + ":" + e.getValue());
			}
			output.put("timedSubgroups", sg);
		}
		//Tehbeard End

		return output;
	}
}
