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

package ca.mcgill.cs.jetuml.diagram.builder;

import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ClassDiagramEdgeConstraints;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.ClassNode;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Point;

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
		assert pDiagram instanceof ClassDiagram;
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
				result = new SimpleOperation( ()-> container.addChild((ChildNode)pNode),
						()-> container.removeChild((ChildNode)pNode));
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
		return pPotentialChild instanceof ClassNode || pPotentialChild instanceof InterfaceNode || 
					pPotentialChild instanceof PackageNode ;
	}
	
	/* Find if the node to be added should be added to a package. Returns null if not. 
	 * If packages overlap, select the last one added, which by default should be on
	 * top. This could be fixed if we ever add a z coordinate to the diagram.
	 */
	private PackageNode findContainer( Iterable<Node> pNodes, Point pPoint)
	{
		PackageNode container = null;
		for( Node node : pNodes )
		{
			if( node instanceof PackageNode && node.view().contains(pPoint) )
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
}
