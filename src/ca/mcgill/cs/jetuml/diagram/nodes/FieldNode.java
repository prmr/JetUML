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

package ca.mcgill.cs.jetuml.diagram.nodes;

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;

/**
 *  A field node in an object diagram.
 */
public final class FieldNode extends NamedNode
{
	private String aValue = "value";
	private Optional<ObjectNode> aObject = Optional.empty(); // The object defining this field
	
	/**
	 * Creates a new field node.
	 */
	public FieldNode()
	{
		setName("name");
	}

	/**
     * Sets the value property value.
     * @param pNewValue the field value
	 */
	public void setValue(String pNewValue)
	{
		aValue = pNewValue;
	}

	/**
     * Gets the value property value.
     * @return the field value
	 */
	public String getValue()
	{
		return aValue;
	}

	@Override
	public Node getParent()
	{
		assert hasParent();
		return aObject.get();
	}

	@Override
	public void link(Node pNode)
	{
		assert pNode != null && pNode instanceof ObjectNode;
		aObject = Optional.of((ObjectNode) pNode);		
	}
	
	@Override
	public void unlink()
	{
		assert hasParent();
		aObject = Optional.empty();
	}
	
	@Override
	public boolean requiresParent()
	{
		return true;
	}
	
	@Override
	protected void buildProperties()
	{
		super.buildProperties();
		properties().add("value", () -> aValue, pValue -> aValue = (String) pValue);
	}

	@Override
	public boolean hasParent()
	{
		return aObject.isPresent();
	}
}

