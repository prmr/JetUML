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

package ca.mcgill.cs.jetuml.diagram.nodes;

import ca.mcgill.cs.jetuml.views.nodes.FieldNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 *  A field node in an object diagram.
 */
public final class FieldNode extends NamedNode implements ChildNode
{
	private String aValue = "";
	private ObjectNode aObject; // The object defining this field

	@Override
	protected NodeView generateView()
	{
		return new FieldNodeView(this);
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
	public ParentNode getParent()
	{
		return aObject;
	}

	@Override
	public void setParent(ParentNode pNode)
	{
		assert pNode == null || pNode instanceof ObjectNode;
		aObject = (ObjectNode) pNode;		
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
}

