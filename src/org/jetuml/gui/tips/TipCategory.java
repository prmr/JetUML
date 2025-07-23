/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.gui.tips;

import static org.jetuml.gui.tips.View.TOPIC;
import static org.jetuml.gui.tips.View.LEVEL;
import static org.jetuml.gui.tips.View.DIAGRAM;

/**
 *  An enum declared with a View parameter to define
 *  all tip categories and the View that the category belongs to.
 */
public enum TipCategory 
{
	CREATING(TOPIC), MODIFYING(TOPIC), SELECTING(TOPIC), COPYING(TOPIC), SEMANTICS(TOPIC), SETTINGS(TOPIC),
	BEGINNER(LEVEL), INTERMEDIATE(LEVEL), ADVANCED(LEVEL),
	CLASS(DIAGRAM), SEQUENCE(DIAGRAM), OBJECT(DIAGRAM), STATE(DIAGRAM), ALL(DIAGRAM);
	
	private final View aView;
	
	TipCategory(View pView)
	{
		aView = pView;
	}
	
	/**
	 * @return The View that the tip belongs to.
	 */
	public View getView()
	{
		return aView;
	}
}
