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

import java.util.List;
import java.util.Optional;

import org.jetuml.annotations.Singleton;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.geom.EdgePath;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.EdgePriority;
import org.jetuml.viewers.Layouter;
import org.jetuml.viewers.edges.EdgeStorage;
import org.jetuml.viewers.edges.StoredEdgeViewer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for class diagrams.
 */
@Singleton
public final class ClassDiagramRenderer// extends AbstractDiagramRenderer
{
	private static final StoredEdgeViewer STORED_EDGE_VIEWER = new StoredEdgeViewer();
	private final EdgeStorage aEdgeStorage = new EdgeStorage();
	private final Layouter aLayouter = new Layouter();
	
	public ClassDiagramRenderer()
	{
		
	}
	
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
		//draw and store nodes 
		RenderingFacade.activateNodeStorages();
		pDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		
		//plan edge paths using Layouter
		aEdgeStorage.clearStorage();
		aLayouter.layout(pDiagram);
		
		//draw edges using plan from EdgeStorage
		for (Edge edge : pDiagram.edges())
		{
			if (aEdgeStorage.contains(edge))
			{
				STORED_EDGE_VIEWER.draw(edge, pGraphics);
			}
			else
			{	//For edges which are not stored (note edges)
				RenderingFacade.draw(edge, pGraphics);
			}
		}
		RenderingFacade.deactivateAndClearNodeStorages();
	}
	
	protected void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		RenderingFacade.draw(pNode, pGraphics);
		pNode.getChildren().forEach(node -> drawNode(node, pGraphics));
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
	 * Returns the edge underneath the given point, if it exists.
	 * 
	 * @param pDiagram The diagram to query
	 * @param pPoint a point
	 * @return An edge containing pPoint or Optional.empty() if no edge is under pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	public final Optional<Edge> edgeAt(Diagram pDiagram, Point pPoint)
	{
		assert pDiagram != null && pPoint != null;
		Optional<Edge> storedEdge =  pDiagram.edges().stream()
				.filter(edge -> STORED_EDGE_VIEWER.contains(edge, pPoint))
				.findFirst();
		if (storedEdge.isEmpty())
		{
			//check if a Note edge is is at pPoint
			return pDiagram.edges().stream()
					.filter(edge -> RenderingFacade.contains(edge, pPoint))
					.findFirst();
		}
		else
		{
			return storedEdge;
		}
		
	}
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
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
				bounds = RenderingFacade.getBounds(node);
			}
			else
			{
				bounds = bounds.add(RenderingFacade.getBounds(node));
			}
		}
		//When getBounds(pDiagram) is called to open an existing class diagram file,
		//aEdgeStorage is initially empty and needs to be filled in order to compute the diagram bounds.
		if (aEdgeStorage.isEmpty())
		{
			aLayouter.layout(pDiagram);
		}
		for(Edge edge : pDiagram.edges())
		{
			if(EdgePriority.isStoredEdge(edge)) 
			{
				bounds = bounds.add(STORED_EDGE_VIEWER.getBounds(edge));
			}
			else //For note edges (which are not stored in EdgeStorage):
			{
				bounds.add(RenderingFacade.getBounds(edge));
			}
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
	 * Draws selection handles on selected diagram elements.
	 * @param pSelected the diagram element of interest
	 * @param pContext the graphics context
	 */
	public void drawSelectionHandles(DiagramElement pSelected, GraphicsContext pContext)
	{
		if (pSelected instanceof Edge && EdgePriority.isStoredEdge((Edge) pSelected))
		{
				STORED_EDGE_VIEWER.drawSelectionHandles((Edge) pSelected, pContext);
		}
		else
		{
			RenderingFacade.drawSelectionHandlesInternal(pSelected, pContext);
		}
	}
	
	/**
	 * Gets the EdgePath of pEdge from storage.
	 * @param pEdge the edge of interest
	 * @return the EdgePath describing the path of pEdge.
	 * @pre aEdgeStorage.contains(pEdge)
	 */
	public EdgePath storedEdgePath(Edge pEdge)
	{
		assert aEdgeStorage.contains(pEdge);
		return aEdgeStorage.getEdgePath(pEdge);
				
	}
	
	/**
	 * Returns whether pEdge is present in aEdgeStorage.
	 * @param pEdge the edge of interest
	 * @return true if aEgdeStorage contains pEgde, false otherwise.
	 * @pre pEdge != null;
	 */
	public boolean storageContains(Edge pEdge)
	{
		assert pEdge != null;
		return aEdgeStorage.contains(pEdge);
		
	}
	
	/**
	 * Returns a list of stored edges connected to pNode.
	 * @param pNode the node of interest
	 * @return a List of edges in storage which are connected to pNode.
	 * @pre pNode != null;
	 */
	public List<Edge> storedEdgesConnectedTo(Node pNode)
	{
		assert pNode != null;
		return aEdgeStorage.edgesConnectedTo(pNode);
	}
	
	/**
	 * Adds stores pEdge and pEdgePath in storage, or updates the EdgePath of pEdge if it is already present in storage.
	 * @param pEdge the edge to store
	 * @param pEdgePath the EdgePath of pEdge to be stored
	 * @pre pEdge != null && pEdgePath != null
	 */
	public void store(Edge pEdge, EdgePath pEdgePath)
	{
		assert pEdge != null && pEdgePath != null;
		aEdgeStorage.store(pEdge, pEdgePath);
	}
	
	/**
	 * Returns the edges in storage which are connected to both pEdge's start node and end node.
	 * @param pEdge the Edge of interest
	 * @return a list of edges in storage which are connected to both pEdge.getEnd() and pEdge.getStart()
	 * @pre pEdge != null
	 */
	public List<Edge> storedEdgesWithSameNodes(Edge pEdge)
	{
		assert pEdge != null;
		return aEdgeStorage.getEdgesWithSameNodes(pEdge);
	}
	
	/**
	 * Returns whether pPoint is available as a connection point based on Egdes which are already in storage.
	 * @param pPoint the Point of interest
	 * @return false if aEdgeStorage contains an EdgePath which starts of ends at pPoint. True otherwise. 
	 */
	public boolean connectionPointAvailableInStorage(Point pPoint)
	{
		return aEdgeStorage.connectionPointIsAvailable(pPoint);
	}
}

