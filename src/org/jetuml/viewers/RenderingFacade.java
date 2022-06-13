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

import org.jetuml.diagram.DiagramElement;
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
	private static IdentityHashMap<Class<? extends Node>, NodeViewer> aNodeViewers = 
			new IdentityHashMap<>();
	private static IdentityHashMap<Class<? extends Edge>, EdgeViewer> aEdgeViewers = 
			new IdentityHashMap<>();
	
	static
	{
		aNodeViewers.put(ActorNode.class, new ActorNodeViewer());
		aNodeViewers.put(CallNode.class, new CallNodeViewer());
		aNodeViewers.put(ClassNode.class, new TypeNodeViewer());
		aNodeViewers.put(FieldNode.class, new FieldNodeViewer());
		aNodeViewers.put(FinalStateNode.class, new CircularStateNodeViewer(true));
		aNodeViewers.put(ImplicitParameterNode.class, new ImplicitParameterNodeViewer());
		aNodeViewers.put(InitialStateNode.class, new CircularStateNodeViewer(false));
		aNodeViewers.put(InterfaceNode.class, new InterfaceNodeViewer());
		aNodeViewers.put(NoteNode.class, new NoteNodeViewer());
		aNodeViewers.put(ObjectNode.class, new ObjectNodeViewer());
		aNodeViewers.put(PackageNode.class, new PackageNodeViewer());
		aNodeViewers.put(PackageDescriptionNode.class, new PackageDescriptionNodeViewer());
		aNodeViewers.put(PointNode.class, new PointNodeViewer());
		aNodeViewers.put(StateNode.class, new StateNodeViewer());
		aNodeViewers.put(UseCaseNode.class, new UseCaseNodeViewer());
		
		aEdgeViewers.put(NoteEdge.class, new NoteEdgeViewer());
		aEdgeViewers.put(UseCaseAssociationEdge.class, new UseCaseAssociationEdgeViewer());
		aEdgeViewers.put(UseCaseGeneralizationEdge.class, new UseCaseGeneralizationEdgeViewer());
		aEdgeViewers.put(UseCaseDependencyEdge.class, new UseCaseDependencyEdgeViewer());
		aEdgeViewers.put(ObjectReferenceEdge.class, new ObjectReferenceEdgeViewer());
		aEdgeViewers.put(ObjectCollaborationEdge.class, new ObjectCollaborationEdgeViewer());
		aEdgeViewers.put(StateTransitionEdge.class, new StateTransitionEdgeViewer());
		aEdgeViewers.put(ReturnEdge.class, new ReturnEdgeViewer());
		aEdgeViewers.put(CallEdge.class, new CallEdgeViewer());
		aEdgeViewers.put(ConstructorEdge.class, new CallEdgeViewer());
		aEdgeViewers.put(DependencyEdge.class, new DependencyEdgeViewer());
		aEdgeViewers.put(AssociationEdge.class,  new AssociationEdgeViewer());
		aEdgeViewers.put(GeneralizationEdge.class, new GeneralizationEdgeViewer());
		aEdgeViewers.put(AggregationEdge.class, new AggregationEdgeViewer());
	}
	
	/**
	 * Convenience method for creating the icon for either a node
	 * or an edge.
	 * 
	 * @param pElement The element for which we want an icon
	 * @return An icon that represents this element
	 * @pre pElement != null
	 */
	public static Canvas createIcon(DiagramElement pElement)
	{
		assert pElement != null;
		if( pElement instanceof Node )
		{
			return aNodeViewers.get(pElement.getClass()).createIcon(pElement);
		}
		else
		{
			assert pElement instanceof Edge;
			return aEdgeViewers.get(pElement.getClass()).createIcon(pElement);
		}
	}
	
	/**
	 * @param pElements The elements whose bounds we are interested in. 
	 * @return The rectangle that bounds all elements in pElements, excluding their parent node.
	 * @pre pElements != null
	 * @pre pElements has at least one element.
	 */
	public static Rectangle getBounds(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		assert pElements.iterator().hasNext();
		Iterator<DiagramElement> elements = pElements.iterator();
		Rectangle bounds = DiagramViewer.getBounds(elements.next());
		while( elements.hasNext() )
		{
			bounds = bounds.add(DiagramViewer.getBounds(elements.next()));
		}
		return bounds;
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
		Rectangle bounds = DiagramViewer.getBounds(next);
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
			return pBounds.add(DiagramViewer.getBounds(pElement));
		}
	}
	
	/**
	 * Activates all the NodeStorages of the NodeViewers present in the registry. 
	 */
	public static void activateNodeStorages()
	{
		for (NodeViewer nodeViewer : aNodeViewers.values())
		{
			nodeViewer.activateNodeStorage();
		}
	}
	
	/**
	 * Deactivates and clears all the NodeStorages of the NodeViewers present in the registry. 
	 */
	public static void deactivateAndClearNodeStorages()
	{
		for (NodeViewer nodeViewer : aNodeViewers.values())
		{
			nodeViewer.deactivateAndClearNodeStorage();
		}
	}
	
	/**
	 * Tests whether pNode contains a point.
	 * 
	 * @param pNode The node to test
	 * @param pPoint The point to test
	 * @return true if this element contains aPoint
	 */
	public static boolean contains(Node pNode, Point pPoint)
	{
		return aNodeViewers.get(pNode.getClass()).contains(pNode, pPoint);
	}
	
	/**
	 * Draw selection handles around pNode.
	 * 
	 * @param pNode The target node
	 * @param pGraphics The graphics context
	 * @pre pNode != null && pGraphics != null
	 */
	public static void drawSelectionHandles(Node pNode, GraphicsContext pGraphics)
	{
		aNodeViewers.get(pNode.getClass()).drawSelectionHandles(pNode, pGraphics);
	}
	
	/**
	 * Draws pNode.
	 * 
	 * @param pNode The node to draw.
	 * @param pGraphics The graphics context
	 * @pre pNode != null
	 */
	public static void draw(Node pNode, GraphicsContext pGraphics)
	{
		assert pNode != null;
		aNodeViewers.get(pNode.getClass()).draw(pNode, pGraphics);
	}
	
	/**
	 * Gets the smallest rectangle that bounds pNode. The bounding rectangle contains all labels.
	 * 
	 * @param pNode The node whose bounds we wish to compute.
	 * @return The bounding rectangle
	 * @pre pNode != null
	 */
	public static Rectangle getBounds(Node pNode)
	{
		assert pNode != null;
		return aNodeViewers.get(pNode.getClass()).getBounds(pNode);
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
		return aNodeViewers.get(pNode.getClass()).getConnectionPoint(pNode, pDirection);
	}
	
	/**
	 * Tests whether pEdge contains a point.
	 * 
	 * @param pEdge The edge to test
	 * @param pPoint The point to test
	 * @return true if this element contains aPoint
	 */
	public static boolean contains(Edge pEdge, Point pPoint)
	{
		return aEdgeViewers.get(pEdge.getClass()).contains(pEdge, pPoint);
	}
	
	/**
	 * Draws pEdge.
	 * 
	 * @param pEdge The edge to draw.
	 * @param pGraphics The graphics context
	 * @pre pEdge != null
	 */
	public static void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		aEdgeViewers.get(pEdge.getClass()).draw(pEdge, pGraphics);
	}
	
	/**
	 * Draw selection handles around pEdge.
	 * 
	 * @param pEdge The target edge
	 * @param pGraphics The graphics context
	 * @pre pEdge != null && pGraphics != null
	 */
	public static void drawSelectionHandles(Edge pEdge, GraphicsContext pGraphics)
	{
		aEdgeViewers.get(pEdge.getClass()).drawSelectionHandles(pEdge, pGraphics);
	}
	
	/**
	 * Gets the smallest rectangle that bounds pEdge. The bounding rectangle contains all labels.
	 * 
	 * @param pEdge The edge whose bounds we wish to compute.
	 * @return The bounding rectangle
	 * @pre pEdge != null
	 */
	public static Rectangle getBounds(Edge pEdge)
	{
		return aEdgeViewers.get(pEdge.getClass()).getBounds(pEdge);
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
		return aEdgeViewers.get(pEdge.getClass()).getConnectionPoints(pEdge);
	}
}
