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

import java.util.IdentityHashMap;
import java.util.Optional;

import org.jetuml.annotations.Singleton;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.viewers.DiagramElementRenderer;
import org.jetuml.viewers.edges.NoteEdgeViewer;
import org.jetuml.viewers.edges.UseCaseAssociationEdgeViewer;
import org.jetuml.viewers.edges.UseCaseDependencyEdgeViewer;
import org.jetuml.viewers.edges.UseCaseGeneralizationEdgeViewer;
import org.jetuml.viewers.nodes.ActorNodeViewer;
import org.jetuml.viewers.nodes.NodeViewer;
import org.jetuml.viewers.nodes.NoteNodeViewer;
import org.jetuml.viewers.nodes.PointNodeViewer;
import org.jetuml.viewers.nodes.UseCaseNodeViewer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * The renderer for use case diagrams.
 */
@Singleton
public final class UseCaseDiagramRenderer implements DiagramRenderer
{
	private IdentityHashMap<Class<? extends DiagramElement>, DiagramElementRenderer> aRenderers = 
			new IdentityHashMap<>();
	
	public static final UseCaseDiagramRenderer INSTANCE = new UseCaseDiagramRenderer();
	
	private UseCaseDiagramRenderer()
	{
		aRenderers.put(ActorNode.class, new ActorNodeViewer());
		aRenderers.put(NoteNode.class, new NoteNodeViewer());
		aRenderers.put(PointNode.class, new PointNodeViewer());
		aRenderers.put(UseCaseNode.class, new UseCaseNodeViewer());
		aRenderers.put(NoteEdge.class, new NoteEdgeViewer());
		aRenderers.put(UseCaseAssociationEdge.class, new UseCaseAssociationEdgeViewer());
		aRenderers.put(UseCaseGeneralizationEdge.class, new UseCaseGeneralizationEdgeViewer());
		aRenderers.put(UseCaseDependencyEdge.class, new UseCaseDependencyEdgeViewer());
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
		assert pDiagram != null && pGraphics != null;
		activateNodeStorages();
		pDiagram.rootNodes().forEach(node -> drawNode(node, pGraphics));
		pDiagram.edges().forEach(edge -> draw(edge, pGraphics));
		deactivateAndClearNodeStorages();
	}
	
	/**
	 * Activates all the NodeStorages of the NodeViewers present in the registry. 
	 */
	public void activateNodeStorages()
	{
		aRenderers.values().stream()
			.filter(renderer -> NodeViewer.class.isAssignableFrom(renderer.getClass()))
			.map(NodeViewer.class::cast)
			.forEach(NodeViewer::activateNodeStorage);
	}
	
	/**
	 * Deactivates and clears all the NodeStorages of the NodeViewers present in the registry. 
	 */
	public void deactivateAndClearNodeStorages()
	{
		aRenderers.values().stream()
			.filter(renderer -> NodeViewer.class.isAssignableFrom(renderer.getClass()))
			.map(NodeViewer.class::cast)
			.forEach(NodeViewer::deactivateAndClearNodeStorage);
	}
	
	protected void drawNode(Node pNode, GraphicsContext pGraphics)
	{
		draw(pNode, pGraphics);
		pNode.getChildren().forEach(node -> drawNode(node, pGraphics));
	}
	
	/**
     * Draws the element.
     * @param pElement The element to draw.
     * @param pGraphics the graphics context
     * @pre pElement != null
	 */
	@Override
   	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
   	{
   		aRenderers.get(pElement.getClass()).draw(pElement, pGraphics);
   	}

	@Override
	public Optional<Edge> edgeAt(Diagram pDiagram, Point pPoint)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Optional<Node> nodeAt(Diagram pDiagram, Point pPoint)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Rectangle getBounds(Diagram pDiagram)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean contains(DiagramElement pElement, Point pPoint)
	{
		return aRenderers.get(pElement.getClass()).contains(pElement, pPoint);
	}

	@Override
	public Canvas createIcon(DiagramElement pElement)
	{
		assert pElement != null;
		return aRenderers.get(pElement.getClass()).createIcon(DiagramType.USECASE, pElement);
	}

	@Override
	public void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics)
	{
		assert pElement != null && pGraphics != null;
		aRenderers.get(pElement.getClass()).drawSelectionHandles(pElement, pGraphics);
	}

	@Override
	public Rectangle getBounds(DiagramElement pElement)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getConnectionPoints(Node pNode, Direction pDirection)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
