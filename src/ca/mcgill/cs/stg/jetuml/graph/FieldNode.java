/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 *  A field node in an object diagram.
 */
public class FieldNode extends RectangularNode
{
	public static final int DEFAULT_WIDTH = 60;
	public static final int DEFAULT_HEIGHT = 20;
	
	private double aAxisX;
	private MultiLineString aName;
	private MultiLineString aValue;
	private Rectangle2D aNameBounds;
	private Rectangle2D aValueBounds;
	private boolean aBoxedValue;
	private double aBoxWidth;

	private ObjectNode aObjectNode;	
	/**
	 * A default field node.
	 */
	public FieldNode()
	{
		aName = new MultiLineString();
		aName.setJustification(MultiLineString.RIGHT);
		aValue = new MultiLineString();
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
   }

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle2D b = getBounds();
		double leftWidth = aName.getBounds(pGraphics2D).getWidth();
		MultiLineString equal = new MultiLineString();
		equal.setText(" = ");
		double midWidth = equal.getBounds(pGraphics2D).getWidth();
      
		double rightWidth = aValue.getBounds(pGraphics2D).getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		rightWidth = Math.max(rightWidth, aBoxWidth - midWidth / 2);

		aNameBounds = new Rectangle2D.Double(b.getX(), b.getY(), leftWidth, b.getHeight());
		aName.draw(pGraphics2D, aNameBounds);
		Rectangle2D mid = new Rectangle2D.Double(b.getX() + leftWidth, b.getY(), midWidth, b.getHeight());
		equal.draw(pGraphics2D, mid);
		aValueBounds = new Rectangle2D.Double(b.getMaxX() - rightWidth, b.getY(), rightWidth, b.getHeight());
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
			pGraphics2D.draw(aValueBounds);
		}
	}

	@Override
	public boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		if (pEdge instanceof ObjectReferenceEdge && pEdge.getEnd() instanceof ObjectNode)
		{
			aValue.setText("");
			return true;
		}
		return false;
	}

	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		return pNode instanceof PointNode;
	}
	
	/**
     * Gets the ObjectNode of this Field Node.
     * @return aObjectNode the ObjectNode which holds this FieldNode.
	 */
	public ObjectNode getObjectNode()
	{
		return aObjectNode;
	}

	/**
     * Sets the Object Node that contains this Field Node.
     * @param pObjectNode the Object node that will contain this Field Node
	 */
	public void setObjectNode(ObjectNode pObjectNode)
	{
		aObjectNode = pObjectNode;
	}

	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		Rectangle2D b = getBounds();
		return new Point2D.Double((b.getMaxX() + b.getX() + aAxisX) / 2, b.getCenterY());
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		aNameBounds = aName.getBounds(pGraphics2D); 
		aValueBounds = aValue.getBounds(pGraphics2D);
		MultiLineString equal = new MultiLineString();
		equal.setText(" = ");
		Rectangle2D e = equal.getBounds(pGraphics2D);
		double leftWidth = aNameBounds.getWidth();
		double midWidth = e.getWidth();
		double rightWidth = aValueBounds.getWidth();
		if(rightWidth == 0)
		{
			rightWidth = DEFAULT_WIDTH / 2;
		}
		rightWidth = Math.max(rightWidth, aBoxWidth - midWidth / 2);
		double width = leftWidth + midWidth + rightWidth;
		double height = Math.max(aNameBounds.getHeight(), Math.max(aValueBounds.getHeight(), e.getHeight()));

		Rectangle2D b = getBounds();
		setBounds(new Rectangle2D.Double(b.getX(), b.getY(), width, height));
		aAxisX = leftWidth + midWidth / 2;
      
		aValueBounds.setFrame(b.getMaxX() - rightWidth, b.getY(), aValueBounds.getWidth(), aValueBounds.getHeight());
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
		cloned.aName = (MultiLineString)aName.clone();
		cloned.aValue = (MultiLineString)aValue.clone();
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
			return aValueBounds;
		}
		else
		{
			return null;
		}
	}
}

