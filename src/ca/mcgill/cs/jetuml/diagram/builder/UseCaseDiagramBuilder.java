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

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.UseCaseDiagram;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.EdgeConstraints;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.diagram.builder.constraints.ConstraintSet;

/**
 * A builder for use case diagram.
 */
public class UseCaseDiagramBuilder extends DiagramBuilder
{
	/**
	 * Creates a new builder for use case diagrams.
	 * 
	 * @param pDiagram The diagram to wrap around.
	 * @pre pDiagram != null;
	 */
	public UseCaseDiagramBuilder( Diagram pDiagram )
	{
		super( pDiagram );
		assert pDiagram instanceof UseCaseDiagram;
	}
	
	@Override
	protected ConstraintSet getAdditionalEdgeConstraints(Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint)
	{
		return new ConstraintSet(
				EdgeConstraints.maxEdges(pEdge, pStart, pEnd, aDiagram, 1),
				EdgeConstraints.noSelfEdge(pStart, pEnd)
		);
	}
}
