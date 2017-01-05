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

package ca.mcgill.cs.stg.jetuml.graph;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.framework.Direction;
import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 *   A package node in a UML diagram.
 */
public class PackageNode extends RectangularNode implements ParentNode, ChildNode
{
	private static final int DEFAULT_TOP_WIDTH = 60;
	private static final int DEFAULT_TOP_HEIGHT = 20;
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 80;
	private static final int NAME_GAP = 3;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	   
	private static JLabel label = new JLabel();

	private String aName;
	private MultiLineString aContents;
	private Rectangle2D aTop;
	private Rectangle2D aBottom;
	private ArrayList<ChildNode> aContainedNodes;
	private ParentNode aContainer;
	   
	/**
     * Construct a package node with a default size.
	 */
	public PackageNode()
	{
		aName = "";
		aContents = new MultiLineString();
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		aTop = new Rectangle2D.Double(0, 0, DEFAULT_TOP_WIDTH, DEFAULT_TOP_HEIGHT);
		aBottom = new Rectangle2D.Double(0, DEFAULT_TOP_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT);
		aContainedNodes = new ArrayList<>();
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Rectangle2D bounds = getBounds();

		label.setText("<html>" + aName + "</html>");
		label.setFont(pGraphics2D.getFont());
		Dimension d = label.getPreferredSize();
		label.setBounds(0, 0, d.width, d.height);

		pGraphics2D.draw(aTop);

		double textX = bounds.getX() + NAME_GAP;
		double textY = bounds.getY() + (aTop.getHeight() - d.getHeight()) / 2;
      
		pGraphics2D.translate(textX, textY);
		label.paint(pGraphics2D);
		pGraphics2D.translate(-textX, -textY);        
     
		pGraphics2D.draw(aBottom);
		aContents.draw(pGraphics2D, aBottom);
	}
   
	@Override
	public Shape getShape()
	{
		GeneralPath path = new GeneralPath();
		path.append(aTop, false);
		path.append(aBottom, false);
		return path;
	}
	
	/**
	 * @return The point that corresponds to the actual top right
	 * corner of the figure (as opposed to bounds).
	 */
	public Point2D getTopRightCorner()
	{
		return new Point2D.Double(aBottom.getMaxX(), aBottom.getMinY());
	}
	
	@Override
	public Point2D getConnectionPoint(Direction pDirection)
	{
		Point2D connectionPoint = super.getConnectionPoint(pDirection);
		if( connectionPoint.getY() < aBottom.getMinY() && aTop.getMaxX() < connectionPoint.getX() )
		{
			// The connection point falls in the empty top-right corner, re-compute it so
			// it intersects the top of the bottom rectangle (basic triangle proportions)
			double delta = aTop.getHeight() * (connectionPoint.getX() - getBounds().getCenterX()) * 2 / 
					getBounds().getHeight();
			double newX = connectionPoint.getX() - delta;
			if( newX < aTop.getMaxX() )
			{
				newX = aTop.getMaxX() + 1;
			}
			return new Point2D.Double(newX, aBottom.getMinY());	
		}
		else
		{
			return connectionPoint;
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		label.setText("<html>" + aName + "</html>");
		label.setFont(pGraphics2D.getFont());
		Dimension d = label.getPreferredSize();
		double topWidth = Math.max(d.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		double topHeight = Math.max(d.getHeight(), DEFAULT_TOP_HEIGHT);
		
		Rectangle2D childBounds = null;
		for( ChildNode child : getChildren() )
		{
			child.layout(pGraph, pGraphics2D, pGrid);
			if( childBounds == null )
			{
				childBounds = child.getBounds();
			}
			else
			{
				childBounds.add(child.getBounds());
			}
		}
		
		Rectangle2D contentsBounds = aContents.getBounds(pGraphics2D);
		
		if( childBounds == null ) // no children; leave (x,y) as is and place default rectangle below.
		{
			setBounds( new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), 
					computeWidth(topWidth, contentsBounds.getWidth(), 0.0),
					computeHeight(topHeight, contentsBounds.getHeight(), 0.0)));
		}
		else
		{
			setBounds( new Rectangle2D.Double(childBounds.getX() - XGAP, childBounds.getY() - topHeight - YGAP, 
					computeWidth(topWidth, contentsBounds.getWidth(), childBounds.getWidth() + 2 * XGAP),
					computeHeight(topHeight, contentsBounds.getHeight(), childBounds.getHeight() + 2 * YGAP)));	
		}
		
		Rectangle2D b = getBounds();
		aTop = new Rectangle2D.Double(b.getX(), b.getY(), topWidth, topHeight);
		aBottom = new Rectangle2D.Double(b.getX(), b.getY() + topHeight, b.getWidth(), b.getHeight() - topHeight);
	}
	
	private double computeWidth(double pTopWidth, double pContentWidth, double pChildrenWidth)
	{
		return max( DEFAULT_WIDTH, pTopWidth + DEFAULT_WIDTH - DEFAULT_TOP_WIDTH, pContentWidth, pChildrenWidth);
	}
	
	private double computeHeight(double pTopHeight, double pContentHeight, double pChildrenHeight)
	{
		return pTopHeight + max( DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT, pContentHeight, pChildrenHeight);
	}
	
	/*
	 * I can't believe I had to implement this.
	 */
	private static double max(double ... pNumbers)
	{
		double maximum = Double.MIN_VALUE;
		for(double number : pNumbers)
		{
			if(number > maximum)
			{
				maximum = number;
			}
		}
		return maximum;
	}
	
	/**
     * Sets the name property value.
     * @param pName the class name
	 */
	public void setName(String pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the class name
	 */
	public String getName()
	{
		return aName;
	}

	/**
     * Sets the contents property value.
     * @param pContents the contents of this class
	 */
	public void setContents(MultiLineString pContents)
	{
		aContents = pContents;
	}
	
	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
		super.translate(pDeltaX, pDeltaY);
		
		aTop = (Rectangle2D)aTop.clone();
		aBottom = (Rectangle2D)aBottom.clone();
		aTop.setFrame(aTop.getX() + pDeltaX, aTop.getY() + pDeltaY, aTop.getWidth(), aTop.getHeight());
		aBottom.setFrame(aBottom.getX() + pDeltaX, aBottom.getY() + pDeltaY, aBottom.getWidth(), aBottom.getHeight());
		for(Node childNode : getChildren())
        {
        	childNode.translate(pDeltaX, pDeltaY);
        }   
	}

	/**
     * Gets the contents property value.
     * @return the contents of this class
	 */
	public MultiLineString getContents()
	{
		return aContents;
	}

	@Override
	public PackageNode clone()
	{
		PackageNode cloned = (PackageNode)super.clone();
		cloned.aContents = aContents.clone();
		cloned.aContainedNodes = new ArrayList<>();
		for( ChildNode child : aContainedNodes )
		{
			// We can't use addChild(...) here because of the interaction with the original parent.
			ChildNode clonedChild = (ChildNode) child.clone();
			clonedChild.setParent(cloned);
			cloned.aContainedNodes.add(clonedChild);
		}
		return cloned;
	}
	
	@Override
	public ParentNode getParent()
	{
		return aContainer;
	}

	@Override
	public void setParent(ParentNode pNode)
	{
		assert pNode instanceof PackageNode || pNode == null;
		aContainer = pNode;
	}

	@Override
	public List<ChildNode> getChildren()
	{
		return aContainedNodes; // TODO there should be a remove operation on PackageNode
	}

	@Override
	public void addChild(int pIndex, ChildNode pNode)
	{
		assert pNode != null;
		assert pIndex >=0 && pIndex <= aContainedNodes.size();
		ParentNode oldParent = pNode.getParent();
		if (oldParent != null)
		{
			oldParent.removeChild(pNode);
		}
		aContainedNodes.add(pIndex, pNode);
		pNode.setParent(this);
	}

	@Override
	public void addChild(ChildNode pNode)
	{
		assert pNode != null;
		addChild(aContainedNodes.size(), pNode);
	}

	@Override
	public void removeChild(ChildNode pNode)
	{
		aContainedNodes.remove(pNode);
		pNode.setParent(null);
	}
	
	@Override
	public boolean requiresParent()
	{
		return false;
	}
	
	/**
	 *  Adds a persistence delegate to a given encoder that
	 * encodes the child nodes of this node.
	 * @param pEncoder the encoder to which to add the delegate
	 */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
		pEncoder.setPersistenceDelegate(PackageNode.class, new DefaultPersistenceDelegate()
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
