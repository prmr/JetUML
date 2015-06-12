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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 *   A package node in a UML diagram.
 */
public class PackageNode extends HierarchicalNode
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

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		label.setText("<html>" + aName + "</html>");
		label.setFont(pGraphics2D.getFont());
		Dimension d = label.getPreferredSize();
		double topWidth = Math.max(d.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH);
		double topHeight = Math.max(d.getHeight(), DEFAULT_TOP_HEIGHT);
		
		Rectangle2D childBounds = null;
		List<HierarchicalNode> children = getChildren();
		for( HierarchicalNode child : children )
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
			snapBounds( pGrid, Math.max(topWidth + DEFAULT_WIDTH - DEFAULT_TOP_WIDTH, Math.max(DEFAULT_WIDTH, contentsBounds.getWidth())),
					topHeight + Math.max(DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT, contentsBounds.getHeight()));
		}
		else
		{
			setBounds( new Rectangle2D.Double(childBounds.getX() - XGAP, childBounds.getY() - topHeight - YGAP, 
					Math.max(topWidth, childBounds.getWidth() + 2 * XGAP), topHeight + childBounds.getHeight() + 2 * YGAP));
		}
		
		Rectangle2D b = getBounds();
		aTop = new Rectangle2D.Double(b.getX(), b.getY(), topWidth, topHeight);
		aBottom = new Rectangle2D.Double(b.getX(), b.getY() + topHeight, b.getWidth(), b.getHeight() - topHeight);
	}
	
	/**
     * Snaps the bounds of this rectangular node so that it has the node position as top left corner and the desired width and
     * height, snapped to the given grid.
     * 
     * @param pGrid the grid to snap to
     * @param pWidth the desired width
     * @param pHeight the desired height
     */
    public void snapBounds(Grid pGrid, double pWidth, double pHeight)
    {
        getBounds().setFrame(getBounds().getX(), getBounds().getY(), pWidth, pHeight);
        pGrid.snap(getBounds());
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
		cloned.aContents = (MultiLineString)aContents.clone();
		return cloned;
	}
}
