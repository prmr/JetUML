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

package ca.mcgill.cs.jetuml.views;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import javafx.scene.canvas.GraphicsContext;

/**
 * A strategy for drawing a diagram and computing geometric properties of a 
 * diagram. This class can be inherited if certain diagram types require specialized 
 * services. This class is stateless.
 */
public class DiagramViewer
{
	/**
	 * Draws pDiagram onto pGraphics.
	 * 
	 * @param pGraphics the graphics context where the
	 *     diagram should be drawn.
	 * @param pDiagram the diagram to draw.
	 * @pre pDiagram != null && pGraphics != null.
	 */
	public final void draw(Diagram pDiagram, GraphicsContext pGraphics)
	{
		assert pDiagram != null && pGraphics != null;
		pDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		pDiagram.edges().forEach(edge -> EdgeViewerRegistry.draw(edge, pGraphics));
	}
	
	private void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		NodeViewerRegistry.draw(pNode, pGraphics);
		pNode.getChildren().forEach(node -> drawNode(node, pGraphics));
	}
	
	/**
	 * Finds the edge that contains the given point, if it 
	 * exists.
	 * 
	 * @param pDiagram The diagram to query
	 * @param pPoint a point
	 * @return An edge containing pPoint or null if no edge contains pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	public final Optional<Edge> findEdge(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		for(Edge edge : pDiagram.edges())
		{
			if(EdgeViewerRegistry.contains(edge, pPoint))
			{
				return Optional.of(edge);
			}
		}
		return Optional.empty();
	}
	
	/**
     * Finds a node that contains the given point. Always returns
     * the deepest child and the last one in a list.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public final Optional<Node> findNode(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		Node result = null;
		for(Node node : pDiagram.rootNodes())
		{
			Node temp = deepFindNode(pDiagram, node, pPoint);
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
	 * @param pDiagram The diagram to query.
	 * @param pNode The starting node for the search.
	 * @param pPoint The point to test for.
	 * @return The deepest child containing pPoint,
	 *     or null if pPoint is not contained by pNode or 
	 *     any of its children.
	 * @pre pNode != null, pPoint != null;
	 */
	protected Node deepFindNode(Diagram pDiagram, Node pNode, Point pPoint)
	{
		assert pDiagram != null && pNode != null && pPoint != null;
		Node node = null;
		for( Node child : pNode.getChildren() )
		{
			node = deepFindNode(pDiagram, child, pPoint);
			if( node != null )
			{
				return node;
			}
		}
		if( NodeViewerRegistry.contains(pNode, pPoint))
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
	 * @param pDiagram The diagram to query
	 * @return The bounding rectangle
	 * @pre pDiagram != null
	 */
	public final Rectangle getBounds(Diagram pDiagram)
	{
		assert pDiagram != null;
		Rectangle bounds = null;
		for(Node node : pDiagram.rootNodes() )
		{
			if(bounds == null)
			{
				bounds = NodeViewerRegistry.getBounds(node);
			}
			else
			{
				bounds = bounds.add(NodeViewerRegistry.getBounds(node));
			}
		}
		for(Edge edge : pDiagram.edges())
		{
			bounds = bounds.add(EdgeViewerRegistry.getBounds(edge));
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
	
	/**
	 * Used during pasting to determine whether the current selection bounds completely overlaps the new elements.
	 * @param pCurrentSelectionBounds The current selection bounds
	 * @param pNewElements Elements to be pasted
	 * @return Is the current selection bounds completely overlapping the new elements
	 */
	public boolean isOverlapping(Rectangle pCurrentSelectionBounds, Iterable<DiagramElement> pNewElements) 
	{
		Rectangle newElementBounds = null;
		for (DiagramElement element : pNewElements) 
		{
			if (newElementBounds == null) 
			{
				newElementBounds = ViewerUtilities.getBounds(element);
			}
			newElementBounds = newElementBounds.add(ViewerUtilities.getBounds(element));
		}
		if (pCurrentSelectionBounds.equals(newElementBounds)) 
		{
			return true;
		}
		return false;
	}
}
