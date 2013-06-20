package de.hydrox.bukkit.DroxPerms.data.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import de.hydrox.bukkit.DroxPerms.data.ATrack;

public class SQLTrack extends ATrack {
	
	private String name;
	private SQLPermissions provider;

	public SQLTrack(String name, SQLPermissions provider) {
		this.name = name;
		this.provider = provider;
		provider.logger.info("Trackname: " + name);
	}

	@Override
	public String getPromoteGroup(String before) {
		SQLGroup fromGroup = (SQLGroup) SQLGroup.getGroup(before);
		if(fromGroup == null) {
			return null;
		}
		String result = null;
		provider.checkConnection();
		PreparedStatement prep = provider.prepGetPromotionGroup;
		try {
			prep.clearParameters();
			prep.setString(1, name);
			prep.setInt(2, fromGroup.getID());
			ResultSet rs = prep.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
			if(numrows == 1) {
				rs.next();
				provider.logger.info("OLDGROUP: " + before + " NEWGROUP: " + rs.getString(1));
				result = rs.getString(1);
			}
			rs.close();
		} catch (SQLException e) {
			SQLPermissions.mysqlError(e);
		}
		return result;
	}

	@Override
	public String getDemoteGroup(String before) {
		SQLGroup fromGroup = (SQLGroup) SQLGroup.getGroup(before);
		if(fromGroup == null) {
			return null;
		}
		String result = null;
		provider.checkConnection();
		PreparedStatement prep = provider.prepGetDemotionGroup;
		try {
			prep.clearParameters();
			prep.setString(1, name);
			prep.setInt(2, fromGroup.getID());
			ResultSet rs = prep.executeQuery();
			rs.last(); int numrows = rs.getRow(); rs.beforeFirst();
			if(numrows == 1) {
				rs.next();
				provider.logger.info("OLDGROUP: " + before + " NEWGROUP: " + rs.getString(1));
				result = rs.getString(1);
			}
			rs.close();
		} catch (SQLException e) {
			SQLPermissions.mysqlError(e);
		}
		return result;
	}
}
