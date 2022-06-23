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

import org.jetuml.annotations.Singleton;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.EdgePath;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.Layouter;
import org.jetuml.viewers.edges.EdgeStorage;
import org.jetuml.viewers.edges.StoredEdgeViewer;
import org.jetuml.viewers.nodes.InterfaceNodeViewer;
import org.jetuml.viewers.nodes.PackageDescriptionNodeViewer;
import org.jetuml.viewers.nodes.PackageNodeViewer;
import org.jetuml.viewers.nodes.TypeNodeViewer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for class diagrams.
 */
@Singleton
public final class ClassDiagramRenderer extends AbstractDiagramRenderer
{
	private static final StoredEdgeViewer STORED_EDGE_VIEWER = new StoredEdgeViewer();
	private final EdgeStorage aEdgeStorage = new EdgeStorage();
	private final Layouter aLayouter = new Layouter();
	
	public ClassDiagramRenderer()
	{
		addElementRenderer(ClassNode.class, new TypeNodeViewer());
		addElementRenderer(InterfaceNode.class, new InterfaceNodeViewer());
		addElementRenderer(PackageNode.class, new PackageNodeViewer());
		addElementRenderer(PackageDescriptionNode.class, new PackageDescriptionNodeViewer());
		
		addElementRenderer(DependencyEdge.class, STORED_EDGE_VIEWER);
		addElementRenderer(AssociationEdge.class,  STORED_EDGE_VIEWER);
		addElementRenderer(DependencyEdge.class, STORED_EDGE_VIEWER);
		addElementRenderer(GeneralizationEdge.class, STORED_EDGE_VIEWER);
		addElementRenderer(AggregationEdge.class, STORED_EDGE_VIEWER);
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
		activateNodeStorages();
		pDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		
		//plan edge paths using Layouter
		aEdgeStorage.clearStorage();
		aLayouter.layout(pDiagram);
		
		//draw edges using plan from EdgeStorage
		pDiagram.edges().forEach(edge -> draw(edge, pGraphics));
		deactivateAndClearNodeStorages();
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
		//When getBounds(pDiagram) is called to open an existing class diagram file,
		//aEdgeStorage is initially empty and needs to be filled in order to compute the diagram bounds.
		if (aEdgeStorage.isEmpty())
		{
			aLayouter.layout(pDiagram);
		}
		return super.getBounds(pDiagram);
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

