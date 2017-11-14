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
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 *  An object node in an object diagram.
 */
public class ObjectNode extends RectangularNode implements ParentNode
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int XGAP = 5;
	private static final int YGAP = 5;

	private double aTopHeight;
	private MultiLineString aName;
	private ArrayList<ChildNode> aFields;

	/**
	 * Construct an object node with a default size.
	 */
	public ObjectNode()
	{
		aName = new MultiLineString(true);
		aName.setUnderlined(true);
		setBounds(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		aFields = new ArrayList<>();
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle2D top = getTopRectangle();
		pGraphics2D.draw(top);
		pGraphics2D.draw(Conversions.toRectangle2D(getBounds()));
		aName.draw(pGraphics2D, Conversions.toRectangle(top));
	}

	/* 
	 * Object Nodes are now responsible for translating their Field Node children.
	 */
	@Override
	public void translate(int pDeltaX, int pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		for (Node childNode : getChildren())
		{
			childNode.translate(pDeltaX, pDeltaY);
		}   
	}    

	/**
	 * Returns the rectangle at the top of the object node.
	 * @return the top rectangle
	 */
	public Rectangle2D getTopRectangle()
	{
		return new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), getBounds().getWidth(), aTopHeight);
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D)
	{
		Rectangle b = aName.getBounds(); 
		b = b.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - YGAP));
		double leftWidth = 0;
		double rightWidth = 0;
		List<ChildNode> fields = getChildren();
		int height = 0;
		if( fields.size() != 0 )
		{
			height = YGAP;
		}
		for(int i = 0; i < fields.size(); i++)
		{
			FieldNode f = (FieldNode)fields.get(i);
			f.layout(pGraph, pGraphics2D);
			Rectangle b2 = f.getBounds();
			height += b2.getHeight() + YGAP;   
			double axis = f.getAxisX();
			leftWidth = Math.max(leftWidth, axis);
			rightWidth = Math.max(rightWidth, b2.getWidth() - axis);
		}
		int width = (int) (2 * Math.max(leftWidth, rightWidth) + 2 * XGAP);
		width = Math.max(width, b.getWidth());
		width = Math.max(width, DEFAULT_WIDTH);
		b = new Rectangle(getBounds().getX(), getBounds().getY(), width, b.getHeight() + height);
		Rectangle snappedBounds = Grid.snapped(b);
		setBounds(snappedBounds);
		b = snappedBounds;
		aTopHeight = b.getHeight() - height;
		int ytop = (int)(b.getY() + aTopHeight + YGAP);
		int xmid = b.getCenter().getX();
		for(int i = 0; i < fields.size(); i++)
		{
			FieldNode f = (FieldNode)fields.get(i);
			Rectangle b2 = f.getBounds();
			f.setBounds(new Rectangle((int)(xmid - f.getAxisX()), (int)ytop, (int)(f.getAxisX() + rightWidth), b2.getHeight()));
			f.setBoxWidth(rightWidth);
			ytop += f.getBounds().getHeight() + YGAP;
		}
	}

	/**
	 * Sets the name property value.
	 * @param pName the new object name
	 */
	public void setName(MultiLineString pName)
	{
		aName = pName;
	}

	/**
	 * Gets the name property value.
	 * @return the object name
	 */
	public MultiLineString getName()
	{
		return aName;
	}

	@Override
	public ObjectNode clone()
	{
		ObjectNode cloned = (ObjectNode)super.clone();
		cloned.aName = aName.clone();
		cloned.aFields = new ArrayList<>();
		
		for( ChildNode child : aFields )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.setParent(cloned);
			cloned.aFields.add(clonedChild);
		}
		
		return cloned;
	}

	@Override
	public void addChild(ChildNode pNode)
	{
		addChild(aFields.size(), pNode);
	}
	
	@Override
	public void addChild(int pIndex, ChildNode pNode)
	{
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aFields.add(pIndex, pNode);
		pNode.setParent(this);
		// prmr unclear why we need this
//		Rectangle2D b = getBounds();
//		b.add(new Rectangle2D.Double(b.getX(), b.getY() + b.getHeight(), FieldNode.DEFAULT_WIDTH, FieldNode.DEFAULT_HEIGHT));
//		setBounds(b);
	}

	@Override
	public List<ChildNode> getChildren()
	{
		return aFields; // TODO there should be a remove operation on ObjectNode
	}

	@Override
	public void removeChild(ChildNode pNode)
	{
		if (pNode.getParent() != this)
		{
			return;
		}
		aFields.remove(pNode);
		pNode.setParent(null);
	}
	
	/**
	 *  Adds a persistence delegate to a given encoder that
	 * encodes the child nodes of this node.
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(ObjectNode.class, new DefaultPersistenceDelegate()
		{
			protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
			{
				super.initialize(pType, pOldInstance, pNewInstance, pOut);
				for(ChildNode node : ((ParentNode) pOldInstance).getChildren())
				{
					pOut.writeStatement( new Statement(pOldInstance, "addChild", new Object[]{ node }) );            
				}
			}
		});
	}
}
