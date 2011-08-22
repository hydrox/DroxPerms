package de.hydrox.bukkit.DroxPerms;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;


public class DroxPermsAPI {
	DroxPerms plugin = null;
	FakeCommandSender fakeCS = null;
	
	public DroxPermsAPI(DroxPerms plugin) {
		this.plugin = plugin;
		this.fakeCS = new FakeCommandSender();
	}

	public String getPlayerGroup(String player) {
		return plugin.dataProvider.getPlayerGroup(player);
	}

	public boolean setPlayerGroup(String player, String group) {
		return plugin.dataProvider.setPlayerGroup(fakeCS, player, group);
	}

	public String[] getPlayerSubgroups(String player) {
		return plugin.dataProvider.getPlayerSubgroups(player);
	}

	public boolean addPlayerSubgroup(String player, String subgroup) {
		return plugin.dataProvider.addPlayerSubgroup(fakeCS, player, subgroup);
	}

	public boolean removePlayerSubgroup(String player, String subgroup) {
		return plugin.dataProvider.removePlayerSubgroup(fakeCS, player, subgroup);
	}

	public boolean addPlayerPermission(String player, String node) {
		return addPlayerPermission(player, null, node);
	}

	public boolean addPlayerPermission(String player, String world, String node) {
		return plugin.dataProvider.addPlayerPermission(fakeCS, player, world, node);
	}

	public boolean removePlayerPermission(String player, String node) {
		return removeGroupPermission(player, null, node);
	}

	public boolean removePlayerPermission(String player, String world,
			String node) {
		return plugin.dataProvider.removePlayerPermission(fakeCS, player, world, node);
	}

	public String getPlayerInfo(String player, String node) {
		return plugin.dataProvider.getPlayerInfo(fakeCS, player, node);
	}

	public boolean setPlayerInfo(String player, String node, String data) {
		return plugin.dataProvider.setPlayerInfo(fakeCS, player, node, data);
	}

	public String[] getGroupSubgroups(String group) {
		return plugin.dataProvider.getGroupSubgroups(group);
	}

	public boolean addGroupSubgroup(String group, String subgroup) {
		return plugin.dataProvider.addGroupSubgroup(fakeCS, group, subgroup);
	}

	public boolean removeGroupSubgroup(String group, String subgroup) {
		return plugin.dataProvider.removeGroupSubgroup(fakeCS, group, subgroup);
	}

	public boolean addGroupPermission(String group, String node) {
		return addGroupPermission(group, null, node);
	}

	public boolean addGroupPermission(String group, String world, String node) {
		return plugin.dataProvider.addGroupPermission(fakeCS, group, world, node);
	}

	public boolean removeGroupPermission(String group, String node) {
		return removeGroupPermission(group, null, node);
	}
	
	public boolean removeGroupPermission(String group, String world,
			String node) {
		return plugin.dataProvider.removeGroupPermission(fakeCS, group, world, node);
	}
	
	public String getGroupInfo(String group, String node) {
		return plugin.dataProvider.getGroupInfo(fakeCS, group, node);
	}

	public boolean setGroupInfo(String group, String node, String data) {
		return plugin.dataProvider.setGroupInfo(fakeCS, group, node, data);
	}
}

class FakeCommandSender implements CommandSender {

	public PermissionAttachment addAttachment(Plugin arg0) {
		return null;
	}

	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return null;
	}

	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2) {
		return null;
	}

	public PermissionAttachment addAttachment(Plugin arg0, String arg1,
			boolean arg2, int arg3) {
		return null;
	}

	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return null;
	}

	public boolean hasPermission(String arg0) {
		return true;
	}

	public boolean hasPermission(Permission arg0) {
		return true;
	}

	public boolean isPermissionSet(String arg0) {
		return false;
	}

	public boolean isPermissionSet(Permission arg0) {
		return false;
	}

	public void recalculatePermissions() {
	}

	public void removeAttachment(PermissionAttachment arg0) {
	}

	public boolean isOp() {
		return false;
	}

	public void setOp(boolean arg0) {
	}

	public Server getServer() {
		return null;
	}

	public void sendMessage(String arg0) {
	}
}
