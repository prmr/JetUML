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
package org.jetuml.persistence;

import org.jetuml.diagram.Diagram;

/**
 * A serialization context automatically finds all the nodes
 * in a diagram, including children nodes, and creates a new map between
 * nodes and identifiers.
 */
public class SerializationContext extends AbstractContext
{
	/**
	 * Automatically creates the map between nodes in pDiagram
	 * and fresh identifiers.
	 * 
	 * @param pDiagram The diagram to load into the context.
	 * @pre pDiagram != null.
	 */
	public SerializationContext(Diagram pDiagram)
	{
		super(pDiagram);
		pDiagram.allNodes()
				.forEach(node -> aNodes.put(node, aNodes.size()));
	}
}
