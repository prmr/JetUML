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
import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.graph.CallNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.FieldNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.ImplicitParameterNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
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
	    
	    ClassRelationshipEdge association = new ClassRelationshipEdge();
	    association.setBentStyle(BentStyle.STRAIGHT);
	    EDGE_PROTOTYPES[1] = association;
	    EDGE_PROTOTYPES[2] = new NoteEdge();
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
					((FieldNode)pNode).setObjectNode((ObjectNode)n2);
				}
			}
			if (!inside)
			{
				return false;
			}
		}
		
		if(!super.add(pNode, pPoint)) 
		{
			return false;
		}
		return true;
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





