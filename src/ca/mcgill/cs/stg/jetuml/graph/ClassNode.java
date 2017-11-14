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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Graphics2D;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

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
		aAttributes.setJustification(MultiLineString.LEFT);
		aName.setText("");
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D); 
		int midHeight = computeMiddle().getHeight();
		double bottomHeight = computeBottom().getHeight();
		Rectangle top = new Rectangle(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), (int) Math.round(getBounds().getHeight() - midHeight - bottomHeight));
		Rectangle mid = new Rectangle(top.getX(), top.getMaxY(), top.getWidth(), (int) Math.round(midHeight));
		aAttributes.draw(pGraphics2D, mid);
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
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	@Override
	protected boolean needsMiddleCompartment()
	{
		return !aAttributes.getText().isEmpty();
	}
	
	/**
	 * @return The area of the middle compartment. The x and y values
	 * are meaningless.
	 */
	@Override
	protected Rectangle computeMiddle()
	{
		if( !needsMiddleCompartment() )
		{
			return new Rectangle(0, 0, 0, 0);
		}
			
		Rectangle attributes = aAttributes.getBounds();
		attributes = attributes.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT));
		return attributes;
	}

	@Override
	public ClassNode clone()
	{
		ClassNode cloned = (ClassNode)super.clone();
		cloned.aAttributes = aAttributes.clone();
		return cloned;
	}
}
