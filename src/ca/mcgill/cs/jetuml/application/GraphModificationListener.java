/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.application;

import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.graph.Property;

/**
 * Specifies a list of callback methods for any object
 * interested in modifications to a graph. Maps to the 
 * Observer interface in the Observer design pattern.
 * 
 * @author EJBQ - Initial code
 * @author Martin P. Robillard Observer inteface refactoring.
 *
 */
public interface GraphModificationListener
{
	/**
	 * Called whenever a node is added to a graph.
	 * @param pGraph The target graph.
	 * @param pNode The node added.
	 */
	void nodeAdded(Graph pGraph, Node pNode);
	
	/**
	 * Called whenever a node is removed from a graph.
	 * @param pGraph The target graph.
	 * @param pNode The node removed.
	 */
	void nodeRemoved(Graph pGraph, Node pNode);

	/**
	 * Called whenever an edge is added to a graph.
	 * @param pGraph The target graph.
	 * @param pEdge The edge added
	 */
	void edgeAdded(Graph pGraph, Edge pEdge);

	/**
	 * Called whenever an edge is removed from a graph.
	 * @param pGraph The target graph.
	 * @param pEdge The edge removed
	 */
	void edgeRemoved(Graph pGraph, Edge pEdge);

	/**
	 * Indicates that the graph is about to be modified
	 * through multiple related operations.
	 */
	void startingCompoundOperation();
	
	/**
	 * Indicates that a compound operation has been
	 * completed. 
	 */
	void finishingCompoundOperation();
	
	/**
	 * Notifies implementers that a property of a graph element has
	 * a new value.
	 * 
	 * @param pProperty The changed property.
	 * @param pOldValue The value of the property before the change.
	 */
	void propertyChanged(Property pProperty, Object pOldValue);
}
