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
package ca.mcgill.cs.jetuml.gui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.gui.SelectionModel;

public class TestSelectionModel
{
	private Edge aEdge1; 
	private Edge aEdge2; 
	private ClassNode aNode1;
	private ClassNode aNode2;
	private PackageNode aPackage1; 
	private PackageNode aPackage2; 
	private SelectionModel aModel;
	
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
		aModel = new SelectionModel( () -> {});
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
		aModel.clearSelection();
		assertFalse(aModel.getLastSelected().isPresent());
		aModel.removeFromSelection(aEdge1);
		assertFalse(aModel.iterator().hasNext());
		assertTrue(aModel.isEmpty());
		assertFalse(aModel.contains(aEdge1));
	}
	
	@Test
	public void testGetSelectedNodesEmpty()
	{
		assertFalse(aModel.getSelectedNodes().iterator().hasNext());
	}
	
	@Test
	public void testGetSelectedNodesJustNodes()
	{
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aNode2);
		Iterator<Node> iterator = aModel.getSelectedNodes().iterator();
		assertSame(aNode1, iterator.next());
		assertSame(aNode2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testGetSelectedNodesJustEdges()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		assertFalse(aModel.getSelectedNodes().iterator().hasNext());
	}
	
	@Test
	public void testGetSelectedNodesNodesAndEdges()
	{
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aNode2);
		Iterator<Node> iterator = aModel.getSelectedNodes().iterator();
		assertSame(aNode1, iterator.next());
		assertSame(aNode2, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testGetSelectedEdgesAndNodes()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aEdge2);
		Iterator<Node> iterator = aModel.getSelectedNodes().iterator();
		assertSame(aNode1, iterator.next());
		assertFalse(iterator.hasNext());
	}
	
	@Test
	public void testContains()
	{
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aEdge1);
		assertTrue(aModel.contains(aNode1));
		assertTrue(aModel.contains(aEdge1));
		assertFalse(aModel.contains(aEdge2));
	}
	
	@Test
	public void testAdd()
	{
		aModel.addToSelection(aEdge1);
		assertEquals(1, size(aModel));
		assertEquals(aEdge1, aModel.getLastSelected().get());
		aModel.addToSelection(aNode1);
		assertEquals(2, size(aModel));
		assertEquals(aNode1, aModel.getLastSelected().get());
		aModel.addToSelection(aEdge1);
		assertEquals(2, size(aModel));
		assertEquals(aEdge1, aModel.getLastSelected().get());
	}
	
	@Test
	public void testAddParentContained()
	{
		aModel.addToSelection(aEdge1);
		aPackage1.addChild(aNode1);
		aModel.addToSelection(aPackage1);
		aModel.addToSelection(aNode1);
		assertEquals(2, size(aModel));
		assertTrue(aModel.contains(aEdge1));
		assertTrue(aModel.contains(aPackage1));
	}
	
	@Test
	public void testAddChildrenContained()
	{
		aPackage1.addChild(aPackage2);
		aPackage2.addChild(aNode1);
		aPackage2.addChild(aNode2);
		aModel.addToSelection(aNode1);
		aModel.addToSelection(aNode2);
		assertEquals(2, size(aModel));
		assertTrue(aModel.contains(aNode1));
		assertTrue(aModel.contains(aNode2));
		aModel.addToSelection(aPackage1);
		assertEquals(1, size(aModel));
		assertTrue(aModel.contains(aPackage1));
	}
	
	@Test
	public void testGetLastSelected()
	{
		assertFalse(aModel.getLastSelected().isPresent());
		aModel.addToSelection(aEdge1);
		assertEquals(aEdge1, aModel.getLastSelected().get());
		aModel.addToSelection(aEdge2);
		assertEquals(aEdge2, aModel.getLastSelected().get());
		aModel.addToSelection(aEdge1);
		assertEquals(aEdge1, aModel.getLastSelected().get());
	}
	
	@Test
	public void testClearSelection()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.addToSelection(aNode1);
		aModel.clearSelection();
		assertEquals(0, size(aModel));
		assertFalse(aModel.getLastSelected().isPresent());
	}
	
	@Test
	public void testRemove()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.addToSelection(aNode1);
		aModel.removeFromSelection(aEdge1);
		assertEquals(2,  size(aModel));
		Iterator<DiagramElement> iterator = aModel.iterator();
		assertEquals(aEdge2, iterator.next());
		assertEquals(aNode1, iterator.next());
		assertEquals(aNode1, aModel.getLastSelected().get());
	}
	
	@Test
	public void testSet()
	{
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.set(aNode1);
		assertEquals(1, size(aModel));
		assertEquals(aNode1, aModel.getLastSelected().get());
		aModel.clearSelection();
		aModel.addToSelection(aEdge1);
		aModel.addToSelection(aEdge2);
		aModel.set(aEdge1);
		assertEquals(1, size(aModel));
		assertEquals(aEdge1, aModel.getLastSelected().get());
	}
}
