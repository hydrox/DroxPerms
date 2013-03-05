package de.hydrox.bukkit.DroxPerms;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import de.hydrox.bukkit.DroxPerms.data.Config;
import de.hydrox.bukkit.DroxPerms.test.TestClassTemplate;
import de.hydrox.bukkit.DroxPerms.test.TestFlatFilePermissions;

public class DroxTestCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender.hasPermission("droxperms.test"))) {
			sender.sendMessage("You don't have permission to run the TestSuite.");
			return true;
		}
		if(Config.getDataProvider().equals("FlatFile")){
			TestClassTemplate.runTests(new TestFlatFilePermissions());
		} else {
			sender.sendMessage("No Tests for DataProvider " + Config.getDataProvider());
		}
		return true;
	}
}
