package de.hydrox.bukkit.DroxPerms.data.flatfile;


import junit.framework.TestCase;

public class GroupTest extends TestCase {
	
	public GroupTest(String name) {
		super(name);
	}
	
	public void setUp() throws Exception {
		super.setUp();
		Group.clearGroups();
	}
	
	public void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testGetName() {
		Group group = new Group();
		assertEquals("default", group.getName());
		
		group = new Group("test1");
		assertEquals("test1", group.getName());
	}
	
	public void testAddPermission() {
		Group group = new Group();
		
		assertFalse(group.addPermission("test1", "Test.test1"));
		group.addWorld("test1");
		assertTrue(group.addPermission("test1", "Test.test1"));
		assertFalse(group.addPermission("test1", "Test.test1"));
		
		assertTrue(group.addPermission("test1", "Test.test2"));
		assertFalse(group.addPermission("test1", "Test.test2"));

		assertFalse(group.addPermission("test2", "Test.test1"));
		group.addWorld("test2");
		assertTrue(group.addPermission("test2", "Test.test1"));
		assertFalse(group.addPermission("test2", "Test.test1"));
	}
	
	public void testRemovePermission() {
		Group group = new Group();
		group.addWorld("test1");
		group.addWorld("test2");
		
		assertFalse(group.removePermission("test1", "Test.test1"));
		group.addPermission("test1", "Test.test1");
		assertTrue(group.removePermission("test1", "Test.test1"));
		assertFalse(group.removePermission("test1", "Test.test1"));

		assertFalse(group.removePermission("test1", "Test.test2"));
		group.addPermission("test1", "Test.test2");
		assertTrue(group.removePermission("test1", "Test.test2"));
		assertFalse(group.removePermission("test1", "Test.test2"));

		assertFalse(group.removePermission("test2", "Test.test1"));
		group.addPermission("test2", "Test.test1");
		assertTrue(group.removePermission("test2", "Test.test1"));
		assertFalse(group.removePermission("test2", "Test.test1"));

	}
	
	public void testAddSubgroup() {
		Group group = new Group();
		Group.addGroup(group);
		assertFalse(group.addSubgroup("test"));
		Group.addGroup(new Group("test"));
		Group.addGroup(new Group("test2"));
		Group.addGroup(new Group("test3"));
		
		assertTrue(group.addSubgroup("test"));
		assertFalse(group.addSubgroup("test"));
		
		assertTrue(group.addSubgroup("test2"));
		assertFalse(group.addSubgroup("test2"));
		
		assertTrue(group.addSubgroup("test3"));
		assertFalse(group.addSubgroup("test3"));
	}

	public void testRemoveSubgroup() {
		Group group = new Group();

		assertFalse(group.removeSubgroup("test"));
		Group.addGroup(new Group("Test"));
		group.addSubgroup("test");
		assertTrue(group.removeSubgroup("test"));
		assertFalse(group.removeSubgroup("test"));
		
		assertFalse(group.removeSubgroup("test2"));
		Group.addGroup(new Group("Test2"));
		group.addSubgroup("test2");
		assertTrue(group.removeSubgroup("test2"));
		assertFalse(group.removeSubgroup("test2"));
		
	}
	
	public void testHasPermissionSimple() {
		Group group = new Group();
		group.addWorld("test");
		group.addWorld("test2");
		
		assertFalse(group.hasPermission("test", "Test.test1"));
		group.addPermission("test", "Test.test1");
		assertTrue(group.hasPermission("test", "Test.test1"));
		assertFalse(group.hasPermission("test", "Test.test2"));
		group.addPermission("test", "Test.test2");
		assertTrue(group.hasPermission("test", "Test.test2"));
		
		assertFalse(group.hasPermission("test2", "Test.test1"));
		group.addPermission("test2", "Test.test1");
		assertTrue(group.hasPermission("test2", "Test.test1"));
		assertFalse(group.hasPermission("test2", "Test.test2"));
		group.addPermission("test2", "Test.test2");
		assertTrue(group.hasPermission("test2", "Test.test2"));
	}

	public void testHasPermissionComplex() {
		Group group = new Group();
		Group.addGroup(group);
		group.addWorld("test");
		group.addWorld("test2");


		Group subgroup1 = new Group("Subgroup1");
		assertTrue(subgroup1.addWorld("test"));
		assertTrue(subgroup1.addWorld("test2"));

		assertTrue(subgroup1.addPermission("test", "Subgroup1.test1"));
		assertTrue(subgroup1.addPermission("test", "Subgroup1.test2"));
		assertTrue(subgroup1.addPermission("test2", "Subgroup1.test1"));

		Group subgroup2 = new Group("Subgroup2");
		assertTrue(subgroup2.addWorld("test"));
		assertTrue(subgroup2.addWorld("test2"));
		assertTrue(subgroup2.addPermission("test", "Subgroup2.test1"));
		assertTrue(subgroup2.addPermission("test", "Subgroup2.test2"));
		assertTrue(subgroup2.addPermission("test2", "Subgroup2.test1"));

		Group subgroup3 = new Group("Subgroup3");
		assertTrue(subgroup3.addWorld("test"));
		assertTrue(subgroup3.addWorld("test2"));
		assertTrue(subgroup3.addPermission("test", "Subgroup3.test1"));
		assertTrue(subgroup3.addPermission("test", "Subgroup3.test2"));
		assertTrue(subgroup3.addPermission("test2", "Subgroup3.test1"));
		
		assertFalse(group.hasPermission("test", "Subgroup1.test1"));
		assertFalse(group.hasPermission("test", "Subgroup1.test2"));
		assertFalse(group.hasPermission("test", "Subgroup2.test1"));
		assertFalse(group.hasPermission("test", "Subgroup2.test2"));
		assertFalse(group.hasPermission("test", "Subgroup3.test1"));
		assertFalse(group.hasPermission("test", "Subgroup3.test2"));
		assertFalse(group.hasPermission("test2", "Subgroup1.test1"));
		assertFalse(group.hasPermission("test2", "Subgroup2.test1"));
		assertFalse(group.hasPermission("test2", "Subgroup3.test1"));
		
		Group.addGroup(subgroup1);
		Group.addGroup(subgroup2);
		Group.addGroup(subgroup3);
		assertTrue(group.addSubgroup("Subgroup1"));
		assertTrue(subgroup1.addSubgroup("Subgroup2"));
		assertTrue(subgroup2.addSubgroup("Subgroup3"));

		assertTrue(group.hasPermission("test", "Subgroup1.test1"));
		assertTrue(group.hasPermission("test", "Subgroup1.test2"));
		assertTrue(group.hasPermission("test", "Subgroup2.test1"));
		assertTrue(group.hasPermission("test", "Subgroup2.test2"));
		assertTrue(group.hasPermission("test", "Subgroup3.test1"));
		assertTrue(group.hasPermission("test", "Subgroup3.test2"));
		assertTrue(group.hasPermission("test2", "Subgroup1.test1"));
		assertTrue(group.hasPermission("test2", "Subgroup2.test1"));
		assertTrue(group.hasPermission("test2", "Subgroup3.test1"));	
	}
	
	public void testAddGroup() {
		assertTrue(Group.addGroup(new Group("default")));
		assertFalse(Group.addGroup(new Group("default")));
		assertTrue(Group.addGroup(new Group("group1")));
		assertFalse(Group.addGroup(new Group("group1")));
		assertFalse(Group.addGroup(new Group("Group1")));
		assertTrue(Group.addGroup(new Group("Group2")));
	}
	
	public void testremoveGroup() {
		assertFalse(Group.removeGroup("test"));
		Group.addGroup(new Group("default"));
		assertFalse(Group.removeGroup("test"));
		Group.addGroup(new Group("test"));
		assertTrue(Group.removeGroup("test"));
		Group.addGroup(new Group("Test"));
		assertTrue(Group.removeGroup("test"));
		Group.addGroup(new Group("test"));
		assertTrue(Group.removeGroup("Test"));
	}
	
	public void testGetGroup() {
		assertEquals(Group.getGroup("group1"), null);
		Group group1 = new Group("group1");
		Group.addGroup(group1);
		assertEquals(Group.getGroup("group1"), group1);

		assertEquals(Group.getGroup("group2"), null);
		Group group2 = new Group("group2");
		Group.addGroup(group2);
		assertEquals(Group.getGroup("group2"), group2);
	}

	public void testExistGroup() {
		assertFalse(Group.existGroup("test1"));
		Group.addGroup(new Group("test1"));
		assertTrue(Group.existGroup("test1"));
		assertTrue(Group.existGroup("Test1"));
		Group.removeGroup("test1");
		Group.addGroup(new Group("Test1"));
		assertTrue(Group.existGroup("test1"));
		assertTrue(Group.existGroup("Test1"));
		Group.removeGroup("Test1");
		assertFalse(Group.existGroup("test1"));
		assertFalse(Group.existGroup("Test1"));
	}
}
