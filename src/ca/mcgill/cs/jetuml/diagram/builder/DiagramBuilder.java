/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/

package ca.mcgill.cs.jetuml.diagram.builder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramElement;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PointNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.DiagramView;

/**
 * Wrapper around a Diagram that provides the logic for converting
 * requests to creates or remove nodes and edges, and convert these
 * requests into operation. An object of this class should perform
 * read-only access to the diagram. However, executing the operations
 * created by methods of this class will change the state of the 
 * diagram.
 */
public abstract class DiagramBuilder
{
	// Arbitrary default value, used to simplify the testing code
	private static final int DEFAULT_DIMENSION = 1000;
	
	protected final Diagram aDiagram;
	private final DiagramView aDiagramView;
	private Dimension aCanvasDimension = new Dimension(DEFAULT_DIMENSION, DEFAULT_DIMENSION);
	
	/**
	 * Creates a builder for pDiagram.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	protected DiagramBuilder( Diagram pDiagram )
	{
		assert pDiagram != null;
		aDiagram = pDiagram;
		aDiagramView = DiagramType.newViewInstanceFor(aDiagram);
	}
	
	/**
	 * @return The DiagramView used by this builder to compute
	 * the diagram geometry.
	 */
	public DiagramView getView()
	{
		return aDiagramView;
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
	
	/**
	 * Returns whether adding pNode at pRequestedPosition is a valid
	 * operation on the diagram. True by default. 
	 * Override to provide cases where this should be false.
	 * 
	 * @param pNode The node to add if possible. 
	 * @param pRequestedPosition The requested position for the node.
	 * @return True if it is possible to add pNode at position pPoint.
	 * @pre pNode != null && pRequestedPosition != null
	 */
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		return true;
	}
	
	private static List<Node> getNodeAndAllChildren(Node pNode)
	{
		List<Node> result = new ArrayList<>();
		result.add(pNode);
		if( pNode instanceof ParentNode )
		{
			for( ChildNode child : ((ParentNode)pNode).getChildren() )
			{
				result.addAll(getNodeAndAllChildren(child));
			}
		}
		return result;
	}
	
	/**
	 * Returns whether adding pEdge between pStart and pEnd
	 * is a valid operation on the diagram. 
	 * 
	 * @param pEdge The requested edge
	 * @param pStart A requested start point
	 * @param pEnd A requested end point
	 * @return True if it's possible to add an edge of this type given the requested points.
	 * @pre pEdge != null && pStart = null && pEnd != null
	 */
	public final boolean canAdd(Edge pEdge, Point pStart, Point pEnd)
	{
		assert pEdge != null && pStart != null && pEnd != null;
		Optional<Node> startNode = aDiagramView.findNode(pStart);
		Optional<Node> endNode = aDiagramView.findNode(pEnd);
		
		if(startNode.isPresent() && startNode.get() instanceof NoteNode && pEdge instanceof NoteEdge)
		{
			return true; // Special case: we can always create a point node.
		}
		if(!startNode.isPresent() || !endNode.isPresent() )
		{
			return false;
		}
		
		ConstraintSet constraints = new ConstraintSet(
				EdgeConstraints.noteEdge(pEdge, startNode.get(), endNode.get()),
				EdgeConstraints.noteNode(pEdge, startNode.get(), endNode.get())
		);
		constraints.merge(getAdditionalEdgeConstraints(pEdge, startNode.get(), endNode.get(), pStart, pEnd));
		return constraints.satisfied();
	}
	
	/**
	 * @param pEdge The edge to add.
	 * @param pStart The start node.
	 * @param pEnd The end node.
 	 * @param pStartPoint the point in the start node.
	 * @param pEndPoint the point in the end node.
	 * @return Additional, diagram type-specific constraints for adding edges.
	 * @pre pEdge != null && pStart != null && pEnd != null && pStartPoint!= null && pEndPoint != null
	 */
	protected abstract ConstraintSet getAdditionalEdgeConstraints(Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint);
	
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
		assert canAdd(pNode, pRequestedPosition);
		positionNode(pNode, pRequestedPosition);
		return new SimpleOperation( ()-> aDiagram.addRootNode(pNode), 
				()-> aDiagram.removeRootNode(pNode));
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
			if( element instanceof Node )
			{
				operation.add(new SimpleOperation(
						()-> aDiagram.addRootNode((Node)element),
						()-> aDiagram.removeRootNode((Node)element)));
			}
			else if( element instanceof Edge)
			{
				operation.add(new SimpleOperation(
						()-> aDiagram.addEdge((Edge)element),
						()-> aDiagram.removeEdge((Edge)element)));
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
	 * @pre pElement != null && aDiagram.contains(pElement);
	 */
	protected List<DiagramElement> getCoRemovals(DiagramElement pElement)
	{
		assert pElement != null && aDiagram.contains(pElement);
		ArrayList<DiagramElement> result = new ArrayList<>();
		if( pElement.getClass() == PointNode.class )
		{
			for( Edge edge : aDiagram.edgesConnectedTo((Node)pElement))
			{
				result.add(edge);
			}
		}
		if( pElement.getClass() == NoteEdge.class )
		{
			Edge edge = (Edge)pElement;
			if( edge.getStart().getClass() == PointNode.class )
			{
				result.add(edge.getStart());
			}
			if( edge.getEnd().getClass() == PointNode.class )
			{
				result.add(edge.getEnd());
			}
		}
		if( pElement instanceof Node )
		{
			List<Node> descendants = getNodeAndAllChildren((Node)pElement);
			for(Edge edge : aDiagram.edges())
			{
				if(descendants.contains(edge.getStart() ) || descendants.contains(edge.getEnd()))
				{
					result.add(edge);
				}
			}
		}
		return result;
	}
	
	/*
	 * Organize the elements to delete so that they can be reinserted properly
	 */
	private List<DiagramElement> tweakOrder(HashSet<DiagramElement> pElements)
	{
		List<DiagramElement> result = new ArrayList<>();
		Map<ObjectNode, List<FieldNode>> fields = new HashMap<>();
		for( DiagramElement element : pElements)
		{
			if( element.getClass() != FieldNode.class )
			{
				result.add(element);
			}
			else
			{
				FieldNode field = (FieldNode) element;
				if( !fields.containsKey((ObjectNode)field.getParent()))
				{
					fields.put((ObjectNode)field.getParent(), new ArrayList<>());
				}
				fields.get((ObjectNode)field.getParent()).add(field);
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
		for( DiagramElement element : result)
		{
			if( element instanceof Edge)
			{
				edges.add((Edge)element);
			}
			else
			{
				result2.add(element);
			}
		}
		Collections.sort(edges, new Comparator<Edge>() 
		{
			@Override
			public int compare(Edge pEdge1, Edge pEdge2)
			{
				return aDiagram.indexOf(pEdge2) - aDiagram.indexOf(pEdge1);
			}
			
		});
		for( Edge edge : edges)
		{
			result2.add(edge);
		}
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
		HashSet<DiagramElement> toDelete = new HashSet<>();
		for( DiagramElement element : pElements)
		{
			toDelete.add(element);
			toDelete.addAll(getCoRemovals(element));
		}
		CompoundOperation result = new CompoundOperation();
		
		for( DiagramElement element : tweakOrder(toDelete))
		{
			if( element instanceof Edge )
			{
				int index = aDiagram.indexOf((Edge)element);
				result.add(new SimpleOperation(
						()-> aDiagram.removeEdge((Edge)element),
						()-> aDiagram.addEdge(index, (Edge)element)));
			}
			else if( element instanceof Node )
			{
				if(hasParent((Node) element))
				{
					result.add(new SimpleOperation(
						createDetachOperation((ChildNode)element),
						createReinsertOperation((ChildNode)element)));
				}
				else
				{
					result.add(new SimpleOperation(
						()-> aDiagram.removeRootNode((Node)element),
						()-> aDiagram.addRootNode((Node)element)));
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
	public final DiagramOperation createMoveNodeOperation(Node pNode, int pX, int pY)
	{
		return new SimpleOperation(
				()-> pNode.translate(pX, pY),
				()-> pNode.translate(-pX, -pY));
	}
	
	/**
	 * Create an operation to add and edge.
	 * 
	 * @param pEdge The edge to add.
	 * @param pStart The starting point.
	 * @param pEnd The end point.
	 * @return The requested operation.
	 */
	public final DiagramOperation createAddEdgeOperation(Edge pEdge, Point pStart, Point pEnd)
	{ 
		assert canAdd(pEdge, pStart, pEnd);
		Node node1 = aDiagramView.findNode(pStart).get();
		Optional<Node> node2in = aDiagramView.findNode(pEnd);
		Node node2 = null;
		if( node2in.isPresent() )
		{
			node2 = node2in.get();
		}
		CompoundOperation result = new CompoundOperation();
		if(node1 instanceof NoteNode && pEdge instanceof NoteEdge)
		{
			node2 = new PointNode();
			node2.translate(pEnd.getX(), pEnd.getY());
			Node end = node2; // Effectively final to include in closure
			result.add(new SimpleOperation(()-> aDiagram.addRootNode(end),
					()-> aDiagram.removeRootNode(end)));
		}
		assert node2 != null;
		completeEdgeAdditionOperation(result, pEdge, node1, node2, pStart, pEnd);
		return result;
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
		pEdge.connect(pStartNode, pEndNode, aDiagram);
		pOperation.add(new SimpleOperation(()-> aDiagram.addEdge(pEdge),
				()-> aDiagram.removeEdge(pEdge)));
	}
	
	private static Runnable createReinsertOperation(ChildNode pNode)
	{
		ParentNode parent = pNode.getParent();
		int index = parent.getChildren().indexOf(pNode);
		return ()-> parent.addChild(index, pNode);
	}
	
	private static Runnable createDetachOperation(ChildNode pNode)
	{
		ParentNode parent = pNode.getParent();
		return ()-> parent.removeChild(pNode);
	}
	
	private Point computePosition(Rectangle pBounds, Point pRequestedPosition)
	{
		int newX = pRequestedPosition.getX();
		int newY = pRequestedPosition.getY();
		if(newX + pBounds.getWidth() > aCanvasDimension.getWidth())
		{
			newX = aCanvasDimension.getWidth() - pBounds.getWidth();
		}
		if (newY + pBounds.getHeight() > aCanvasDimension.getHeight())
		{
			newY = aCanvasDimension.getHeight() - pBounds.getHeight();
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
		Rectangle bounds = pNode.view().getBounds();
		Point position = computePosition(bounds, pRequestedPosition);
		pNode.translate(position.getX() - bounds.getX(), position.getY() - bounds.getY());
	}
	
	/**
	 * @param pNode A node to check for parenthood.
	 * @return True iif pNode has a non-null parent.
	 */
	private static boolean hasParent(Node pNode)
	{
		return (pNode instanceof ChildNode) && ((ChildNode)pNode).getParent() != null;
	}
}
