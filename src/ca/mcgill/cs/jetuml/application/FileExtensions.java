/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.LinkedList;
import java.util.List;

import javafx.stage.FileChooser.ExtensionFilter;


/**
 * A utility class to create and access diagram extension 
 * filters used by the file chooser.
 */
public final class FileExtensions
{
	private static List<ExtensionFilter> aFileFilters = new LinkedList<>();
	
	private FileExtensions() {}
	
	/**
	 * Constructs all file extension filters related to the diagrams.
	 */
	static 
	{
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("application.file.name"), 
				"*" + RESOURCES.getString("application.file.extension")));
		
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("classdiagram.file.name"), 
				"*" + RESOURCES.getString("classdiagram.file.extension") + RESOURCES.getString("application.file.extension")));
		
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("sequencediagram.file.name"), 
				"*" + RESOURCES.getString("sequencediagram.file.extension") + RESOURCES.getString("application.file.extension")));
		
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("statediagram.file.name"), 
				"*" + RESOURCES.getString("statediagram.file.extension") + RESOURCES.getString("application.file.extension")));
		
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("objectdiagram.file.name"), 
				"*" + RESOURCES.getString("objectdiagram.file.extension") + RESOURCES.getString("application.file.extension")));
		
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("usecasediagram.file.name"), 
				"*" + RESOURCES.getString("usecasediagram.file.extension") + RESOURCES.getString("application.file.extension")));
		
		aFileFilters.add(new ExtensionFilter(RESOURCES.getString("application.file.all"), "*.*"));
	}
	
	/**
	 * @return list of all diagram extension filters
	 */
	public static List<ExtensionFilter> getAll()
	{
		return aFileFilters;
	}
	
	/**
	 * @param pDescription description of the filter
	 * @return the corresponding diagram extension filter
	 */
	public static ExtensionFilter get(String pDescription) 
	{
		for(ExtensionFilter filter: aFileFilters) 
		{
			if(filter.getDescription().equals(pDescription))
			{
				return filter;
			}
		}
		return null;
	}
}
	
	