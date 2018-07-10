/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.gui.DiagramCanvas;
import ca.mcgill.cs.jetuml.gui.DiagramCanvasController;
import ca.mcgill.cs.jetuml.gui.DiagramTabToolBar;
import ca.mcgill.cs.jetuml.gui.SelectionModel;

public class TestClipboard2
{
	private Clipboard2 aClipboard;
	private Field aNodesField;
	private Field aEdgesField;
	private ClassNode aNode1;
	
	public TestClipboard2() throws ReflectiveOperationException
	{
		aNodesField = Clipboard2.class.getDeclaredField("aNodes");
		aNodesField.setAccessible(true);
		aEdgesField = Clipboard2.class.getDeclaredField("aEdges");
		aEdgesField.setAccessible(true);
	}
	
	@SuppressWarnings("unchecked")
	private List<Node> getClipboardNodes()
	{
		try 
		{
			return (List<Node>) aNodesField.get(aClipboard);
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private List<Edge> getClipboardEdges()
	{
		try
		{
			return (List<Edge>) aEdgesField.get(aClipboard);
		}
		catch( ReflectiveOperationException e)
		{
			fail();
			return null;
		}
	}
	
	@Before
	public void setup()
	{
		aClipboard = Clipboard2.instance();		
		aNode1 = new ClassNode();
	}
	
	@Test
	public void testCopySingleNodeNoReposition()
	{
		aClipboard.copy(Arrays.asList(aNode1));
		assertEquals(1, getClipboardNodes().size());
		assertFalse(getClipboardNodes().contains(aNode1)); // Clone
		assertEquals(new Point(0,0), getClipboardNodes().get(0).position());
	}
	
	@Test
	public void testCopySingleNodeReposition()
	{
		aNode1.translate(10, 10);
		aClipboard.copy(Arrays.asList(aNode1));
		assertEquals(1, getClipboardNodes().size());
		assertFalse(getClipboardNodes().contains(aNode1)); // Clone
		assertEquals(new Point(0,0), getClipboardNodes().get(0).position());
	}
}
