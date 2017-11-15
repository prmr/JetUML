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

package ca.mcgill.cs.stg.jetuml.graph.nodes;

import java.awt.Graphics2D;
import java.awt.Shape;

import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Direction;
import ca.mcgill.cs.stg.jetuml.geom.Point;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 *  A field node in an object diagram.
 */
public class FieldNode extends RectangularNode implements ChildNode
{
	public static final int DEFAULT_WIDTH = 60;
	public static final int DEFAULT_HEIGHT = 20;
	
	private double aAxisX;
	private MultiLineString aName;
	private MultiLineString aValue;
	private Rectangle aNameBounds;
	private Rectangle aValueBounds;
	private boolean aBoxedValue;
	private double aBoxWidth;
	private ObjectNode aObject; // The object defining this field

	/**
	 * A default field node.
	 */
	public FieldNode()
	{
		aName = new MultiLineString();
		aName.setJustification(MultiLineString.RIGHT);
		aValue = new MultiLineString();
		setBounds(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
   }

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle b = getBounds();
		int leftWidth = aName.getBounds().getWidth();
		MultiLineString equal = new MultiLineString();
		equal.setText(" = ");
		int midWidth = equal.getBounds().getWidth();
      
		int rightWidth = aValue.getBounds().getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		rightWidth = (int) Math.max(rightWidth, aBoxWidth - midWidth / 2.0);

		aNameBounds = new Rectangle(b.getX(), b.getY(), leftWidth, b.getHeight());
		aName.draw(pGraphics2D, aNameBounds);
		Rectangle mid = new Rectangle(b.getX() + leftWidth, b.getY(), midWidth, b.getHeight());
		equal.draw(pGraphics2D, mid);
		aValueBounds = new Rectangle(b.getMaxX() - rightWidth, b.getY(), rightWidth, b.getHeight());
		if(aBoxedValue)
		{
			aValue.setJustification(MultiLineString.CENTER);
		}
		else
		{
			aName.setJustification(MultiLineString.LEFT);
		}
		aValue.draw(pGraphics2D, aValueBounds);
		if(aBoxedValue)
		{
			pGraphics2D.draw(Conversions.toRectangle2D(aValueBounds));
		}
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Rectangle b = getBounds();
		return new Point((b.getMaxX() + b.getX() + aAxisX) / 2, b.getCenter().getY());
	}

	@Override
	public void layout(Graph pGraph)
	{
		aNameBounds = aName.getBounds(); 
		aValueBounds = aValue.getBounds();
		MultiLineString equal = new MultiLineString();
		equal.setText(" = ");
		Rectangle e = equal.getBounds();
		int leftWidth = aNameBounds.getWidth();
		int midWidth = e.getWidth();
		int rightWidth = aValueBounds.getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		rightWidth = (int) Math.max(rightWidth, aBoxWidth - midWidth / 2.0);
		double width = leftWidth + midWidth + rightWidth;
		double height = Math.max(aNameBounds.getHeight(), Math.max(aValueBounds.getHeight(), e.getHeight()));

		Rectangle bounds = getBounds();
		setBounds(new Rectangle(bounds.getX(), bounds.getY(), (int)width, (int)height));
		aAxisX = leftWidth + midWidth / 2;
		aValueBounds = new Rectangle(bounds.getMaxX() - rightWidth, bounds.getY(), aValueBounds.getWidth(), aValueBounds.getHeight());
	}

	/**
     * Sets the name property value.
     * @param pName the field name
	 */
	public void setName(MultiLineString pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the field name
	 */
	public MultiLineString getName()
	{
		return aName;
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

	/**
     * Sets the box width.
     * @param pBoxWidth the new box width
	 */
	public void setBoxWidth(double pBoxWidth)
	{
		aBoxWidth = pBoxWidth;
	}
   
	/**
     * Sets the boxedValue property value.
     * @param pNewValue the new property value
	 */
	public void setBoxedValue(boolean pNewValue)
	{
		aBoxedValue = pNewValue;
	}

	/**
     * Gets the boxedValue property value.
     * @return the property value
	 */
	public boolean isBoxedValue()
	{
		return aBoxedValue;
	}

	@Override
	public FieldNode clone()
	{
		FieldNode cloned = (FieldNode)super.clone();
		cloned.aName = aName.clone();
		cloned.aValue = aValue.clone();
		return cloned;
	}

	/**
     * Gets the x-offset of the axis (the location
     * of the = sign) from the left corner of the bounding rectangle.
     * @return the x-offset of the axis
	 */
	public double getAxisX()
	{
		return aAxisX;
	}
   
	@Override
	public Shape getShape()
	{
		if(aBoxedValue)
		{
			return Conversions.toRectangle2D(aValueBounds);
		}
		else
		{
			return null;
		}
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
}

