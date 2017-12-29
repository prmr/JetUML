/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

import ca.mcgill.cs.jetuml.application.MultiLineString;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Properties;
import ca.mcgill.cs.jetuml.graph.ValueExtractor;
import ca.mcgill.cs.jetuml.graph.ValueExtractor.Type;
import ca.mcgill.cs.jetuml.views.nodes.FieldNodeView;
import ca.mcgill.cs.jetuml.views.nodes.NodeView;

/**
 *  A field node in an object diagram.
 */
public class FieldNode extends NamedNode implements ChildNode
{
	private MultiLineString aValue;
	private ObjectNode aObject; // The object defining this field

	/**
	 * A default field node.
	 */
	public FieldNode()
	{
		getName().setJustification(MultiLineString.Align.RIGHT);
		aValue = new MultiLineString();
   }
	
	@Override
	protected NodeView generateView()
	{
		return new FieldNodeView(this);
	}

	/**
     * Sets the value property value.
     * @param pNewValue the field value
	 */
	public void setValue(MultiLineString pNewValue)
	{
		aValue = pNewValue;
	}

	/**
     * Gets the value property value.
     * @return the field value
	 */
	public MultiLineString getValue()
	{
		return aValue;
	}

	@Override
	public FieldNode clone()
	{
		FieldNode cloned = (FieldNode) super.clone();
		cloned.aValue = aValue.clone();
		return cloned;
	}

	/**
     * Gets the x-offset of the axis (the location
     * of the = sign) from the left corner of the bounding rectangle.
     * @return the x-offset of the axis
	 */
	public int obtainAxis()
	{
		return ((FieldNodeView)view()).getAxis();
	}
	
	/**
	 * @param pBounds The new bounds
	 */
	public void setBounds(Rectangle pBounds)
	{
		((FieldNodeView)view()).setBounds(pBounds);
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
	public Properties properties()
	{
		Properties properties = super.properties();
		properties.put("value", aValue.getText());
		return properties;
	}
	
	@Override
	public void initialize(ValueExtractor pExtractor)
	{
		super.initialize(pExtractor);
		aValue.setText((String)pExtractor.get("value", Type.STRING));
	}
}

