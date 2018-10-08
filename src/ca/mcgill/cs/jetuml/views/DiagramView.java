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

package ca.mcgill.cs.jetuml.views;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;

/**
 * A wrapper for a diagram that can draw the diagram on a graphics context
 * and provide information about the geometry of the diagram.
 */
public class DiagramView
{
	protected final Diagram aDiagram;
	
	/**
	 * Creates a new DiagramView that wraps pDiagram.
	 * @param pDiagram The wrapped diagram.
	 * @pre pDiagram != null.
	 */
	public DiagramView(Diagram pDiagram)
	{
		aDiagram = pDiagram;
	}
	
	/**
	 * @return The diagram wrapped by this view.
	 */
	public Diagram getDiagram()
	{
		return aDiagram;
	}
	
	/**
	 * Draws the diagram onto pGraphics.
	 * 
	 * @param pGraphics the graphics context where the
	 * diagram should be drawn.
	 * @pre pGraphics != null.
	 */
	public final void draw(GraphicsContext pGraphics)
	{
		assert pGraphics != null;
		aDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		aDiagram.edges().forEach( edge -> edge.view().draw(pGraphics));
	}
	
	private void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		pNode.view().draw(pGraphics);
		if(pNode instanceof ParentNode)
		{
			((ParentNode)pNode).getChildren().forEach(node -> drawNode(node, pGraphics));
		}
	}
	
	/**
	 * Finds the edge that contains the given point, if it 
	 * exists.
	 * 
	 * @param pPoint a point
	 * @return An edge containing pPoint or null if no edge contains pPoint
	 * @pre pPoint != null
	 */
	public final Optional<Edge> findEdge(Point pPoint)
	{
		assert pPoint != null;
		for(Edge edge : aDiagram.edges())
		{
			if(edge.view().contains(pPoint))
			{
				return Optional.of(edge);
			}
		}
		return Optional.empty();
	}
	
	/**
     * Finds a node that contains the given point. Always returns
     * the deepest child and the last one in a list.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pPoint != null.
     */
	public final Optional<Node> findNode(Point pPoint)
	{
		Node result = null;
		for(Node node : aDiagram.rootNodes())
		{
			Node temp = deepFindNode(node, pPoint);
			if (temp != null)
			{
				result = temp;
			}
		}
		return Optional.ofNullable(result);
	}
	
	/**
	 * Find the "deepest" child that contains pPoint,
	 * where depth is measured in terms of distance from
	 * pNode along the parent-child relation.
	 * @param pNode The starting node for the search.
	 * @param pPoint The point to test for.
	 * @return The deepest child containing pPoint,
	 * or null if pPoint is not contained by pNode or 
	 * any of its children.
	 * @pre pNode != null, pPoint != null;
	 */
	protected Node deepFindNode(Node pNode, Point pPoint)
	{
		assert pNode != null && pPoint != null;
		Node node = null;
		if (pNode instanceof ParentNode)
		{
			for (Node child : ((ParentNode) pNode).getChildren())
			{
				node = deepFindNode(child, pPoint);
				if(node != null)
				{
					return node;
				}
			}
		}
		if (pNode.view().contains(pPoint))
		{
			return pNode;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
	 * 
	 * @return The bounding rectangle
	 */
	public final Rectangle getBounds()
	{
		Rectangle bounds = null;
		for(Node node : aDiagram.rootNodes() )
		{
			if(bounds == null)
			{
				bounds = node.view().getBounds();
			}
			else
			{
				bounds = bounds.add(node.view().getBounds());
			}
		}
		for(Edge edge : aDiagram.edges())
		{
			bounds = bounds.add(edge.view().getBounds());
		}
		if(bounds == null )
		{
			return new Rectangle(0, 0, 0, 0);
		}
		else
		{
			return new Rectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
		}
	}
}
