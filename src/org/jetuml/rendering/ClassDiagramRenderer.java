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

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.geom.EdgePath;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.Layouter;
import org.jetuml.viewers.edges.StoredEdgeViewer;
import org.jetuml.viewers.nodes.InterfaceNodeViewer;
import org.jetuml.viewers.nodes.PackageDescriptionNodeViewer;
import org.jetuml.viewers.nodes.PackageNodeViewer;
import org.jetuml.viewers.nodes.TypeNodeViewer;

import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for class diagrams.
 */
public final class ClassDiagramRenderer extends AbstractDiagramRenderer
{
	private static final StoredEdgeViewer STORED_EDGE_VIEWER = new StoredEdgeViewer();
	private final Layouter aLayouter = new Layouter();
	
	public ClassDiagramRenderer(Diagram pDiagram)
	{
		super(pDiagram);
		addElementRenderer(ClassNode.class, new TypeNodeViewer(this));
		addElementRenderer(InterfaceNode.class, new InterfaceNodeViewer(this));
		addElementRenderer(PackageNode.class, new PackageNodeViewer(this));
		addElementRenderer(PackageDescriptionNode.class, new PackageDescriptionNodeViewer(this));
		
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
		if (aLayouter.isEmpty())
		{
			aLayouter.layout(pDiagram);
		}
		return super.getBounds(pDiagram);
	}
	
	public Optional<EdgePath> getStoredEdgePath(Edge pEdge)
	{
		return aLayouter.getStoredEdgePath(pEdge);
	}
}

