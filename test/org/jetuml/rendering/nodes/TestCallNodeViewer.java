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
package org.jetuml.rendering.nodes;

import static org.jetuml.rendering.FontMetrics.DEFAULT_FONT_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestCallNodeViewer
{
	private static int userDefinedFontSize;
	private ImplicitParameterNode aImplicitParameterNode1;
	private ImplicitParameterNode aImplicitParameterNode2;
	private Diagram aDiagram;
	private SequenceDiagramRenderer aRenderer;
	private CallNode aDefaultCallNode1;
	private CallNode aDefaultCallNode2;
	private CallNode aCallNode1;
	private CallEdge aCallEdge1;
	private CallEdge aCallEdge2;
	private ConstructorEdge aConstructorEdge;
	
	@BeforeAll
	public static void setupClass()
	{
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, DEFAULT_FONT_SIZE);
	}
	
	@BeforeEach
	void setup()
	{
		aDiagram = new Diagram(DiagramType.SEQUENCE);
		aRenderer = new SequenceDiagramRenderer(aDiagram);
		aImplicitParameterNode1 = new ImplicitParameterNode();
		aImplicitParameterNode2 = new ImplicitParameterNode();
		aDefaultCallNode1 = new CallNode();
		aDefaultCallNode2 = new CallNode();
		aCallNode1 = new CallNode();
		aCallEdge1 = new CallEdge();
		aCallEdge2 = new CallEdge();
		aConstructorEdge = new ConstructorEdge();
	}
	
	@AfterAll
	public static void restorePreferences()
	{
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}
	
	@Test
	void testGetBoundsSecondCalleeOfCaller()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aImplicitParameterNode2.translate(200, 0);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2);
		aDiagram.addEdge(aCallEdge1);
		
		aImplicitParameterNode2.addChild(aCallNode1);
		aCallEdge2.connect(aDefaultCallNode1, aCallNode1);
		aDiagram.addEdge(aCallEdge2);
		
		aRenderer.getBounds(); // Trigger rendering pass
		
		assertEquals(new Rectangle(30, 80, 16, 120), aRenderer.getBounds(aDefaultCallNode1));
		assertEquals(new Rectangle(230, 100, 16, 30), aRenderer.getBounds(aDefaultCallNode2));
		assertEquals(new Rectangle(230, 150, 16, 30), aRenderer.getBounds(aCallNode1));
	}	
	
	@Test
	void testGetBoundsWithConstructorCall()
	{
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);
		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		
		aConstructorEdge.connect(aDefaultCallNode1, aDefaultCallNode2);
		aDiagram.addEdge(aConstructorEdge);
		
		aRenderer.getBounds(); // Trigger rendering pass
		
		assertEquals(new Rectangle(30, 80, 16, 135), aRenderer.getBounds(aDefaultCallNode1));
		assertEquals(new Rectangle(30, 165, 16, 30), aRenderer.getBounds(aDefaultCallNode2));
	}
}
