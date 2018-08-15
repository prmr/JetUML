/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.views;

import ca.mcgill.cs.jetuml.diagram.ControlFlow;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.nodes.CallNode;
import ca.mcgill.cs.jetuml.geom.Point;

/**
 * A wrapper for a sequence diagram that can draw the diagram on a graphics context
 * and provide information about the geometry of the diagram.
 */
public class SequenceDiagramView extends DiagramView
{
	/**
	 * Creates a new DiagramView that wraps pDiagram.
	 * @param pDiagram The wrapped diagram.
	 * @pre pDiagram != null.
	 */
	public SequenceDiagramView(Diagram pDiagram)
	{
		super(pDiagram);
	}
	
	@Override
	protected Node deepFindNode( Node pNode, Point pPoint )
	{		
		ControlFlow flow = new ControlFlow((SequenceDiagram)aDiagram);
		if( pNode instanceof CallNode )
		{
			for(Node child : flow.getCallees(pNode))
			{
				if ( child != null )
				{
					Node node = deepFindNode(child, pPoint);
					if ( node != null )
					{
						return node;
					}
				}
			}
		}
		return super.deepFindNode(pNode, pPoint);
	}
}
