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

package ca.mcgill.cs.jetuml.diagram.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ClassDiagramEdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.AbstractPackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.TypeNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * A builder for class diagrams.
 */
public class ClassDiagramBuilder extends DiagramBuilder
{
	private static final int PADDING = 10;
	private static final int TOP_HEIGHT = 20;
	
	/**
	 * Creates a new builder for class diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null && pDiagram.getType() == DiagramType.CLASS
	 */
	public ClassDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram.getType() == DiagramType.CLASS;
	}
	
	@Override
	public DiagramOperation createAddNodeOperation(Node pNode, Point pRequestedPosition)
	{
		DiagramOperation result = null;
		if( validChild(pNode))
		{
			Optional<PackageNode> container = findContainer(aDiagram.rootNodes(), pRequestedPosition);
			if( container.isPresent() )
			{
				if( container.get().getChildren().size()==0 )
				{
					// If pNode would be the first child node, position the node according to its container's position
					positionNode(pNode, new Point(container.get().position().getX() + PADDING, 
							container.get().position().getY() + PADDING + TOP_HEIGHT));
				}
				else 
				{
					positionNode(pNode, pRequestedPosition);
				}
				result = new SimpleOperation( ()->  
				{ 
					pNode.attach(aDiagram);
					container.get().addChild(pNode); 
				},
				()-> 
				{ 
					pNode.detach();
					container.get().removeChild(pNode); 
				});
			}
		}
		if( result == null )
		{
			result = super.createAddNodeOperation(pNode, pRequestedPosition);
		}
		return result;
	}
	
	@Override
	protected ConstraintSet getAdditionalEdgeConstraints(Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint)
	{
		return new ConstraintSet(
				EdgeConstraints.maxEdges(pEdge, pStart, pEnd, aDiagram, 1),
				ClassDiagramEdgeConstraints.noSelfGeneralization(pEdge, pStart, pEnd),
				ClassDiagramEdgeConstraints.noSelfDependency(pEdge, pStart, pEnd),
				ClassDiagramEdgeConstraints.noDirectCycles(DependencyEdge.class, pEdge, pStart, pEnd),
				ClassDiagramEdgeConstraints.noDirectCycles(GeneralizationEdge.class, pEdge, pStart, pEnd),
				ClassDiagramEdgeConstraints.noDirectCycles(AggregationEdge.class, pEdge, pStart, pEnd),
				ClassDiagramEdgeConstraints.noDirectCycles(AssociationEdge.class, pEdge, pStart, pEnd),
				ClassDiagramEdgeConstraints.noCombinedAssociationAggregation(pEdge, pStart, pEnd)
		);
	}
	
	private static boolean validChild(Node pPotentialChild)
	{
		return pPotentialChild instanceof TypeNode || pPotentialChild instanceof AbstractPackageNode;
	}
	
	/* 
	 * Finds if the node to be added should be added to a package. Returns Optional.empty() if not. 
	 * If packages overlap, select the last one added, which by default should be on
	 * top. This could be fixed if we ever add a z coordinate to the diagram.
	 */
	private Optional<PackageNode> findContainer( List<Node> pNodes, Point pPoint)
	{
		PackageNode container = null;
		for( Node node : pNodes )
		{
			if( node instanceof PackageNode && NodeViewerRegistry.contains(node, pPoint) )
			{
				container = (PackageNode) node;
			}
		}
		if( container == null )
		{
			return Optional.empty();
		}
		List<Node> children = new ArrayList<>(container.getChildren());
		if( children.size() == 0 )
		{
			return Optional.of(container);
		}
		else
		{
			Optional<PackageNode> deeperContainer = findContainer( children, pPoint );
			if( deeperContainer.isPresent() )
			{
				return deeperContainer;
			}
			else 
			{
				return Optional.of(container);
			}
		}
	}

	/*
	 * Finds the package node under the position of the first node in pNodes.
	 * Returns Optional.empty() if there is no such package node for the nodes in pNodes to attach to,
	 * or the package node is already in the pNodes.
	 */
	private Optional<PackageNode> findPackageToAttach(List<Node> pNodes)
	{
		assert pNodes != null && pNodes.size() > 0;
		List<Node> rootNodes = new ArrayList<>(aDiagram.rootNodes());
		Point requestedPosition = pNodes.get(0).position();
		for( Node pNode: pNodes )
		{
			if(aDiagram.containsAsRoot(pNode))
			{
				rootNodes.remove(pNode);
			}
		}
		Optional<PackageNode> packageNode = findContainer(rootNodes, requestedPosition);
		if( !packageNode.isPresent() )
		{
			return Optional.empty();
		}
		// Returns Optional.empty() if the package node is in pNodes or contains any node in pNodes
		for(Node pNode: pNodes)
		{
			if( packageNode.get() == pNode || packageNode.get().getChildren().contains(pNode) )
			{
				return Optional.empty();
			}
		}
		return packageNode;
	}
	
	/*
	 * Returns true iff all the nodes in pNodes are linkable to a parent, or if the list is empty.
	 * @pre pNodes != null
	 */
	private static boolean allLinkable(List<Node> pNodes)
	{
		assert pNodes != null;
		return pNodes.stream().allMatch(ClassDiagramBuilder::isLinkable);
	}
	
	/*
	 * @param pNode The node to check
	 * @return True if the node is a valid child that does not already
	 * have a parent.
	 * @pre pNode != null
	 */
	private static boolean isLinkable(Node pNode)
	{
		return validChild(pNode) && !pNode.hasParent();
	}
	
	/*
	 * @param pNode The node to check
	 * @return True if the node is a valid child that already
	 * has a parent.
	 * @pre pNode != null
	 */
	private static boolean isUnlinkable(Node pNode)
	{
		// To be on the safe side we should technically check
		// that pNode also does not require a parent, but since
		// currently all linkable nodes do not require a parent, skip.
		return pNode.hasParent(); 
	}
	
	/*
	 * Returns true iff all the nodes in pNodes are detachable
	 * @pre pNodes != null && pNodes.size() > 0
	 */
	private static boolean allUnlinkable(List<Node> pNodes)
	{
		assert pNodes != null && pNodes.size() > 0;
		return pNodes.stream().allMatch(ClassDiagramBuilder::isUnlinkable);
	}

	/*
	 * Finds the parent of all the nodes in pNodes. Returns Optional.empty() if the nodes have different parents.
	 */
	private static Optional<Node> findSharedParent(List<Node> pNodes)
	{
		assert allUnlinkable(pNodes);
		// Get the parent of the first child node and check with other nodes
		Node parent = pNodes.get(0).getParent();
		for(Node pNode: pNodes)
		{
			if(parent != pNode.getParent())
			{
				return Optional.empty();
			}
		}
		return Optional.of(parent);
	}
	
	/**
	 * Returns whether attaching the nodes in pNodes to the package node under the position
	 * of the first node in pNodes is a valid operation on the diagram.
	 * 
	 * @param pNodes The nodes to attach. 
	 * @return True if it is possible to attach pNodes to the package node.
	 * @pre pNodes != null;
	 */
	public boolean canLinkToPackage(List<Node> pNodes)
	{
		assert pNodes!= null;
		if( pNodes.isEmpty() ) 
		{
			return false;
		}
		return allLinkable(pNodes) && findPackageToAttach(pNodes).isPresent();
	}
	
	/**
	 * Returns whether detaching the nodes in pNodes from the their parent is
	 * a valid operation on the diagram.
	 * 
	 * @param pNodes The nodes to detach.
	 * @return True if it is possible to detach pNodes from their parents, false otherwise
	 *     or if the list is empty.
	 * @pre pNodes != null;
	 */
	public boolean canUnlinkFromPackage(List<Node>pNodes)
	{
		assert pNodes!= null;
		if( pNodes.isEmpty() )
		{
			return false;
		}
		return allUnlinkable(pNodes) && findSharedParent(pNodes).isPresent();
	}
	
	/**
	 * Creates an operation that attaches all the nodes in pNodes to the package node under 
	 * the position of the first node in pNodes.
	 * 
	 * @param pNodes The nodes to attach.
	 * @return The requested operation.
	 * @pre canAttachToPackage(pNodes);
	 */
	public DiagramOperation createLinkToPackageOperation(List<Node>pNodes)
	{
		assert canLinkToPackage(pNodes);
		PackageNode packageNode = findPackageToAttach(pNodes).get();
		return new SimpleOperation( 
				()-> 
				{
					for( Node pNode: pNodes )
					{
						aDiagram.removeRootNode(pNode);
						packageNode.addChild(pNode);
						pNode.link(packageNode);
					}
				},
				()->
				{
					for( Node pNode: pNodes )
					{
						aDiagram.addRootNode(pNode);
						packageNode.removeChild(pNode);
					}
				});	
	}
	

	/**
	 * Creates an operation that detaches all the nodes in pNodes from their parent.
	 * 
	 * @param pNodes The nodes to detach.
	 * @return The requested operation.
	 * @pre canUnlinkFromPackage(pNodes);
	 */
	public DiagramOperation createUnlinkFromPackageOperation(List<Node> pNodes)
	{
		assert canUnlinkFromPackage(pNodes);
		Node parent = findSharedParent(pNodes).get();
		// CSOFF:
		Node outerParent = parent.hasParent() ? parent.getParent() : null; //CSON:
		if( outerParent == null )
		{
			// The parent of the nodes in pNodes does not have parent, 
			// set the detached nodes as root nodes in the diagram
			return new SimpleOperation( ()-> 
					{
						for( Node pNode: pNodes )
						{
							aDiagram.addRootNode(pNode);
							parent.removeChild(pNode);
						}
					},
					()->
					{
						for( Node pNode: pNodes )
						{
							aDiagram.removeRootNode(pNode);
							parent.addChild(pNode);
						}
					});	
		}
		else 
		{
			// Attach the detached nodes to the parent of their current parent
			return new SimpleOperation( 
					()-> 
					{
						for( Node pNode: pNodes )
						{
							parent.removeChild(pNode);
							outerParent.addChild(pNode);
						}
					},
					()->
					{
						for( Node pNode: pNodes )
						{
							outerParent.removeChild(pNode);
							parent.addChild(pNode);
						}
					});	
		}
	}
}
