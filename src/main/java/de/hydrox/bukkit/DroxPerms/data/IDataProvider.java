package de.hydrox.bukkit.DroxPerms.data;

import org.bukkit.command.CommandSender;

/**
 * Interface for accessing and modifying Groups and Player. This includes
 * accessing and modifying the main Group of a Player, adding, removing
 * retrieving Sub-Groups of Players, adding and removing Permissions of Players
 * and Groups and calculating the final Player-Permissions.
 * 
 * @author Matthias Söhnholz
 * 
 */
public interface IDataProvider {

	/**
	 * Creates a Player
	 * 
	 * @param name
	 *            name of the Player
	 * @return returns true if the Player could be created.
	 */
	public boolean createPlayer(String name);

	/**
	 * Creates a Group
	 * 
	 * @param name
	 *            name of the Group
	 * @return returns true if the Group could be created.
	 */
	public boolean createGroup(CommandSender sender, String name);
	
	/**
	 * Returns the main-group of the Player
	 * 
	 * @param player
	 *            player to be queried.
	 * @return main group of the Player
	 */
	public String getPlayerGroup(String player);

	/**
	 * Sets the main-group of the Player
	 * 
	 * @param player
	 *            player to be modified
	 * @param subgroup
	 *            group to be set
	 */
	public boolean setPlayerGroup(CommandSender sender, String player, String group);

	/**
	 * Returns the sub-groups of the Player
	 * 
	 * @param player
	 *            player to be queried
	 * @return array of sub-groups
	 */
	public String[] getPlayerSubgroups(String player);

	/**
	 * Adds a Sub-group to a Player
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be added
	 */
	public boolean addPlayerSubgroup(CommandSender sender, String player, String subgroup);

	/**
	 * Removes a Sub-group from a Player
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be removed
	 */
	public boolean removePlayerSubgroup(CommandSender sender, String player, String subgroup);

	public boolean addPlayerPermission(CommandSender sender, String player, String world, String node);

	public boolean removePlayerPermission(CommandSender sender, String player, String world,
			String node);

	public String[] getPlayerPermissions(String player, String world);

	public boolean setPlayerInfo(CommandSender sender, String player, String node, String data);

	public String getPlayerInfo(CommandSender sender, String player, String node);

	public String[] getGroupSubgroups(String group);

	public boolean addGroupSubgroup(CommandSender sender, String group, String subgroup);

	public boolean removeGroupSubgroup(CommandSender sender, String group, String subgroup);

	public boolean addGroupPermission(CommandSender sender, String group, String world, String node);

	public boolean removeGroupPermission(CommandSender sender, String group, String world,
			String node);

	public String[] getGroupPermissions(String group, String world);

	public boolean setGroupInfo(CommandSender sender, String group, String node, String data);

	public String getGroupInfo(CommandSender sender, String group, String node);

	public void save();
}
