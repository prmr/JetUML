/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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

package org.jetuml.diagram.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.PackageNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.nodes.PackageNodeRenderer;

/**
 * Wrapper around a DiagramRenderer that provides the logic for converting
 * requests to creates or remove nodes and edges, and convert these
 * requests into operation. An object of this class should perform
 * read-only access to the underlying diagram. However, executing the operations
 * created by methods of this class will change the state of the 
 * underlying diagram.
 */
public abstract class DiagramBuilder
{
	// Arbitrary default value, used to simplify the testing code
	private static final int DEFAULT_DIMENSION = 1000;
	
	protected final DiagramRenderer aDiagramRenderer;
	private Dimension aCanvasDimension = new Dimension(DEFAULT_DIMENSION, DEFAULT_DIMENSION);
	
	/**
	 * Creates a builder for the diagram wrapped by pDiagram, and an embedded renderer.
	 * 
	 * @param pDiagram The diagram renderer to wrap around.
	 * @pre pDiagram != null;
	 */
	protected DiagramBuilder( Diagram pDiagram )
	{
		assert pDiagram != null;
		aDiagramRenderer = DiagramType.newRendererInstanceFor(pDiagram);
	}
	
	/**
	 * @return The diagram wrapped by this builder.
	 */
	public final Diagram diagram()
	{
		return aDiagramRenderer.diagram();
	}
	
	/**
	 * @return The encapsulated renderer.
	 */
	public final DiagramRenderer renderer()
	{
		return aDiagramRenderer;
	}
	
	/**
	 * Provide information to this builder about the size
	 * of the canvas the diagram is built on.
	 * 
	 * @param pDimension The canvas size.
	 * @pre pDimension != null.
	 */
	public void setCanvasDimension(Dimension pDimension)
	{
		assert pDimension != null;
		aCanvasDimension = pDimension;
	}
	
	private static List<Node> getNodeAndAllChildren(Node pNode)
	{
		List<Node> result = new ArrayList<>();
		result.add(pNode);
		pNode.getChildren().forEach(node -> result.addAll(getNodeAndAllChildren(node)));
		return result;
	}

	/** 
	 * The default behavior is to position the node so it entirely fits in the diagram, then 
	 * add it as a root node.
	 * @param pNode The node to add.
	 * @param pRequestedPosition A point that is the requested position of the node.
	 * @return The requested operation
	 * @pre pNode != null && pRequestedPosition != null
	 * @pre canAdd(pNode, pRequestedPosition)
	 */
	public DiagramOperation createAddNodeOperation(Node pNode, Point pRequestedPosition)
	{
		assert pNode != null && pRequestedPosition != null;
		// Skip the condition check for add node
		//assert canAdd(pNode, pRequestedPosition);
		positionNode(pNode, pRequestedPosition);
		return new SimpleOperation( ()-> aDiagramRenderer.diagram().addRootNode(pNode), 
				()-> aDiagramRenderer.diagram().removeRootNode(pNode));
	}
	
	/**
	 * Creates an operation that adds all the elements in pElements. Assumes all nodes
	 * are root nodes and all edges are connected, and that there are no dangling references.
	 * 
	 * @param pElements The elements to add.
	 * @return The requested operation
	 * @pre pElements != null
	 */
	public final DiagramOperation createAddElementsOperation(Iterable<DiagramElement> pElements)
	{
		CompoundOperation operation = new CompoundOperation();
		for( DiagramElement element : pElements)
		{
			if( element instanceof Node node)
			{
				operation.add(new SimpleOperation(
						()-> aDiagramRenderer.diagram().addRootNode(node),
						()-> aDiagramRenderer.diagram().removeRootNode(node)));
			}
			else if( element instanceof Edge edge)
			{
				/* We need to re-connect the edge to set the correct value for the
				 * reference to the diagram, to cover the cases where elements might 
				 * be added by being copied from one diagram and pasted into another.
				 */
				operation.add(new SimpleOperation(
						()-> 
						{ 
							aDiagramRenderer.diagram().addEdge(edge); 
							edge.connect(edge.start(), edge.end());	
						},
						()-> aDiagramRenderer.diagram().removeEdge((Edge)element)));
			}
		}
		
		return operation;
	}
	
	/**
	 * Finds the elements that should be removed if pElement is removed,
	 * to preserve the integrity of the diagram.
	 * 
	 * @param pElement The element to remove.
	 * @return The list of elements that have to be removed with pElement.
	 * @pre pElement != null && aDiagramRenderer.contains(pElement);
	 */
	protected List<DiagramElement> getCoRemovals(DiagramElement pElement)
	{
		assert pElement != null && aDiagramRenderer.diagram().contains(pElement);
		ArrayList<DiagramElement> result = new ArrayList<>();
		result.add(pElement);
		if( pElement.getClass() == PointNode.class )
		{
			for( Edge edge : aDiagramRenderer.diagram().edgesConnectedTo((Node)pElement))
			{
				result.add(edge);
			}
		}
		if( pElement.getClass() == NoteEdge.class )
		{
			Edge edge = (Edge)pElement;
			if( edge.start().getClass() == PointNode.class )
			{
				result.add(edge.start());
			}
			if( edge.end().getClass() == PointNode.class )
			{
				result.add(edge.end());
			}
		}
		if( pElement instanceof Node node)
		{
			List<Node> descendants = getNodeAndAllChildren(node);
			for(Edge edge : aDiagramRenderer.diagram().edges())
			{
				if(descendants.contains(edge.start() ) || descendants.contains(edge.end()))
				{
					result.add(edge);
					// Special case that if we remove a note edge we must always 
					// remove the point node as well.
					if( edge instanceof NoteEdge )
					{
						if( edge.start() instanceof PointNode )
						{
							result.add(edge.start());
						}
						if( edge.end() instanceof PointNode )
						{
							result.add(edge.end());
						}
					}
				}
			}
		}
		return result;
	}
	
	/*
	 * Organize the elements to delete so that they can be reinserted properly
	 */
	private List<DiagramElement> tweakOrder(Set<DiagramElement> pElements)
	{
		List<DiagramElement> result = new ArrayList<>();
		Map<ObjectNode, List<FieldNode>> fields = new HashMap<>();
		for( DiagramElement element : pElements )
		{
			if( element.getClass() != FieldNode.class )
			{
				result.add(element);
			}
			else
			{
				FieldNode field = (FieldNode) element;
				if( !fields.containsKey(field.getParent()) )
				{
					fields.put((ObjectNode)field.getParent(), new ArrayList<>());
				}
				fields.get(field.getParent()).add(field);
			}
		}
		for( ObjectNode object : fields.keySet() )
		{
			List<FieldNode> nodes = fields.get(object);
			Collections.sort(nodes, new Comparator<FieldNode>()
			{
				@Override
				public int compare(FieldNode pField1, FieldNode pField2)
				{
					return pField2.getParent().getChildren().indexOf(pField2) - 
							pField1.getParent().getChildren().indexOf(pField1);
				}
			});
			for( FieldNode node : nodes )
			{
				result.add(node);
			}
		}
		ArrayList<DiagramElement> result2 = new ArrayList<>();
		ArrayList<Edge> edges = new ArrayList<>();
		ArrayList<Node> nodes = new ArrayList<>();
		for( DiagramElement element : result )
		{
			if( element instanceof Edge edge)
			{
				edges.add(edge);
			}
			else if( element instanceof Node node && node.hasParent() )
			{
				nodes.add(node);
			}
			else
			{
				result2.add(element);
			}
		}
		Collections.sort(edges, (pEdge1, pEdge2) -> aDiagramRenderer.diagram().indexOf(pEdge2) - aDiagramRenderer.diagram().indexOf(pEdge1));
		Collections.sort(nodes, new Comparator<Node>() 
		{
			@Override
			public int compare(Node pNode1, Node pNode2)
			{
				Node parent1 = pNode1.getParent();
				Node parent2 = pNode2.getParent();
				if( parent1 == parent2 )
				{
					return parent2.getChildren().indexOf(pNode2) -  parent1.getChildren().indexOf(pNode1);
				}
				else 
				{
					return aDiagramRenderer.diagram().rootNodes().indexOf(parent2) - 
							aDiagramRenderer.diagram().rootNodes().indexOf(parent1);
				}
			}
		});
		result2.addAll(edges);
		result2.addAll(nodes);
		return result2;
	}
	
	/**
	 * Creates an operation that removes all the elements in pElements.
	 * 
	 * @param pElements The elements to remove.
	 * @return The requested operation.
	 * @pre pElements != null.
	 */
	public final DiagramOperation createRemoveElementsOperation(Iterable<DiagramElement> pElements)
	{
		assert pElements != null;
		Set<DiagramElement> toDelete = new HashSet<>();
		for( DiagramElement element : pElements)
		{
			toDelete.addAll(getCoRemovals(element));
		}
		CompoundOperation result = new CompoundOperation();
		
		for( DiagramElement element : tweakOrder(toDelete))
		{
			if( element instanceof Edge edge)
			{
				int index = aDiagramRenderer.diagram().indexOf(edge);
				result.add(new SimpleOperation(
						()-> aDiagramRenderer.diagram().removeEdge(edge),
						()-> aDiagramRenderer.diagram().addEdge(index, edge)));
			}
			else if( element instanceof Node node)
			{
				if(node.hasParent())
				{
					result.add(new SimpleOperation(
						createDetachOperation(node),
						createReinsertOperation(node)));
				}
				else
				{
					result.add(new SimpleOperation(
						()-> aDiagramRenderer.diagram().removeRootNode(node),
						()-> aDiagramRenderer.diagram().addRootNode(node)));
				}
			}
		}
		return result;
	}
	
	/**
	 * Create an operation to move a node.
	 * 
	 * @param pNode The node to move.
	 * @param pX The amount to move the node in the x-coordinate.
	 * @param pY The amount to move the node in the y-coordinate.
 	 * @return The requested operation.
 	 * @pre pNode != null.
	 */
	public static DiagramOperation createMoveNodeOperation(Node pNode, int pX, int pY)
	{
		return new SimpleOperation(
				()-> pNode.translate(pX, pY),
				()-> pNode.translate(-pX, -pY));
	}
	
	/**
	 * Create an operation to add and edge. By default an edge is create between
	 * the start and end point of the rubberband. The special cases are handled
	 * by overriding the method.
	 * 
	 * @param pEdge The edge to add.
	 * @param pStart The starting point.
	 * @param pEnd The end point.
	 * @return The requested operation.
	 */
	public final DiagramOperation createAddEdgeOperation(Edge pEdge, Point pStart, Point pEnd)
	{ 
		assert pEdge != null && pStart != null && pEnd != null;
		
		Node startNode = detectStartNode(pStart); 				// Must exist
		Node endNode = detectEndNode(pEdge, startNode, pEnd);   // Can be created as a result of the method call
		Edge edge = obtainEdge(pEdge, pStart, pEnd);
		
		CompoundOperation addEdgeOperation = new CompoundOperation();
		if(!diagram().allNodes().contains(endNode))
		{
			addEdgeOperation.add(new SimpleOperation(()-> aDiagramRenderer.diagram().addRootNode(endNode),
					() -> aDiagramRenderer.diagram().removeRootNode(endNode)));
		}
		completeEdgeAdditionOperation(addEdgeOperation, edge, startNode, endNode, pStart, pEnd);
		return addEdgeOperation;
	}
	
	/**
	 * Allows subclasses to overried the edge used by the user to create an edge.
	 * To support special cases where a different edge is more appropriate given the 
	 * start and end points than the one chosen by the user. By default this method returns pOriginalEdge.
	 * 
	 * @param pOriginalEdge The edge originally selected by the user.
	 * @param pStart The start point for the edge.
	 * @param pEnd The end point for the edge.
	 * @return An edge object to add to the diagram.
	 */
	protected Edge obtainEdge(Edge pOriginalEdge, Point pStart, Point pEnd)
	{
		return pOriginalEdge;
	}
	
	/*
	 * Returns the node under pStartPoint. Using this method assumes that
	 * there is one. This constraint must be enforced externally.
	 */
	private Node detectStartNode(Point pStartPoint)
	{
		Optional<Node> maybeNode1 = aDiagramRenderer.nodeAt(pStartPoint);
		// Because we should only reach here if the edge creation gesture was started from a node
		assert maybeNode1.isPresent(); 
 		return maybeNode1.get();
	}
	
	/*
	 * Returns the node under pEndPoint. If there is no such node, this 
	 * method creates a PointNode at pEndPoint. If the edge is a note 
	 * edge, a PointNode is created if the end node is not an note node
	 */
	private Node detectEndNode(Edge pEdge, Node pStartNode, Point pEndPoint)
	{
		Optional<Node> optionalEndNode = aDiagramRenderer.nodeAt(pEndPoint);

		if( pStartNode.getClass() == NoteNode.class && pEdge.getClass() == NoteEdge.class || 
				optionalEndNode.isEmpty() )
		{
			Node endNode = new PointNode();
			endNode.translate(pEndPoint.x(), pEndPoint.y());
			return endNode;
		}
		else
		{
			return optionalEndNode.get();
		}
	}
	
	/**
	 * Finishes the addition operation. By default, this just connects the edge to the nodes
	 * and adds the edge to the diagram.
	 * 
	 * @param pOperation The operation being constructed. 
	 * @param pEdge The edge to add.
	 * @param pStartNode The start node.
	 * @param pEndNode The end node.
	 * @param pStartPoint The start point.
	 * @param pEndPoint The end point.
	 * @pre No null references as arguments.
	 */
	protected void completeEdgeAdditionOperation( CompoundOperation pOperation, Edge pEdge, Node pStartNode, Node pEndNode,
			Point pStartPoint, Point pEndPoint)
	{
		pEdge.connect(pStartNode, pEndNode);
		pOperation.add(new SimpleOperation(()-> aDiagramRenderer.diagram().addEdge(pEdge),
				()-> aDiagramRenderer.diagram().removeEdge(pEdge)));
	}
	
	private static Runnable createReinsertOperation(Node pNode)
	{
		Node parent = pNode.getParent();
		int index = parent.getChildren().indexOf(pNode);
		return ()-> parent.addChild(index, pNode);
	}
	
	private Runnable createDetachOperation(Node pNode)
	{
		Node parent = pNode.getParent();
		if(parent.getClass()==PackageNode.class && parent.getChildren().size()==1)
		{
			return ()-> 
			{ 
				Rectangle parentBound = packageNodeRenderer().getBounds(parent);
				parent.removeChild(pNode); 
				parent.translate( parentBound.x()-parent.position().x(),  parentBound.y()-parent.position().y() );
			};
		}
		return ()-> 
		{ 
			parent.removeChild(pNode); 
		};
	}
	
	protected PackageNodeRenderer packageNodeRenderer()
	{
		return (PackageNodeRenderer)aDiagramRenderer.rendererFor(PackageNode.class);
	}
	
	private Point computePosition(Dimension pDimension, Point pRequestedPosition)
	{
		int newX = pRequestedPosition.x();
		int newY = pRequestedPosition.y();
		if(newX + pDimension.width() > aCanvasDimension.width())
		{
			newX = aCanvasDimension.width() - pDimension.width();
		}
		if(newY + pDimension.height() > aCanvasDimension.height())
		{
			newY = aCanvasDimension.height() - pDimension.height();
		}
		return new Point(newX, newY);
	}
	
	/**
	 * Positions pNode as close to the requested position as possible.
	 * 
	 * @param pNode The node to position. 
	 * @param pRequestedPosition The requested position.
	 * @pre pNode != null && pRequestedPosition != null
	 */
	protected void positionNode(Node pNode, Point pRequestedPosition)
	{
		assert pNode != null && pRequestedPosition != null;
		Dimension bounds = renderer().getDefaultDimension(pNode);
		Point position = computePosition(bounds, pRequestedPosition);
		pNode.translate(position.x(), position.y());
	}
}
