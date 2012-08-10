package de.hydrox.bukkit.DroxPerms;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class DroxTimedCommands implements CommandExecutor {

	private DroxPerms plugin;

	public DroxTimedCommands(DroxPerms plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl,
			String[] args) {
		if (!(sender.hasPermission("droxperms.timed"))) {
			sender.sendMessage("You don't have permission to modify timed data.");
			return true;
		}

		if(args.length == 0 ){return false;}

		//Promote user along a track
		if (args[0].equalsIgnoreCase("promote") && args.length == 4) {
			String player = args[1];
			String track = args[2];
			if(!isModifySelf(sender,player)){sender.sendMessage("Cannot modify self");return true;}

			long time = timeString(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique username given or player not online?");
				return true;
			}

			if(plugin.dataProvider==null){sender.sendMessage("NULL DP");}
			if(!plugin.dataProvider.setTimedTrack(sender, player, track, time)){
				sender.sendMessage(ChatColor.RED + "Operation unsuccessful.");
			}
			plugin.refreshPlayer(Bukkit.getPlayer(player));

		}

		//add a timed subgroup to a user
		if (args[0].equalsIgnoreCase("addsub") && args.length == 4) {
			String player = args[1];
			String subgroup = args[2];
			if(!isModifySelf(sender,player)){sender.sendMessage("Cannot modify self");return true;}

			long time = timeString(args[3]);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + "Operation unsuccessfull. non-unique username given or player not online?");
				return true;
			}

			if(!plugin.dataProvider.addTimedSubgroup(sender, player, subgroup, time)){
				sender.sendMessage(ChatColor.RED + "Operation unsuccessful.");
			}
			plugin.refreshPlayer(Bukkit.getPlayer(player));

		}

		//
		if (args[0].equalsIgnoreCase("info") && args.length == 2) {
			String player = args[1];
			String track = plugin.dataProvider.getTimedTrack(sender, player);
			long expires = plugin.dataProvider.getTimedTrackExpires(sender, player);

			Map<String,Long> sg = plugin.dataProvider.getTimedSubgroups(sender, player);
			if(sg==null){sender.sendMessage(ChatColor.RED + "Operation unsuccessful");}

			if(track !=null){
				sender.sendMessage("TRACK " + track + " " + expires + " expires: " + new SimpleDateFormat().format(new Date(expires * 1000L)));
			}

			for(Entry<String,Long> e : sg.entrySet()){
				sender.sendMessage("SUBGROUP " + e.getKey() + " " + e.getValue() + " expires " + new SimpleDateFormat().format(new Date(e.getValue() * 1000L)));
			}


		}

		//process player times and demote as nessecary
		if (args[0].equalsIgnoreCase("checktimed") && args.length == 2) {
			String player = args[1];
			if(!plugin.dataProvider.processTimes(sender, player)){
				sender.sendMessage(ChatColor.RED + "Operation unsuccessful");
			}
			plugin.refreshPlayer(Bukkit.getPlayer(player));
			return true;
		}

		if(args[0].equalsIgnoreCase("canceltrack")){
			String player = args[1];
			if(!plugin.dataProvider.cancelTimed(sender, player,null)){
				sender.sendMessage(ChatColor.RED + "Operation unsuccessful");
			}
			plugin.refreshPlayer(Bukkit.getPlayer(player));
			return true;
		}

		if(args[0].equalsIgnoreCase("cancelsub")){
			String player = args[1];
			String subgroup = args[2];
			if(!plugin.dataProvider.cancelTimed(sender, player,subgroup)){
				sender.sendMessage(ChatColor.RED + "Operation unsuccessful");
			}
			plugin.refreshPlayer(Bukkit.getPlayer(player));
			return true;
		}

		return true;
	}

	/**
	 * Check if sender is the player to be modified, if so, check permission
	 * @param sender
	 * @param name
	 * @return true if user has permission to modify themselves/the target, false otherwise
	 */
	private boolean isModifySelf(CommandSender sender,String name){
		return sender instanceof Player ? ((Player)sender).getName().equals(name) ? ((Player)sender).hasPermission("droxperms.players.self") : true : true;


	}

	private long timeString(String time){
		long t = 0L;
		String current = "";
		for(char c : time.toLowerCase().toCharArray()){
			if(c >= '0' && c <= '9'){
				current += c;
			}

			switch(c){
			case 's': t += Long.parseLong(current);current="";break;
			case 'm': t += 60*Long.parseLong(current);current="";break;
			case 'h': t += 3600*Long.parseLong(current);current="";break;
			case 'd': t += 86400*Long.parseLong(current);current="";break;
			case 'w': t += 7*86400*Long.parseLong(current);current="";break;
			}
		}
		if(current.length() > 0){
			t += Long.parseLong(current);current="";
		}
		return t;
	}
}
