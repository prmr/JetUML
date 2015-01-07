/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.horstmann.violet.framework;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.filechooser.FileFilter;

/**
   A file filter that accepts all files with a given set
   of extensions.
*/
public class ExtensionFilter extends FileFilter
{
	private String aDescription;
	private String[] aExtensions;
	
	/**
	 *  Constructs an extension file filter.
	 *  @param pDescription the description (e.g. "Woozle files")
	 *  @param pExtensions the accepted extensions (e.g.
	 *   new String[] { ".woozle", ".wzl" })
	 *   @pre pDescription != null
	 *   @pre pExtensions != null && pExtensions.length > 0
	 *   @pre None of the elements in pExtensions are null.
	 */
	public ExtensionFilter(String pDescription, String[] pExtensions)
	{
		assert pDescription != null;
		assert pExtensions != null;
		assert pExtensions.length > 0;
		for( String extension : pExtensions )
		{
			assert extension != null;
		}
		aDescription = pDescription; 
		aExtensions = pExtensions;
	}

	/**
     * Constructs an extension file filter.
     * @param pDescription the description (e.g. "Woozle files")
     * @param pExtensions the accepted extensions, separated
     * by | (e.g.".woozle|.wzl" })
     * @pre pDescription != null
     * @pre pExtensions != null
	 */
   	public ExtensionFilter(String pDescription, String pExtensions)
   	{
   		assert pDescription != null;
		assert pExtensions != null;
	   	aDescription = pDescription; 
	   	StringTokenizer tokenizer = new StringTokenizer(pExtensions, "|");
	   	aExtensions = new String[tokenizer.countTokens()];
	   	for(int i = 0; i < this.aExtensions.length; i++)
	   	{
		   aExtensions[i] = tokenizer.nextToken();
	   	}
   	}
   
   	@Override
   	public boolean accept(File pFile)
   	{  
   		if( pFile.isDirectory() )
   		{
   			return true;
   		}
      
   		String fileName = pFile.getName().toLowerCase();
   		for(int i = 0; i < aExtensions.length; i++)
		{
			if(fileName.endsWith(aExtensions[i].toLowerCase()))
			{
				return true;
			}
		}
   		return false;
   }
   
   	@Override
   	public String getDescription()
   	{ 
   		return aDescription; 
   	}
   
   	/**
   	 * @return The extensions for this filter.
   	 */
   	public String[] getExtensions()
   	{
   		return aExtensions;
   	}
}
