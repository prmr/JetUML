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
package ca.mcgill.cs.jetuml.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import ca.mcgill.cs.jetuml.application.SelectionList;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.edges.CallEdge;
import ca.mcgill.cs.jetuml.graph.edges.NoteEdge;
import ca.mcgill.cs.jetuml.graph.nodes.ClassNode;
import ca.mcgill.cs.jetuml.graph.nodes.PackageNode;

public class TestSelectionList
{
	private Edge aEdge1; 
	private Edge aEdge2; 
	private ClassNode aNode1;
	private ClassNode aNode2;
	private ClassNode aNode3;
	private PackageNode aPackage1; 
	private PackageNode aPackage2; 
	private PackageNode aPackage3;
	private SelectionList aList;
	
	@Before
	public void setup()
	{
		aEdge1 = new CallEdge();
		aEdge2 = new NoteEdge();
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aNode3 = new ClassNode();
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		aPackage3 = new PackageNode();
		aList = new SelectionList();
	}
	
	@Test
	public void testEmpty()
	{
		aList.clearSelection();
		assertNull(aList.getLastSelected());
		aList.remove(aEdge1);
		assertFalse(aList.iterator().hasNext());
		assertEquals(0, aList.size());
		assertFalse(aList.contains(aEdge1));
	}
	
	@Test
	public void testContains()
	{
		aList.add(aNode1);
		aList.add(aEdge1);
		assertTrue(aList.contains(aNode1));
		assertTrue(aList.contains(aEdge1));
		assertFalse(aList.contains(aEdge2));
	}
	
	@Test
	public void testAdd()
	{
		aList.add(aEdge1);
		assertEquals(1, aList.size());
		assertEquals(aEdge1, aList.getLastSelected());
		aList.add(aNode1);
		assertEquals(2, aList.size());
		assertEquals(aNode1, aList.getLastSelected());
		aList.add(aEdge1);
		assertEquals(2, aList.size());
		assertEquals(aEdge1, aList.getLastSelected());
	}
	
	@Test
	public void testAddParentContained()
	{
		aList.add(aEdge1);
		aPackage1.addChild(aNode1);
		aList.add(aPackage1);
		aList.add(aNode1);
		assertEquals(2, aList.size());
		assertTrue(aList.contains(aEdge1));
		assertTrue(aList.contains(aPackage1));
	}
	
	@Test
	public void testTransitivelyContained()
	{
		aList.add(aEdge1);
		aPackage1.addChild(aNode1);
		aList.add(aPackage1);
		aList.add(aNode1);
		aPackage1.addChild(aNode2);
		assertEquals(2, aList.size());
		assertTrue(aList.transitivelyContains(aEdge1));
		assertTrue(aList.transitivelyContains(aPackage1));
		assertTrue(aList.transitivelyContains(aNode2));
		assertTrue(aList.transitivelyContains(aNode1));
	}
	
	@Test
	public void testAddChildrenContained()
	{
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aNode1);
		aPackage2.addChild(aNode2);
		aList.add(aNode1);
		aList.add(aNode2);
		assertEquals(2, aList.size());
		assertTrue(aList.contains(aNode1));
		assertTrue(aList.contains(aNode2));
		aList.add(aPackage1);
		assertEquals(1, aList.size());
		assertTrue(aList.contains(aPackage1));
	}
	
	@Test
	public void testGetLastSelected()
	{
		assertNull(aList.getLastSelected());
		aList.add(aEdge1);
		assertEquals(aEdge1, aList.getLastSelected());
		aList.add(aEdge2);
		assertEquals(aEdge2, aList.getLastSelected());
		aList.add(aEdge1);
		assertEquals(aEdge1, aList.getLastSelected());
	}
	
	@Test
	public void testGetLastNode()
	{
		assertNull(aList.getLastNode());
		aList.add(aEdge1);
		assertNull(aList.getLastNode());
		aList.add(aEdge2);
		assertNull(aList.getLastNode());
		aList.add(aNode1);
		assertEquals(aNode1, aList.getLastNode());
		aList.add(aNode2);
		assertEquals(aNode2, aList.getLastNode());
		aList.add(aEdge1);
		assertEquals(aNode2, aList.getLastNode());
		aList.add(aNode2);
		assertEquals(aNode2, aList.getLastNode());
	}
	
	@Test
	public void testClearSelection()
	{
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.add(aNode1);
		aList.clearSelection();
		assertEquals(0, aList.size());
		assertNull(aList.getLastSelected());
	}
	
	@Test
	public void testRemove()
	{
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.add(aNode1);
		aList.remove(aEdge1);
		assertEquals(2,  aList.size());
		Iterator<GraphElement> iterator = aList.iterator();
		assertEquals(aEdge2, iterator.next());
		assertEquals(aNode1, iterator.next());
		assertEquals(aNode1, aList.getLastSelected());
	}
	
	@Test
	public void testParentContained()
	{
		assertFalse(aList.parentContained(aEdge1));
		assertFalse(aList.parentContained(aNode1));
		aList.add(aEdge1);
		assertFalse(aList.parentContained(aEdge1));
		aList.add(aNode1);
		assertFalse(aList.parentContained(aEdge1));
		assertFalse(aList.parentContained(aNode1));

		aPackage1.addChild(aNode1);
		assertFalse(aList.parentContained(aNode1));
		
		aList.add(aPackage1);
		assertTrue(aList.parentContained(aNode1));
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aNode2);
		aPackage2.addChild(aPackage3);
		aPackage3.addChild(aNode3);

		assertFalse(aList.parentContained(aPackage1));
		assertTrue(aList.parentContained(aNode2));
		assertTrue(aList.parentContained(aPackage2));
		assertTrue(aList.parentContained(aPackage3));
		assertTrue(aList.parentContained(aNode3));
	}
	
	@Test
	public void testSet()
	{
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.set(aNode1);
		assertEquals(1, aList.size());
		assertEquals(aNode1, aList.getLastSelected());
		aList.clearSelection();
		aList.add(aEdge1);
		aList.add(aEdge2);
		aList.set(aEdge1);
		assertEquals(1, aList.size());
		assertEquals(aEdge1, aList.getLastSelected());
	}
}
