/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

package ca.mcgill.cs.jetuml.diagram.builder.constraints;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.AssociationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.DependencyEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;

/**
 * Methods to create edge addition constraints that only apply to
 * class diagrams. CSOFF:
 */
public final class ClassDiagramEdgeConstraints
{
	private ClassDiagramEdgeConstraints() {}
	
	/*
	 * Self edges are not allowed for Generalization edges.
	 */
	public static Constraint noSelfGeneralization(Edge pEdge, Node pStart, Node pEnd)
	{
		return ()-> 
		{
			return !( pEdge.getClass() == GeneralizationEdge.class && pStart == pEnd );
		};
	}
	
	/*
	 * Self edges are not allowed for Dependency edges.
	 */
	public static Constraint noSelfDependency(Edge pEdge, Node pStart, Node pEnd)
	{
		return () ->
		{
			return !( pEdge.getClass() == DependencyEdge.class && pStart == pEnd );
		};
	}
	
	/*
	 * There can't be two edges of a given type, one in each direction, between two nodes.
	 */
	public static Constraint noDirectCycles(Class<? extends Edge> pEdgeType, 
			Edge pEdge, Node pStart, Node pEnd)
	{
		return () ->
		{
			if( pEdge.getClass() != pEdgeType )
			{
				return true;
			}
			for( Edge edge : pStart.getDiagram().get().edgesConnectedTo(pStart) )
			{
				if( edge.getClass() == pEdgeType && edge.getEnd() == pStart && edge.getStart() == pEnd )
				{
					return false;
				}
			}
			return true;
		};
	}
	
	/*
	 * There can't be both an association and an aggregation edge between two nodes
	 */
	public static Constraint noCombinedAssociationAggregation(Edge pEdge, Node pStart, Node pEnd)
	{
		return () ->
		{
			if( pEdge.getClass() != AssociationEdge.class && pEdge.getClass() != AggregationEdge.class )
			{
				return true;
			}
			for( Edge edge : pStart.getDiagram().get().edgesConnectedTo(pStart) )
			{
				boolean targetEdge = edge.getClass() == AssociationEdge.class || edge.getClass() == AggregationEdge.class;
				boolean sameInOneDirection = edge.getStart() == pStart && edge.getEnd() == pEnd;
				boolean sameInOtherDirection = edge.getStart() == pEnd && edge.getEnd() == pStart;
				if( targetEdge && (sameInOneDirection || sameInOtherDirection))
				{
					return false;
				}
			}
			return true;
		};
	}
}
