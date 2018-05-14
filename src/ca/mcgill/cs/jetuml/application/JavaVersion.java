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
 * Represents a version of the Java programming language. The numbering scheme of any
 * version is converted to the Java 10 numbering scheme, so that, e.g., 1.8.0_171 
 * should be represented by an instance of this class as 8.0.171. Instances of this class
 * are immutable.
 * 
 * @see http://openjdk.java.net/jeps/223
 * 
 * For simplicity, trailing zeros are allowed in this representation.
 */
public class JavaVersion implements Comparable<JavaVersion>
{
	private static final int NEW_NUMBERING_SCHEME_BASELINE = 9;
	
	private final int aMajor;
	private final int aMinor;
	private final int aSecurity;
	
	/**
	 * Creates a new Java version by specifying all fields.
	 * 
	 * @param pMajorVersion The major version. Must be greater than zero. Should
	 * represent the JDK version, e.g., 1,2,3,4,5,6,7, etc.
	 * @param pMinorVersion The minor version, if applicable, or zero.
	 * @param pPatch The patch/update version, if applicable, or zero.
	 */
	public JavaVersion(int pMajorVersion, int pMinorVersion, int pPatch)
	{
		assert pMajorVersion > 0;
		assert pMinorVersion >= 0;
		assert pPatch >= 0;
		aMajor = pMajorVersion;
		aMinor = pMinorVersion;
		aSecurity = pPatch;
	}
	
	/**
	 * Creates a new Java Version by detecting it from the system.
	 * @throws JavaVersionNotDetectedException If the version string is not found on the 
	 * system, or there is an error parsing the version string.
	 */
	public JavaVersion() throws JavaVersionNotDetectedException
	{
		String versionString = obtainJavaVersionStringFromSystem();
		String[] tokens = versionString.split("\\.");
		if( tokens.length < 1 || tokens.length > 3 )
		{
			throw new JavaVersionNotDetectedException("Canot parse version string: " + versionString);
		}
		try
		{
			int major = Integer.parseInt(tokens[0]);
			int[] result = new int[] {0, 0, 0};
			if( major >= NEW_NUMBERING_SCHEME_BASELINE )
			{
				result = parseWithNewNumberingScheme(tokens);
			}
			else
			{
				if( major != 1 || tokens.length < 2 )
				{
					throw new JavaVersionNotDetectedException("Unrecognized version string: " + versionString);
				}
				result = parseWithOldNumberingScheme(tokens);
			}
			aMajor = result[0];
			aMinor = result[1];
			aSecurity = result[2];
		}
		catch( NumberFormatException exception)
		{
			throw new JavaVersionNotDetectedException("Canot parse version string: " + versionString);
		}
	}
	
	@Override
	public String toString()
	{
		return String.format("%d.%d.%d", aMajor, aMinor, aSecurity);
	}
	
	private static String obtainJavaVersionStringFromSystem()
	{
		String version = System.getProperty("java.version");
		if( version == null )
		{
			version = System.getProperty("java.runtime.version");
		}
		if( version == null )
		{
			throw new JavaVersionNotDetectedException("Version not found in system properties");
		}
		return version;
	}
	
	/**
	 * Parses a version string in the Java 9 scheme. 
	 * @param pTokens the period-separated tokens in the version string.
	 * @return A major.minor.security triple.
	 * @throws NumberFormatException is the integers do not parse.
	 */
	private int[] parseWithNewNumberingScheme(String[] pTokens)
	{
		assert pTokens.length >= 1 ;
		int[] result = new int[] {0, 0, 0};
		result[0] = Integer.parseInt(pTokens[0]);
		if( pTokens.length >= 2 )
		{
			result[1] = Integer.parseInt(pTokens[1]);
		}
		if( pTokens.length == 3 )
		{
			result[2] = Integer.parseInt(pTokens[2]);
		}
		
		return result;
	}
	
	/**
	 * Parses a version string in the pre Java 9 scheme. 
	 * @param pTokens the period-separated tokens in the version string.
	 * @return A major.minor.security triple.
	 * @throws NumberFormatException is the integers do not parse.
	 */
	private int[] parseWithOldNumberingScheme(String[] pTokens)
	{
		assert pTokens.length >= 2 ;
		int[] result = new int[] {0, 0, 0};
		
		int minor = Integer.parseInt(pTokens[1]);
		if( minor < 2 )
		{
			result[0] = 1;
			result[1] = minor;
		}
		else
		{
			result[0] = minor;
			result[1] = 0;
		}
		
		if( pTokens.length == 3 )
		{
			String[] tokens = pTokens[2].split("_");
			if( tokens.length == 1 )
			{
				if( result[0] == 1 )
				{
					result[2] = Integer.parseInt(tokens[0]);
				}
				else
				{
					result[1] = Integer.parseInt(tokens[0]);
					result[2] = 0;
				}
			}
			else if( tokens.length == 2 )
			{
				if( Integer.parseInt(tokens[0]) == 0 ) 
				{
					result[2] = Integer.parseInt(tokens[1]);
				}
				else
				{
					result[1] = Integer.parseInt(tokens[0]);
					result[2] = Integer.parseInt(tokens[1]);
				}
			}
		}
		
		return result;
	}

	@Override
	public int compareTo(JavaVersion pVersion)
	{
		int majorDiff = aMajor - pVersion.aMajor;
		if( majorDiff != 0 )
		{
			return majorDiff;
		}
		int minorDiff = aMinor - pVersion.aMinor;
		if( minorDiff != 0 )
		{
			return minorDiff;
		}
		return aSecurity - pVersion.aSecurity;
	}
}
