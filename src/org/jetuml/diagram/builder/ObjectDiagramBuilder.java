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

package org.jetuml.diagram.builder;

import java.util.ArrayList;

import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.builder.constraints.ConstraintSet;
import org.jetuml.diagram.builder.constraints.EdgeConstraints;
import org.jetuml.diagram.builder.constraints.ObjectDiagramEdgeConstraints;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Point;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.RenderingFacade;

/**
 * A builder for object diagram.
 */
public class ObjectDiagramBuilder extends DiagramBuilder
{
	private static final ConstraintSet CONSTRAINTS = new ConstraintSet(
			
			EdgeConstraints.noteEdge(),
			EdgeConstraints.noteNode(),
			EdgeConstraints.maxEdges(1),
			ObjectDiagramEdgeConstraints.collaboration(),
			ObjectDiagramEdgeConstraints.reference()
		);
			
	/**
	 * Creates a new builder for object diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	public ObjectDiagramBuilder( DiagramRenderer pDiagramRenderer )
	{
		super( pDiagramRenderer );
		assert pDiagramRenderer.diagram().getType() == DiagramType.OBJECT;
	}
	
	@Override
	protected ConstraintSet getEdgeConstraints()
	{
		return CONSTRAINTS;
	}
	
	@Override
	protected void completeEdgeAdditionOperation( CompoundOperation pOperation, Edge pEdge, Node pStartNode, Node pEndNode,
			Point pStartPoint, Point pEndPoint)
	{
		super.completeEdgeAdditionOperation(pOperation, pEdge, pStartNode, pEndNode, pStartPoint, pEndPoint);
		if( pStartNode.getClass() == FieldNode.class )
		{
			final FieldNode node = (FieldNode) pStartNode;
			final String oldValue = node.getValue();
			pOperation.add(new SimpleOperation(()-> node.setValue(""),
					()-> node.setValue(oldValue)));
		}
	}
	
	@Override
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		boolean result = true;
		if( pNode instanceof FieldNode && findObject((FieldNode)pNode, pRequestedPosition) == null )
		{
			result = false;
		}
		return result;
	}
	
	/* Find if the node to be added can be added to an object. Returns null if not. 
	 * If a node is already the parent of the field (from a previously undone operation),
	 * return this node. Otherwise, find if a node is at the point
	 */
	private ObjectNode findObject(FieldNode pNode, Point pPoint)
	{
		ArrayList<ObjectNode> candidates = new ArrayList<>();
		for( Node node : aDiagramRenderer.diagram().rootNodes() )
		{
			if( node == pNode )
			{
				continue;
			}
			else if( pNode.hasParent() && pNode.getParent() == node )
			{
				return (ObjectNode)node;
			}
			else if( RenderingFacade.contains(node, pPoint) && canAddNodeAsChild(node, pNode))
			{
				candidates.add((ObjectNode)node); // canAddNodeAsChild ensures the downcast is valid
			}
		}
		// Pick the last node in the list as some inexact but simple
		// heuristic for choosing the top node. We'll need a z-coordinate to do better.
		if( candidates.size() > 0 )
		{
			return candidates.get(candidates.size()-1);
		}
		else
		{
			return null;
		}
	}
	
	private static boolean canAddNodeAsChild(Node pParent, Node pPotentialChild)
	{
		if( pParent instanceof ObjectNode )
		{
			return pPotentialChild instanceof FieldNode;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public DiagramOperation createAddNodeOperation(Node pNode, Point pRequestedPosition)
	{
		assert canAdd(pNode, pRequestedPosition);
		DiagramOperation result = null;
		if( pNode instanceof FieldNode )
		{
			ObjectNode object = findObject((FieldNode)pNode, pRequestedPosition);
			
			if( object != null )
			{
				result = new SimpleOperation( 
						()-> 
						{ pNode.attach(aDiagramRenderer.diagram()); 
						object.addChild(pNode); },
						()-> 
						{ 
							pNode.detach();
							object.removeChild(pNode);
						}
						);
			}
		}
		if( result == null )
		{
			result = super.createAddNodeOperation(pNode, pRequestedPosition);
		}
		return result;
	}

}
