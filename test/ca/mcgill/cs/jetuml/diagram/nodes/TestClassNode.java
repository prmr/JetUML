/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
 *     
 * See: https://github.com/prmr/JetUML
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.diagram.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestClassNode
{
	private ClassNode aNode1;
	
	@BeforeEach
	public void setup()
	{
		aNode1 = new ClassNode();
	}
	
	@Test
	public void testDefault()
	{
		assertEquals("", aNode1.getName());
		String attributes = aNode1.getAttributes();
		assertEquals("", attributes);
		assertFalse(aNode1.hasParent());
	}
	
	@Test
	public void testSetName()
	{
		aNode1.setName("Foo");
		assertEquals("Foo", aNode1.getName());
	}
	
	@Test
	public void testLink()
	{
		PackageNode package1 = new PackageNode();
		PackageNode package2 = new PackageNode();
		aNode1.link(package1);
		assertTrue( aNode1.getParent() == package1 );
		aNode1.link(package2);
		assertTrue( aNode1.getParent() == package2 );
		aNode1.unlink();
		assertFalse( aNode1.hasParent() );
	}
	
	@Test
	public void testClone_PropertiesCopied()
	{
		aNode1.setName("FOO");
		aNode1.setMethods("FOO1");
		aNode1.setAttributes("FIELDS");
		ClassNode clone = (ClassNode) aNode1.clone();
		assertEquals("FOO", clone.getName());
		assertEquals("FOO1", clone.getMethods());
		assertEquals("FIELDS", clone.getAttributes());
	}
	
	/**
	 * Copying a class node makes a shallow copy of the parent.
	 */
	@Test
	public void testClone_ParentCopied()
	{
		PackageNode package1 = new PackageNode();
		package1.setName("Foo");
		aNode1.link(package1);
		ClassNode clone = (ClassNode) aNode1.clone();
		assertEquals("Foo", ((PackageNode)clone.getParent()).getName());
		assertSame(package1, clone.getParent());
	}
	
	@Test
	public void testRequiresParent()
	{
		assertFalse(aNode1.requiresParent());
	}
}
