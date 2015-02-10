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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 *  An object node in an object diagram.
 */
public class ObjectNode extends RectangularNode
{
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	
	private double aTopHeight;
	private MultiLineString aName;

	/**
     * Construct an object node with a default size.
	 */
	public ObjectNode()
	{
		aName = new MultiLineString();
		aName.setUnderlined(true);
		aName.setSize(MultiLineString.LARGE);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle2D top = getTopRectangle();
		pGraphics2D.draw(top);
		pGraphics2D.draw(getBounds());
		aName.draw(pGraphics2D, top);
	}
	
	/* 
	 * Object Nodes are now responsible for translating their Field Node children.
	 */
	@Override
    public void translate(double pDeltaX, double pDeltaY)
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
	public boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		return pEdge instanceof ClassRelationshipEdge && pEdge.getEnd() != null;
	}

	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		if(pDirection.getX() > 0)
		{
			return new Point2D.Double(getBounds().getMaxX(), getBounds().getMinY() + aTopHeight / 2);
		}
		else
		{
			return new Point2D.Double(getBounds().getX(), getBounds().getMinY() + aTopHeight / 2);
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		Rectangle2D b = aName.getBounds(pGraphics2D); 
		b.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - YGAP));
		double leftWidth = 0;
		double rightWidth = 0;
		List<Node> fields = getChildren();
		double height = 0;
		if( fields.size() != 0 )
		{
			height = YGAP;
		}
		for(int i = 0; i < fields.size(); i++)
		{
			FieldNode f = (FieldNode)fields.get(i);
			f.layout(pGraph, pGraphics2D, pGrid);
			Rectangle2D b2 = f.getBounds();
			height += b2.getBounds().getHeight() + YGAP;   
			double axis = f.getAxisX();
			leftWidth = Math.max(leftWidth, axis);
			rightWidth = Math.max(rightWidth, b2.getWidth() - axis);
		}
		double width = 2 * Math.max(leftWidth, rightWidth) + 2 * XGAP;
		width = Math.max(width, b.getWidth());
		width = Math.max(width, DEFAULT_WIDTH);
		b = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), width, b.getHeight() + height);
		pGrid.snap(b);
		setBounds(b);
		aTopHeight = b.getHeight() - height;
		double ytop = b.getY() + aTopHeight + YGAP;
		double xmid = b.getCenterX();
		for(int i = 0; i < fields.size(); i++)
		{
			FieldNode f = (FieldNode)fields.get(i);
			Rectangle2D b2 = f.getBounds();
			f.setBounds(new Rectangle2D.Double(xmid - f.getAxisX(), ytop, f.getAxisX() + rightWidth, b2.getHeight()));
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
		cloned.aName = (MultiLineString)aName.clone();
		return cloned;
	}

	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		List<Node> fields = getChildren();
		if(pNode instanceof PointNode)
		{
			return true;
		}
		if(!(pNode instanceof FieldNode))
		{
			return false;
		}
		if(fields.contains(pNode))
		{
			return true;
		}
		int i = 0;
		while (i < fields.size() && ((Node)fields.get(i)).getBounds().getY() < pPoint.getY())
		{
			i++;
		}
		addChild(i, pNode);
		return true;
	}
   
	/*
     *  This is a patch to ensure that object diagrams can
     * be read back in correctly. 
     */
	@Override
   public void addChild(Node pNode)
   {
		super.addChild(pNode);
		Rectangle2D b = getBounds();
		b.add(new Rectangle2D.Double(b.getX(), b.getY() + b.getHeight(), FieldNode.DEFAULT_WIDTH, FieldNode.DEFAULT_HEIGHT));
      setBounds(b);
   }
}
