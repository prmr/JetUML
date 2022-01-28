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

package ca.mcgill.cs.jetuml.diagram.nodes;

import ca.mcgill.cs.jetuml.diagram.PropertyName;

/**
 * A class node in a class diagram.
 */
public final class ClassNode extends TypeNode
{
	private String aAttributes = "";
	
	/**
	 * Constructs a new ClassNode with an empty name and no
	 * attributes or methods.
	 */
	public ClassNode()
	{
		setName("");
	}

	/**
     * Sets the attributes property value.
     * @param pNewValue the attributes of this class
     * @pre pNewValue != null
	 */
	public void setAttributes(String pNewValue)
	{
		assert pNewValue != null;
		aAttributes = pNewValue;
	}

	/**
     * Gets the attributes property value.
     * @return the attributes of this class
	 */
	@Override
	public String getAttributes()
	{
		return aAttributes;
	}

	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().addAt(PropertyName.ATTRIBUTES, () -> aAttributes, pAttributes -> aAttributes = (String)pAttributes, 1);
	}
}
