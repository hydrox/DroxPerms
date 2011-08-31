package de.hydrox.bukkit.DroxPerms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DroxTestCommands implements CommandExecutor {

    private DroxPerms plugin;

    public DroxTestCommands(DroxPerms plugin) {
        this.plugin = plugin;
    }
    
    @Override
	public boolean onCommand(CommandSender sender, Command command,
			String label, String[] args) {
		// TODO Auto-generated method stub
		return false;
	}

}
