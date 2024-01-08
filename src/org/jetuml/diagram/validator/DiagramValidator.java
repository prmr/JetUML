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

import java.util.Optional;

/**
 * A type that allows to check the Diagram's semantic validity.
 */
public interface DiagramValidator
{
	/**
	 * Convenience method to check if the validation finds no violation.
	 * 
	 * @return True if the content of the diagram does not violate
	 * any structural or semantic rules. 
	 */
	default boolean isValid()
	{
		return validate().isEmpty();
	}
	
	/**
	 * Checks if any rule violates the correctness of this diagram. Returns
	 * the first violation detected, or empty if none are detected.
	 * @return Optional.empty if the diagram is correct, or a Violation that 
	 * describes the problem if not.
	 */
	Optional<Violation> validate();
}
