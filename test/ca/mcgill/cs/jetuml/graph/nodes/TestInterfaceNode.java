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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.application.MultiLineString;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.graph.nodes.PackageNode;

public class TestInterfaceNode
{
	private InterfaceNode aNode1;
	private Graphics2D aGraphics;
	
	@Before
	public void setup()
	{
		aNode1 = new InterfaceNode();
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
		MultiLineString name = aNode1.getName();
		assertEquals( MultiLineString.Align.CENTER, name.obtainJustification() );
		assertTrue(name.isBold());
		assertFalse(name.isUnderlined());
		assertEquals("\u00ABinterface\u00BB\n", name.getText());
		MultiLineString methods = aNode1.getMethods();
		assertEquals( MultiLineString.Align.LEFT, methods.obtainJustification() );
		assertFalse(methods.isBold());
		assertFalse(methods.isUnderlined());
		assertEquals("", methods.getText());
		assertEquals(new Rectangle(0,0,100,60), aNode1.view().getBounds());
		assertNull(aNode1.getParent());
	}
	
	@Test
	public void testSetName()
	{
		MultiLineString name = new MultiLineString();
		name.setText("Foo");
		aNode1.setName(name);
		assertEquals("Foo", aNode1.getName().getText());
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
		InterfaceNode clone = aNode1.clone();
		MultiLineString name = clone.getName();
		assertEquals( MultiLineString.Align.CENTER, name.obtainJustification() );
		assertTrue(name.isBold());
		assertFalse(name.isUnderlined());
		assertEquals("\u00ABinterface\u00BB\n", name.getText());
		assertFalse(name == aNode1.getName() );
		MultiLineString methods = clone.getMethods();
		assertEquals( MultiLineString.Align.LEFT, methods.obtainJustification() );
		assertFalse(methods.isBold());
		assertFalse(methods.isUnderlined());
		assertEquals("", methods.getText());
		assertFalse(methods == aNode1.getMethods() );
		assertEquals(new Rectangle(0,0,100,60), clone.view().getBounds());
		assertTrue(clone.view().getBounds().equals(aNode1.view().getBounds()));
		assertTrue(clone.getParent().equals(aNode1.getParent()));
	}
}
