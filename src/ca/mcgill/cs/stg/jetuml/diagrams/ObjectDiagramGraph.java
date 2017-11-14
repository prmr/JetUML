/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.FieldNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.ObjectCollaborationEdge;
import ca.mcgill.cs.stg.jetuml.graph.ObjectNode;
import ca.mcgill.cs.stg.jetuml.graph.ObjectReferenceEdge;

/**
 *   An UML-style object diagram that shows object references.
 */
public class ObjectDiagramGraph extends Graph
{
	private static final Node[] NODE_PROTOTYPES = new Node[3];
	private static final Edge[] EDGE_PROTOTYPES = new Edge[3];
	
	static
	{
		NODE_PROTOTYPES[0] = new ObjectNode();
	      
		FieldNode f = new FieldNode();
	    MultiLineString fn = new MultiLineString();
	    fn.setText("name");
	    f.setName(fn);
	    MultiLineString fv = new MultiLineString();
	    fv.setText("value");
	    f.setValue(fv);
	    NODE_PROTOTYPES[1] = f;
	      
	    NODE_PROTOTYPES[2] = new NoteNode();
	    
	    EDGE_PROTOTYPES[0] = new ObjectReferenceEdge();
	    
	    EDGE_PROTOTYPES[1] = new ObjectCollaborationEdge();
	    EDGE_PROTOTYPES[2] = new NoteEdge();
	}
	
	@Override
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2, Point2D pPoint2)
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

	 * @see ca.mcgill.cs.stg.jetuml.graph.Graph#add(ca.mcgill.cs.stg.jetuml.graph.Node, java.awt.geom.Point2D)
	 */
	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
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
		super.addNode(pNode, pPoint);
		return true;
	}
	
	/* Find if the node to be added can be added to an object. Returns null if not. 
	 * If a node is already the parent of the field (from a previously undone operation),
	 * return this node. Otherwise, find if a node is at the point
	 */
	private ObjectNode findObject(FieldNode pNode, Point2D pPoint)
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
			else if( node.contains(Conversions.toPoint(pPoint)) && canAddNodeAsChild(node, pNode))
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
	protected void completeEdgeAddition(Node pOrigin, Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		if( pOrigin instanceof FieldNode )
		{
			MultiLineString oldValue = ((FieldNode)pOrigin).getValue().clone();
			((FieldNode)pOrigin).getValue().setText("");
			notifyPropertyChanged(pOrigin, "value", oldValue, ((FieldNode)pOrigin).getValue());
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
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("object.extension");
	}

	@Override
	public String getDescription() 
	{
		return ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.UMLEditorStrings").getString("object.name");
	}
}





