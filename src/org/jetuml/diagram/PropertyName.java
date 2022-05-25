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
package org.jetuml.diagram;

import static org.jetuml.application.ApplicationResources.RESOURCES;

/**
 * List of all the possible names of Property objects.
 */
public enum PropertyName
{
	// List in alphabetical order.
	AGGREGATION_TYPE("Aggregation Type"),
	ATTRIBUTES("attributes"),
	CONTENTS("contents"),
	DIRECTIONALITY("directionality"),
	END_LABEL("endLabel"),
	GENERALIZATION_TYPE("Generalization Type"),
	METHODS("methods"),
	MIDDLE_LABEL("middleLabel"),
	NAME("name"),
	OPEN_BOTTOM("openBottom"),
	SIGNAL("signal"),
	START_LABEL("startLabel"),
	USE_CASE_DEPENDENCY_TYPE("Dependency Type"),
	VALUE("value");
	
	/* External representation of the property when externalizing it to diagram files.
	 * In principle it should be possible to derive this directly from enum name, but 
	 * this was not done consistently at first and changing the external representation
	 * now would break backward compatibility. We could implement this change with the 
	 * next major version, with a corresponding version migrator. TODO
	 */
	private final String aExternal;
	
	PropertyName(String pExternal)
	{
		assert pExternal != null;
		aExternal = pExternal;
	}
	
	/**
	 * @return The name of this property for purpose of externalizing it in a diagram file.
	 */
	public String external()
	{
		return aExternal;
	}
	
	/**
	 * @return The name of the property as users should see it, that is, 
	 *     obtained from the application resources.
	 */
	public String visible()
	{
		return RESOURCES.getString("property." + name().toLowerCase());
	}
}
