package de.hydrox.bukkit.DroxPerms.test;

import java.lang.reflect.Method;
import java.util.logging.Logger;

public class TestClassTemplate {

	protected void setUp() throws Exception {	
	}

	protected void tearDown() throws Exception {
	}

	protected void assertTrue(boolean result) throws TestClassException {
		if (!result) {
			throw new TestClassException();
		}
	}
	
	protected void assertFalse(boolean result) throws TestClassException {
		if (result) {
			throw new TestClassException();
		}
	}
	
	protected void assertEquals(String toTest, String expected) throws TestClassException {
		if (!toTest.equals(expected)) {
			throw new TestClassException();
		}
	}
	

	public static void runTests(TestClassTemplate toTest) {
		Method[] methods = toTest.getClass().getMethods();
		int passed = 0;
		int failed = 0;
		for (Method method : methods) {
			if (method.getName().startsWith("test")) {
				try {
					toTest.setUp();
					method.invoke(toTest);
				} catch (Exception e) {
					e.getCause().printStackTrace();
					Logger.getLogger("Minecraft").severe("[DroxPerms] Test " + method.getName() + " of Class " + toTest.getClass().getName() + " failed!");
					failed++;
					continue;
				}
				Logger.getLogger("Minecraft").info("[DroxPerms] Test " + method.getName() + " of Class " + toTest.getClass().getName() + " passed!");
				passed++;
			}
		}
		Logger.getLogger("Minecraft").severe("[DroxPerms] Test of Class " + toTest.getClass().getName() + " complete. Passed: " + passed + " Failed: " + failed);		
	}
}
