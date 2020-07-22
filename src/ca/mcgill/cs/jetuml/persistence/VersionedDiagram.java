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
package ca.mcgill.cs.jetuml.persistence;

import ca.mcgill.cs.jetuml.application.Version;
import ca.mcgill.cs.jetuml.diagram.Diagram;

/**
 * Wrapper for a diagram object that also stores
 * the version of JetUML with which the diagram was
 * serialized, and whether it needed to be transformed
 * to be migrated to 3.0.
 */
public final class VersionedDiagram
{
	private final Diagram aDiagram;
	private final Version aOriginalVersion;
	private final boolean aMigrated;
	
	VersionedDiagram(Diagram pDiagram, Version pVersion, boolean pMigrated)
	{
		aDiagram = pDiagram;
		aOriginalVersion = pVersion;
		aMigrated = pMigrated;
	}
	
	/**
	 * @return The diagram wrapped by this object.
	 */
	public Diagram diagram()
	{
		return aDiagram;
	}
	
	/**
	 * @return The version of JetUML with which this diagram had
	 *     been serialized.
	 */
	public Version version()
	{
		return aOriginalVersion;
	}
	
	/**
	 * @return True if the diagram was transformed from its 
	 *     original encoding.
	 */
	public boolean wasMigrated()
	{
		return aMigrated;
	}
}
