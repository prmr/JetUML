/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2019 by the contributors of the JetUML project.
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
import java.util.List;
import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ClassDiagramEdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ParentNode;
import ca.mcgill.cs.jetuml.diagram.nodes.TypeNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * A builder for class diagrams.
 */
public class ClassDiagramBuilder extends DiagramBuilder
{
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
				positionNode(pNode, pRequestedPosition);
				result = new SimpleOperation( ()->  
				{ 
					pNode.attach(aDiagram);
					container.get().addChild((ChildNode)pNode); 
				},
				()-> 
				{ 
					pNode.detach();
					container.get().removeChild((ChildNode)pNode); 
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
				ClassDiagramEdgeConstraints.noSelfGeneralization(pEdge, pStart, pEnd)
		);
	}
	
	private static boolean validChild(Node pPotentialChild)
	{
		return pPotentialChild instanceof TypeNode || 
					pPotentialChild instanceof PackageNode ;
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
	 * Returns true iff all the nodes in pNodes are attachable, or if the list is empty.
	 * @pre pNodes != null
	 */
	private static boolean allAttachable(List<Node> pNodes)
	{
		assert pNodes != null;
		return pNodes.stream().allMatch(ClassDiagramBuilder::isAttachable);
	}
	
	/*
	 * @param pNode The node to check
	 * @return True if the node is a valid child that does not already
	 * have a parent.
	 * @pre pNode != null
	 */
	private static boolean isAttachable(Node pNode)
	{
		return validChild(pNode) && ((ChildNode)pNode).getParent() == null;
	}
	
	/*
	 * @param pNode The node to check
	 * @return True if the node is a valid child that already
	 * has a parent.
	 * @pre pNode != null
	 */
	private static boolean isDetachable(Node pNode)
	{
		return validChild(pNode) && ((ChildNode)pNode).getParent() != null;
	}
	
	/*
	 * Returns true iff all the nodes in pNodes are detachable
	 * @pre pNodes != null && pNodes.size() > 0
	 */
	private static boolean allDetachable(List<Node> pNodes)
	{
		assert pNodes != null && pNodes.size() > 0;
		return pNodes.stream().allMatch(ClassDiagramBuilder::isDetachable);
	}

	/*
	 * Finds the parent of all the nodes in pNodes. Returns Optional.empty() if the nodes have different parents.
	 */
	private static Optional<ParentNode> findSharedParent(List<Node> pNodes)
	{
		assert allDetachable(pNodes);
		// Get the parent of the first child node and check with other nodes
		ParentNode parent = ((ChildNode)pNodes.get(0)).getParent();
		for(Node pNode: pNodes)
		{
			if(parent != ((ChildNode)pNode).getParent())
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
	public boolean canAttachToPackage(List<Node> pNodes)
	{
		assert pNodes!= null;
		if( pNodes.isEmpty() ) 
		{
			return false;
		}
		return allAttachable(pNodes) && findPackageToAttach(pNodes).isPresent();
	}
	
	/**
	 * Returns whether detaching the nodes in pNodes from the their parent is
	 * a valid operation on the diagram.
	 * 
	 * @param pNodes The nodes to detach.
	 * @return True if it is possible to detach pNodes from their parents, false otherwise
	 * or if the list is empty.
	 * @pre pNodes != null;
	 */
	public boolean canDetachFromPackage(List<Node>pNodes)
	{
		assert pNodes!= null;
		if( pNodes.isEmpty() )
		{
			return false;
		}
		return allDetachable(pNodes) && findSharedParent(pNodes).isPresent();
	}
	
	/**
	 * Creates an operation that attaches all the nodes in pNodes to the package node under 
	 * the position of the first node in pNodes.
	 * 
	 * @param pNodes The nodes to attach.
	 * @return The requested operation.
	 * @pre canAttachToPackage(pNodes);
	 */
	public DiagramOperation createAttachToPackageOperation(List<Node>pNodes)
	{
		assert canAttachToPackage(pNodes);
		PackageNode packageNode = findPackageToAttach(pNodes).get();
		return new SimpleOperation( 
				()-> 
				{
					for( Node pNode: pNodes )
					{
						aDiagram.removeRootNode(pNode);
						packageNode.addChild((ChildNode)pNode);
						((ChildNode)pNode).setParent(packageNode);
					}
				},
				()->
				{
					for( Node pNode: pNodes )
					{
						aDiagram.addRootNode(pNode);
						packageNode.removeChild((ChildNode)pNode);
						((ChildNode)pNode).setParent(null);
					}
				});	
	}
	

	/**
	 * Creates an opeartion that detaches all the nodes in pNodes from their parent.
	 * 
	 * @param pNodes The nodes to detach.
	 * @return The requested operation.
	 * @pre canDetachFromPackage(pNodes);
	 */
	public DiagramOperation createDetachFromPackageOperation(List<Node> pNodes)
	{
		assert canDetachFromPackage(pNodes);
		ParentNode parent = findSharedParent(pNodes).get();
		// CSOFF:
		ParentNode outerParent = (parent instanceof ChildNode) ? ((ChildNode)parent).getParent() : null; //CSON:
		if( outerParent == null )
		{
			// The parent of the nodes in pNodes does not have parent, attach the detached nodes to the Diagram
			return new SimpleOperation( 
					()-> 
					{
						for( Node pNode: pNodes )
						{
							aDiagram.addRootNode(pNode);
							parent.removeChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(null);
						}
					},
					()->
					{
						for( Node pNode: pNodes )
						{
							aDiagram.removeRootNode(pNode);
							parent.addChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(parent);
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
							outerParent.addChild((ChildNode)pNode);
							parent.removeChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(outerParent);
						}
					},
					()->
					{
						for( Node pNode: pNodes )
						{
							outerParent.removeChild((ChildNode)pNode);
							parent.addChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(parent);
						}
					});	
		}
	}
}
