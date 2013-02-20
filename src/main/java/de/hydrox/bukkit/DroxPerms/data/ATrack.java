package de.hydrox.bukkit.DroxPerms.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public abstract class ATrack {
	private static Map<String, ATrack> tracks = new HashMap<String, ATrack>();

	private String name;
	private Map<String, String> mapping;

	public String getPromoteGroup(String before) {
		return mapping.get(before.toLowerCase());
	}
	
	public String getDemoteGroup(String before) {
		Set<String> keys = mapping.keySet();
		for (String key : keys) {
			if (mapping.get(key).equalsIgnoreCase(before)) {
				return key;
			}
		}
		return null;
	}
	
	public static boolean addTrack(ATrack track) {
		if (existTrack(track.name.toLowerCase())) {
			return false;
		}
		tracks.put(track.name.toLowerCase(), track);
		return true;
	}

	public static boolean removeTrack(String name) {
		if (existTrack(name.toLowerCase())) {
			tracks.remove(name.toLowerCase());
			return true;
		}
		return false;
	}

	public static ATrack getTrack(String name) {
		return tracks.get(name.toLowerCase());
	}

	public static boolean existTrack(String name) {
		if (tracks.containsKey(name.toLowerCase())) {
			return true;
		}
		return false;
	}

	public static void clearTracks() {
		tracks.clear();
	}
	
	public static Iterator<ATrack> iter() {
		return tracks.values().iterator();
	}

}
