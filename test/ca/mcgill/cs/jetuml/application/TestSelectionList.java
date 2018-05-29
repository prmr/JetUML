/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016, 2018 by the contributors of the JetUML project.
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
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.gui.SelectionModel;

public class TestSelectionList
{
	private Edge aEdge1; 
	private Edge aEdge2; 
	private ClassNode aNode1;
	private ClassNode aNode2;
	private PackageNode aPackage1; 
	private PackageNode aPackage2; 
	private SelectionModel aList;
	
	/**
	  * Load JavaFX toolkit and environment.
	  */
	 @BeforeClass
	 @SuppressWarnings("unused")
	 public static void setupClass()
	 {
		 JavaFXLoader loader = JavaFXLoader.instance();
	 }
	
	@Before
	public void setup()
	{
		aEdge1 = new CallEdge();
		aEdge2 = new NoteEdge();
		aNode1 = new ClassNode();
		aNode2 = new ClassNode();
		aPackage1 = new PackageNode();
		aPackage2 = new PackageNode();
		aList = new SelectionModel( () -> {});
	}
	
	private static int size(SelectionModel pModel)
	{
		int size = 0;
		for( @SuppressWarnings("unused") DiagramElement element : pModel )
		{
			size++;
		}
		return size;
	}
	
	@Test
	public void testEmpty()
	{
		aList.clearSelection();
		assertFalse(aList.getLastSelected().isPresent());
		aList.removeFromSelection(aEdge1);
		assertFalse(aList.iterator().hasNext());
		assertTrue(aList.isEmpty());
		assertFalse(aList.contains(aEdge1));
	}
	
	@Test
	public void testContains()
	{
		aList.addToSelection(aNode1);
		aList.addToSelection(aEdge1);
		assertTrue(aList.contains(aNode1));
		assertTrue(aList.contains(aEdge1));
		assertFalse(aList.contains(aEdge2));
	}
	
	@Test
	public void testAdd()
	{
		aList.addToSelection(aEdge1);
		assertEquals(1, size(aList));
		assertEquals(aEdge1, aList.getLastSelected().get());
		aList.addToSelection(aNode1);
		assertEquals(2, size(aList));
		assertEquals(aNode1, aList.getLastSelected().get());
		aList.addToSelection(aEdge1);
		assertEquals(2, size(aList));
		assertEquals(aEdge1, aList.getLastSelected().get());
	}
	
	@Test
	public void testAddParentContained()
	{
		aList.addToSelection(aEdge1);
		aPackage1.addChild(aNode1);
		aList.addToSelection(aPackage1);
		aList.addToSelection(aNode1);
		assertEquals(2, size(aList));
		assertTrue(aList.contains(aEdge1));
		assertTrue(aList.contains(aPackage1));
	}
	
	@Test
	public void testAddChildrenContained()
	{
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aNode1);
		aPackage2.addChild(aNode2);
		aList.addToSelection(aNode1);
		aList.addToSelection(aNode2);
		assertEquals(2, size(aList));
		assertTrue(aList.contains(aNode1));
		assertTrue(aList.contains(aNode2));
		aList.addToSelection(aPackage1);
		assertEquals(1, size(aList));
		assertTrue(aList.contains(aPackage1));
	}
	
	@Test
	public void testGetLastSelected()
	{
		assertFalse(aList.getLastSelected().isPresent());
		aList.addToSelection(aEdge1);
		assertEquals(aEdge1, aList.getLastSelected().get());
		aList.addToSelection(aEdge2);
		assertEquals(aEdge2, aList.getLastSelected().get());
		aList.addToSelection(aEdge1);
		assertEquals(aEdge1, aList.getLastSelected().get());
	}
	
	@Test
	public void testClearSelection()
	{
		aList.addToSelection(aEdge1);
		aList.addToSelection(aEdge2);
		aList.addToSelection(aNode1);
		aList.clearSelection();
		assertEquals(0, size(aList));
		assertFalse(aList.getLastSelected().isPresent());
	}
	
	@Test
	public void testRemove()
	{
		aList.addToSelection(aEdge1);
		aList.addToSelection(aEdge2);
		aList.addToSelection(aNode1);
		aList.removeFromSelection(aEdge1);
		assertEquals(2,  size(aList));
		Iterator<DiagramElement> iterator = aList.iterator();
		assertEquals(aEdge2, iterator.next());
		assertEquals(aNode1, iterator.next());
		assertEquals(aNode1, aList.getLastSelected().get());
	}
	
	@Test
	public void testSet()
	{
		aList.addToSelection(aEdge1);
		aList.addToSelection(aEdge2);
		aList.set(aNode1);
		assertEquals(1, size(aList));
		assertEquals(aNode1, aList.getLastSelected().get());
		aList.clearSelection();
		aList.addToSelection(aEdge1);
		aList.addToSelection(aEdge2);
		aList.set(aEdge1);
		assertEquals(1, size(aList));
		assertEquals(aEdge1, aList.getLastSelected().get());
	}
}
