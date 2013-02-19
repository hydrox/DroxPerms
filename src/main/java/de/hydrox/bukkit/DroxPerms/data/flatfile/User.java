package de.hydrox.bukkit.DroxPerms.data.flatfile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import de.hydrox.bukkit.DroxPerms.data.Config;

public class User {
	private static Map<String, User> users = new HashMap<String, User>();
	private static Map<String, User> backupUsers = new HashMap<String, User>();
	private static boolean testmode = false;

	private String name;
	private String group;
	private List<String> subgroups;
	private List<String> globalPermissions;
	private Map<String, List<String>> permissions;
	private Map<String, String> info;
	private boolean dirty;

	//Tehbeard Start
	private String timedTrack;
	private long timedTrackExpires;
	private Map<String,Long> timedSubgroups;
	//Tehbeard End

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

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
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

	public boolean isDirty() {
		return dirty;
	}

	public void clean() {
		dirty = false;
	}

	public void dirty() {
		dirty = true;
	}

	public Map<String, List<String>> getPermissions(String world) {
		Map<String, List<String>> result = new HashMap<String, List<String>>();
		List<String> groupperms = new ArrayList<String>();
		//add group permissions
		groupperms.add("droxperms.meta.group." + group);
		if (world != null) {
			groupperms.add("droxperms.meta.group." + group + "." + Config.getRealWorld(world));
		}
		result.put("group", groupperms);
		//add subgroup permissions
		if (subgroups != null) {
			ArrayList<String> subgroupperms = new ArrayList<String>();
			for (Iterator<String> iterator = subgroups.iterator(); iterator.hasNext();) {
				String subgroup = iterator.next();
				subgroupperms.add("droxperms.meta.group." + subgroup);
				if (world != null) {
					subgroupperms.add("droxperms.meta.group." + subgroup + "." + Config.getRealWorld(world));
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
			ArrayList<String> worldperms = new ArrayList<String>();
			if (permissions.get(Config.getRealWorld(world)) != null) {
				worldperms.addAll(permissions.get(Config.getRealWorld(world)));
			}
			result.put("world", worldperms);
		}
		return result;
	}

	public boolean setGroup(String newGroup) {
		if (Group.existGroup(newGroup)) {
			group = newGroup.toLowerCase();
			dirty = true;
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
			dirty = true;
			return true;
		}

		if (permissions == null) {
			permissions = new HashMap<String, List<String>>();
		}
		List<String> permArray = permissions.get(Config.getRealWorld(world).toLowerCase());
		if (permArray == null) {
			permArray = new ArrayList<String>();
			permissions.put(Config.getRealWorld(world).toLowerCase(), permArray);
		}
		if (permArray.contains(permission)) {
			return false;
		}
		permArray.add(permission);
		dirty = true;
		return true;
	}

	public boolean removePermission(String world, String permission) {
		if (world == null) {
			if (globalPermissions != null && globalPermissions.contains(permission)) {
				globalPermissions.remove(permission);
				dirty = true;
				return true;
			}
			return false;
		}

		if (permissions == null) {
			return false;
		}
		List<String> permArray = permissions.get(Config.getRealWorld(world).toLowerCase());
		if (permArray == null) {
			permArray = new ArrayList<String>();
			permissions.put(Config.getRealWorld(world).toLowerCase(), permArray);
		}
		if (permArray.contains(permission)) {
			permArray.remove(permission);
			dirty = true;
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
				dirty = true;
				return true;
			}
		}
		return false;
	}

	public boolean removeSubgroup(String subgroup) {
		if(subgroups != null && subgroups.contains(subgroup.toLowerCase())) {
			subgroups.remove(subgroup.toLowerCase());
			dirty = true;
			return true;
		}
		return false;
	}

	public boolean setInfo(String node, String data) {
		if (info == null) {
			info = new HashMap<String, String>();
		}
		if(data == null) {
			info.remove(node);
			dirty();
			return true;
		}
		info.put(node, data);
		dirty();
		return true;
	}

	//Tehbeard Start
	public boolean setTimedTrack(String track,long expires){
		if(track == null || Track.existTrack(track)){
			this.timedTrack = track;
			this.timedTrackExpires = expires;
			dirty();
			return true;
		}
		return false;
	}


	public String getTimedTrack(){
		return this.timedTrack;
	}

	public long getTimedTrackExpires(){
		return this.timedTrackExpires;
	}

	public boolean setTimedSubgroup(String subgroup,long expires){
		if(!Group.existGroup(subgroup)){return false;}
		if(expires <= 0L){
			this.timedSubgroups.remove(subgroup);
		}else{
			this.timedSubgroups.put(subgroup, expires);
		}
		dirty();
		return true;
	}


	public boolean hasTimedSubgroup(String subgroup){
		return this.timedSubgroups.containsKey(subgroup);
	}

	public long getTimedSubgroupExpires(String subgroup){
		if(hasTimedSubgroup(subgroup)){
			return this.timedSubgroups.get(subgroup);
		}
		return 0L;
	}

	public Map<String,Long> getTimedSubgroups(){
		return this.timedSubgroups;
	}

	//Tehbeard End

	public String getInfo(String node) {
		if (info == null) {
			return null;
		}
		return info.get(node);
	}

	public List<String> getSubgroups() {
		if (subgroups == null) {
			subgroups = new ArrayList<String>();
		}
		return subgroups;
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

	public static void setTestMode() {
		if (!testmode) {
			backupUsers = users;
			users = new HashMap<String, User>();
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
