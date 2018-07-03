/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.diagram.builder;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ImplicitParameterNode;
import ca.mcgill.cs.jetuml.geom.Point;

public class SequenceDiagramBuilder extends DiagramBuilder
{
	private static final int CALL_NODE_YGAP = 5;
	
	public SequenceDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram instanceof SequenceDiagram;
	}

	@Override
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		boolean result = true;
		if(pNode instanceof CallNode && insideTargetArea(pRequestedPosition) == null)
		{
			result = false;
		}
		return result;
	}
	
	/* 
	 * Adds the node, ensuring that call nodes can only be added if the
	 * point is inside the space of the related ImplicitParameterNode
	 * @see ca.mcgill.cs.jetuml.diagram.Diagram#add(ca.mcgill.cs.jetuml.diagram.Node, java.awt.geom.Point2D)
	 */
	@Override
	public void addNode(Node pNode, Point pPoint, int pMaxWidth, int pMaxHeight)
	{
		if(pNode instanceof CallNode) 
		{
			ImplicitParameterNode target = insideTargetArea(pPoint);
			if( target != null )
			{
				target.addChild((ChildNode)pNode);
			}
			else
			{
				return;
			}
		}
		super.addNode(pNode, pPoint, pMaxWidth, pMaxHeight);
	}
	
	/*
	 * If pPoint is inside an ImplicitParameterNode but below its top
	 * rectangle, returns that node. Otherwise, returns null.
	 */
	public ImplicitParameterNode insideTargetArea(Point pPoint)
	{
		for( Node node : aDiagram.getRootNodes() )
		{
			if(node instanceof ImplicitParameterNode && node.view().contains(pPoint))
			{
				if( !(pPoint.getY() < ((ImplicitParameterNode)node).getTopRectangle().getMaxY() + CALL_NODE_YGAP))
				{
					return (ImplicitParameterNode) node;
				}
			}
		}
		return null;
	}
}
