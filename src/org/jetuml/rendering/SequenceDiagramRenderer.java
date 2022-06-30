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
import org.jetuml.viewers.edges.CallEdgeViewer;
import org.jetuml.viewers.edges.ReturnEdgeViewer;
import org.jetuml.viewers.nodes.CallNodeViewer;
import org.jetuml.viewers.nodes.ImplicitParameterNodeViewer;

/**
 * The renderer for sequence diagrams.
 */
public final class SequenceDiagramRenderer extends AbstractDiagramRenderer
{
	public SequenceDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(CallNode.class, new CallNodeViewer(this));
		addElementRenderer(ImplicitParameterNode.class, new ImplicitParameterNodeViewer(this));
		addElementRenderer(ReturnEdge.class, new ReturnEdgeViewer(this));
		addElementRenderer(CallEdge.class, new CallEdgeViewer(this));
		addElementRenderer(ConstructorEdge.class, new CallEdgeViewer(this));
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
			.filter(node -> ((ImplicitParameterNodeViewer)rendererFor(ImplicitParameterNode.class)).getTopRectangle(node).contains(pPoint))
			.findFirst();
		return topRectangleSelected.or(() -> super.selectableNodeAt(pPoint));				
	}
}
