package de.hydrox.bukkit.DroxPerms.data;

public class Configuration {
	private String dataProvider = null;
	
	public Configuration() {
		dataProvider = "FlatFile";
		// TODO Needs config-file reader
	}
	
	public String getDataProvider() {
		return dataProvider;
	}
}
