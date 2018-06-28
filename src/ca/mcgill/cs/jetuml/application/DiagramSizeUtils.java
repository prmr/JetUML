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
package ca.mcgill.cs.jetuml.application;

/**
 * Utility methods for managing the size of a 
 * diagram view.
 */
public final class DiagramSizeUtils
{
	public static final int MAX_SIZE = 5000;
	public static final int MIN_SIZE = 250;
	
	private DiagramSizeUtils()
	{}
	
	/**
	 * @param pValue A value to test
	 * @return True if pValue is a valid diagram dimension
	 */
	public static boolean isValid(int pValue)
	{
		return pValue >= MIN_SIZE && pValue <= MAX_SIZE;
	}
	
	/**
	 * @param pText The value to test
	 * @return True if pText can be converted to an integer
	 * that is a valid diagram dimension.
	 */
	public static boolean isValid(String pText)
	{
		try
		{
			return isValid(Integer.parseInt(pText));
		}
		catch( NumberFormatException pException )
		{
			return false;
		}
	}
}
