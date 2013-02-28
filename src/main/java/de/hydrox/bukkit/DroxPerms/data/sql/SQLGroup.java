package de.hydrox.bukkit.DroxPerms.data.sql;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import de.hydrox.bukkit.DroxPerms.data.AGroup;

public class SQLGroup extends AGroup {

	private SQLPermissions provider= null;
	
	private int ID;
	
	public SQLGroup(int ID, String name, SQLPermissions provider) {
		this.ID = ID;
		this.name = name;
		this.provider = provider;
		provider.logger.info("Groupname: " + name + " ID: " + ID);
	}

	@Override
	public Map<String, List<String>> getPermissions(String world) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean addPermission(String world, String permission) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removePermission(String world, String permission) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean addSubgroup(String subgroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean removeSubgroup(String subgroup) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean hasPermission(String world, String permission) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

	@Override
	public boolean setInfo(String node, String data) {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
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
	public List<String> getSubgroups() {
		throw new NotImplementedException();
		// TODO Auto-generated method stub
	}

}
