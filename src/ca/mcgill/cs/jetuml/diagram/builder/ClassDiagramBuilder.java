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
 * A builder for class diagram.
 */
public class ClassDiagramBuilder extends DiagramBuilder
{
	/**
	 * Creates a new builder for class diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
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
			PackageNode container = findContainer(aDiagram.rootNodes(), pRequestedPosition);
			if( container != null )
			{
				positionNode(pNode, pRequestedPosition);
				result = new SimpleOperation( ()->  
				{ 
					pNode.attach(aDiagram);
					container.addChild((ChildNode)pNode); 
				},
				()-> 
				{ 
					pNode.detach();
					container.removeChild((ChildNode)pNode); 
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
	 * Find if the node to be added should be added to a package. Returns null if not. 
	 * If packages overlap, select the last one added, which by default should be on
	 * top. This could be fixed if we ever add a z coordinate to the diagram.
	 */
	private PackageNode findContainer( Iterable<Node> pNodes, Point pPoint)
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
			return null;
		}
		List<Node> children = new ArrayList<>(container.getChildren());
		if( children.size() == 0 )
		{
			return container;
		}
		else
		{
			PackageNode deeperContainer = findContainer( children, pPoint );
			if( deeperContainer == null )
			{
				return container;
			}
			else
			{
				return deeperContainer;
			}
		}
	}

	/*
	 * Find the package node under the position of the first node in pNodes.
	 * Returns null if there is no such package node for the nodes in pNodes to attach to,
	 * or the package node is already in the pNodes.
	 */
	private PackageNode findPackageToAttach(Iterable<Node> pNodes)
	{
		Point pRequestedPosition = null;
		List<Node> rootNodes = new ArrayList<>();
		aDiagram.rootNodes().forEach(rootNodes::add);
		for(Node pNode: pNodes)
		{
			// Get the position of the first node in pNodes
			if(pRequestedPosition== null)
			{
				pRequestedPosition = pNode.position();
			}
			if(aDiagram.containsAsRoot(pNode))
			{
				rootNodes.remove(pNode);
			}
		}
		PackageNode pPackageNode = findContainer(rootNodes, pRequestedPosition);
		if(pPackageNode == null)
		{
			return null;
		}
		// Returns null if the package node is in pNodes or contains any node in pNodes
		for(Node pNode: pNodes)
		{
			if(pPackageNode == pNode || pPackageNode.getChildren().contains(pNode))
			{
				pPackageNode = null;
			}
		}
		return pPackageNode;
	}
	
	/*
	 * Returns true iff all the nodes in pNodes have non-null parents
	 */
	private static boolean haveNonNullParent(Iterable<Node> pNodes)
	{
		boolean haveNonNullParent = false;
		for(Node pNode: pNodes)
		{
			haveNonNullParent = validChild(pNode) && ((ChildNode)pNode).getParent() != null;
		}
		return haveNonNullParent;
	}
	
	/*
	 * Retruns true iff all the nodes in pNodes have null parents
	 */
	private static boolean haveNullParent(Iterable<Node> pNodes)
	{
		boolean haveNonNullParent = false;
		for(Node pNode: pNodes)
		{
			haveNonNullParent = validChild(pNode) && ((ChildNode)pNode).getParent() == null;
		}
		return haveNonNullParent;
	}

	/*
	 * Find the parent of all the nodes in pNodes. Return null if the nodes have different parents.
	 */
	private static ParentNode findSharedParent(Iterable<Node> pNodes)
	{
		assert haveNonNullParent(pNodes);
		List<ChildNode> pChildNodes = new ArrayList<>();
		for(Node pNode: pNodes)
		{
			pChildNodes.add((ChildNode)pNode); 
		}
		// Get the parent of the first child node and check with other nodes
		ParentNode pParent = pChildNodes.remove(0).getParent();
		for(ChildNode pChild: pChildNodes)
		{
			if(pParent != pChild.getParent())
			{
				return null;
			}
		}
		return pParent;
	}
	
	/**
	 * Return true if the parent of all the nodes in pNodes is null and there exists 
	 * a package node under the position of the first node in pNodes.
	 */
	@Override
	public boolean canAttachToPackage(Iterable<Node> pNodes)
	{
		return haveNullParent(pNodes) && findPackageToAttach(pNodes) != null;
	}
	
	/**
	 * Return true if the nodes in pNodes have the same non-null parent.
	 */
	@Override
	public boolean canDetachFromPackage(Iterable<Node>pNodes)
	{
		return haveNonNullParent(pNodes) && findSharedParent(pNodes)!= null;
	}
	
	/**
	 * Creates an opeartion that attaches all the nodes in pNodes to the package node under 
	 * the position of the first node in pNodes.
	 */
	@Override
	public DiagramOperation createAttachToPackageOperation(Iterable<Node>pNodes)
	{
		PackageNode pPackageNode = findPackageToAttach(pNodes);
		return new SimpleOperation( 
				()-> 
				{
					for(Node pNode: pNodes)
					{
						aDiagram.removeRootNode(pNode);
						pPackageNode.addChild((ChildNode)pNode);
						((ChildNode)pNode).setParent(pPackageNode);
					}
				},
				()->
				{
					for(Node pNode: pNodes)
					{
						aDiagram.addRootNode(pNode);
						pPackageNode.removeChild((ChildNode)pNode);
						((ChildNode)pNode).setParent(null);
					}
				});	
	}
	
	/**
	 * Creates an opeartion that detaches all the nodes in pNodes from their parent. 
	 */
	@Override
	public DiagramOperation createDetachFromPackageOperation(Iterable<Node> pNodes)
	{
		ParentNode pParent = findSharedParent(pNodes);
		ParentNode outerParent = (pParent instanceof ChildNode)? ((ChildNode)pParent).getParent():null;
		if(outerParent == null)
		{
			// The parent of the nodes in pNodes does not have parent, attach the detached nodes to the Diagram
			return new SimpleOperation( 
					()-> 
					{
						for(Node pNode: pNodes)
						{
							aDiagram.addRootNode(pNode);
							pParent.removeChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(null);
						}
					},
					()->
					{
						for(Node pNode: pNodes)
						{
							aDiagram.removeRootNode(pNode);
							pParent.addChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(pParent);
						}
					});	
		}
		else 
		{
			// Attach the detached nodes to the parent of their current parent
			return new SimpleOperation( 
					()-> 
					{
						for(Node pNode: pNodes)
						{
							outerParent.addChild((ChildNode)pNode);
							pParent.removeChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(outerParent);
						}
					},
					()->
					{
						for(Node pNode: pNodes)
						{
							outerParent.removeChild((ChildNode)pNode);
							pParent.addChild((ChildNode)pNode);
							((ChildNode)pNode).setParent(pParent);
						}
					});	
		}
	}
}
