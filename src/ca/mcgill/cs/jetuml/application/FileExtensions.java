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
package ca.mcgill.cs.jetuml.application;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import ca.mcgill.cs.jetuml.diagram.DiagramType;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * A utility class to create and access diagram extension 
 * filters used by the file chooser.
 */
public final class FileExtensions
{
	private static final String EXTENSION_JET = ".jet";
	
	private static final ExtensionFilter FILTER_APPLICATION = 
			new ExtensionFilter(RESOURCES.getString("application.file.name"), "*" + EXTENSION_JET);
	private static final ExtensionFilter FILTER_ALL = 
			new ExtensionFilter(RESOURCES.getString("application.file.all"), "*.*");
	
	private static Map<DiagramType, ExtensionFilter> aExtensionFilters = createFilters();
	
	private FileExtensions() {}
	
	private static Map<DiagramType, ExtensionFilter> createFilters()
	{
		Map<DiagramType, ExtensionFilter> map = new EnumMap<>(DiagramType.class);
		for( DiagramType diagramType : DiagramType.values() )
		{
			map.put(diagramType,  new ExtensionFilter(diagramType.getFileNameDescription(), 
					"*" + diagramType.getFileExtension() + EXTENSION_JET));
		}
		return map;
	}
	
	/**
	 * @return An unmodifiable list of all filters applicable to the application. This list
	 *     includes one filter for each diagram type, the general filter for all file 
	 *     types, and the "application" filter for all diagram file types. Never null.
	 */
	public static List<ExtensionFilter> all()
	{
		List<ExtensionFilter> result = aExtensionFilters.entrySet().stream()
				.sorted(comparingByKey())
				.map(Map.Entry::getValue)
				.collect(toList());
		result.add(0, FILTER_APPLICATION);
		result.add(FILTER_ALL);
		return Collections.unmodifiableList(result);
	}
	
	/**
	 * @param pDiagramType The diagram type to query.
	 * @return The extension filter for pDiagram type. This object is the same
	 *     object than the one contained in the return value of method all(), they 
	 *     can be compared by identity.
	 * @pre pDiagramType != null
	 */
	public static ExtensionFilter forDiagramType(DiagramType pDiagramType)
	{
		assert pDiagramType != null;
		return aExtensionFilters.get(pDiagramType);
	}
	
	/**
	 * @param pFile The file to clip, if applicable.
	 * @return A file with the same name as pFile, but with
	 *     the application extension removed. If there is no application
	 *     extension to clip, the same file object is returned.
	 * @pre pFile != null
	 */
	public static File clipApplicationExtension(File pFile)
	{
		assert pFile != null;
		if( !pFile.getAbsolutePath().endsWith(EXTENSION_JET))
		{
			return pFile;
		}
		return new File(pFile.getAbsolutePath()
				.substring(0, pFile.getAbsolutePath().length() - EXTENSION_JET.length()));
	}
}
	
	