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

package org.jetuml.viewers;

import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;

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
	public void draw(Diagram pDiagram, GraphicsContext pGraphics)
	{
		assert pDiagram != null && pGraphics != null;
		RenderingFacade.activateNodeStorages();
		pDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		pDiagram.edges().forEach(edge -> RenderingFacade.draw(edge, pGraphics));
		RenderingFacade.deactivateAndClearNodeStorages();
	}
	
	protected void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		RenderingFacade.draw(pNode, pGraphics);
		pNode.getChildren().forEach(node -> drawNode(node, pGraphics));
	}
	
	/**
	 * Returns the edge underneath the given point, if it exists.
	 * 
	 * @param pDiagram The diagram to query
	 * @param pPoint a point
	 * @return An edge containing pPoint or Optional.empty() if no edge is under pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	public Optional<Edge> edgeAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		return pDiagram.edges().stream()
				.filter(edge -> RenderingFacade.contains(edge, pPoint))
				.findFirst();
	}
		
	/**
     * Finds a node that contains the given point. Always returns
     * the deepest child and the last one in a list.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public final Optional<Node> nodeAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		return pDiagram.rootNodes().stream()
			.map(node -> deepFindNode(pDiagram, node, pPoint))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.reduce((first, second) -> second);
	}
	
	/**
     * Finds a node that contains the given point, if this is a node that can be 
     * selected. The difference between this method and nodeAt is that it is specialized for
     * nodes that can be selected by the users, whereas nodeAt is also used for edge creation.
     * By default, this method has the same behavior as nodeAt.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public Optional<Node> selectableNodeAt(Diagram pDiagram, Point pPoint)
	{
		return nodeAt(pDiagram, pPoint);
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
	protected Optional<Node> deepFindNode(Diagram pDiagram, Node pNode, Point pPoint)
	{
		assert pDiagram != null && pNode != null && pPoint != null;
		
		return pNode.getChildren().stream()
			.map(node -> deepFindNode(pDiagram, node, pPoint))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst()
			.or( () -> Optional.of(pNode).filter(originalNode -> RenderingFacade.contains(originalNode, pPoint)));
	}
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
	 * 
	 * @param pDiagram The diagram to query
	 * @return The bounding rectangle
	 * @pre pDiagram != null
	 */
	public Rectangle getBounds(Diagram pDiagram)
	{
		assert pDiagram != null;
		Rectangle bounds = null;
		for(Node node : pDiagram.rootNodes() )
		{
			if(bounds == null)
			{
				bounds = RenderingFacade.getBounds(node);
			}
			else
			{
				bounds = bounds.add(RenderingFacade.getBounds(node));
			}
		}
		for(Edge edge : pDiagram.edges())
		{
			bounds = bounds.add(RenderingFacade.getBounds(edge));
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
	 * Obtains the bounds for an element.
	 * 
	 * @param pElement The element whose bounds we want
	 * @return The bounds for this element.
	 * @pre pElement != null
	 */
	public static Rectangle getBounds(DiagramElement pElement)
	{
		assert pElement != null;
		if( pElement instanceof Node )
		{
			return RenderingFacade.getBounds((Node)pElement);
		}
		else
		{
			assert pElement instanceof Edge;
			return RenderingFacade.getBounds((Edge)pElement);
		}
	}
}
