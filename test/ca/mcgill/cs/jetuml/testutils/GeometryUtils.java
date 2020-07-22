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
package ca.mcgill.cs.jetuml.testutils;

/**
 * Support for handling geometric values in tests.
 */
public final class GeometryUtils
{
	private enum OS { WINDOWS, MAC, LINUX, UNKNOWN }
	
	private static final OS detectedOS = detectOS();
	
	private GeometryUtils() {}
	
	private static final OS detectOS()
	{	
		OS result = OS.UNKNOWN;
		try
		{
			String os = System.getProperty("os.name").toLowerCase();
			if( os.contains("windows"))
			{
				result = OS.WINDOWS;
			}
			else if( os.contains("linux"))
			{
				result = OS.LINUX;
			}
			else if( os.contains("mac"))
			{
				result = OS.MAC;
			}
		}
		catch( SecurityException e )
		{} // Default to unknown
		
		return result;	
	}
	
	/** 
	 * Returns the value that corresponds to the current operating system.
	 * If the os is unknowns, the windows value is used.
	 * 
	 * @param pWindows The value for windows.
	 * @param pMac The value for Mac.
	 * @param pLinux The value for linux.
	 * @return The value that corresponds to the detected operating system.
	 */
	public static int osDependent(int pWindows, int pMac, int pLinux)
	{
		if( detectedOS == OS.MAC )
		{
			return pMac;
		}
		else if( detectedOS == OS.LINUX )
		{
			return pLinux;
		}
		else
		{
			return pWindows;
		}
	}
}
