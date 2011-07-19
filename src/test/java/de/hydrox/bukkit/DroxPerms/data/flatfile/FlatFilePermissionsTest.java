package de.hydrox.bukkit.DroxPerms.data.flatfile;

import junit.framework.TestCase;

public class FlatFilePermissionsTest extends TestCase {
	
	private FlatFilePermissions ffp;
	
	protected void setUp() throws Exception {
		super.setUp();
		ffp = new FlatFilePermissions();
		Group.clearGroups();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testCreatePlayer() {
		assertTrue(ffp.createPlayer("test1"));
		assertFalse(ffp.createPlayer("test1"));
		assertTrue(ffp.createPlayer("Test1"));
		assertFalse(ffp.createPlayer("Test1"));
	}

	public void testCreateGroup() {
		assertTrue(ffp.createGroup("test1"));
		assertFalse(ffp.createGroup("test1"));
		assertFalse(ffp.createGroup("Test1"));
	}

	public void testGetSetPlayerGroup() {
		assertFalse(ffp.setPlayerGroup("test1", "test1"));
		ffp.createPlayer("test1");
		assertFalse(ffp.setPlayerGroup("test1", "test1"));
		
		ffp.createGroup("test1");
		assertEquals(ffp.getPlayerGroup("test1"),"default");
		assertTrue(ffp.setPlayerGroup("test1", "test1"));
		assertEquals(ffp.getPlayerGroup("test1"),"test1");

		ffp.createPlayer("test2");
		assertEquals(ffp.getPlayerGroup("test2"),"default");
		assertEquals(ffp.getPlayerGroup("test1"),"test1");
		
		assertFalse(ffp.setPlayerGroup("test2", "test2"));
		ffp.createGroup("test2");
		assertTrue(ffp.setPlayerGroup("test2", "test2"));
		assertEquals(ffp.getPlayerGroup("test2"),"test2");
		assertEquals(ffp.getPlayerGroup("test1"),"test1");
	}

	public void testGetPlayerSubgroups() {
		ffp.createGroup("subgroup1");
		ffp.createGroup("subgroup2");
		ffp.createGroup("subgroup3");
		ffp.createPlayer("test1");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 0);
		ffp.addPlayerSubgroup("test1", "subgroup1");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		ffp.addPlayerSubgroup("test1", "subGroup1");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		ffp.addPlayerSubgroup("test1", "subGroup2");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		
		ffp.createPlayer("test2");
		assertTrue(ffp.getPlayerSubgroups("test2").length == 0);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		ffp.addPlayerSubgroup("test2", "subgroup1");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		ffp.addPlayerSubgroup("test2", "subGroup1");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		ffp.addPlayerSubgroup("test2", "subGroup3");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		
		String[] subgroups = ffp.getPlayerSubgroups("test1");
		assertTrue(subgroups[0].equals("subgroup1") || subgroups[0].equals("subgroup2"));
		assertTrue(subgroups[1].equals("subgroup1") || subgroups[1].equals("subgroup2"));
		
		subgroups = ffp.getPlayerSubgroups("test2");
		assertTrue(subgroups[0].equals("subgroup1") || subgroups[0].equals("subgroup3"));
		assertTrue(subgroups[1].equals("subgroup1") || subgroups[1].equals("subgroup3"));
	}

	public void testAddPlayerSubgroup() {
		ffp.createGroup("subgroup1");
		ffp.createGroup("subgroup2");
		assertFalse(ffp.addPlayerSubgroup("test1", "unknown"));
		assertFalse(ffp.addPlayerSubgroup("test1", "subgroup1"));

		ffp.createPlayer("test1");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 0);
		assertFalse(ffp.addPlayerSubgroup("test1", "unknown"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 0);
		assertTrue(ffp.addPlayerSubgroup("test1", "subgroup1"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		assertFalse(ffp.addPlayerSubgroup("test1", "subgroup1"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		assertFalse(ffp.addPlayerSubgroup("test1", "subgrOup1"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		assertTrue(ffp.addPlayerSubgroup("test1", "subgroup2"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);

		ffp.createPlayer("test2");
		assertTrue(ffp.getPlayerSubgroups("test2").length == 0);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		assertFalse(ffp.addPlayerSubgroup("test2", "unknown"));
		assertTrue(ffp.getPlayerSubgroups("test2").length == 0);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		assertTrue(ffp.addPlayerSubgroup("test2", "subgroup1"));
		assertTrue(ffp.getPlayerSubgroups("test2").length == 1);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		assertFalse(ffp.addPlayerSubgroup("test2", "subgroup1"));
		assertTrue(ffp.getPlayerSubgroups("test2").length == 1);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		assertFalse(ffp.addPlayerSubgroup("test2", "subgrOup1"));
		assertTrue(ffp.getPlayerSubgroups("test2").length == 1);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		assertTrue(ffp.addPlayerSubgroup("test2", "subgroup2"));
		assertTrue(ffp.getPlayerSubgroups("test2").length == 2);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
	}

	public void testRemovePlayerSubgroup() {
		ffp.createGroup("subgroup1");
		ffp.createGroup("subgroup2");
		ffp.createGroup("subgroup3");
		
		assertFalse(ffp.removePlayerSubgroup("test1", "unknown"));
		assertFalse(ffp.removePlayerSubgroup("test1", "subgroup1"));
		
		ffp.createPlayer("test1");
		assertFalse(ffp.removePlayerSubgroup("test1", "unknown"));
		assertFalse(ffp.removePlayerSubgroup("test1", "subgroup1"));

		ffp.addPlayerSubgroup("test1", "subgroup1");
		ffp.addPlayerSubgroup("test1", "subgroup2");
		ffp.addPlayerSubgroup("test1", "subgroup3");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 3);
		
		ffp.removePlayerSubgroup("test1", "subgroup2");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 2);
		ffp.removePlayerSubgroup("test1", "subgroup3");
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);

		ffp.createPlayer("test2");
		assertFalse(ffp.removePlayerSubgroup("test2", "unknown"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		assertFalse(ffp.removePlayerSubgroup("test2", "subgroup1"));
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);

		ffp.addPlayerSubgroup("test2", "subgroup1");
		ffp.addPlayerSubgroup("test2", "subgroup2");
		ffp.addPlayerSubgroup("test2", "subgroup3");
		assertTrue(ffp.getPlayerSubgroups("test2").length == 3);
		
		ffp.removePlayerSubgroup("test2", "subgroup2");
		assertTrue(ffp.getPlayerSubgroups("test2").length == 2);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
		ffp.removePlayerSubgroup("test2", "subgroup1");
		assertTrue(ffp.getPlayerSubgroups("test2").length == 1);
		assertTrue(ffp.getPlayerSubgroups("test1").length == 1);
	}

	public void testAddPlayerPermission() {
		assertFalse(ffp.addPlayerPermission("test1", "world", "test.test1"));

		ffp.createPlayer("test1");
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 0);
		assertTrue(ffp.addPlayerPermission("test1", "world", "test.test1"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);
		assertFalse(ffp.addPlayerPermission("test1", "world", "test.test1"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);
		assertTrue(ffp.addPlayerPermission("test1", "world", "test.test2"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);

		ffp.createPlayer("test2");
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 0);
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);
		assertTrue(ffp.addPlayerPermission("test2", "world", "test.test1"));
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 1);
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);
		assertFalse(ffp.addPlayerPermission("test2", "world", "test.test1"));
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 1);
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);
		assertTrue(ffp.addPlayerPermission("test2", "world", "test.test3"));
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 2);
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);
		
		assertTrue(ffp.addPlayerPermission("test1", "world_2", "test.test1"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 2);
		assertTrue(ffp.getPlayerPermissions("test1", "world_2").length == 1);
		
		String[] permissions = ffp.getPlayerPermissions("test1", "world");
		assertTrue(permissions[0].equals("test.test1") || permissions[0].equals("test.test2"));
		assertTrue(permissions[1].equals("test.test1") || permissions[1].equals("test.test2"));
		
		permissions = ffp.getPlayerPermissions("test2", "world");
		assertTrue(permissions[0].equals("test.test1") || permissions[0].equals("test.test3"));
		assertTrue(permissions[1].equals("test.test1") || permissions[1].equals("test.test3"));

		permissions = ffp.getPlayerPermissions("test1", "world_2");
		assertTrue(permissions[0].equals("test.test1"));
	}

	public void testRemovePlayerPermission() {
		assertFalse(ffp.removePlayerPermission("test1", "world", "unknown"));
		assertFalse(ffp.removePlayerPermission("test1", "world", "subgroup1"));
		
		ffp.createPlayer("test1");
		assertFalse(ffp.removePlayerPermission("test1", "world", "unknown"));
		assertFalse(ffp.removePlayerPermission("test1", "world", "subgroup1"));

		ffp.addPlayerPermission("test1", "world", "subgroup1");
		ffp.addPlayerPermission("test1", "world", "subgroup2");
		ffp.addPlayerPermission("test1", "world", "subgroup3");
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 3);
		
		assertTrue(ffp.removePlayerPermission("test1", "world", "subgroup2"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 2);
		assertTrue(ffp.removePlayerPermission("test1", "world", "subgroup3"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);

		ffp.createPlayer("test2");
		assertFalse(ffp.removePlayerPermission("test2", "world", "unknown"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);
		assertFalse(ffp.removePlayerPermission("test2", "world", "subgroup1"));
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);

		ffp.addPlayerPermission("test2", "world", "subgroup1");
		ffp.addPlayerPermission("test2", "world", "subgroup2");
		ffp.addPlayerPermission("test2", "world", "subgroup3");
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 3);
		
		ffp.removePlayerPermission("test2", "world", "subgroup2");
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 2);
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);
		ffp.removePlayerPermission("test2", "world", "subgroup1");
		assertTrue(ffp.getPlayerPermissions("test2", "world").length == 1);
		assertTrue(ffp.getPlayerPermissions("test1", "world").length == 1);
	}

	public void testGetPlayerPermissionsSimple() {
		throw new UnsupportedOperationException();
	}

	public void testGetPlayerPermissionsComplex() {
		throw new UnsupportedOperationException();
	}

	public void testAddGroupPermission() {
		throw new UnsupportedOperationException();
	}

	public void testRemoveGroupPermission() {
		throw new UnsupportedOperationException();
	}

	public void testSetGroupSubgroup() {
		throw new UnsupportedOperationException();
	}

	public void testGetGroupSubgroups() {
		throw new UnsupportedOperationException();
	}

	public void testAddGroupSubgroup() {
		throw new UnsupportedOperationException();
	}

	public void testRemoveGroupSubgroup() {
		throw new UnsupportedOperationException();
	}

	public void testGetGroupPermissionsComplex() {
		throw new UnsupportedOperationException();
	}
}
