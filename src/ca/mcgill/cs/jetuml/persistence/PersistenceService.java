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
package ca.mcgill.cs.jetuml.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

import ca.mcgill.cs.jetuml.diagram.Diagram;

/**
 * Services for saving and loading Diagram objects (i.e., UML diagrams).
 */
public final class PersistenceService
{
	private PersistenceService() {}
	
	/**
     * Saves the current graph in a file. 
     * 
     * @param pGraph The graph to save
     * @param pFile The file to save
     * @throws IOException If there is a problem writing to pFile.
     * @pre pGraph != null.
     * @pre pFile != null.
     */
	public static void save(Diagram pGraph, File pFile) throws IOException
	{
		assert pGraph != null && pFile != null;
		try( PrintWriter out = new PrintWriter(new FileWriter(pFile)))
		{
			out.println(JsonEncoder.encode(pGraph).toString());
		}
	}
	
	/**
	 * Reads a graph from a file.
	 * 
	 * @param pFile The file to read the graph from.
	 * @return The graph that is read in
	 * @throws IOException if the graph cannot be read.
	 * @throws DeserializationException if there is a problem decoding the file.
	 * @pre pFile != null
	 */
	public static Diagram read(File pFile) throws IOException, DeserializationException
	{
		assert pFile != null;
		try( BufferedReader in = new BufferedReader(new FileReader(pFile)))
		{
			Diagram graph = JsonDecoder.decode(new JSONObject(in.readLine()));
			return graph;
		}
		catch( JSONException e )
		{
			throw new DeserializationException("Cannot decode the file", e);
		}
	}
}
