/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jetuml.JavaFXLoader;
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
import org.junit.jupiter.api.Test;

public class ImplicitParameterNodeRendererTest {

	private static int userDefinedFontSize;
	private ImplicitParameterNode aImplicitParameterNode1 = new ImplicitParameterNode();
	private ImplicitParameterNode aImplicitParameterNode2 = new ImplicitParameterNode();
	private ImplicitParameterNode aImplicitParameterNode3 = new ImplicitParameterNode();
	private Diagram aDiagram = new Diagram(DiagramType.SEQUENCE);
	private SequenceDiagramRenderer aRenderer = new SequenceDiagramRenderer(aDiagram);
	private CallNode aDefaultCallNode1 = new CallNode();
	private CallNode aDefaultCallNode2 = new CallNode();
	private CallNode aCallNode1 = new CallNode();
	private CallEdge aCallEdge1 = new CallEdge();
	private ConstructorEdge aConstructorEdge1 = new ConstructorEdge();
	private ConstructorEdge aConstructorEdge2 = new ConstructorEdge();

	@BeforeAll
	public static void setupClass() {
		userDefinedFontSize = UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize);
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, UserPreferences.DEFAULT_FONT_SIZE);
		JavaFXLoader.load();
	}

	@AfterAll
	public static void restorePreferences() {
		UserPreferences.instance().setInteger(IntegerPreference.fontSize, userDefinedFontSize);
	}

	@Test
	void testGetBoundsWithOneNode() {
		assertEquals(new Rectangle(0, 0, 80, 120), aRenderer.getBounds(aImplicitParameterNode1));
	}

	@Test
	public void testGetBoundsWithOneNodeInDiagram() {
		aDiagram.addRootNode(aImplicitParameterNode1);
		assertEquals(new Rectangle(0, 0, 80, 120), aRenderer.getBounds(aImplicitParameterNode1));
	}

	@Test
	void testGetBoundsContructorCallWithFirstCallee() {
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.translate(100, 0);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);

		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);

		aConstructorEdge1.connect(aDefaultCallNode1, aDefaultCallNode2);
		aDiagram.addEdge(aConstructorEdge1);

		aRenderer.getBounds(); // Trigger rendering pass

		assertEquals(new Rectangle(0, 0, 80, 235), aRenderer.getBounds(aImplicitParameterNode1));
		assertEquals(new Rectangle(100, 100, 80, 115), aRenderer.getBounds(aImplicitParameterNode2));
	}

	@Test
	void testGetBoundsContructorCallWithSecondCallCallee1() {
		aImplicitParameterNode1.addChild(aDefaultCallNode1);
		aImplicitParameterNode2.translate(100, 0);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);

		aImplicitParameterNode3.translate(200, 0);
		aImplicitParameterNode3.addChild(aCallNode1);

		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aDiagram.addRootNode(aImplicitParameterNode3);

		aCallEdge1.connect(aDefaultCallNode1, aDefaultCallNode2);
		aConstructorEdge1.connect(aDefaultCallNode1, aCallNode1);
		aDiagram.addEdge(aCallEdge1);
		aDiagram.addEdge(aConstructorEdge1);

		aRenderer.getBounds(); // Trigger rendering pass

		assertEquals(new Rectangle(0, 0, 80, 285), aRenderer.getBounds(aImplicitParameterNode1));
		assertEquals(new Rectangle(100, 0, 80, 150), aRenderer.getBounds(aImplicitParameterNode2));
		assertEquals(new Rectangle(200, 150, 80, 115), aRenderer.getBounds(aImplicitParameterNode3));
	}

	@Test
	void testGetBoundsContructorCallWithSecondCallCallee2() {
		aImplicitParameterNode1.addChild(aDefaultCallNode1);

		aImplicitParameterNode2.translate(100, 0);
		aImplicitParameterNode2.addChild(aDefaultCallNode2);

		aImplicitParameterNode3.translate(200, 0);
		aImplicitParameterNode3.addChild(aCallNode1);

		aDiagram.addRootNode(aImplicitParameterNode1);
		aDiagram.addRootNode(aImplicitParameterNode2);
		aDiagram.addRootNode(aImplicitParameterNode3);

		aConstructorEdge1.connect(aDefaultCallNode1, aDefaultCallNode2);
		aConstructorEdge2.connect(aDefaultCallNode1, aCallNode1);
		aDiagram.addEdge(aConstructorEdge1);
		aDiagram.addEdge(aConstructorEdge2);

		aRenderer.getBounds(); // Trigger rendering pass

		assertEquals(new Rectangle(0, 0, 80, 350), aRenderer.getBounds(aImplicitParameterNode1));
		assertEquals(new Rectangle(100, 100, 80, 115), aRenderer.getBounds(aImplicitParameterNode2));
		assertEquals(new Rectangle(200, 215, 80, 115), aRenderer.getBounds(aImplicitParameterNode3));
	}
}
