/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
 ******************************************************************************/
package org.jetuml.rendering;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Optional;

import org.jetuml.diagram.ControlFlow;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.edges.CallEdgeRenderer;
import org.jetuml.rendering.edges.ReturnEdgeRenderer;
import org.jetuml.rendering.nodes.CallNodeRenderer;
import org.jetuml.rendering.nodes.ImplicitParameterNodeRenderer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for sequence diagrams.
 */
public final class SequenceDiagramRenderer extends AbstractDiagramRenderer
{
	private final Map<Node, Integer> aLifelineXPositions = new IdentityHashMap<>();
	
	public SequenceDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(CallNode.class, new CallNodeRenderer(this));
		addElementRenderer(ImplicitParameterNode.class, new ImplicitParameterNodeRenderer(this));
		addElementRenderer(ReturnEdge.class, new ReturnEdgeRenderer(this));
		addElementRenderer(CallEdge.class, new CallEdgeRenderer(this));
		addElementRenderer(ConstructorEdge.class, new CallEdgeRenderer(this));
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics); // TODO Remove
		assert pGraphics != null;
		computeLifelineXPositions();
//		activateNodeStorages();
		// 1. Compute lifeline x positions by iterating through implicit parameter nodes
		// 2. Compute call node y positions by iterating through call nodes in call sequence order
		// 3. Compute call node bottom y coordinate by iterating through call nodes in reverse call sequence order
		// 4. Render nodes
		// 4. Render edges
//		aDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
//		aDiagram.edges().forEach(edge -> draw(edge, pGraphics));
//		deactivateAndClearNodeStorages();
	}
	
	private void computeLifelineXPositions()
	{
		aLifelineXPositions.clear();
		for( Node node : diagram().rootNodes() )
		{
			if(node.getClass() == ImplicitParameterNode.class)
			{
				aLifelineXPositions.put(node, implicitParameterNodeRenderer().getCenterXCoordinate(node));
			}
		}
	}
	
	private ImplicitParameterNodeRenderer implicitParameterNodeRenderer()
	{
		return (ImplicitParameterNodeRenderer)rendererFor(ImplicitParameterNode.class);
	}
	
	@Override
	protected Optional<Node> deepFindNode(Node pNode, Point pPoint)
	{
		Optional<Node> result = Optional.empty();
		if( pNode.getClass() == CallNode.class )
		{
			result = new ControlFlow(diagram()).getCallees(pNode).stream()
				.map(node -> deepFindNode(node, pPoint))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.findFirst();
		}
		return result.or(() -> super.deepFindNode(pNode, pPoint));
	}
	
	/*
	 * This specialized version supports selecting implicit parameter nodes only by 
	 * selecting their top rectangle.
	 */
	@Override
	public Optional<Node> selectableNodeAt(Point pPoint)
	{
		Optional<Node> topRectangleSelected = diagram().rootNodes().stream()
			.filter(node -> node.getClass() == ImplicitParameterNode.class)
			.filter(node -> ((ImplicitParameterNodeRenderer)rendererFor(ImplicitParameterNode.class)).getTopRectangle(node).contains(pPoint))
			.findFirst();
		return topRectangleSelected.or(() -> super.selectableNodeAt(pPoint));				
	}
}
