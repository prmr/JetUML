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

package org.jetuml.diagram.builder;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.StateNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

public class TestStateDiagramBuilder
{
	private Diagram aDiagram;
	private StateDiagramBuilder aBuilder;
	private InitialStateNode aInitial;
	private StateNode aStateNode1;
	private StateNode aStateNode2;
	private FinalStateNode aFinal;
	private NoteNode aNoteNode;
	
	private StateTransitionEdge aEdge1;
	private StateTransitionEdge aEdge2;
	private StateTransitionEdge aEdge3;
	private NoteEdge aNoteEdge;
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@BeforeEach
	public void setUp()
	{
		aDiagram = new Diagram(DiagramType.STATE);
		aBuilder = new StateDiagramBuilder(aDiagram);
		
		aInitial = new InitialStateNode();
		aStateNode1 = new StateNode();
		aStateNode2 = new StateNode();
		aFinal = new FinalStateNode();
		aNoteNode = new NoteNode();
		
		aEdge1 = new StateTransitionEdge();
		aEdge2 = new StateTransitionEdge();
		aEdge3 = new StateTransitionEdge();
		aNoteEdge = new NoteEdge();
	}
	
	private void connectAndAdd(Edge pEdge, Node pStart, Node pEnd)
	{
		pEdge.connect(pStart, pEnd);
		aDiagram.addEdge(pEdge);
	}
}
