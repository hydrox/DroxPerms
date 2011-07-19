package de.hydrox.bukkit.DroxPerms.data;

/**
 * Interface for accessing and modifying Groups and Player. This includes
 * accessing and modifying the main Group of a Player, adding, removing
 * retrieving Sub-Groups of Players, adding and removing Permissions of
 * Players and Groups and calculating the final Player-Permissions.
 * 
 * @author Matthias Söhnholz
 * 
 */
public interface IDataProvider {

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
	public void setPlayerGroup(String player, String subgroup);

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
	public void addPlayerSubgroup(String player, String subgroup);

	/**
	 * Removes a Sub-group from a Player
	 * 
	 * @param player
	 *            Player to be modified
	 * @param subgroup
	 *            sub-group to be removed
	 */
	public void removePlayerSubgroup(String player, String subgroup);
	
	public void addPlayerPermission(String player, String world, String node);
	
	public void removePlayerPermission(String player, String world, String node);
	
	public void setGroupSubgroup(String group, String subgroup);

	public String[] getGroupSubgroups(String group);

	public void addGroupSubgroup(String group, String subgroup);

	public void removeGroupSubgroup(String group, String subgroup);

	public void addGroupPermission(String player, String world, String node);
	
	public void removeGroupPermission(String player, String world, String node);
	
	public String[] getPlayerPermissions(String player, String world);
}
