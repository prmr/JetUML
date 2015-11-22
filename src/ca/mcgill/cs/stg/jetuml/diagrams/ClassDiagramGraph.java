/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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
import java.util.List;
import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.graph.AggregationEdge;
import ca.mcgill.cs.stg.jetuml.graph.ChildNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.InterfaceNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;

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
		EDGE_PROTOTYPES[4] = new AggregationEdge();
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

	private static boolean canAddNodeAsChild(Node pPotentialChild)
	{
		return pPotentialChild instanceof ClassNode || pPotentialChild instanceof InterfaceNode || 
					pPotentialChild instanceof PackageNode ;
	}
	
	/* Find if the node to be added should be added to a package. Returns null if not. 
	 * If packages overlap, select the last one added, which by default should be on
	 * top. This could be fixed if we ever add a z coordinate to the graph.
	 */
	private PackageNode findContainer( List<Node> pNodes, Point2D pPoint)
	{
		PackageNode container = null;
		for( Node node : pNodes )
		{
			if( node instanceof PackageNode && node.contains(pPoint) )
			{
				container = (PackageNode) node;
			}
		}
		if( container == null )
		{
			return null;
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		List<Node> children = new ArrayList(((PackageNode)container).getChildren());
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
	
	@Override
	public boolean add(Node pNode, Point2D pPoint)
	{
		if( canAddNodeAsChild(pNode))
		{
			PackageNode container = null;
			if( pNode instanceof ChildNode && ((ChildNode) pNode).getParent() != null )
			{
				container = (PackageNode)((ChildNode) pNode).getParent();
			}
			else
			{
				container = findContainer(aRootNodes, pPoint);
			}
			if( container != null )
			{
				container.addChild((ChildNode)pNode);
			}
		}
		super.add(pNode, pPoint);
		return true;
	}
}





