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
 *******************************************************************************/
package org.jetuml.viewers;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.CallEdge;
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.edges.ReturnEdge;
import org.jetuml.diagram.edges.StateTransitionEdge;
import org.jetuml.diagram.edges.UseCaseAssociationEdge;
import org.jetuml.diagram.edges.UseCaseDependencyEdge;
import org.jetuml.diagram.edges.UseCaseGeneralizationEdge;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.CallNode;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.ImplicitParameterNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.diagram.nodes.StateNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.UseCaseDiagramRenderer;
import org.jetuml.viewers.edges.AggregationEdgeViewer;
import org.jetuml.viewers.edges.AssociationEdgeViewer;
import org.jetuml.viewers.edges.CallEdgeViewer;
import org.jetuml.viewers.edges.DependencyEdgeViewer;
import org.jetuml.viewers.edges.EdgeViewer;
import org.jetuml.viewers.edges.GeneralizationEdgeViewer;
import org.jetuml.viewers.edges.NoteEdgeViewer;
import org.jetuml.viewers.edges.ObjectCollaborationEdgeViewer;
import org.jetuml.viewers.edges.ObjectReferenceEdgeViewer;
import org.jetuml.viewers.edges.ReturnEdgeViewer;
import org.jetuml.viewers.edges.StateTransitionEdgeViewer;
import org.jetuml.viewers.edges.UseCaseAssociationEdgeViewer;
import org.jetuml.viewers.edges.UseCaseDependencyEdgeViewer;
import org.jetuml.viewers.edges.UseCaseGeneralizationEdgeViewer;
import org.jetuml.viewers.nodes.ActorNodeViewer;
import org.jetuml.viewers.nodes.CallNodeViewer;
import org.jetuml.viewers.nodes.CircularStateNodeViewer;
import org.jetuml.viewers.nodes.FieldNodeViewer;
import org.jetuml.viewers.nodes.ImplicitParameterNodeViewer;
import org.jetuml.viewers.nodes.InterfaceNodeViewer;
import org.jetuml.viewers.nodes.NodeViewer;
import org.jetuml.viewers.nodes.NoteNodeViewer;
import org.jetuml.viewers.nodes.ObjectNodeViewer;
import org.jetuml.viewers.nodes.PackageDescriptionNodeViewer;
import org.jetuml.viewers.nodes.PackageNodeViewer;
import org.jetuml.viewers.nodes.PointNodeViewer;
import org.jetuml.viewers.nodes.StateNodeViewer;
import org.jetuml.viewers.nodes.TypeNodeViewer;
import org.jetuml.viewers.nodes.UseCaseNodeViewer;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * Meant as a single access point for all services that require rendering
 * a diagram and its elements.
 */
public class RenderingFacade
{
	private static IdentityHashMap<DiagramType, DiagramRenderer> 
		aDiagramRenderers = new IdentityHashMap<>();
	
	private static IdentityHashMap<Class<? extends DiagramElement>, DiagramElementRenderer> aRenderers = 
			new IdentityHashMap<>();
	
	static
	{
		aRenderers.put(ActorNode.class, new ActorNodeViewer());
		aRenderers.put(CallNode.class, new CallNodeViewer());
		aRenderers.put(ClassNode.class, new TypeNodeViewer());
		aRenderers.put(FieldNode.class, new FieldNodeViewer());
		aRenderers.put(FinalStateNode.class, new CircularStateNodeViewer(true));
		aRenderers.put(ImplicitParameterNode.class, new ImplicitParameterNodeViewer());
		aRenderers.put(InitialStateNode.class, new CircularStateNodeViewer(false));
		aRenderers.put(InterfaceNode.class, new InterfaceNodeViewer());
		aRenderers.put(NoteNode.class, new NoteNodeViewer());
		aRenderers.put(ObjectNode.class, new ObjectNodeViewer());
		aRenderers.put(PackageNode.class, new PackageNodeViewer());
		aRenderers.put(PackageDescriptionNode.class, new PackageDescriptionNodeViewer());
		aRenderers.put(PointNode.class, new PointNodeViewer());
		aRenderers.put(StateNode.class, new StateNodeViewer());
		aRenderers.put(UseCaseNode.class, new UseCaseNodeViewer());
		
		aRenderers.put(NoteEdge.class, new NoteEdgeViewer());
		aRenderers.put(UseCaseAssociationEdge.class, new UseCaseAssociationEdgeViewer());
		aRenderers.put(UseCaseGeneralizationEdge.class, new UseCaseGeneralizationEdgeViewer());
		aRenderers.put(UseCaseDependencyEdge.class, new UseCaseDependencyEdgeViewer());
		aRenderers.put(ObjectReferenceEdge.class, new ObjectReferenceEdgeViewer());
		aRenderers.put(ObjectCollaborationEdge.class, new ObjectCollaborationEdgeViewer());
		aRenderers.put(StateTransitionEdge.class, new StateTransitionEdgeViewer());
		aRenderers.put(ReturnEdge.class, new ReturnEdgeViewer());
		aRenderers.put(CallEdge.class, new CallEdgeViewer());
		aRenderers.put(ConstructorEdge.class, new CallEdgeViewer());
		aRenderers.put(DependencyEdge.class, new DependencyEdgeViewer());
		aRenderers.put(AssociationEdge.class,  new AssociationEdgeViewer());
		aRenderers.put(GeneralizationEdge.class, new GeneralizationEdgeViewer());
		aRenderers.put(AggregationEdge.class, new AggregationEdgeViewer());
		
		aDiagramRenderers.put(DiagramType.USECASE, UseCaseDiagramRenderer.INSTANCE);
	}
	
	/**
	 * Creates the icon for the target diagram element.
	 * 
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	public static Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		assert pElement != null;
		if( pDiagramType == DiagramType.USECASE ) // TODO Generalize
		{
			aDiagramRenderers.get(pDiagramType).createIcon(pElement);
		}
		return aRenderers.get(pElement.getClass()).createIcon(null, pElement); // TODO remove null
	}
	
	/**
	 * @param pElements The elements whose bounds we are interested in. 
	 * @return A rectangle that represents the bounding box of the 
	 *     entire selection including the bounds of their parent nodes.
	 * @pre pElements != null
	 * @pre pElements has at least one element.
	 */
	public static Rectangle getBoundsIncludingParents(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		assert pElements.iterator().hasNext();
		Iterator<DiagramElement> elements = pElements.iterator();
		DiagramElement next = elements.next();
		Rectangle bounds = getBounds(next);
		bounds = addBounds(bounds, next);
		while( elements.hasNext() )
		{
			bounds = addBounds(bounds, elements.next());
		}
		return bounds;
	}
	
	// Recursively enlarge the current rectangle to include the selected DiagramElements
	private static Rectangle addBounds(Rectangle pBounds, DiagramElement pElement)
	{
		if( pElement instanceof Node && ((Node) pElement).hasParent())
		{
			return addBounds(pBounds, ((Node) pElement).getParent());
		}
		else
		{
			return pBounds.add(getBounds(pElement));
		}
	}
	
	/**
	 * Activates all the NodeStorages of the NodeViewers present in the registry. 
	 */
	public static void activateNodeStorages()
	{
		aRenderers.values().stream()
			.filter(renderer -> NodeViewer.class.isAssignableFrom(renderer.getClass()))
			.map(NodeViewer.class::cast)
			.forEach(NodeViewer::activateNodeStorage);
	}
	
	/**
	 * Deactivates and clears all the NodeStorages of the NodeViewers present in the registry. 
	 */
	public static void deactivateAndClearNodeStorages()
	{
		aRenderers.values().stream()
			.filter(renderer -> NodeViewer.class.isAssignableFrom(renderer.getClass()))
			.map(NodeViewer.class::cast)
			.forEach(NodeViewer::deactivateAndClearNodeStorage);
	}
	
	/**
	 * Tests whether pElement contains a point.
	 * 
	 * @param pElement The element to test
	 * @param pPoint The point to test
	 * @return true if this element contains aPoint
	 */
	public static boolean contains(DiagramElement pElement, Point pPoint)
	{
		return aRenderers.get(pElement.getClass()).contains(pElement, pPoint);
	}
	
	/**
	 * Draw selection handles around the element.
	 * 
	 * @param pElement The target element
	 * @param pGraphics The graphics context
	 * @pre pElement != null && pGraphics != null
	 */
	public static void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics)
	{
		aRenderers.get(pElement.getClass()).drawSelectionHandles(pElement, pGraphics);
	}
	
	/**
	 * Draws the element on the canvas.
	 * 
	 * @param pElement The element to draw.
	 * @param pGraphics The graphics context
	 * @pre pElement != null
	 */
	public static void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		assert pElement != null;
		aRenderers.get(pElement.getClass()).draw(pElement, pGraphics);
	}
	
	/**
	 * Gets the smallest rectangle that bounds the element. The bounding rectangle contains all labels.
	 * 
	 * @param pElement The element whose bounds we wish to compute.
	 * @return The bounding rectangle
	 * @pre pElement != null
	 */
	public static Rectangle getBounds(DiagramElement pElement)
	{
		assert pElement != null;
		return aRenderers.get(pElement.getClass()).getBounds(pElement);
	}

	/**
	 * Gets the points at which pNode is connected to its nodes.
	 * 
	 * @param pNode The target node
	 * @param pDirection The desired direction.
	 * @return A connection point on the node.
	 * @pre pNode != null && pDirection != null
	 * 
	 */
	public static Point getConnectionPoints(Node pNode, Direction pDirection)
	{
		assert pNode != null;
		return ((NodeViewer)aRenderers.get(pNode.getClass())).getConnectionPoint(pNode, pDirection);
	}
	
	/**
	 * Gets the points at which pEdge is connected to its nodes.
	 * 
	 * @param pEdge The target edge
	 * @return a line joining the two connection points
	 * @pre pEdge != null
	 * 
	 */
	public static Line getConnectionPoints(Edge pEdge)
	{
		return ((EdgeViewer)aRenderers.get(pEdge.getClass())).getConnectionPoints(pEdge);
	}
	
	/**
	 * Draws pDiagram onto pGraphics.
	 * 
	 * @param pGraphics the graphics context where the
	 *     diagram should be drawn.
	 * @param pDiagram the diagram to draw.
	 * @pre pDiagram != null && pGraphics != null.
	 */
	public static void draw(Diagram pDiagram, GraphicsContext pGraphics)
	{
		DiagramType.viewerFor(pDiagram).draw(pDiagram, pGraphics);
	}
	
	/**
	 * Returns the edge underneath the given point, if it exists.
	 * 
	 * @param pDiagram The diagram to query
	 * @param pPoint a point
	 * @return An edge containing pPoint or Optional.empty() if no edge is under pPoint
	 * @pre pDiagram != null && pPoint != null
	 */
	public static Optional<Edge> edgeAt(Diagram pDiagram, Point pPoint)
	{
		return DiagramType.viewerFor(pDiagram).edgeAt(pDiagram, pPoint);
	}
	
	/**
     * Finds a node that contains the given point. Always returns
     * the deepest child and the last one in a list.
     * @param pDiagram The diagram to query.
     * @param pPoint A point
     * @return a node containing pPoint or null if no nodes contain pPoint
     * @pre pDiagram != null && pPoint != null.
     */
	public static Optional<Node> nodeAt(Diagram pDiagram, Point pPoint)
	{
		return DiagramType.viewerFor(pDiagram).nodeAt(pDiagram, pPoint);
	}
	
	/**
	 * Gets the smallest rectangle enclosing the diagram.
	 * 
	 * @param pDiagram The diagram to query
	 * @return The bounding rectangle
	 * @pre pDiagram != null
	 */
	public static Rectangle getBounds(Diagram pDiagram)
	{
		return DiagramType.viewerFor(pDiagram).getBounds(pDiagram);
	}
	
	/* 
	 * Returns the type of the diagram this element is a part of. 
	 * @pre the element is part of a diagram.
	 */
	private static DiagramType diagramType(DiagramElement pElement)
	{
		if( pElement instanceof Edge )
		{
			return ((Edge)pElement).getDiagram().getType();
		}
		else
		{
			return ((Node)pElement).getDiagram().get().getType();
		}
	}
}
