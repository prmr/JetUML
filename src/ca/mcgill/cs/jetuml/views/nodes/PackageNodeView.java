/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2018 by the contributors of the JetUML project.
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
package ca.mcgill.cs.jetuml.views.nodes;

import java.util.List;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.nodes.ChildNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a package in a class diagram.
 */
public class PackageNodeView extends RectangleBoundedNodeView
{
	private static final int DEFAULT_WIDTH = 100;
	private static final int DEFAULT_HEIGHT = 80;
	private static final int DEFAULT_TOP_WIDTH = 60;
	private static final int DEFAULT_TOP_HEIGHT = 20;
	private static final int NAME_GAP = 3;
	private static final int XGAP = 5;
	private static final int YGAP = 5;
	private static final StringViewer NAME_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	private static final StringViewer CONTENTS_VIEWER = new StringViewer(StringViewer.Align.CENTER, false, false);
	
	private Rectangle aTop;
	private Rectangle aBottom;

	
	/**
	 * @param pNode The node to wrap.
	 */
	public PackageNodeView(PackageNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		aTop = new Rectangle(0, 0, DEFAULT_TOP_WIDTH, DEFAULT_TOP_HEIGHT);
		aBottom = new Rectangle(0, DEFAULT_TOP_HEIGHT, DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT);
	}
	
	private String name()
	{
		return ((PackageNode)node()).getName();
	}
	
	private String contents()
	{
		return ((PackageNode)node()).getContents();
	}
	
	private List<ChildNode> children()
	{
		return ((PackageNode)node()).getChildren();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);
		Rectangle bounds = getBounds();
		int textX = bounds.getX() + NAME_GAP;
		int textY = (int)(bounds.getY());
		Rectangle nameRectangle = new Rectangle(textX, textY, (int)aTop.getWidth(), (int)aTop.getHeight());
		Rectangle contentsRectangle = new Rectangle(textX, textY + DEFAULT_TOP_HEIGHT + YGAP, (int)aBottom.getWidth(), (int)aBottom.getHeight());
		NAME_VIEWER.draw(name(), pGraphics, nameRectangle);
		CONTENTS_VIEWER.draw(contents(), pGraphics, contentsRectangle);
	}
	
	@Override
	public void fillShape(GraphicsContext pGraphics, boolean pShadow) 
	{
		Rectangle bounds = getBounds();
		if (pShadow) 
		{
			pGraphics.setFill(SHADOW_COLOR);
			pGraphics.fillRect(bounds.getX(), bounds.getY(), aTop.getWidth(), aTop.getHeight());
			pGraphics.fillRect(bounds.getX(), bounds.getY() + aTop.getHeight(), aBottom.getWidth(), aBottom.getHeight());
		}
		else 
		{
			pGraphics.setFill(BACKGROUND_COLOR);
			pGraphics.fillRect(bounds.getX(), bounds.getY(), aTop.getWidth(), aTop.getHeight());
			pGraphics.fillRect(bounds.getX(), bounds.getY() + aTop.getHeight(), aBottom.getWidth(), aBottom.getHeight());
			pGraphics.strokeRect(bounds.getX(), bounds.getY(), aTop.getWidth(), aTop.getHeight());
			pGraphics.strokeRect(bounds.getX(), bounds.getY() + aTop.getHeight(), aBottom.getWidth(), aBottom.getHeight());
		}	
	}
	
	/**
	 * @return The point that corresponds to the actual top right
	 * corner of the figure (as opposed to bounds).
	 */
	public Point2D getTopRightCorner()
	{
		return new Point2D(aBottom.getMaxX(), aBottom.getY());
	}
	
	@Override
	public Point getConnectionPoint(Direction pDirection)
	{
		Point connectionPoint = super.getConnectionPoint(pDirection);
		if( connectionPoint.getY() < aBottom.getY() && aTop.getMaxX() < connectionPoint.getX() )
		{
			// The connection point falls in the empty top-right corner, re-compute it so
			// it intersects the top of the bottom rectangle (basic triangle proportions)
			int delta = aTop.getHeight() * (connectionPoint.getX() - getBounds().getCenter().getX()) * 2 / 
					getBounds().getHeight();
			int newX = connectionPoint.getX() - delta;
			if( newX < aTop.getMaxX() )
			{
				newX = aTop.getMaxX() + 1;
			}
			return new Point(newX, aBottom.getY());	
		}
		else
		{
			return connectionPoint;
		}
	}

	@Override
	public void layout(Diagram pGraph)
	{
		Rectangle nameBounds = NAME_VIEWER.getBounds(name());
		int topWidth = (int)Math.max(nameBounds.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		int topHeight = (int)Math.max(nameBounds.getHeight(), DEFAULT_TOP_HEIGHT);
		
		Rectangle childBounds = null;
		for( ChildNode child : children() )
		{
			child.view().layout(pGraph);
			if( childBounds == null )
			{
				childBounds = child.view().getBounds();
			}
			else
			{
				childBounds = childBounds.add(child.view().getBounds());
			}
		}
		
		Rectangle contentsBounds = CONTENTS_VIEWER.getBounds(contents());
		
		if( childBounds == null ) // no children; leave (x,y) as is and place default rectangle below.
		{
			setBounds( new Rectangle(getBounds().getX(), getBounds().getY(), 
					(int)computeWidth(topWidth, contentsBounds.getWidth(), 0.0),
					(int)computeHeight(topHeight, contentsBounds.getHeight(), 0.0)));
		}
		else
		{
			setBounds( new Rectangle(childBounds.getX() - XGAP, (int)(childBounds.getY() - topHeight - YGAP), 
					(int)computeWidth(topWidth, contentsBounds.getWidth(), childBounds.getWidth() + 2 * XGAP),
					(int)computeHeight(topHeight, contentsBounds.getHeight(), childBounds.getHeight() + 2 * YGAP)));	
		}
		
		Rectangle b = getBounds();
		aTop = new Rectangle(b.getX(), b.getY(), topWidth, topHeight);
		aBottom = new Rectangle(b.getX(), b.getY() + topHeight, b.getWidth(), b.getHeight() - topHeight);
	}
	
	/**
	 * @param pX the new X coordinate.
	 * @param pY the new Y coordinate.
	 */
	public void translateTop(int pX, int pY)
	{
		aTop = aTop.translated(pX, pY);
	}
	
	/**
	 * @param pX the new X coordinate.
	 * @param pY the new Y coordinate.
	 */
	public void translateBottom( int pX, int pY)
	{
		aBottom = aBottom.translated(pX, pY);
	}
	
	private double computeWidth(double pTopWidth, double pContentWidth, double pChildrenWidth)
	{
		return max( DEFAULT_WIDTH, pTopWidth + DEFAULT_WIDTH - DEFAULT_TOP_WIDTH, pContentWidth, pChildrenWidth);
	}
	
	private double computeHeight(double pTopHeight, double pContentHeight, double pChildrenHeight)
	{
		return pTopHeight + max( DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT, pContentHeight, pChildrenHeight);
	}
	
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

}
