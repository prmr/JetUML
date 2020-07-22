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

public class TestPackageDescriptionNode
{
	private PackageDescriptionNode aPackageDescriptionNode;
	private PackageNode aPackageNode;
	
	@BeforeEach
	public void setup()
	{
		aPackageDescriptionNode = new PackageDescriptionNode();
		aPackageNode = new PackageNode();
	}
	
	@Test
	public void testDefault()
	{
		assertEquals(0,aPackageDescriptionNode.getChildren().size());
		assertEquals("", aPackageDescriptionNode.getName().toString());
		assertFalse(aPackageDescriptionNode.hasParent());
	}
	
	@Test
	public void testLink()
	{
		aPackageDescriptionNode.link(aPackageNode);
		assertTrue(aPackageDescriptionNode.hasParent());
		assertSame(aPackageNode, aPackageDescriptionNode.getParent());
		
		aPackageDescriptionNode.unlink();
		assertFalse(aPackageDescriptionNode.hasParent());
	}
	
	@Test
	public void testRequiresParent()
	{
		assertFalse(aPackageDescriptionNode.requiresParent());
	}
	
	@Test
	public void testAllowsChildren()
	{
		assertFalse(aPackageDescriptionNode.allowsChildren());
	}
	
	@Test
	public void testSetName()
	{
		aPackageDescriptionNode.setName("Foo");
		assertEquals("Foo", aPackageDescriptionNode.getName());
	}
	
	@Test
	public void testSetContents()
	{
		aPackageDescriptionNode.setContents("Foo");
		assertEquals("Foo", aPackageDescriptionNode.getContents());
	}
	
	@Test
	public void testClone()
	{
		aPackageDescriptionNode.setName("Name");
		aPackageDescriptionNode.setContents("Contents");
		aPackageDescriptionNode.link(aPackageNode);
		PackageDescriptionNode clone = aPackageDescriptionNode.clone();
		assertEquals("Name", clone.getName());
		assertEquals("Contents", clone.getContents());
		assertSame(aPackageNode, clone.getParent());
	}
}
