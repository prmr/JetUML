/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

package ca.mcgill.cs.stg.jetuml.diagrams;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.HierarchicalNode;
import ca.mcgill.cs.stg.jetuml.graph.InterfaceNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;
import ca.mcgill.cs.stg.jetuml.graph.PointNode;

/**
 *  A UML class diagram.
 */
public class ClassDiagramGraph extends Graph
{
	//CSOFF:
	private static final Node[] NODE_PROTOTYPES = new Node[] {new ClassNode(), new InterfaceNode(), new PackageNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[7];
	
	static
	{
		EDGE_PROTOTYPES[0] = ClassRelationshipEdge.createDependencyEdge();
	      
		EDGE_PROTOTYPES[1] = ClassRelationshipEdge.createInheritanceEdge();

		EDGE_PROTOTYPES[2] = ClassRelationshipEdge.createInterfaceInheritanceEdge();

		EDGE_PROTOTYPES[3] = ClassRelationshipEdge.createAssociationEdge();

		EDGE_PROTOTYPES[4] = ClassRelationshipEdge.createAggregationEdge();

		EDGE_PROTOTYPES[5] = ClassRelationshipEdge.createCompositionEdge();
		
		EDGE_PROTOTYPES[6] = new NoteEdge();	
	}
	//CSON:
	
	@Override
	public Node[] getNodePrototypes()
	{
		return NODE_PROTOTYPES;
	}

	@Override
	public Edge[] getEdgePrototypes()
	{
		return EDGE_PROTOTYPES;
	}

	@Override
	public String getFileExtension() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("class.extension");
	}

	@Override
	public String getDescription() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("class.name");
	}
	
	private boolean canAddNode(Node pParent, Node pPotentialChild)
	{
		if( pParent instanceof ClassNode || pParent instanceof InterfaceNode )
		{
			return pPotentialChild instanceof PointNode;
		}
		else if( pParent instanceof PackageNode )
		{
			return pPotentialChild instanceof ClassNode || pPotentialChild instanceof InterfaceNode || 
					pPotentialChild instanceof PackageNode || pPotentialChild instanceof NoteNode;
		}
		return false;
	}
	
	private void addNode(Node pParent, Node pChild, Point2D pPoint)
	{
		if( pParent instanceof PackageNode )
		{
			((PackageNode)pParent).addChild((HierarchicalNode)pChild);
		}
	}
	
	/**
	 * Adds a node to the graph so that the top left corner of
	 * the bounding rectangle is at the given point.
	 * @param pNode the node to add
	 * @param pPoint the desired location
	 * @return True if the node was added.
	 */
	public boolean add(Node pNode, Point2D pPoint)
	{
		aModListener.startCompoundListening();

		if(!super.add(pNode, pPoint))
		{
			aModListener.endCompoundListening();
			return false;
		}
		
		boolean accepted = false;
		for(int i = aNodes.size() - 1; i >= 0 && !accepted; i--)
		{
			Node parent = aNodes.get(i);
			if (parent == pNode)
			{
				continue;
			}
			if (parent.contains(pPoint) && canAddNode(parent, pNode))
			{
				addNode(parent, pNode, pPoint);
				if(pNode instanceof HierarchicalNode && parent instanceof HierarchicalNode)
				{
					HierarchicalNode curNode = (HierarchicalNode) pNode;
					HierarchicalNode parentParent = (HierarchicalNode) parent;
					aModListener.childAttached(this, parentParent.getChildren().indexOf(pNode), parentParent, curNode);
				}
				accepted = true;
			}
		}
		aModListener.endCompoundListening();
		return true;
	}
	
	/**
	 * Removes a node and all edges that start or end with that node.
	 * @param pNode the node to remove
	 * @return false if node was already deleted, true if deleted properly
	 */
	public boolean removeNode(Node pNode)
	{
		if(aNodesToBeRemoved.contains(pNode))
		{
			return false;
		}
		aModListener.startCompoundListening();
		// notify nodes of removals
		for(int i = 0; i < aNodes.size(); i++)
		{
			Node n2 = aNodes.get(i);
			if(n2 instanceof HierarchicalNode && pNode instanceof HierarchicalNode)
			{
				HierarchicalNode curNode = (HierarchicalNode) n2;
				HierarchicalNode parentParent = (HierarchicalNode) pNode;
				if(curNode.getParent()!= null && curNode.getParent().equals(pNode))
				{
					aModListener.childDetached(this, parentParent.getChildren().indexOf(curNode), parentParent, curNode);
				}
			}
		}
		/*Remove the children too @JoelChev*/
		if(pNode instanceof HierarchicalNode)
		{
			ArrayList<HierarchicalNode> children = new ArrayList<HierarchicalNode>(((HierarchicalNode) pNode).getChildren());
			//We create a shallow clone so deleting children does not affect the loop
			for(Node childNode: children)
			{
				removeNode(childNode);
			}
		}
		super.removeNode(pNode);
		aModListener.endCompoundListening();
		return true;
	}
}





