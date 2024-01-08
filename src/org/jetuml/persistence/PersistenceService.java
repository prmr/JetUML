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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.validator.DiagramValidator;
import org.jetuml.diagram.validator.Violation;
import org.jetuml.persistence.DeserializationException.Category;
import org.jetuml.persistence.json.JsonException;
import org.jetuml.persistence.json.JsonParser;

/**
 * Services for saving and loading Diagram objects. The files are encoded
 * in UTF-8.
 */
public final class PersistenceService
{
	private PersistenceService() {}
	
	/**
     * Saves the current diagram in a file. 
     * 
     * @param pDiagram The diagram to save
     * @param pFile The file in which to save the diagram
     * @throws IOException If there is a problem writing to pFile.
     * @pre pDiagram != null.
     * @pre pFile != null.
     */
	public static void save(Diagram pDiagram, File pFile) throws IOException
	{
		assert pDiagram != null && pFile != null;
		try( PrintWriter out = new PrintWriter(
				new OutputStreamWriter(new FileOutputStream(pFile), StandardCharsets.UTF_8)))
		{
			out.println(JsonEncoder.encode(pDiagram).toString());
		}
	}
	
	/**
	 * Reads a diagram from a file.
	 * 
	 * @param pFile The file to read the diagram from.
	 * @return The diagram that is read in
	 * @throws IOException if the diagram cannot be read.
	 * @throws DeserializationException if there is a problem decoding the file.
	 * @pre pFile != null
	 */
	public static Diagram read(File pFile) throws IOException, DeserializationException
	{
		assert pFile != null;
		try
		{
			String inputLine = Files.readString(pFile.toPath(), StandardCharsets.UTF_8);
			Diagram diagram = new JsonDecoder(JsonParser.parse(inputLine)).decode();
			DiagramValidator validator = DiagramType.newValidatorInstanceFor(diagram);
			Optional<Violation> violation = validator.validate();
			if( violation.isPresent() )
			{
				if(violation.get().isStructural())
				{
					throw new DeserializationException(Category.STRUCTURAL, "Diagram has invalid structure");
				}
				else
				{
					throw new DeserializationException(Category.SEMANTIC, "Diagram has invalid semantics");
				}
			}
			return diagram;
		}
		catch(JsonException exception)
		{
			throw new DeserializationException(Category.SYNTACTIC, exception.getMessage());
		}
	}
}
