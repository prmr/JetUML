/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.ObjectDiagram;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.edges.CallEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ReturnEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class TestSequenceDiagramEdgeConstraints
{
	private SequenceDiagram aDiagram;
	private ImplicitParameterNode aParameter1;
	private ImplicitParameterNode aParameter2;
	private CallNode aCallNode1;
	private CallNode aCallNode2;
	private CallNode aCallNode3;
	private CallEdge aCallEdge;
	private ReturnEdge aReturnEdge;

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
	public void setUp()
	{
		aDiagram = new SequenceDiagram();
		aParameter1 = new ImplicitParameterNode();
		aParameter2 = new ImplicitParameterNode();
		aCallNode1 = new CallNode();
		aCallNode2 = new CallNode();	
		aCallNode3 = new CallNode();
		aCallEdge = new CallEdge();
		aReturnEdge = new ReturnEdge();
	}
	
	private void createDiagram()
	{
		aDiagram.addRootNode(aParameter1);
		aDiagram.addRootNode(aParameter2);
		aParameter1.addChild(aCallNode1);
		aParameter1.addChild(aCallNode3);
		aParameter2.addChild(aCallNode2);
	}
	
	@Test
	public void testNoEdgeFromParameterNodeNotParameterNode()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.noEdgesFromParameter(aCallNode1).satisfied());
	}
	
	@Test
	public void testNoEdgeFromParameterNodeParameterNode()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.noEdgesFromParameter(aParameter1).satisfied());
	}
	
	@Test
	public void testreturnEdgeNotReturnEdge()
	{
		createDiagram();
		assertTrue(SequenceDiagramEdgeConstraints.returnEdge(aCallEdge, aCallNode1, aCallNode2, aDiagram).satisfied());
	}
	
	@Test
	public void testreturnEdgeIncompatibleStart()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge(aReturnEdge, aParameter1, aCallNode2, aDiagram).satisfied());
	}
	
	@Test
	public void testreturnEdgeIncompatibleEnd()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge(aReturnEdge, aCallNode1, aParameter2, aDiagram).satisfied());
	}
	
	@Test
	public void testreturnEdgeEndNoCaller()
	{
		createDiagram();
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge(aReturnEdge, aCallNode1, aCallNode2, aDiagram).satisfied());
	}
	
	@Test
	public void testreturnEdgeEndNotCaller()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge);
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge(aReturnEdge, aCallNode2, aCallNode3, aDiagram).satisfied());
	}
	
	@Test
	public void testreturnEdgeSelfCaller()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode3, aDiagram);
		aDiagram.addEdge(aCallEdge);
		assertFalse(SequenceDiagramEdgeConstraints.returnEdge(aReturnEdge, aCallNode3, aCallNode1, aDiagram).satisfied());
	}
	
	@Test
	public void testreturnEdgeValid()
	{
		createDiagram();
		aCallEdge.connect(aCallNode1, aCallNode2, aDiagram);
		aDiagram.addEdge(aCallEdge);
		assertTrue(SequenceDiagramEdgeConstraints.returnEdge(aReturnEdge, aCallNode2, aCallNode1, aDiagram).satisfied());
	}
	
	
}