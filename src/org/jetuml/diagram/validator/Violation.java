/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.diagram.validator;

import org.jetuml.application.ApplicationResources;

/**
 * Represents a problem that invalidates a diagram for rendering in JetUML.
 */
public final class Violation
{
	private static final String KEY_PREFIX = "error.";
	
	private enum Category
	{
		STRUCTURAL, SEMANTIC
	}
	
	private final Category aCategory;
	private final String aDescriptor;
	
	private Violation(Category pCategory, String pDescriptor) 
	{
		aCategory = pCategory;
		aDescriptor = pDescriptor;
	}
	
	/**
	 * @param pDescriptor A key to a property that describes the violation.
	 * @return A new structural Violation with descriptor pDescriptor 
	 */
	public static Violation newStructuralViolation(String pDescriptor)
	{
		assert pDescriptor != null;
		return new Violation(Category.STRUCTURAL, KEY_PREFIX + pDescriptor);
	}
	
	/**
	 * @param pDescriptor A key to a property that describes the violation.
	 * @return A constraint that was violated
	 */
	public static Violation newSemanticViolation(EdgeConstraint pConstraint)
	{
		assert pConstraint != null;
		return new Violation(Category.SEMANTIC, KEY_PREFIX + pConstraint.getClass().getSimpleName());
	}
	
	/**
	 * @return True if this is a structural violation.
	 */
	public boolean isStructural()
	{
		return aCategory == Category.STRUCTURAL;
	}
	
	/**
	 * @return True if this is a structural violation.
	 */
	public boolean isSemantic()
	{
		return aCategory == Category.SEMANTIC;
	}
	
	/**
	 * @return The externalized description of this violation.
	 */
	public String description()
	{
		return ApplicationResources.RESOURCES.getString(aDescriptor);
	}
	
	@Override
	public String toString()
	{
		return String.format("[%s Violation: %s", aCategory.name().toLowerCase(), description());
	}
}
