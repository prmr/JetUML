/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.graph.nodes;

import ca.mcgill.cs.jetuml.framework.MultiLineString;
import ca.mcgill.cs.jetuml.views.nodes.ClassNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 * A class node in a class diagram.
 */
public class ClassNode extends InterfaceNode
{
	private MultiLineString aAttributes;

	/**
     * Construct a class node with a default size.
	 */
	public ClassNode()
	{
		aAttributes = new MultiLineString();
		aAttributes.setJustification(MultiLineString.Align.LEFT);
		getName().setText("");
	}
	
	@Override
	protected NodeView generateView()
	{
		return new ClassNodeView(this);
	}

	/**
     * Sets the attributes property value.
     * @param pNewValue the attributes of this class
	 */
	public void setAttributes(MultiLineString pNewValue)
	{
		aAttributes = pNewValue;
	}

	/**
     * Gets the attributes property value.
     * @return the attributes of this class
	 */
	public MultiLineString getAttributes()
	{
		return aAttributes;
	}

	@Override
	public ClassNode clone()
	{
		ClassNode cloned = (ClassNode)super.clone();
		cloned.aAttributes = aAttributes.clone();
		return cloned;
	}
}
