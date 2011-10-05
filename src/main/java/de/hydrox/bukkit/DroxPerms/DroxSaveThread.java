package de.hydrox.bukkit.DroxPerms;

public class DroxSaveThread extends Thread {
	private DroxPerms plugin = null;
	
	public DroxSaveThread(DroxPerms plugin) {
		this.plugin = plugin;
	}
	
	public void run() {
		plugin.logger.info("[DroxPerms] Auto-saving changes.");
        plugin.dataProvider.save();
    }
}
