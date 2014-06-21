package de.hydrox.bukkit.DroxPerms.data.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import de.hydrox.bukkit.DroxPerms.DroxPerms;
import de.hydrox.bukkit.DroxPerms.data.APermissions;
import de.hydrox.bukkit.DroxPerms.data.ATrack;
import de.hydrox.bukkit.DroxPerms.data.AUser;
import de.hydrox.bukkit.DroxPerms.data.Config;
import de.hydrox.bukkit.DroxPerms.utils.uuid.MojangWebAPI;

public class SQLPermissions extends APermissions {

	public static final String NODE = "MySQL";

	protected Connection conn;

	private String host;
	private String database;
	protected static String tableprefix;
	private String username;
	private String password;
	private int port;
	protected Logger logger;

	//prepared statements
	private PreparedStatement prepGetAllPlayerNames;
	private PreparedStatement prepGetPlayerByUUID;
	private PreparedStatement prepGetExactPlayer;
	private PreparedStatement prepGetPartialPlayer;

	protected PreparedStatement prepGetLastPlayerName;

	protected PreparedStatement prepGetUserGroup;
	protected PreparedStatement prepGetUserSubgroups;
	protected PreparedStatement prepGetUserPermissions;
	protected PreparedStatement prepGetUserInfoComplete;
	protected PreparedStatement prepGetUserInfoNode;

	protected PreparedStatement prepSetUserGroup;
	protected PreparedStatement prepAddUserPermission;
	protected PreparedStatement prepAddUserInfoNode;
	protected PreparedStatement prepAddUserSubgroup;

	protected PreparedStatement prepRemoveUserPermission;
	protected PreparedStatement prepRemoveUserSubgroup;

	protected PreparedStatement prepGetAllGroups;

	protected PreparedStatement prepGetGroupSubgroups;
	protected PreparedStatement prepGetGroupPermissions;
	protected PreparedStatement prepGetGroupInfoComplete;
	protected PreparedStatement prepGetGroupInfoNode;

	protected PreparedStatement prepAddGroupPermission;
	protected PreparedStatement prepAddGroupInfoNode;
	protected PreparedStatement prepAddGroupSubgroup;

	protected PreparedStatement prepRemoveGroupPermission;
	protected PreparedStatement prepRemoveGroupSubgroup;

	protected PreparedStatement prepCreateGroup;
	protected PreparedStatement prepCreatePlayer;
	protected PreparedStatement prepDeletePlayer;

	protected PreparedStatement prepGetDemotionGroup;
	protected PreparedStatement prepGetPromotionGroup;

	public SQLPermissions(ConfigurationSection config, DroxPerms plugin) throws SQLException{
		APermissions.plugin = plugin;
		this.logger = plugin.getLogger();
		this.host = config.getString("host", "localhost");
		this.port = config.getInt("port",3306);
		this.database = config.getString("database");
		tableprefix = config.getString("prefix");
		this.username = config.getString("username");
		this.password = config.getString("password");
		try {
			Class.forName("com.mysql.jdbc.Driver");

			createConnection();

			checkAndMakeTable();
			prepareStatements();
			if(conn == null){
				throw new SQLException("Failed to start");
			}
		} catch (ClassNotFoundException e) {
			logger.severe("MySQL Library not found!");
		}
		
		PreparedStatement getWorldId = conn.prepareStatement("SELECT COUNT(worldID) FROM " + SQLPermissions.tableprefix + "worlds WHERE worldName = ?;");
		PreparedStatement addWorld = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "worlds (worldName) values (?);");
		for (String key : Config.getWorlds()) {
			getWorldId.clearParameters();
			getWorldId.setString(1, key);
			ResultSet rs = getWorldId.executeQuery();
			rs.next();
			int rowCount = rs.getInt(1);
			if (rowCount == 0) {
				logger.info("World " + key + " doesn't exists in Worldtable, inserting it now.");
				addWorld.clearParameters();
				addWorld.setString(1, key);
				addWorld.executeUpdate();
			} else {
				logger.info("World " + key + " exists in Worldtable.");
			}
		}

		try {
			ResultSet rs = prepGetAllGroups.executeQuery();
			while(rs.next()){
				String adding = rs.getString(2); 
				SQLGroup.addGroup(new SQLGroup(rs.getInt(1), rs.getString(2), this));
				logger.info("GROUPNAME: " + adding);
			}
			rs.close();

		} catch (SQLException e) {
			checkConnection();
			mysqlError(e);
		}

	}

	/**
	 * Connection to the database.
	 * @throws SQLException
	 */
	private void createConnection() {
		String conUrl = String.format("jdbc:mysql://%s:%s/%s",
				host,
				port,
				database);

		logger.info("Configuring....");
		Properties conStr = new Properties();
		conStr.put("user",username);
		conStr.put("password",password);
		conStr.put("autoReconnect", "true");

		logger.info("Connecting....");

		try {
			conn = DriverManager.getConnection(conUrl,conStr);

		} catch (SQLException e) {
			mysqlError(e);
			conn = null;
		}


	}

	protected synchronized Connection getConnection() {
		return conn;
	}
	
	/**
	 * 
	 * @return
	 */
	protected synchronized boolean checkConnection(){
//		logger.info("Checking connection");
		try {
			if(conn == null || !conn.isValid(0)){
				logger.info("Something is derp, rebooting connection.");
				createConnection();
				if(conn!=null){
					logger.info("Rebuilding statements");
					prepareStatements();
				}
				else
				{
					logger.info("Reboot failed!");
				}

			}
		} catch (SQLException e) {
			conn = null;
			return false;
		}
//		logger.info("Checking is " + conn != null ? "up" : "down");
		return conn != null;
	}

	protected void checkAndMakeTable(){
		try{
			ResultSet rs = conn.getMetaData().getTables(null, null, tableprefix + "groups", null);
			logger.info("Checking for table: " + tableprefix + "groups");
			if (!rs.next()) {
				logger.info(tableprefix + "groups" + " not found, creating tables");

				java.util.Scanner s = new java.util.Scanner(plugin.getResource("MySQL-Tables")).useDelimiter(";");
				while (s.hasNext()) {
					String query = s.next();
					query = query.replace("$PREFIX", tableprefix);
					if (query.startsWith("--")) {
						continue;
					}

					PreparedStatement ps = conn.prepareStatement(query);

					ps.executeUpdate();
					ps.close();
					
				}
				logger.info("created tables");
			}
			else
			{
				logger.info("Table found");
			}
			rs.close();
		} catch (SQLException e) {
			mysqlError(e);
		}
	}
	
	protected void prepareStatements(){
		try{
			logger.fine("Preparing statements");

			prepGetAllPlayerNames = conn.prepareStatement("SELECT playerName FROM " + tableprefix + "players"); //TODO returns uuids instead of names
			prepGetPlayerByUUID = conn.prepareStatement("SELECT playerID, playerName FROM " + tableprefix + "players WHERE playerName=?");
			prepGetExactPlayer = conn.prepareStatement("SELECT playerID, playerName, nodeData FROM " + tableprefix + "players NATURAL JOIN " + tableprefix + "playerInfoNodes WHERE nodeName = 'lastName' AND nodeData = ?");
			prepGetPartialPlayer = conn.prepareStatement("SELECT playerID, playerName, nodeData FROM " + tableprefix + "players NATURAL JOIN " + tableprefix + "playerInfoNodes WHERE nodeName = 'lastName' AND nodeData LIKE ?");
			prepGetLastPlayerName = conn.prepareStatement("SELECT nodeData FROM " + tableprefix + "playerInfoNodes WHERE playerID=? AND nodeName = 'lastName'");

			prepGetUserGroup = conn.prepareStatement("SELECT groupName FROM " + SQLPermissions.tableprefix + "groups AS groups JOIN " + SQLPermissions.tableprefix + "players AS players ON groups.groupID=players.groupID WHERE players.playerID=?");
			prepGetUserSubgroups = conn.prepareStatement("SELECT groupName FROM " + SQLPermissions.tableprefix + "groups AS groups JOIN " + SQLPermissions.tableprefix + "playerSubgroups AS subgroups ON subgroups.subgroupID=groups.groupID JOIN " + SQLPermissions.tableprefix + "players AS players ON players.playerID=subgroups.playerID WHERE players.playerID=?");
			prepGetUserPermissions = conn.prepareStatement("SELECT permissionNode, value FROM " + SQLPermissions.tableprefix + "playerPermissions AS permissions JOIN " + SQLPermissions.tableprefix + "players AS players ON players.playerID=permissions.playerID JOIN " + SQLPermissions.tableprefix + "worlds AS worlds ON worlds.worldID=permissions.worldID WHERE players.playerID=? AND worlds.worldName=?");
			prepGetUserInfoComplete = conn.prepareStatement("SELECT nodeName, nodeData FROM " + SQLPermissions.tableprefix + "playerInfoNodes AS infonodes JOIN " + SQLPermissions.tableprefix + "players AS players ON players.playerID=infonodes.playerID WHERE players.playerID=?");
			prepGetUserInfoNode = conn.prepareStatement("SELECT nodeData FROM " + SQLPermissions.tableprefix + "playerInfoNodes AS infonodes JOIN " + SQLPermissions.tableprefix + "players AS players ON players.playerID=infonodes.playerID WHERE players.playerID=? AND infonodes.nodeName=?");
			
			prepAddUserPermission = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "playerPermissions " +
					"(playerID, worldID, permissionNode, value) " +
					"values (?,(SELECT worldID FROM " + SQLPermissions.tableprefix + "worlds WHERE worldName = ?),?,?) ON DUPLICATE KEY UPDATE `value`=?;");
			prepAddUserInfoNode = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "playerInfoNodes" +
					"(playerID, nodeName, nodeData) values (?,?,?) ON DUPLICATE KEY UPDATE `nodeData`=?;");
			prepSetUserGroup = conn.prepareStatement("UPDATE " + SQLPermissions.tableprefix + "players SET groupID=(SELECT groupID FROM " + SQLPermissions.tableprefix + "groups WHERE groupName=?) WHERE playerID=?;");
			prepAddUserSubgroup = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "playerSubgroups (playerID,subgroupID) values (?,(SELECT groupID FROM " + SQLPermissions.tableprefix + "groups WHERE groupName=?));");

			prepRemoveUserPermission = conn.prepareStatement("DELETE FROM " + SQLPermissions.tableprefix + "playerPermissions WHERE playerID=? AND worldID=(SELECT worldID FROM " + SQLPermissions.tableprefix + "worlds WHERE worldName = ?) AND permissionNode=?;");
			prepRemoveUserSubgroup = conn.prepareStatement("DELETE FROM " + SQLPermissions.tableprefix + "playerSubgroups WHERE playerID=? AND subgroupID=(SELECT groupID FROM " + SQLPermissions.tableprefix + "groups WHERE groupName=?);");

			prepGetAllGroups = conn.prepareStatement("SELECT groupID, groupName FROM " + tableprefix + "groups");

			prepGetGroupSubgroups = conn.prepareStatement("SELECT groups.groupName FROM " + SQLPermissions.tableprefix + "groups AS groups JOIN " + SQLPermissions.tableprefix + "groupSubgroups AS subgroups ON subgroups.subgroupID=groups.groupID JOIN " + SQLPermissions.tableprefix + "groups AS groups2 ON groups2.groupID=subgroups.groupID WHERE groups2.groupID=?");
			prepGetGroupPermissions = conn.prepareStatement("SELECT permissionNode, value FROM " + SQLPermissions.tableprefix + "groupPermissions AS permissions JOIN " + SQLPermissions.tableprefix + "groups AS groups ON groups.groupID=permissions.groupID JOIN " + SQLPermissions.tableprefix + "worlds AS worlds ON worlds.worldID=permissions.worldID WHERE groups.groupID=? AND worlds.worldName=?");
			prepGetGroupInfoComplete = conn.prepareStatement("SELECT nodeName, nodeData FROM " + SQLPermissions.tableprefix + "groupInfoNodes AS infonodes JOIN " + SQLPermissions.tableprefix + "groups AS groups ON groups.groupID=infonodes.groupID WHERE groups.groupID=?");
			prepGetGroupInfoNode = conn.prepareStatement("SELECT nodeData FROM " + SQLPermissions.tableprefix + "groupInfoNodes AS infonodes JOIN " + SQLPermissions.tableprefix + "groups AS groups ON groups.groupID=infonodes.groupID WHERE groups.groupID=? AND infonodes.nodeName=?");

			prepAddGroupPermission = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "groupPermissions " +
					"(groupID, worldID, permissionNode, value) " +
					"values (?,(SELECT worldID FROM " + SQLPermissions.tableprefix + "worlds WHERE worldName = ?),?,?) ON DUPLICATE KEY UPDATE `value`=?;");
			prepAddGroupInfoNode = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "groupInfoNodes" +
					"(groupID, nodeName, nodeData) values (?,?,?) ON DUPLICATE KEY UPDATE `nodeData`=?;");
			prepAddGroupSubgroup = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "groupSubgroups (groupID,subgroupID) values (?,(SELECT groupID FROM " + SQLPermissions.tableprefix + "groups WHERE groupName=?));");

			prepRemoveGroupPermission = conn.prepareStatement("DELETE FROM " + SQLPermissions.tableprefix + "groupPermissions WHERE groupID=? AND worldID=(SELECT worldID FROM " + SQLPermissions.tableprefix + "worlds WHERE worldName = ?) AND permissionNode=?;");
			prepRemoveGroupSubgroup = conn.prepareStatement("DELETE FROM " + SQLPermissions.tableprefix + "groupSubgroups WHERE groupID=? AND subgroupID=(SELECT groupID FROM " + SQLPermissions.tableprefix + "groups WHERE groupName=?);");

			prepCreateGroup = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "groups" + "(groupName) values (?);",Statement.RETURN_GENERATED_KEYS);

			prepCreatePlayer = conn.prepareStatement("INSERT INTO " + SQLPermissions.tableprefix + "players" + "(playerName, groupID) values (?, ?);",Statement.RETURN_GENERATED_KEYS);

			prepDeletePlayer = conn.prepareStatement("DELETE FROM " + SQLPermissions.tableprefix + "players" + " WHERE playerName = ?;");

			prepGetDemotionGroup = conn.prepareStatement("SELECT groupName FROM " + tableprefix + "groups WHERE groupID = (SELECT lowerGroupID FROM " + tableprefix + "tracks WHERE trackName=? AND higherGroupID=?);");
			prepGetPromotionGroup = conn.prepareStatement("SELECT groupName FROM " + tableprefix + "groups WHERE groupID = (SELECT higherGroupID FROM " + tableprefix + "tracks WHERE trackName=? AND lowerGroupID=?);");

			logger.fine("Set player stat statement created");
			logger.info("Initaised MySQL Data Provider.");
		} catch (SQLException e) {
			mysqlError(e);
		}
	}

	public void save() {
	}

	@Override
	public boolean createPlayer(UUID uuid) {
		if(getUserByUUID(uuid) != null) {
			return false;
		} else {
			PreparedStatement prep = prepCreatePlayer;
			int num=0;
			try {
				SQLGroup group = (SQLGroup) SQLGroup.getGroup("default");
				prep.clearParameters();
				prep.setString(1, uuid.toString());
				prep.setInt(2, group.getID());
				num = prep.executeUpdate();
				if (num==1) {
					return true;
				}
			} catch (SQLException e) {
				if (e.getErrorCode()!=1062) {
					SQLPermissions.mysqlError(e);
				}
			}
			return false;
		}
	}

	public boolean deletePlayer(CommandSender sender, String name) {
		AUser user = getExactUserByName(name);
		if (user != null) {
			return deletePlayer(sender, user);
		}
		return false;
	}
	@Override
	public boolean deletePlayer(CommandSender sender, AUser user) {
		Player player = plugin.getServer().getPlayer(user.getUUID());
		if (player != null) {
			sender.sendMessage(ChatColor.RED + "Can't delete online Player.");
			return false;
		}
		if (user != null) {
			PreparedStatement prep = prepDeletePlayer;
			int num=0;
			try {
				prep.clearParameters();
				prep.setString(1, user.getUUID().toString());
				num = prep.executeUpdate();
				if (num==1) {
					sender.sendMessage(ChatColor.GREEN + "Deleted Player " + user.getName() + ".");
					return true;
				}
			} catch (SQLException e) {
					SQLPermissions.mysqlError(e);
			}
			return true;
		}
		sender.sendMessage(ChatColor.RED + "No Player with this exact name found.");

		return false;
	}

	@Override
	public boolean createGroup(CommandSender sender, String name) {
		if (SQLGroup.existGroup(name)) {
			return false;
		} else {
			PreparedStatement prep = prepCreateGroup;
			int num=0;
			try {
				prep.clearParameters();
				prep.setString(1, name);
				num = prep.executeUpdate();
				if (num==1) {
					ResultSet rs = prep.getGeneratedKeys();
					rs.next();
					SQLGroup.addGroup(new SQLGroup(rs.getInt(1), name, this));
					return true;
				}
			} catch (SQLException e) {
				if (e.getErrorCode()!=1062) {
					SQLPermissions.mysqlError(e);
				}
			}
			return false;
		}
	}

	@Override
	public AUser getExactUserByName(String name) {
		AUser player = null;
		try {
			prepGetExactPlayer.clearParameters();
			prepGetExactPlayer.setString(1, name);
			ResultSet rs = prepGetExactPlayer.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
//			logger.info("ROWS: " + numrows);
			if(numrows == 1) {
				rs.next();
				
//				String adding = rs.getString(2); 
//				logger.info("PLAYERNAME: " + adding);
				UUID uuid = UUID.fromString(rs.getString(2));
				player = new SQLUser(rs.getInt(1), uuid, rs.getString(3), this);
				rs.close();
				
			}

		} catch (SQLException e) {
			checkConnection();
			mysqlError(e);
		}
		return player;
	}

	@Override
	public AUser getUserByUUID(UUID uuid) {
		AUser player = null;
		try {
			prepGetPlayerByUUID.clearParameters();
			prepGetPlayerByUUID.setString(1, uuid.toString());
			ResultSet rs = prepGetExactPlayer.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
//			logger.info("ROWS: " + numrows);
			if(numrows == 1) {
				rs.next();
				
//				String adding = rs.getString(2); 
//				logger.info("PLAYERNAME: " + adding);
				player = new SQLUser(rs.getInt(1), uuid, this);
				rs.close();
				
			}

		} catch (SQLException e) {
			checkConnection();
			mysqlError(e);
		}
		return player;
	}

	@Override
	public AUser getPartialUserByName(String name) {
		AUser player = null;
		try {
			prepGetPartialPlayer.clearParameters();
			prepGetPartialPlayer.setString(1, name + "%");
			ResultSet rs = prepGetPartialPlayer.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
//			logger.info("ROWS: " + numrows);
			if(numrows == 1) {
				rs.next();
				
//				String adding = rs.getString(2); 
//				logger.info("PLAYERNAME: " + adding);
				UUID uuid = UUID.fromString(rs.getString(2));
				player = new SQLUser(rs.getInt(1), uuid, rs.getString(3), this);
				rs.close();
				
			} else if (numrows > 1) {
				while(rs.next()) {
					int id = rs.getInt(1);
					String username = rs.getString(3);
					if (username.equalsIgnoreCase(name)) {
						UUID uuid = UUID.fromString(rs.getString(2));
						player = new SQLUser(id, uuid, username, this);
						break;
					}
				}
				rs.close();

			}

		} catch (SQLException e) {
			checkConnection();
			mysqlError(e);
		}
		return player;
	}

	@Override
	public Set<String> getAllUserNames() {
		Set<String> set = new HashSet<String>();
		try {
			ResultSet rs = prepGetAllPlayerNames.executeQuery();
			while(rs.next()){
				String adding = rs.getString(1); 
				set.add(adding);
//				logger.info("PLAYERNAME: " + adding);
			}
			rs.close();

		} catch (SQLException e) {
			checkConnection();
			mysqlError(e);
		}
		return set;
	}

    /**
     * Show nicer error messages for mysql errors
     * @param e
     */
    public static void mysqlError(SQLException e){
    	Logger logger = plugin.getLogger();
        logger.severe("=========================================");
        logger.severe("              DATABASE ERROR             ");
        logger.severe("=========================================");
        logger.severe("An error occured while trying to connect to the DroxPerms database");
        logger.severe("Mysql error code: "+ e.getErrorCode());

        switch(e.getErrorCode()){
        case 1042:logger.severe("Cannot find hostname provided");break;
        case 1044:
        case 1045:logger.severe("Cannot connect to database, check user credentials, database exists and that user is able to log in from this remote machine");break;
        case 1049:logger.severe("Cannot locate database, check you spelt it correctly and username has access rights.");break;

        default:logger.severe("Error code not found, either check the error code online, or post on the dev.bukkit.org/server-mods/droxperms page");break; 
        }

        logger.severe("=========================================");
        logger.severe("            Begin error dump             ");
        logger.severe("=========================================");
        e.printStackTrace();
        logger.severe("=========================================");
        logger.severe("             End error dump              ");
        logger.severe("=========================================");

    }

	@Override
	public ATrack getTrack(String track) {
		return new SQLTrack(track, this);
	}
	
	public boolean migrateToNewerVersion() {
		FileConfiguration configuration = plugin.getConfig();
		String dbVersion = configuration.getString("DatabaseVersion", "0.5.1");
		
		if ("0.5.1".equals(dbVersion)) {
			logger.info("detected Storage version 0.5.1, update to 1.0.0");
			try {
				PreparedStatement prepGetPlayerNamesToMigrate = conn.prepareStatement("SELECT playerName,playerID FROM " + tableprefix + "players WHERE playerID NOT IN (SELECT playerID FROM " + tableprefix + "playerInfoNodes WHERE nodeName = 'lastName') ORDER BY playerID LIMIT 100;");
				//PreparedStatement prepGetPlayerNamesToMigrate = conn.prepareStatement("SELECT playerName,playerID FROM " + tableprefix + "players WHERE playerID NOT IN (SELECT playerID FROM " + tableprefix + "playerInfoNodes WHERE nodeName = 'lastName') AND playerID < 10 ORDER BY playerID LIMIT 100;");
				PreparedStatement prepMigratePlayer1= conn.prepareStatement("UPDATE " + tableprefix + "players SET playerName = ? WHERE playerID = ?;");
				Map<String, Integer> playerChunk = new HashMap<String, Integer>();
				Set<String> ignoreList = new HashSet<String>();
				do {
				ResultSet rs = prepGetPlayerNamesToMigrate.executeQuery();
				playerChunk.clear();
				while(rs.next()){
					String name = rs.getString(1); 
					int id = rs.getInt(2);
					if(ignoreList.contains(name)) { continue;}
					//logger.info(id + ": " + name);
					playerChunk.put(name, id);
				}
				ArrayList<String> playerNames = new ArrayList<String>(playerChunk.keySet());
				Map<String, UUID> resultFromMojang = MojangWebAPI.lookupUUIDS(playerNames);
				long startTime = System.currentTimeMillis();
				for (Iterator<String> iterator = playerNames.iterator(); iterator
						.hasNext();) {
					String name = iterator.next();
					UUID uuid = resultFromMojang.get(name);
					int id = playerChunk.get(name);
					//logger.info(uuid + ": " + name + " : " + id);
					if (uuid == null) {
						logger.info(uuid + ": " + name + " : " + id);
						if (MojangWebAPI.hasPaid(name)) {
							logger.info("'"+name + "' has paid, something is WRONG. DROP IT");
							//MojangWebAPI.lookupUUIDFallback(name);
							ignoreList.add(name);
							playerChunk.remove(name);
						} else {
							logger.info(name + " is no longer a valid account. DROP IT");
						}
						PreparedStatement prep = prepDeletePlayer;
						prep.clearParameters();
						prep.setString(1, name);
						prep.executeUpdate();
						continue;
					}
					
					prepMigratePlayer1.clearParameters();
					prepMigratePlayer1.setString(1, uuid.toString());
					prepMigratePlayer1.setInt(2, id);
					prepMigratePlayer1.addBatch();
					
					prepAddUserInfoNode.clearParameters();
					prepAddUserInfoNode.setInt(1, id);
					prepAddUserInfoNode.setString(2, "lastName");
					prepAddUserInfoNode.setString(3, name);
					prepAddUserInfoNode.setString(4, name);
					prepAddUserInfoNode.addBatch();
					//prepAddUserInfoNode.executeUpdate();
				}
				prepMigratePlayer1.executeBatch();
				prepAddUserInfoNode.executeBatch();
				
				logger.info("time: " + (System.currentTimeMillis() - startTime) + "ms.");
				rs.close();
				logger.info("PlayerChunk Size: " + playerChunk.size());
				} while (playerChunk.size() > 0);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			dbVersion = "1.0.0";
			logger.info("updated Storage to 1.0.0");
			configuration.set("DatabaseVersion", dbVersion);
			try {
				configuration.save(new File(plugin.getDataFolder() + File.separator + "config.yml"));
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		logger.info("Storage Migration complete");
		return true;
	}
}
