package de.hydrox.bukkit.DroxPerms.test;

import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import de.hydrox.bukkit.DroxPerms.data.flatfile.FlatFilePermissions;
import de.hydrox.bukkit.DroxPerms.data.flatfile.Group;
import de.hydrox.bukkit.DroxPerms.data.flatfile.User;

public class TestFlatFilePermissions extends TestClassTemplate{

	private FlatFilePermissions ffp;
	private FakeCommandSender fakeCS = null;

	protected void setUp() throws Exception {
		super.setUp();
		ffp = new FlatFilePermissions();
		this.fakeCS = new FakeCommandSender();
		Group.setTestMode();
		Group.clearGroups();
		User.setTestMode();
		User.clearUsers();
	}

	protected void tearDown(){
		Group.setNormalMode();
		User.setNormalMode();
	}

	public void testCreatePlayer() throws TestClassException {
		assertTrue(ffp.createPlayer("test1"));
		assertFalse(ffp.createPlayer("test1"));
		assertFalse(ffp.createPlayer("Test1"));
	}

	public void testCreateGroup() throws TestClassException {
		assertTrue(ffp.createGroup(fakeCS, "test1"));
		assertFalse(ffp.createGroup(fakeCS, "test1"));
		assertFalse(ffp.createGroup(fakeCS, "Test1"));
	}

	public void testGetSetPlayerGroup() throws TestClassException {
		assertFalse(ffp.setPlayerGroup(fakeCS, "test1", "test1"));
		ffp.createPlayer("test1");
		assertFalse(ffp.setPlayerGroup(fakeCS, "test1", "test1"));

		ffp.createGroup(fakeCS, "test1");
		assertEquals(ffp.getPlayerGroup("test1"), "default");
		assertTrue(ffp.setPlayerGroup(fakeCS, "test1", "test1"));
		assertEquals(ffp.getPlayerGroup("test1"), "test1");

		ffp.createPlayer("test2");
		assertEquals(ffp.getPlayerGroup("test2"), "default");
		assertEquals(ffp.getPlayerGroup("test1"), "test1");

		assertFalse(ffp.setPlayerGroup(fakeCS, "test2", "test2"));
		ffp.createGroup(fakeCS, "test2");
		assertTrue(ffp.setPlayerGroup(fakeCS, "test2", "test2"));
		assertEquals(ffp.getPlayerGroup("test2"), "test2");
		assertEquals(ffp.getPlayerGroup("test1"), "test1");
	}

	public void testGetPlayerSubgroups() throws TestClassException {
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.createGroup(fakeCS, "testsubgroup2");
		ffp.createGroup(fakeCS, "testsubgroup3");
		assertTrue(ffp.getPlayerSubgroups("test1") == null);
		ffp.createPlayer("test1");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 0);
		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup1");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubGroup1");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubGroup2");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);

		ffp.createPlayer("test2");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 0);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup1");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 1);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		ffp.addPlayerSubgroup(fakeCS, "test2", "testsubGroup1");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 1);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		ffp.addPlayerSubgroup(fakeCS, "test2", "testsubGroup3");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 2);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);

		String[] subgroups = ffp.getPlayerSubgroups("test1").toArray(new String[0]);
		assertTrue(subgroups[0].equals("testsubgroup1")
				|| subgroups[0].equals("testsubgroup2"));
		assertTrue(subgroups[1].equals("testsubgroup1")
				|| subgroups[1].equals("testsubgroup2"));

		subgroups = ffp.getPlayerSubgroups("test2").toArray(new String[0]);
		assertTrue(subgroups[0].equals("testsubgroup1")
				|| subgroups[0].equals("testsubgroup3"));
		assertTrue(subgroups[1].equals("testsubgroup1")
				|| subgroups[1].equals("testsubgroup3"));
	}

	public void testAddPlayerSubgroup() throws TestClassException {
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.createGroup(fakeCS, "testsubgroup2");
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test1", "testunknown"));
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup1"));

		ffp.createPlayer("test1");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 0);
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test1", "testunknown"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 0);
		assertTrue(ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup1"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup1"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgrOup1"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		assertTrue(ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup2"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);

		ffp.createPlayer("test2");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 0);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test2", "testunknown"));
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 0);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		assertTrue(ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 1);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 1);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		assertFalse(ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgrOup1"));
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 1);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		assertTrue(ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup2"));
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 2);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
	}

	public void testRemovePlayerSubgroup() throws TestClassException {
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.createGroup(fakeCS, "testsubgroup2");
		ffp.createGroup(fakeCS, "testsubgroup3");

		assertFalse(ffp.removePlayerSubgroup(fakeCS, "test1", "testunknown"));
		assertFalse(ffp.removePlayerSubgroup(fakeCS, "test1", "testsubgroup1"));

		ffp.createPlayer("test1");
		assertFalse(ffp.removePlayerSubgroup(fakeCS, "test1", "unknown"));
		assertFalse(ffp.removePlayerSubgroup(fakeCS, "test1", "testsubgroup1"));

		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup1");
		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup2");
		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup3");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 3);

		ffp.removePlayerSubgroup(fakeCS, "test1", "testsubgroup2");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 2);
		ffp.removePlayerSubgroup(fakeCS, "test1", "testsubgroup3");
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);

		ffp.createPlayer("test2");
		assertFalse(ffp.removePlayerSubgroup(fakeCS, "test2", "testunknown"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		assertFalse(ffp.removePlayerSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);

		ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup1");
		ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup2");
		ffp.addPlayerSubgroup(fakeCS, "test2", "testsubgroup3");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 3);

		ffp.removePlayerSubgroup(fakeCS, "test2", "testsubgroup2");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 2);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
		ffp.removePlayerSubgroup(fakeCS, "test2", "testsubgroup1");
		assertEquals(ffp.getPlayerSubgroups("test2").size(), 1);
		assertEquals(ffp.getPlayerSubgroups("test1").size(), 1);
	}

	public void testAddPlayerPermission() throws TestClassException {
		assertFalse(ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test1"));

		ffp.createPlayer("test1");
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 0);
		assertTrue(ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test1"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);
		assertFalse(ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test1"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);
		assertTrue(ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test2"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 2);

		ffp.createPlayer("test2");
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 0);
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 2);
		assertTrue(ffp.addPlayerPermission(fakeCS, "test2", "world", "test.test1"));
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 1);
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 2);
		assertFalse(ffp.addPlayerPermission(fakeCS, "test2", "world", "test.test1"));
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 1);
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 2);
		assertTrue(ffp.addPlayerPermission(fakeCS, "test2", "world", "test.test3"));
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 2);
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 2);
	}

	public void testRemovePlayerPermission() throws TestClassException {
		assertFalse(ffp.removePlayerPermission(fakeCS, "test1", "world", "testunknown"));
		assertFalse(ffp.removePlayerPermission(fakeCS, "test1", "world", "testsubgroup1"));

		ffp.createPlayer("test1");
		assertFalse(ffp.removePlayerPermission(fakeCS, "test1", "world", "testunknown"));
		assertFalse(ffp.removePlayerPermission(fakeCS, "test1", "world", "testsubgroup1"));

		ffp.addPlayerPermission(fakeCS, "test1", "world", "testsubgroup1");
		ffp.addPlayerPermission(fakeCS, "test1", "world", "testsubgroup2");
		ffp.addPlayerPermission(fakeCS, "test1", "world", "testsubgroup3");
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 3);

		assertTrue(ffp.removePlayerPermission(fakeCS, "test1", "world", "testsubgroup2"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 2);
		assertTrue(ffp.removePlayerPermission(fakeCS, "test1", "world", "testsubgroup3"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);

		ffp.createPlayer("test2");
		assertFalse(ffp.removePlayerPermission(fakeCS, "test2", "world", "testunknown"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);
		assertFalse(ffp.removePlayerPermission(fakeCS, "test2", "world", "testsubgroup1"));
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);

		ffp.addPlayerPermission(fakeCS, "test2", "world", "testsubgroup1");
		ffp.addPlayerPermission(fakeCS, "test2", "world", "testsubgroup2");
		ffp.addPlayerPermission(fakeCS, "test2", "world", "testsubgroup3");
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 3);

		ffp.removePlayerPermission(fakeCS, "test2", "world", "testsubgroup2");
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 2);
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);
		ffp.removePlayerPermission(fakeCS, "test2", "world", "testsubgroup1");
		assertEquals(ffp.getPlayerPermissions("test2", "world", false).get("world").toArray().length, 1);
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 1);
	}

	public void testGetPlayerPermissions() throws TestClassException {
		ffp.createPlayer("test1");
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 0);
		ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test1");
		ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test2");
		ffp.addPlayerPermission(fakeCS, "test1", "world", "test.test3");
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 3);
		ffp.addPlayerPermission(fakeCS, "test1", null, "test.global.test1");
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 3);
		assertEquals(ffp.getPlayerPermissions("test1", null, false).get("global").toArray().length, 1);
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.addPlayerSubgroup(fakeCS, "test1", "testsubgroup1");
		assertEquals(ffp.getPlayerPermissions("test1", "world", false).get("world").toArray().length, 3);
	}

	public void testAddGroupPermission() throws TestClassException {
		assertFalse(ffp.addGroupPermission(fakeCS, "test1", "world", "test.test1"));
		assertFalse(ffp.addGroupPermission(fakeCS, "test1", null, "test.test1"));

		ffp.createGroup(fakeCS, "test1");
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 0);
		assertTrue(ffp.addGroupPermission(fakeCS, "test1", "world", "test.test1"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);
		assertFalse(ffp.addGroupPermission(fakeCS, "test1", "world", "test.test1"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);
		assertTrue(ffp.addGroupPermission(fakeCS, "test1", "world", "test.test2"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 2);

		ffp.createGroup(fakeCS, "test2");
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 0);
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 2);
		assertTrue(ffp.addGroupPermission(fakeCS, "test2", "world", "test.test1"));
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 1);
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 2);
		assertFalse(ffp.addGroupPermission(fakeCS, "test2", "world", "test.test1"));
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 1);
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 2);
		assertTrue(ffp.addGroupPermission(fakeCS, "test2", "world", "test.test3"));
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 2);
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 2);

		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 0);
		assertTrue(ffp.addGroupPermission(fakeCS, "test1", null, "test.test1"));
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 1);
		assertFalse(ffp.addGroupPermission(fakeCS, "test1", null, "test.test1"));
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 1);
		assertTrue(ffp.addGroupPermission(fakeCS, "test1", null, "test.test2"));
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 2);

		assertEquals(ffp.getGroupPermissions("test2", null).get("global").toArray().length, 0);
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 2);
		assertTrue(ffp.addGroupPermission(fakeCS, "test2", null, "test.test1"));
		assertEquals(ffp.getGroupPermissions("test2", null).get("global").toArray().length, 1);
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 2);
		assertFalse(ffp.addGroupPermission(fakeCS, "test2", null, "test.test1"));
		assertEquals(ffp.getGroupPermissions("test2", null).get("global").toArray().length, 1);
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 2);
		assertTrue(ffp.addGroupPermission(fakeCS, "test2", null, "test.test3"));
		assertEquals(ffp.getGroupPermissions("test2", null).get("global").toArray().length, 2);
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 2);

	}

	public void testRemoveGroupPermission() throws TestClassException {
		assertFalse(ffp.removeGroupPermission(fakeCS, "test1", "world", "testunknown"));
		assertFalse(ffp.removeGroupPermission(fakeCS, "test1", "world", "testsubgroup1"));

		ffp.createGroup(fakeCS, "test1");
		assertFalse(ffp.removeGroupPermission(fakeCS, "test1", "world", "testunknown"));
		assertFalse(ffp.removeGroupPermission(fakeCS, "test1", "world", "testsubgroup1"));

		ffp.addGroupPermission(fakeCS, "test1", "world", "testsubgroup1");
		ffp.addGroupPermission(fakeCS, "test1", "world", "testsubgroup2");
		ffp.addGroupPermission(fakeCS, "test1", "world", "testsubgroup3");
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 3);

		assertTrue(ffp.removeGroupPermission(fakeCS, "test1", "world", "testsubgroup2"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 2);
		assertTrue(ffp.removeGroupPermission(fakeCS, "test1", "world", "testsubgroup3"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);

		ffp.createGroup(fakeCS, "test2");
		assertFalse(ffp.removeGroupPermission(fakeCS, "test2", "world", "testunknown"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);
		assertFalse(ffp.removeGroupPermission(fakeCS, "test2", "world", "testsubgroup1"));
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);

		ffp.addGroupPermission(fakeCS, "test2", "world", "testsubgroup1");
		ffp.addGroupPermission(fakeCS, "test2", "world", "testsubgroup2");
		ffp.addGroupPermission(fakeCS, "test2", "world", "testsubgroup3");
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 3);

		ffp.removeGroupPermission(fakeCS, "test2", "world", "testsubgroup2");
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 2);
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);
		ffp.removeGroupPermission(fakeCS, "test2", "world", "testsubgroup1");
		assertEquals(ffp.getGroupPermissions("test2", "world").get("world").toArray().length, 1);
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 1);
	}

	public void testGetGroupSubgroups() throws TestClassException {
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.createGroup(fakeCS, "testsubgroup2");
		ffp.createGroup(fakeCS, "testsubgroup3");
		assertTrue(ffp.getGroupSubgroups("test1") == null);
		ffp.createGroup(fakeCS, "test1");
		assertEquals(ffp.getGroupSubgroups("test1").size(), 0);
		ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup1");
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		ffp.addGroupSubgroup(fakeCS, "test1", "testsubGroup1");
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		ffp.addGroupSubgroup(fakeCS, "test1", "testsubGroup2");
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);

		ffp.createGroup(fakeCS, "test2");
		assertEquals(ffp.getGroupSubgroups("test2").size(), 0);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup1");
		assertEquals(ffp.getGroupSubgroups("test2").size(), 1);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		ffp.addGroupSubgroup(fakeCS, "test2", "testsubGroup1");
		assertEquals(ffp.getGroupSubgroups("test2").size(), 1);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		ffp.addGroupSubgroup(fakeCS, "test2", "testsubGroup3");
		assertEquals(ffp.getGroupSubgroups("test2").size(), 2);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);

		String[] subgroups = ffp.getGroupSubgroups("test1").toArray(new String[0]);
		assertTrue(subgroups[0].equals("testsubgroup1")
				|| subgroups[0].equals("testsubgroup2"));
		assertTrue(subgroups[1].equals("testsubgroup1")
				|| subgroups[1].equals("testsubgroup2"));

		subgroups = ffp.getGroupSubgroups("test2").toArray(new String[0]);
		assertTrue(subgroups[0].equals("testsubgroup1")
				|| subgroups[0].equals("testsubgroup3"));
		assertTrue(subgroups[1].equals("testsubgroup1")
				|| subgroups[1].equals("testsubgroup3"));
	}

	public void testAddGroupSubgroup() throws TestClassException {
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.createGroup(fakeCS, "testsubgroup2");
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test1", "testunknown"));
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup1"));

		ffp.createGroup(fakeCS, "test1");
		assertEquals(ffp.getGroupSubgroups("test1").size(), 0);
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test1", "testunknown"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 0);
		assertTrue(ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup1"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup1"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test1", "testsubgrOup1"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		assertTrue(ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup2"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);

		ffp.createGroup(fakeCS, "test2");
		assertEquals(ffp.getGroupSubgroups("test2").size(), 0);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test2", "testunknown"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 0);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		assertTrue(ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 1);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 1);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		assertFalse(ffp.addGroupSubgroup(fakeCS, "test2", "testsubgrOup1"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 1);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		assertTrue(ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup2"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 2);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
	}

	public void testRemoveGroupSubgroup() throws TestClassException {
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.createGroup(fakeCS, "testsubgroup2");
		ffp.createGroup(fakeCS, "testsubgroup3");

		assertFalse(ffp.removeGroupSubgroup(fakeCS, "test1", "testunknown"));
		assertFalse(ffp.removeGroupSubgroup(fakeCS, "test1", "testsubgroup1"));

		ffp.createGroup(fakeCS, "test1");
		assertFalse(ffp.removeGroupSubgroup(fakeCS, "test1", "unknown"));
		assertFalse(ffp.removeGroupSubgroup(fakeCS, "test1", "testsubgroup1"));

		ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup1");
		ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup2");
		ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup3");
		assertEquals(ffp.getGroupSubgroups("test1").size(), 3);

		assertTrue(ffp.removeGroupSubgroup(fakeCS, "test1", "testsubgroup2"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 2);
		assertTrue(ffp.removeGroupSubgroup(fakeCS, "test1", "testsubgroup3"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);

		ffp.createGroup(fakeCS, "test2");
		assertFalse(ffp.removeGroupSubgroup(fakeCS, "test2", "testunknown"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		assertFalse(ffp.removeGroupSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);

		ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup1");
		ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup2");
		ffp.addGroupSubgroup(fakeCS, "test2", "testsubgroup3");
		assertEquals(ffp.getGroupSubgroups("test2").size(), 3);

		assertTrue(ffp.removeGroupSubgroup(fakeCS, "test2", "testsubgroup2"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 2);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
		assertTrue(ffp.removeGroupSubgroup(fakeCS, "test2", "testsubgroup1"));
		assertEquals(ffp.getGroupSubgroups("test2").size(), 1);
		assertEquals(ffp.getGroupSubgroups("test1").size(), 1);
	}

	public void testGetGroupPermissions() throws TestClassException {
		ffp.createGroup(fakeCS, "test1");
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 0);
		ffp.addGroupPermission(fakeCS, "test1", "world", "test.test1");
		ffp.addGroupPermission(fakeCS, "test1", "world", "test.test2");
		ffp.addGroupPermission(fakeCS, "test1", "world", "test.test3");
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 3);
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 0);
		ffp.addGroupPermission(fakeCS, "test1", null, "test.global.test1");
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 3);
		assertEquals(ffp.getGroupPermissions("test1", null).get("global").toArray().length, 1);
		ffp.createGroup(fakeCS, "testsubgroup1");
		ffp.addGroupSubgroup(fakeCS, "test1", "testsubgroup1");
		assertEquals(ffp.getGroupPermissions("test1", "world").get("world").toArray().length, 3);
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
		Logger.getLogger("Minecraft").info("[DroxPerms] TestSuite: "+arg0);
	}

	public void sendMessage(String[] arg0) {
	}

	public String getName() {
		return null;
	}
}
