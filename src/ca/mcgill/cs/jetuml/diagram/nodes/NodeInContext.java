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

package ca.mcgill.cs.jetuml.diagram.nodes;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.Properties;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 * A wrapper for a node that stores a reference to the 
 * diagram that contains the node.
 */
public final class NodeInContext implements Node
{
	private final Node aNode;
	private final Diagram aDiagram;
	
	/**
	 * @param pNode The node to wrap
	 * @param pDiagram The diagram that contains this node.
	 */
	public NodeInContext(Node pNode, Diagram pDiagram)
	{
		aNode = pNode;
		aDiagram = pDiagram;
	}
	
	@Override
	public Properties properties()
	{
		return aNode.properties();
	}

	@Override
	public Point position()
	{
		return aNode.position();
	}

	@Override
	public void moveTo(Point pPoint)
	{
		aNode.moveTo(pPoint);
	}

	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		aNode.translate(pDeltaX, pDeltaY);	
	}

	@Override
	public Node clone()
	{
		assert false; // This method should not be used
		try
		{
			return (Node) super.clone();
		}
		catch( CloneNotSupportedException e )
		{
			return null;
		}
	}

	@Override
	public NodeView view()
	{
		return aNode.view();
	}
	
	/**
	 * @return The diagram that contains this node.
	 */
	public Diagram getDiagram()
	{
		return aDiagram;
	}
	
	/**
	 * @return The wrapped node.
	 */
	public Node getNode()
	{
		return aNode;
	}
	
}
