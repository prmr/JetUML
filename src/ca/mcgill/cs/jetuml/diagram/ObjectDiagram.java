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

package ca.mcgill.cs.jetuml.diagram;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.ArrayList;

import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectCollaborationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.FieldNode;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ObjectNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 *  An UML-style object diagram that shows object references.
 */
public class ObjectDiagram extends Diagram
{
	private static final Node[] NODE_PROTOTYPES = new Node[3];
	private static final Edge[] EDGE_PROTOTYPES = new Edge[3];
	
	static
	{
		NODE_PROTOTYPES[0] = new ObjectNode();
	      
		FieldNode fieldNode = new FieldNode();
	    fieldNode.setName("name");
	    fieldNode.setValue("value");
	    
	    NODE_PROTOTYPES[1] = fieldNode;
	    NODE_PROTOTYPES[2] = new NoteNode();
	    
	    EDGE_PROTOTYPES[0] = new ObjectReferenceEdge();
	    EDGE_PROTOTYPES[1] = new ObjectCollaborationEdge();
	    EDGE_PROTOTYPES[2] = new NoteEdge();
	}
	
	@Override
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2, Point pPoint2)
	{
		if( !super.canConnect(pEdge, pNode1, pNode2, pPoint2) )
		{
			return false;
		}
		if( pNode1 instanceof ObjectNode )
		{
			return (pEdge instanceof ObjectCollaborationEdge && pNode2 instanceof ObjectNode) ||
					(pEdge instanceof NoteEdge && pNode2 instanceof NoteNode);
		}
		if( pNode1 instanceof FieldNode )
		{
			return pEdge instanceof ObjectReferenceEdge && pNode2 instanceof ObjectNode;
		}
		return true;
	}
	
	/* 
	 * See if, given its position, the node should be added as a child of
	 * an ObjectNode.

	 * @see ca.mcgill.cs.jetuml.diagram.Diagram#add(ca.mcgill.cs.jetuml.diagram.Node, java.awt.geom.Point2D)
	 */
	@Override
	public boolean addNode(Node pNode, Point pPoint, int pMaxWidth, int pMaxHeight)
	{
		if( pNode instanceof FieldNode )
		{
			ObjectNode object = findObject((FieldNode)pNode, pPoint);
			
			if( object != null )
			{
				object.addChild((ChildNode)pNode); // Must be called before super.add so that the node's parent isn't null
			}
			else
			{
				return false;
			}
		}
		super.addNode(pNode, pPoint, pMaxWidth, pMaxHeight);
		return true;
	}
	
	@Override
	public boolean canAdd(Node pNode, Point pRequestedPosition)
	{
		boolean result = true;
		if( pNode instanceof FieldNode && findObject((FieldNode)pNode, pRequestedPosition) == null )
		{
			result = false;
		}
		return result;
	}
	
	/* Find if the node to be added can be added to an object. Returns null if not. 
	 * If a node is already the parent of the field (from a previously undone operation),
	 * return this node. Otherwise, find if a node is at the point
	 */
	private ObjectNode findObject(FieldNode pNode, Point pPoint)
	{
		ArrayList<ObjectNode> candidates = new ArrayList<>();
		for( Node node : aRootNodes )
		{
			if( node == pNode )
			{
				continue;
			}
			else if( pNode.getParent() == node )
			{
				return (ObjectNode)node;
			}
			else if( node.view().contains(pPoint) && canAddNodeAsChild(node, pNode))
			{
				candidates.add((ObjectNode)node); // canAddNodeAsChild ensures the downcast is valid
			}
		}
		// Pick the last node in the list as some inexact but simple
		// heuristic for choosing the top node. We'll need a z-coordinate to do better.
		if( candidates.size() > 0 )
		{
			return candidates.get(candidates.size()-1);
		}
		else
		{
			return null;
		}
	}
	
	private static boolean canAddNodeAsChild(Node pParent, Node pPotentialChild)
	{
		if( pParent instanceof ObjectNode )
		{
			return pPotentialChild instanceof FieldNode;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	protected void completeEdgeAddition(Node pOrigin, Edge pEdge, Point pPoint1, Point pPoint2)
	{
		if( pOrigin instanceof FieldNode )
		{
			String oldValue = ((FieldNode)pOrigin).getValue();
			((FieldNode)pOrigin).setValue("");
			notifyPropertyChanged(pOrigin, "value", oldValue);
		}
	}
	
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
		return RESOURCES.getString("objectdiagram.file.extension");
	}

	@Override
	public String getDescription() 
	{
		return RESOURCES.getString("objectdiagram.file.name");
	}
}
