/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 * An implicit parameter node in a sequence diagram. The 
 * visual portion of this node includes the top rectangle (object) and
 * its vertical life line. The ImplicitParamterNode's creator is the
 * CallNode that is the source of a <<creates>> edge that leads to 
 * this node, or null if this node is node created as part of the 
 * sequence.
 */
public class ImplicitParameterNode extends RectangularNode implements ParentNode
{
	private static final int DEFAULT_TOP_HEIGHT = 60;
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 120;
	
	private double aTopHeight;
	private MultiLineString aName;
	private List<ChildNode> aCallNodes = new ArrayList<>();

	/**
     * Construct an object node with a default size.
	 */
	public ImplicitParameterNode()
	{
		aName = new MultiLineString();
		aName.setUnderlined(true);
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		aTopHeight = DEFAULT_TOP_HEIGHT;
	}

	@Override
	public boolean contains(Point2D pPoint)
	{
		Rectangle2D bounds = getBounds();
		return bounds.getX() <= pPoint.getX() && pPoint.getX() <= bounds.getX() + bounds.getWidth();
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle2D top = getTopRectangle();
		pGraphics2D.draw(top);
		aName.draw(pGraphics2D, top);
		double xmid = getBounds().getCenterX();
		Line2D line = new Line2D.Double(xmid, top.getMaxY(), xmid, getBounds().getMaxY());
		Stroke oldStroke = pGraphics2D.getStroke();
		// CSOFF:
		pGraphics2D.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, new float[] { 5.0f, 5.0f }, 0.0f));
		// CSON:
		pGraphics2D.draw(line);
		pGraphics2D.setStroke(oldStroke);
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
	public Shape getShape()
	{ return getTopRectangle(); }
   
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
		b.add(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_TOP_HEIGHT));      
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), b.getWidth(), b.getHeight());
		pGrid.snap(top);
		setBounds(new Rectangle2D.Double(top.getX(), top.getY(), top.getWidth(), getBounds().getHeight()));
		aTopHeight = top.getHeight();
	}

	/**
     * Sets the name property value.
     * @param pName the name of this object
	 */
	public void setName(MultiLineString pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the name of this object
	 */
	public MultiLineString getName()
	{
		return aName;
	}

	@Override
	public ImplicitParameterNode clone()
	{
		ImplicitParameterNode cloned = (ImplicitParameterNode) super.clone();
		cloned.aName = (MultiLineString) aName.clone();
		cloned.aCallNodes = new ArrayList<>();
		for( ChildNode child : aCallNodes )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.setParent(cloned);
			cloned.aCallNodes.add(clonedChild);
		}
		return cloned;
	}
	
	@Override
	public List<ChildNode> getChildren()
	{
		return aCallNodes;
	}

	@Override
	public void addChild(int pIndex, ChildNode pNode)
	{
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aCallNodes.add(pIndex, pNode);
		pNode.setParent(this);
	}
	
	/**
	 * Adds a child in the right sequence in the list of calls.
	 * @param pChild The child to add
	 * @param pPoint The point selected.
	 */
	public void addChild(ChildNode pChild, Point2D pPoint)
	{
		int i = 0;
		while(i < aCallNodes.size() && aCallNodes.get(i).getBounds().getY() <= pPoint.getY())
		{
			i++;
		}
		addChild(i, pChild);
	}

	@Override
	public void addChild(ChildNode pNode)
	{
		addChild(aCallNodes.size(), pNode);
	}

	@Override
	public void removeChild(ChildNode pNode)
	{
		if (pNode.getParent() != this)
		{
			return;
		}
		aCallNodes.remove(pNode);
		pNode.setParent(null);
	}
	
	/**
	 *  Adds a persistence delegate to a given encoder that
	 * encodes the child nodes of this node.
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(ImplicitParameterNode.class, new DefaultPersistenceDelegate()
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
