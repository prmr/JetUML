/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.rendering;

import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;

/**
 * Represents the priority level for edges so that Layouter can lay out edges in a hierarchical order. 
 * Arranged in decreasing order of priority (INHERITANCE has the highest priority)
 */
public enum EdgePriority 
{
	INHERITANCE, IMPLEMENTATION, AGGREGATION, COMPOSITION,
	ASSOCIATION, DEPENDENCY, SELF_EDGE, OTHER;
	
	/**
	 * Gets the EdgePriority of pEdge.
	 * @param pEdge the edge of interest
	 * @return the EdgePriority associated with pEdge
	 * @pre pEdge!=null
	 * CSOFF:
	 */
	public static EdgePriority priorityOf(Edge pEdge)
	{
		assert pEdge != null;
		if(pEdge.start()!= null && pEdge.end() != null && pEdge.start().equals(pEdge.end()))
		{
			return EdgePriority.SELF_EDGE;
		}
		else if(pEdge instanceof GeneralizationEdge edge)
		{
			if(edge.getType() == GeneralizationEdge.Type.Inheritance) 
			{
				return EdgePriority.INHERITANCE;
			}
			else
			{
				return EdgePriority.IMPLEMENTATION;
			}
		}
		else if(pEdge instanceof AggregationEdge edge)
		{
			if(edge.getType() == AggregationEdge.Type.Aggregation)
			{
				return EdgePriority.AGGREGATION;
			}
			else
			{
				return EdgePriority.COMPOSITION;
			}
		}
		else if(pEdge instanceof AssociationEdge)
		{
			return EdgePriority.ASSOCIATION;
		}
		else if(pEdge instanceof DependencyEdge)
		{
			return EdgePriority.DEPENDENCY;
		}
		else
		{
			return EdgePriority.OTHER;
		}
	} // CSON:
	
	/**
	 * Returns whether pPriority describes a segmented edge.
	 * Since Layouter plans the paths of self-edges separately, self-edges are not segmented by this method. 
	 * @param pPriority the EdgePriority level of interest
	 * @return true if the pPriority is the priority for a segmented edge, false otherwise
	 * @pre pPriority!=null;
	 */
	public static boolean isSegmented(EdgePriority pPriority)
	{
		assert pPriority != null;
		if(pPriority == EdgePriority.INHERITANCE || pPriority == EdgePriority.IMPLEMENTATION)
		{
			return true;
		}
		else 
		{
			return pPriority == EdgePriority.AGGREGATION || pPriority == EdgePriority.COMPOSITION || 
					pPriority == EdgePriority.ASSOCIATION;    	
		}
	}
	
	/**
	 * Returns whether pEdge is segmented.
	 * @param pEdge the edge of interest
	 * @return true if pEdge is segmented, false otherwise.
	 */
	public static boolean isSegmented(Edge pEdge)
	{
		return isSegmented(priorityOf(pEdge));
	}
	
	/**
	 * Returns whether pEdge is a  class diagram Edge which can be stored in EdgeStorage by Layouter. 
	 * @param pEdge the edge of interest
	 * @return true if pEdge should be stored in EdgeStorage, false otherwise. 
	 */
	public static boolean isStoredEdge(Edge pEdge)
	{
		return priorityOf(pEdge)!= OTHER;
	}
}
