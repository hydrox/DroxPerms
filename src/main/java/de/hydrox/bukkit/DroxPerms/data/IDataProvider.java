package de.hydrox.bukkit.DroxPerms.data;

import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

/**
 * Interface for accessing and modifying Groups and Player. This includes
 * accessing and modifying the main Group of a Player, adding, removing
 * retrieving Sub-Groups of Players, adding and removing Permissions of Players
 * and Groups and calculating the final Player-Permissions.
 * 
 * @author Matthias Söhnholz
 * @since 0.1.0
 * 
 */
public interface IDataProvider {

	/**
	 * Creates a Player
	 * 
	 * @param name
	 *            name of the Player
	 * @return returns true if the Player could be created.
	 * @since 0.1.0
	 */
	boolean createPlayer(String name);

	/**
	 * Deletes a Player
	 * 
	 * @param name
	 *            name of the Player
	 * @return returns true if the Player could be deleted.
	 * @since 0.1.0
	 */
	boolean deletePlayer(CommandSender sender, String name);

	/**
	 * 
	 * @param sender
	 * @param name
	 *            name of the Group
	 * 
	 * @return returns true if the Group could be created.
	 * @since 0.1.0
	 */
	boolean createGroup(CommandSender sender, String name);

	/**
	 * Returns the main-group of the Player.
	 * 
	 * @param player
	 *            player to be queried.
	 * 
	 * @return main group of the Player, or "" if player doesn't exist
	 */
	String getPlayerGroup(String player);

	/**
	 * Sets the main-group of the Player.
	 * 
	 * @param sender
	 * @param player
	 *            player to be modified
	 * @param group
	 *            group to be set
	 * @return success of operation
	 */
	boolean setPlayerGroup(CommandSender sender, String player,
			String group);

	/**
	 * Returns the sub-groups of the Player.
	 * 
	 * @param player
	 *            Player to be queried
	 * @return ArrayList<String> containing the subgroups, null if Player doesn't exist
	 */
	List<String> getPlayerSubgroups(String player);

	/**
	 * Returns the sub-groups of the Player.
	 * 
	 * @param player
	 *            Player to be queried
	 * @return ArrayList<String> containing the subgroups, null if Player doesn't exist
	 */
	List<String> getPlayerSubgroupsSimple(String player);

	/**
	 * Adds a Sub-group to a Player.
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be added
	 * @return success of operation
	 */
	boolean addPlayerSubgroup(CommandSender sender, String player,
			String subgroup);

	/**
	 * Removes a Sub-group from a Player.
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be removed
	 * @return success of operation
	 */
	boolean removePlayerSubgroup(CommandSender sender, String player,
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
	 * @return success of operation
	 */
	boolean addPlayerPermission(CommandSender sender, String player,
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
	 * @return success of operation
	 */
	boolean removePlayerPermission(CommandSender sender, String player,
			String world, String node);

	/**
	 * Returns all Permissions for a Player from a given world. Set world to
	 * null for global permission.
	 * 
	 * @param player
	 *            Player to be queried.
	 * @param world
	 *            World to be queried.
	 * @return array of String containing the players permissions
	 */
	Map<String, List<String>> getPlayerPermissions(String player, String world, boolean partial);

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
	 * @return success of operation
	 */
	boolean setPlayerInfo(CommandSender sender, String player,
			String node, String data);

	/**
	 * Gets Data from the Players Info-node.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be queried
	 * @param node
	 *            Info-node to be read
	 * @return Info-node, is null if Player or Info-node doesn't exist
	 */
	String getPlayerInfo(CommandSender sender, String player, String node);

	/**
	 * Returns all Subgroups of a given Group.
	 * 
	 * @param group
	 *            Group to be queried
	 * @return ArrayList<String> containing the subgroups, null if Group doesn't exist
	 */
	List<String> getGroupSubgroups(String group);

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
	boolean addGroupSubgroup(CommandSender sender, String group,
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
	boolean removeGroupSubgroup(CommandSender sender, String group,
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
	boolean addGroupPermission(CommandSender sender, String group,
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
	boolean removeGroupPermission(CommandSender sender, String group,
			String world, String node);

	/**
	 * Returns all Permissions for a Group from a given world. Set world to null
	 * for global permission.
	 * 
	 * @param group
	 *            Group to be queried.
	 * @param world
	 *            World to be queried.
	 * @return array of String containing the Groups Permissions
	 */
	Map<String, List<String>> getGroupPermissions(String group, String world);

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
	 * @return success of operation
	 */
	boolean setGroupInfo(CommandSender sender, String group,
			String node, String data);

	/**
	 * Gets Data from the Groups Info-node.
	 * 
	 * @param sender
	 * @param group
	 *            Group to be queried
	 * @param node
	 *            Info-node to be read
	 * @return Info-node, is null if Player or Info-node doesn't exist
	 */
	String getGroupInfo(CommandSender sender, String group, String node);

	/**
	 * Promotes a Player along a given Track.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be promoted
	 * @param track
	 *            Track to be used
	 * @return true if Player has been promoted, false if not
	 */
	boolean promotePlayer(CommandSender sender, String player, String track);

	/**
	 * Demotes a Player along a given Track.
	 * 
	 * @param sender
	 * @param player
	 *            Player to be demoted
	 * @param track
	 *            Track to be used
	 * @return true if Player has been demoted, false if not
	 */
	boolean demotePlayer(CommandSender sender, String player, String track);

	//Tehbeard Start
	/**
	 * Sets up a player to be promoted for a specific length of time. 
	 * @param sender
	 * @param player player to promote
	 * @param track track to promote along
	 * @param time number of seconds to promote for
	 * 
	 * Should return data on current track and time left if an entry already exists
	 * e.g. royalty 54000 
	 * @return true if successful.
	 */
	boolean setTimedTrack(CommandSender sender,String player,String track,long time);

	/**
	 * Sets up a player to have a subgroup for a specific length of time
	 * @param sender
	 * @param player player to add subgroup to
	 * @param subgroup subgroup to add to
	 * @param time number of seconds to promote for
	 * @return true if successful.
	 */
	boolean addTimedSubgroup(CommandSender sender,String player,String subgroup,long time);


	/**
	 * Return track player is on
	 * @param sender
	 * @param player
	 * @return
	 */
	String getTimedTrack(CommandSender sender,String player);

	/**
	 * Return timestamp of them track expires
	 * @param sender
	 * @param player
	 * @return
	 */
	long getTimedTrackExpires(CommandSender sender,String player);

	/**
	 * return map of subgroup/expires
	 * @param sender
	 * @param player
	 * @return
	 */
	Map<String,Long> getTimedSubgroups(CommandSender sender,String player);

	/**
	 * Process player and demote as nessecary
	 * @param sender
	 * @param player player to process
	 * @return 
	 */
	boolean processTimes(CommandSender sender,String player);


	/**
	 * Cancels a timed track or subgroup
	 * @param sender
	 * @param player player to process
	 * @param group subgroup to cancel, use null to indicate track
	 * @return
	 */
	boolean cancelTimed(CommandSender sender,String player,String group);

	//Tehbeard End
	Map<String, List<String>> getGroupMembers();

	Map<String, List<String>> getSubgroupMembers();

	String getUserNameFromPart(String partialName);
	void save();
}
