/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package org.jetuml.application;

import java.util.StringJoiner;

/**
 * Represents a version of JetUML. 
 * @param major The major version number.
 * @param minor The minor version number.
 * @param patch The patch number.
 */
public record Version(int major, int minor, int patch)
{	
	@Override
	public String toString()
	{
		StringJoiner versionString = new StringJoiner(".");
		versionString.add(Integer.toString(major));
		versionString.add(Integer.toString(minor));
		if( patch > 0 )
		{
			versionString.add(Integer.toString(patch));
		}
		return versionString.toString();
	}
	
	/**
	 * Parses a string representing a version.
	 * 
	 * @param pVersionString The string to parse.
	 * @return A Version object
	 * @throws IllegalArgumentException if the string cannot be parsed into a valid version
	 * @pre pVersionString != null
	 */
	public static Version parse(String pVersionString)
	{
		String[] tokens = pVersionString.split("\\.");
		if( tokens.length < 2)
		{
			throw new IllegalArgumentException("Invalid version string: " + pVersionString);
		}
		int major = 0;
		int minor = 0;
		int patch = 0;
		try
		{
			major = Integer.parseInt(tokens[0]);
			minor = Integer.parseInt(tokens[1]);
			if( tokens.length == 3 )
			{
				patch = Integer.parseInt(tokens[2]);
			}
			else if( tokens.length > 3 )
			{
				throw new IllegalArgumentException("Invalid version string: " + pVersionString);
			}
		}
		catch( NumberFormatException e )
		{
			throw new IllegalArgumentException("Invalid version string: " + pVersionString);
		}
		return new Version(major, minor, patch);
	}
	
	/**
	 * This method is functionally equivalent to the canonical constructor and 
	 * is included to provide a complete and coherent API for creating versions.
	 * Using this method is preferred over the constructor because it performs
	 * an assertion check on the input.
	 * 
	 * @param pMajor The major component.
	 * @param pMinor The minor component.
	 * @param pPatch The patch component.
	 * @return The object that corresponds to the specified version.
	 * @pre pMajor >= 0 && pMinor >= 0 && pPatch >=0
	 */
	public static Version create(int pMajor, int pMinor, int pPatch)
	{
		assert pMajor >= 0 && pMinor >= 0 && pPatch >=0;
		return new Version(pMajor, pMinor, pPatch);
	}
	
	/**
	 * A convenience method to create a version without a patch number.
	 * 
	 * @param pMajor The major component.
	 * @param pMinor The minor component.
	 * @return The object that corresponds to the specified version.
	 * @pre pMajor >= 0 && pMinor >= 0
	 */
	public static Version create(int pMajor, int pMinor)
	{
		return create(pMajor, pMinor, 0);
	}
}
