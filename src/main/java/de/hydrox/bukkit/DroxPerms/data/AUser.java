package de.hydrox.bukkit.DroxPerms.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

public class AUser {
	protected static Map<String, AUser> users = new HashMap<String, AUser>();
	protected static Map<String, AUser> backupUsers = new HashMap<String, AUser>();
	protected static boolean testmode = false;

	protected String name;
	protected String group;
	protected List<String> subgroups;
	protected List<String> globalPermissions;
	protected Map<String, List<String>> permissions;
	protected Map<String, String> info;
	protected boolean dirty;

	//Tehbeard Start
	protected String timedTrack;
	protected long timedTrackExpires;
	protected Map<String,Long> timedSubgroups;
	//Tehbeard End

	public String getName() {
		return name;
	}

	public String getGroup() {
		return group;
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
		if (AGroup.existGroup(newGroup)) {
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
		if(AGroup.existGroup(subgroup.toLowerCase())) {
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
		if(track == null || ATrack.existTrack(track)){
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
		if(!AGroup.existGroup(subgroup)){return false;}
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

	public Map<String, String> getInfoComplete() {
		if (info == null) {
			return null;
		}
		return new HashMap<String, String>(info);
	}

	public List<String> getSubgroups() {
		if (subgroups == null) {
			subgroups = new ArrayList<String>();
		}
		return subgroups;
	}

	public static boolean addUser(AUser user) {
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

	public static AUser getUser(String name) {
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

	public static Iterator<AUser> iter() {
		return users.values().iterator();
	}

	public static void setTestMode() {
		if (!testmode) {
			backupUsers = users;
			users = new HashMap<String, AUser>();
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
