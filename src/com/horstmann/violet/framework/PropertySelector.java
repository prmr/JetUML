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

import java.beans.PropertyEditorSupport;

/**
 * A helper class for showing names of objects in a property
 * sheet that allows the user to pick one of a finite set of 
 * named values.
*/
public class PropertySelector extends PropertyEditorSupport
{
	private String[] aNames;
	private Object[] aValues;
	
	/**
     * Constructs a selector that correlates names and objects.
     * @param pTags the strings to display in a combo box
     * @param pValues the corresponding object values
	 */
	public PropertySelector(String[] pTags, Object[] pValues)
	{
		aNames = pTags;
		aValues = pValues;
	}

	@Override
	public String[] getTags()
	{
		return aNames;
	}

	@Override
	public String getAsText()
	{
		for(int i = 0; i < aValues.length; i++)
		{
			if(getValue().equals(aValues[i]))
			{
				return aNames[i];
			}
		}
		return null;
	}

	@Override
	public void setAsText(String pString)
	{
		for(int i = 0; i < aNames.length; i++)
		{
			if(pString.equals(aNames[i]))
			{
				setValue(aValues[i]);
			}
		}
	}

 
}
