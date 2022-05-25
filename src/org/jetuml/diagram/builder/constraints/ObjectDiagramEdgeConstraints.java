/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/

package org.jetuml.diagram.builder.constraints;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.edges.ObjectCollaborationEdge;
import org.jetuml.diagram.edges.ObjectReferenceEdge;
import org.jetuml.diagram.nodes.FieldNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.geom.Point;

/**
 * Methods to create edge addition constraints that only apply to
 * state diagrams. CSOFF:
 */
public final class ObjectDiagramEdgeConstraints
{
	private ObjectDiagramEdgeConstraints() {}
	
	/*
	 * A collaboration edge can only be between two object nodes
	 */
	public static Constraint collaboration()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			return !(pEdge.getClass() == ObjectCollaborationEdge.class && 
					(pStart.getClass() != ObjectNode.class || pEnd.getClass() != ObjectNode.class));
		};
	}
	
	/*
	 * A reference edge can only be between an object node and a field node.
	 */
	public static Constraint reference()
	{
		return (Edge pEdge, Node pStart, Node pEnd, Point pStartPoint, Point pEndPoint, Diagram pDiagram)->
		{
			return !(pEdge.getClass() == ObjectReferenceEdge.class &&
					(pStart.getClass() != FieldNode.class || pEnd.getClass() != ObjectNode.class));
		};
	}
}