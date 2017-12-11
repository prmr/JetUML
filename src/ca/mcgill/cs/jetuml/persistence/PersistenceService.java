/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.persistence;

import java.io.OutputStream;

import ca.mcgill.cs.jetuml.graph.Graph;

/**
 * Services for saving and loading Graph objects (i.e., UML diagrams).
 * 
 * @author Martin P. Robillard
 */
public final class PersistenceService
{
	private PersistenceService() {}
	
	/**
     * Saves the current graph in a file. 
     * 
     * @param pGraph The graph to save
     * @param pOut the stream for saving
     */
	public static void saveFile(Graph pGraph, OutputStream pOut)
	{

	}
}
