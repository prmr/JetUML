/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.diagram.validator;

import java.util.Optional;

import org.jetuml.diagram.Node;

/**
 * A type that allows to check the Diagram's semantic validity.
 */
public interface DiagramValidator
{
	/**
	 * Should always return hasValidStructure() && hasValidSemantics().
	 * 
	 * @return True if the content of the diagram does not violate
	 * any structural or semantic rules. 
	 */
	boolean isValid();
	
	/**
	 * Checks if any rule violates the correctness of this diagram.
	 * @return Optional.empty if the diagram is correct, or a Violation that 
	 * describes the problem if not.
	 */
	Optional<Violation> validate();
	
	/**
	 * The structure of a diagram consists of the type of nodes and edges
	 * and any additional constraints on the nodes. This method does 
	 * not validate the parent-child relation between nodes because 
	 * the API for creating nodes does not allow invalid relations
	 * (see {@link Node#allowsAsChild(Node)}). This validations must thus 
	 * be done before a child is linked to a parent. Diagrams
	 * are assumed to respect the invariant that a link between a child
	 * and a parent is always valid.
	 * 
	 * @return True iff the diagram is well-formed.
	 */
	boolean hasValidStructure();
	
	/**
	 * The semantic rules validated by this method concern the legality of 
	 * edge connections.
	 * 	  
	 * @return True iff the diagram respects all required semantic validation rules.
	 * @pre hasValidStructure()
	 */
	boolean hasValidSemantics();
}
