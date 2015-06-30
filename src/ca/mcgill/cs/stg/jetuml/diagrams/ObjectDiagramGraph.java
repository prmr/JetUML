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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.FieldNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.ObjectNode;
import ca.mcgill.cs.stg.jetuml.graph.ObjectReferenceEdge;
import ca.mcgill.cs.stg.jetuml.graph.PointNode;

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
	    
	    ClassRelationshipEdge association = new ClassRelationshipEdge();
	    EDGE_PROTOTYPES[1] = association;
	    EDGE_PROTOTYPES[2] = new NoteEdge();
	}
	
	@Override
	public boolean canConnect(Edge pEdge, Node pNode1, Node pNode2)
	{
		if( !super.canConnect(pEdge, pNode1, pNode2) )
		{
			return false;
		}
		if( pNode1 instanceof ObjectNode )
		{
			return (pEdge instanceof ClassRelationshipEdge && pNode2 instanceof ObjectNode) ||
					(pEdge instanceof NoteEdge && pNode2 instanceof NoteNode);
		}
		if( pNode1 instanceof FieldNode )
		{
			return pEdge instanceof ObjectReferenceEdge && pNode2 instanceof ObjectNode;
		}
		return true;
	}
	
	@Override
	public boolean add(Node pNode, Point2D pPoint)
	{
		
		if(pNode instanceof FieldNode) // must be inside an Object Node.
		{
			Collection<Node> nodes = getNodes();
			boolean inside = false;
			Iterator<Node> iter = nodes.iterator();
			while(!inside && iter.hasNext())
			{
				Node n2 = (Node)iter.next();
				if(n2 instanceof ObjectNode && n2.contains(pPoint)) 
				{
					inside = true;
					((FieldNode)pNode).setParent((ObjectNode)n2);
				}
			}
			if (!inside)
			{
				return false;
			}
		}
		
		return superadd(pNode, pPoint); 
	}
	
	@Override
	protected void addEdge(Node pOrigin, Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		if( pOrigin instanceof FieldNode )
		{
			((FieldNode)pOrigin).getValue().setText("");
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
	
	private boolean canAddNode(Node pParent, Node pPotentialChild)
	{
		if( pParent instanceof FieldNode )
		{
			return pPotentialChild instanceof PointNode;
		}
		else if( pParent instanceof ObjectNode )
		{
			if(pPotentialChild instanceof PointNode)
			{
				return true;
			}
			if(!(pPotentialChild instanceof FieldNode))
			{
				return false;
			}
			List<ChildNode> fields = ((ObjectNode)pParent).getChildren();
			FieldNode fNode = (FieldNode) pPotentialChild;
			return !fields.contains(fNode);
		}
		return false;
	}
	
	private void addNode(Node pParent, Node pChild, Point2D pPoint)
	{
		if( pParent instanceof ObjectNode )
		{
			List<ChildNode> fields = ((ObjectNode)pParent).getChildren();
			FieldNode fNode = (FieldNode) pChild;
			int i = 0;
			while(i < fields.size() && ((Node)fields.get(i)).getBounds().getY() < pPoint.getY())
			{
				i++;
			}
			((ObjectNode)pParent).addChild(i, fNode);
		}
	}
	
	/**
	 * Adds a node to the graph so that the top left corner of
	 * the bounding rectangle is at the given point.
	 * @param pNode the node to add
	 * @param pPoint the desired location
	 * @return True if the node was added.
	 */
	private boolean superadd(Node pNode, Point2D pPoint)
	{
		aModListener.startCompoundListening();

		super.add(pNode, pPoint);
				
		for(Node node : aNodes)
		{
			if(node == pNode)
			{
				continue;
			}
			if(node.contains(pPoint) && canAddNode(node, pNode))
			{
				addNode(node, pNode, pPoint);
				break;
			}
		}
		aModListener.endCompoundListening();
		return true;
	}
}





