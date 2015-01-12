package com.horstmann.violet;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestUMLEditor
{
	@Test
	public void testIsOKJVMVersion()
	{
		assertTrue(UMLEditor.isOKJVMVersion("1.7.0_67"));
		assertTrue(UMLEditor.isOKJVMVersion("1.7.1_0"));
		assertTrue(UMLEditor.isOKJVMVersion("1.7.0"));
		assertFalse(UMLEditor.isOKJVMVersion("1.6.0_35"));
	}
}
