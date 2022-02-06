/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
/**
 * 
 */
package ca.mcgill.cs.jetuml.viewers.nodes;

import static ca.mcgill.cs.jetuml.viewers.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ConstructorEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * @author Madonna Huang
 *
 */
public class TestImplicitParameterNodeViewer 
{
	private static int userDefinedFontSize;
	private ImplicitParameterNode aImplicitParameterNode1;
	private ImplicitParameterNode aImplicitParameterNode2;
	private ImplicitParameterNode aImplicitParameterNode3;
	private Diagram aDiagram;
	private CallNode aDefaultCallNode1;
	private CallNode aDefaultCallNode2;
	private CallNode aCallNode1;
	private CallEdge aCallEdge1;
	private ConstructorEdge aConstructorEdge1;
	private ConstructorEdge aConstructorEdge2;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setup()
	{
		aDiagram = new Diagram(DiagramType.SEQUENCE);
		aImplicitParameterNode1 = new ImplicitParameterNode();
		aImplicitParameterNode2 = new ImplicitParameterNode();
		aImplicitParameterNode3 = new ImplicitParameterNode();
		aDefaultCallNode1 = new CallNode();
		aDefaultCallNode2 = new CallNode();
		aCallNode1 = new CallNode();
		aCallEdge1 = new CallEdge();
		aConstructorEdge1 = new ConstructorEdge();
		aConstructorEdge2 = new ConstructorEdge();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	public void testGetBoundsWithOneNode()
	{
		assertEquals(new Rectangle(0, 0, 80, 120), NodeViewerRegistry.getBounds(aImplicitParameterNode1));
	}
	
	@Test
	public void testGetBoundsWithOneNodeInDiagram()
	{
		aDiagram.addRootNode(aImplicitParameterNode1);
		assertEquals(new Rectangle(0, 0, 80, 120), NodeViewerRegistry.getBounds(aImplicitParameterNode1));
	}
	
	@Test
	public void testGetBoundsContructorCallWithFirstCallee()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aDefaultCallNode1.attach(aDiagram);
		
		aImplicitParameterNode2.translate(100, 0);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDefaultCallNode2.attach(aDiagram);
		
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		aConstructorEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aDiagram.addEdge(aConstructorEdge1);
		assertEquals(new Rectangle(0, 0, 80, 235), NodeViewerRegistry.getBounds(aImplicitParameterNode1));
		assertEquals(new Rectangle(100, 100, 80, 115), NodeViewerRegistry.getBounds(aImplicitParameterNode2));
	}
	
	@Test
	public void testGetBoundsContructorCallWithSecondCallCallee1()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aDefaultCallNode1.attach(aDiagram);
		
		aImplicitParameterNode2.translate(100, 0);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDefaultCallNode2.attach(aDiagram);
		
		aImplicitParameterNode3.translate(200, 0);
		aImplicitParameterNode3.addChild(aCallNode1);
		
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aDiagram.addRootNode(aImplicitParameterNode3);
		
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aConstructorEdge1.connect(aDefaultCallNode1, aCallNode1, aDiagram);
		aDiagram.addEdge(aCallEdge1);
		aDiagram.addEdge(aConstructorEdge1);
		
		assertEquals(new Rectangle(0, 0, 80, 285), NodeViewerRegistry.getBounds(aImplicitParameterNode1));
		assertEquals(new Rectangle(100, 0, 80, 150), NodeViewerRegistry.getBounds(aImplicitParameterNode2));
		assertEquals(new Rectangle(200, 150, 80, 115), NodeViewerRegistry.getBounds(aImplicitParameterNode3));
	}
	
	@Test
	public void testGetBoundsContructorCallWithSecondCallCallee2()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aDefaultCallNode1.attach(aDiagram);
		
		aImplicitParameterNode2.translate(100, 0);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDefaultCallNode2.attach(aDiagram);
		
		aImplicitParameterNode3.translate(200, 0);
		aImplicitParameterNode3.addChild(aCallNode1);
		
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aDiagram.addRootNode(aImplicitParameterNode3);
		
		aConstructorEdge1.connect(aDefaultCallNode1, aDefaultCallNode2, aDiagram);
		aConstructorEdge2.connect(aDefaultCallNode1, aCallNode1, aDiagram);
		aDiagram.addEdge(aConstructorEdge1);
		aDiagram.addEdge(aConstructorEdge2);
		
		assertEquals(new Rectangle(0, 0, 80, 350), NodeViewerRegistry.getBounds(aImplicitParameterNode1));
		assertEquals(new Rectangle(100, 100, 80, 115), NodeViewerRegistry.getBounds(aImplicitParameterNode2));
		assertEquals(new Rectangle(200, 215, 80, 115), NodeViewerRegistry.getBounds(aImplicitParameterNode3));
	}
}
