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

package org.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.junit.jupiter.api.Test;

public class TestStateDiagramEdgeConstraints
{
	private Diagram aDiagram = new Diagram(DiagramType.STATE);
	private DiagramRenderer aRenderer = DiagramType.newRendererInstanceFor(aDiagram);
	private StateNode aState = new StateNode();
	private InitialStateNode aInitialNode = new InitialStateNode();
	private FinalStateNode aFinalNode = new FinalStateNode();
	private StateTransitionEdge aEdge = new StateTransitionEdge();
	private Point aPoint = new Point(0,0);

	private void createDiagram()
	{
		aDiagram.addRootNode(aState);
		aDiagram.addRootNode(aInitialNode);
		aDiagram.addRootNode(aFinalNode);
	}
	
	@Test
	void testNoEdgeToInitialNodeFalse()
	{
		createDiagram();
		assertFalse(StateDiagramEdgeConstraints.noEdgeToInitialNode().satisfied(aEdge, aState, aInitialNode, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoEdgeToInitialNodeTrue()
	{
		createDiagram();
		assertTrue(StateDiagramEdgeConstraints.noEdgeToInitialNode().satisfied(aEdge, aInitialNode, aState, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoEdgeFromFinalNodeInapplicableEdge()
	{
		createDiagram();
		assertTrue(StateDiagramEdgeConstraints.noEdgeFromFinalNode().satisfied(new NoteEdge(), aFinalNode, aState, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoEdgeFromFinalNodeApplicableEdgeFalse()
	{
		createDiagram();
		assertFalse(StateDiagramEdgeConstraints.noEdgeFromFinalNode().satisfied(aEdge, aFinalNode, aState, aPoint, aPoint, aRenderer));
	}
	
	@Test
	void testNoEdgeFromFinalNodeApplicableEdgeTrue()
	{
		createDiagram();
		assertTrue(StateDiagramEdgeConstraints.noEdgeFromFinalNode().satisfied(aEdge, aState, aState, aPoint, aPoint, aRenderer));
	}
}