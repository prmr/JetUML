/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.StateTransitionEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.FinalStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InitialStateNode;
import ca.mcgill.cs.jetuml.diagram.nodes.StateNode;

public class TestStateDiagramEdgeConstraints
{
	private Diagram aDiagram;
	private StateNode aState;
	private InitialStateNode aInitialNode;
	private FinalStateNode aFinalNode;
	private StateTransitionEdge aEdge;

	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.STATE);
		aState = new StateNode();
		aInitialNode = new InitialStateNode();
		aFinalNode = new FinalStateNode();
		aEdge = new StateTransitionEdge();
	}
	
	private void createDiagram()
	{
		aDiagram.addRootNode(aState);
		aDiagram.addRootNode(aInitialNode);
		aDiagram.addRootNode(aFinalNode);
	}
	
	@Test
	public void testNoEdgeToInitialNodeFalse()
	{
		createDiagram();
		assertFalse(StateDiagramEdgeConstraints.noEdgeToInitialNode(aInitialNode).satisfied());
	}
	
	@Test
	public void testNoEdgeToInitialNodeTrue()
	{
		createDiagram();
		assertTrue(StateDiagramEdgeConstraints.noEdgeToInitialNode(aState).satisfied());
	}
	
	@Test
	public void testNoEdgeFromFinalNodeInapplicableEdge()
	{
		createDiagram();
		assertTrue(StateDiagramEdgeConstraints.noEdgeFromFinalNode(new NoteEdge(), aFinalNode).satisfied());
	}
	
	@Test
	public void testNoEdgeFromFinalNodeApplicableEdgeFalse()
	{
		createDiagram();
		assertFalse(StateDiagramEdgeConstraints.noEdgeFromFinalNode(aEdge, aFinalNode).satisfied());
	}
	
	@Test
	public void testNoEdgeFromFinalNodeApplicableEdgeTrue()
	{
		createDiagram();
		assertTrue(StateDiagramEdgeConstraints.noEdgeFromFinalNode(aEdge, aState).satisfied());
	}
}