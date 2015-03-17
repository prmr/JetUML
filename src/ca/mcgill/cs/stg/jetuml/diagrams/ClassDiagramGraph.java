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

import java.util.ResourceBundle;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.BentStyle;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;
import ca.mcgill.cs.stg.jetuml.graph.AggregationEdge;
import ca.mcgill.cs.stg.jetuml.graph.AssociationEdge;
import ca.mcgill.cs.stg.jetuml.graph.ClassNode;
import ca.mcgill.cs.stg.jetuml.graph.ClassRelationshipEdge;
import ca.mcgill.cs.stg.jetuml.graph.CompositionEdge;
import ca.mcgill.cs.stg.jetuml.graph.DependencyEdge;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.InheritanceEdge;
import ca.mcgill.cs.stg.jetuml.graph.InterfaceInheritanceEdge;
import ca.mcgill.cs.stg.jetuml.graph.InterfaceNode;
import ca.mcgill.cs.stg.jetuml.graph.Node;
import ca.mcgill.cs.stg.jetuml.graph.NoteEdge;
import ca.mcgill.cs.stg.jetuml.graph.NoteNode;
import ca.mcgill.cs.stg.jetuml.graph.PackageNode;

/**
 *   A UML class diagram.
 */
public class ClassDiagramGraph extends Graph
{
	//CSOFF:
	private static final Node[] NODE_PROTOTYPES = new Node[] {new ClassNode(), new InterfaceNode(), new PackageNode(), new NoteNode()};
	private static final Edge[] EDGE_PROTOTYPES = new Edge[7];
	
	static
	{
		EDGE_PROTOTYPES[0] = new DependencyEdge();
	      
		EDGE_PROTOTYPES[1] = new InheritanceEdge();

		EDGE_PROTOTYPES[2] = new InterfaceInheritanceEdge();

		EDGE_PROTOTYPES[3] = new AssociationEdge();

		EDGE_PROTOTYPES[4] = new AggregationEdge();

		EDGE_PROTOTYPES[5] = new CompositionEdge();
		
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
}





