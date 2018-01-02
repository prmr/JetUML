/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.graph.nodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestClassNode
{
	private ClassNode aNode1;
	private Graphics2D aGraphics;
	
	@Before
	public void setup()
	{
		aNode1 = new ClassNode();
		aGraphics = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB).createGraphics();
	}
	
	@After
	public void teardown()
	{
		aGraphics.dispose();
	}
	
	@Test
	public void testDefault()
	{
		assertEquals("", aNode1.getName());
		String attributes = aNode1.getAttributes();
		assertEquals("", attributes);
		assertEquals(new Rectangle(0,0,100,60), aNode1.view().getBounds());
		assertNull(aNode1.getParent());
	}
	
	@Test
	public void testSetName()
	{
		aNode1.setName("Foo");
		assertEquals("Foo", aNode1.getName());
	}
	
	@Test
	public void testSetParent()
	{
		PackageNode package1 = new PackageNode();
		PackageNode package2 = new PackageNode();
		aNode1.setParent(package1);
		assertTrue( aNode1.getParent() == package1 );
		aNode1.setParent(package2);
		assertTrue( aNode1.getParent() == package2 );
		aNode1.setParent(null);
		assertNull( aNode1.getParent() );
	}
	
	@Test
	public void testClone()
	{
		PackageNode package1 = new PackageNode();
		aNode1.setParent(package1);
		ClassNode clone = (ClassNode) aNode1.clone();
		String name = clone.getName();
		assertEquals("", name);
		String methods = clone.getMethods();
		assertEquals("", methods);
		assertEquals(new Rectangle(0,0,100,60), clone.view().getBounds());
		assertTrue(clone.view().getBounds().equals(aNode1.view().getBounds()));
		assertTrue(clone.getParent().equals(aNode1.getParent()));
	}
}
