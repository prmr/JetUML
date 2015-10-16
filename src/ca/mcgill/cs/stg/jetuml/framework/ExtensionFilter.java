/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
   A file filter that accepts all files with a given set
   of extensions.
*/
public class ExtensionFilter extends FileFilter
{
	private String aDescription;
	private String aExtension;
	
	/**
     * Constructs an extension file filter.
     * @param pDescription the description (e.g. "Woozle files")
     * @param pExtension the single accepted extension that corresponds
     * to this type of file.
     * @pre pDescription != null
     * @pre pExtensions != null
     * @pre pExtensions != ""
	 */
   	public ExtensionFilter(String pDescription, String pExtension)
   	{
   		assert pDescription != null;
		assert pExtension != null;
		assert pExtension.length() > 0;
	   	aDescription = pDescription; 
	   	aExtension = pExtension;
   	}
   
   	@Override
   	public boolean accept(File pFile)
   	{  
   		if( pFile.isDirectory() )
   		{
   			return true;
   		}
      
   		String fileName = pFile.getName().toLowerCase();
   		if(fileName.endsWith(aExtension.toLowerCase()))
		{
			return true;
		}
   		return false;
   }
   
   	@Override
   	public String getDescription()
   	{ 
   		return aDescription; 
   	}
   
   	/**
   	 * @return The extension for this filter.
   	 */
   	public String getExtension()
   	{
   		return aExtension;
   	}
}
