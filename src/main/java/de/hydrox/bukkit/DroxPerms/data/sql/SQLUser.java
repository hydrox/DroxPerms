package de.hydrox.bukkit.DroxPerms.data.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import de.hydrox.bukkit.DroxPerms.data.AUser;

public class SQLUser extends AUser {
	
	private SQLPermissions provider= null;
	
	private int ID;
	private String name;
	
	public SQLUser(int ID, String name, SQLPermissions provider) {
		this.ID = ID;
		this.name = name;
		this.provider = provider;
		provider.logger.info("Username: " + name + " ID: " + ID);
	}

	@Override
	public boolean addPermission(String world, String permission) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean addSubgroup(String subgroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public String getGroup() {
		provider.checkConnection();
		PreparedStatement prep = provider.prepGetUserGroup;
		try {
			prep.clearParameters();
			prep.setInt(1, ID);
			ResultSet rs = prep.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
			provider.logger.info("ROWS: " + numrows);
			if(numrows == 1) {
				rs.next();
				
				String adding = rs.getString(1); 
				provider.logger.info("GROUPNAME: " + adding);
				rs.close();
				return adding;
			}
		} catch (SQLException e) {
			SQLPermissions.mysqlError(e);
		} 
		return null;
	}

	@Override
	public String getInfo(String node) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String, String> getInfoComplete() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, List<String>> getPermissions(String world) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public List<String> getSubgroups() {
		List<String> list = new ArrayList<String>();
		provider.checkConnection();
		PreparedStatement prep = provider.prepGetUserSubgroups;
		try {
			prep.clearParameters();
			prep.setInt(1, ID);
			ResultSet rs = prep.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
			provider.logger.info("ROWS: " + numrows);
			while(rs.next()){
				String adding = rs.getString(1); 
				list.add(adding);
				provider.logger.info("SUBGROUP FOR PLAYER " + name + ": " + adding);
			}
			rs.close();
		} catch (SQLException e) {
			provider.checkConnection();
			SQLPermissions.mysqlError(e);
		} 
		return list;
	}

	@Override
	public long getTimedSubgroupExpires(String subgroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String, Long> getTimedSubgroups() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public String getTimedTrack() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public long getTimedTrackExpires() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasTimedSubgroup(String subgroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removePermission(String world, String permission) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removeSubgroup(String subgroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setGroup(String newGroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setInfo(String node, String data) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setTimedSubgroup(String subgroup, long expires) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setTimedTrack(String track, long expires) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	private Map<String, Boolean> getWorldPermissions(String world) {
		HashMap<String, Boolean> result = new HashMap<String, Boolean>();
		provider.checkConnection();
		PreparedStatement prep = provider.prepGetUserPermissions;
		try {
			prep.clearParameters();
			prep.setInt(1, ID);
			prep.setString(2, world);
			ResultSet rs = prep.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
			provider.logger.info("ROWS: " + numrows);
			while(rs.next()) {
				
				provider.logger.info("PERMISSION: " + rs.getString(1) + " VALUE: " + rs.getBoolean(2));
				rs.close();
				result.put(rs.getString(1), rs.getBoolean(2));
			}
		} catch (SQLException e) {
			SQLPermissions.mysqlError(e);
		} 
		return result;
	}

}
