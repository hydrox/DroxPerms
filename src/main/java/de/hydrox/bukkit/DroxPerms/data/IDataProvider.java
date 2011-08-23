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
	 * 
	 * @param sender
	 * @param name
	 *            name of the Group
	 * 
	 * @return returns true if the Group could be created.
	 */
	public boolean createGroup(CommandSender sender, String name);

	/**
	 * Returns the main-group of the Player.
	 * 
	 * @param player
	 *            player to be queried. sender
	 * 
	 * @return main group of the Player
	 */
	public String getPlayerGroup(String player);

	/**
	 * Sets the main-group of the Player.
	 * 
	 * @param sender
	 * @param player
	 *            player to be modified
	 * @param subgroup
	 *            group to be set
	 */
	public boolean setPlayerGroup(CommandSender sender, String player,
			String group);

	/**
	 * Returns the sub-groups of the Player.
	 * 
	 * @param player
	 *            player to be queried
	 * @return array of sub-groups
	 */
	public String[] getPlayerSubgroups(String player);

	/**
	 * Adds a Sub-group to a Player.
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be added
	 */
	public boolean addPlayerSubgroup(CommandSender sender, String player,
			String subgroup);

	/**
	 * Removes a Sub-group from a Player.
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be removed
	 */
	public boolean removePlayerSubgroup(CommandSender sender, String player,
			String subgroup);

	/**
	 * Adds a Permission to a Player. Set world to null for global permission.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be modified
	 * @param world
	 *            World the Permission belongs to
	 * @param node
	 *            Permission-node to add
	 * @return
	 */
	public boolean addPlayerPermission(CommandSender sender, String player,
			String world, String node);

	/**
	 * Removes a Permission from a Player. Set world to null for global
	 * permission.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be modified
	 * @param world
	 *            World the Permission belongs to
	 * @param node
	 *            Permission-node to remove
	 * @return
	 */
	public boolean removePlayerPermission(CommandSender sender, String player,
			String world, String node);

	/**
	 * Returns all Permissions for a Player from a given world. Set world to
	 * null for global permission.
	 * 
	 * @param player
	 *            Player to be queried.
	 * @param world
	 *            World to be queried.
	 * @return
	 */
	public String[] getPlayerPermissions(String player, String world);

	/**
	 * Sets Data in the Players Info-node.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be modified.
	 * @param node
	 *            Info-node to be set.
	 * @param data
	 *            Data to be written.
	 * @return
	 */
	public boolean setPlayerInfo(CommandSender sender, String player,
			String node, String data);

	/**
	 * Gets Data from the Players Info-node.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be queried
	 * @param node
	 *            Info-node to be read
	 * @return Info-node
	 */
	public String getPlayerInfo(CommandSender sender, String player, String node);

	/**
	 * Returns all Subgroups of a given Group.
	 * 
	 * @param group
	 *            Group to be queried
	 * @return Array of Subgroup-Strings
	 */
	public String[] getGroupSubgroups(String group);

	/**
	 * Adds a Subgroup to a given Group.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be modified
	 * @param subgroup
	 *            Subgroup to be added
	 * @return success of operation
	 */
	public boolean addGroupSubgroup(CommandSender sender, String group,
			String subgroup);

	/**
	 * Removes a Subgroup from a given Group.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be modified
	 * @param subgroup
	 *            Subgroup to be removed
	 * @return success of operation
	 */
	public boolean removeGroupSubgroup(CommandSender sender, String group,
			String subgroup);

	/**
	 * Adds a Permission to a Group. Set world to null for global permission.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be modified
	 * @param world
	 *            World the Permission belongs to
	 * @param node
	 *            Permission-node to add
	 * @return success of operation
	 */
	public boolean addGroupPermission(CommandSender sender, String group,
			String world, String node);

	/**
	 * Removes a Permission from a Group. Set world to null for global
	 * permission.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be modified
	 * @param world
	 *            World the Permission belongs to
	 * @param node
	 *            Permission-node to remove
	 * @return success of operation
	 */
	public boolean removeGroupPermission(CommandSender sender, String group,
			String world, String node);

	/**
	 * Returns all Permissions for a Group from a given world. Set world to null
	 * for global permission.
	 * 
	 * @param group
	 *            Group to be queried.
	 * @param world
	 *            World to be queried.
	 * @return
	 */
	public String[] getGroupPermissions(String group, String world);

	/**
	 * Sets Data in the Groups Info-node.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be modified.
	 * @param node
	 *            Info-node to be set.
	 * @param data
	 *            Data to be written.
	 * @return
	 */
	public boolean setGroupInfo(CommandSender sender, String group,
			String node, String data);

	/**
	 * Gets Data from the Groups Info-node.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be queried
	 * @param node
	 *            Info-node to be read
	 * @return Info-node
	 */
	public String getGroupInfo(CommandSender sender, String group, String node);

	public void save();
}
