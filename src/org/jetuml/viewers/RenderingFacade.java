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
import org.jetuml.diagram.edges.ConstructorEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.InterfaceNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Direction;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.ObjectDiagramRenderer;
import org.jetuml.rendering.SequenceDiagramRenderer;
import org.jetuml.rendering.StateDiagramRenderer;
import org.jetuml.rendering.UseCaseDiagramRenderer;
import org.jetuml.viewers.edges.AggregationEdgeViewer;
import org.jetuml.viewers.edges.AssociationEdgeViewer;
import org.jetuml.viewers.edges.CallEdgeViewer;
import org.jetuml.viewers.edges.DependencyEdgeViewer;
import org.jetuml.viewers.edges.EdgeViewer;
import org.jetuml.viewers.edges.GeneralizationEdgeViewer;
import org.jetuml.viewers.edges.NoteEdgeViewer;
import org.jetuml.viewers.nodes.InterfaceNodeViewer;
import org.jetuml.viewers.nodes.NodeViewer;
import org.jetuml.viewers.nodes.NoteNodeViewer;
import org.jetuml.viewers.nodes.PackageDescriptionNodeViewer;
import org.jetuml.viewers.nodes.PackageNodeViewer;
import org.jetuml.viewers.nodes.PointNodeViewer;
import org.jetuml.viewers.nodes.TypeNodeViewer;

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
	
	private static Optional<Diagram> aActiveDiagram = Optional.empty();
	
	static
	{
		aRenderers.put(ClassNode.class, new TypeNodeViewer());
		aRenderers.put(InterfaceNode.class, new InterfaceNodeViewer());
		aRenderers.put(NoteNode.class, new NoteNodeViewer());
		aRenderers.put(PackageNode.class, new PackageNodeViewer());
		aRenderers.put(PackageDescriptionNode.class, new PackageDescriptionNodeViewer());
		aRenderers.put(PointNode.class, new PointNodeViewer());
		
		aRenderers.put(NoteEdge.class, new NoteEdgeViewer());
		aRenderers.put(ConstructorEdge.class, new CallEdgeViewer());
		aRenderers.put(DependencyEdge.class, new DependencyEdgeViewer());
		aRenderers.put(AssociationEdge.class,  new AssociationEdgeViewer());
		aRenderers.put(GeneralizationEdge.class, new GeneralizationEdgeViewer());
		aRenderers.put(AggregationEdge.class, new AggregationEdgeViewer());
		
		aDiagramRenderers.put(DiagramType.USECASE, UseCaseDiagramRenderer.INSTANCE);
		aDiagramRenderers.put(DiagramType.OBJECT, ObjectDiagramRenderer.INSTANCE);
		aDiagramRenderers.put(DiagramType.STATE, StateDiagramRenderer.INSTANCE);
		aDiagramRenderers.put(DiagramType.SEQUENCE, SequenceDiagramRenderer.INSTANCE);
	}
	
	private static boolean isImplemented()
	{
		return diagramType() == DiagramType.USECASE ||
				diagramType() == DiagramType.OBJECT ||
				diagramType() == DiagramType.SEQUENCE ||
				diagramType() == DiagramType.STATE;
	}
	
	private static DiagramType diagramType()
	{
		return aActiveDiagram.get().getType();
	}
	
	/**
	 * Caches the diagram to be rendered. All subsequent rendering operations
	 * will be assumed to target this diagram, until the next call to prepare.
	 * 
	 * @param pDiagram The diagram to prepare for rendering.
	 */
	public static void prepareFor(Diagram pDiagram)
	{
		assert pDiagram != null;
		aActiveDiagram = Optional.of(pDiagram);
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
		if( pDiagramType == DiagramType.USECASE || pDiagramType == DiagramType.OBJECT || pDiagramType == DiagramType.STATE || pDiagramType == DiagramType.SEQUENCE) // TODO Generalize
		{
			return aDiagramRenderers.get(pDiagramType).createIcon(pElement);
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
		assert pElement != null;
		if( isImplemented() ) // TODO Generalize
		{
			return aDiagramRenderers.get(diagramType()).contains(pElement, pPoint);
		}
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
		assert pElement != null && pGraphics != null;
		if( isImplemented() ) // TODO Generalize
		{
			aDiagramRenderers.get(diagramType()).drawSelectionHandles(pElement, pGraphics);
		}
		else
		{
			aRenderers.get(pElement.getClass()).drawSelectionHandles(pElement, pGraphics);
		}
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
		assert pElement != null && pGraphics != null;
		if( isImplemented() )
		{
			aDiagramRenderers.get(diagramType()).draw(pElement, pGraphics);
		}
		else
		{
			aRenderers.get(pElement.getClass()).draw(pElement, pGraphics);
		}
	}
	
	/**
	 * Gets the points at which pNode is connected to its nodes.
	 * 
	 * @param pNode The target node
	 * @param pDirection The desired direction.
	 * @return A connection point on the node.
	 * @pre pNode != null && pDirection != null
	 */
	public static Point getConnectionPoints(Node pNode, Direction pDirection)
	{
		assert pNode != null;
		if( isImplemented() )
		{
			return aDiagramRenderers.get(diagramType()).getConnectionPoints(pNode, pDirection);
		}
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
		assert pEdge != null;
		if( isImplemented() )
		{
			return aDiagramRenderers.get(diagramType()).getConnectionPoints(pEdge);
		}
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
		assert pDiagram != null && pGraphics != null;
		if( isImplemented() )
		{
			aDiagramRenderers.get(pDiagram.getType()).draw(pDiagram, pGraphics);
		}
		else
		{
			DiagramType.viewerFor(pDiagram).draw(pDiagram, pGraphics);
		}
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
		assert pDiagram != null && pPoint != null;
		if( isImplemented() )
		{
			return aDiagramRenderers.get(pDiagram.getType()).edgeAt(pDiagram, pPoint);
		}
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
		assert pDiagram != null && pPoint != null;
		if( isImplemented() )
		{
			return aDiagramRenderers.get(pDiagram.getType()).nodeAt(pDiagram, pPoint);
		}
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
		assert pDiagram != null;
		if( isImplemented() )
		{
			return aDiagramRenderers.get(pDiagram.getType()).getBounds(pDiagram);
		}
		return DiagramType.viewerFor(pDiagram).getBounds(pDiagram);
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
		if( isImplemented() )
		{
			return aDiagramRenderers.get(aActiveDiagram.get().getType()).getBounds(pElement);
		}
		return aRenderers.get(pElement.getClass()).getBounds(pElement);
	}
}
