/*
Violet - A program for editing UML diagrams.

Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package ca.mcgill.cs.stg.violetta.graph;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import javax.swing.JLabel;

import com.horstmann.violet.framework.Grid;
import com.horstmann.violet.framework.MultiLineString;

/**
 *   A package node in a UML diagram.
 */
@SuppressWarnings("serial")
public class PackageNode extends RectangularNode
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
		Rectangle2D bounds = getBounds();

		label.setText("<html>" + aName + "</html>");
		label.setFont(pGraphics2D.getFont());
		Dimension d = label.getPreferredSize();
      
		aTop = new Rectangle2D.Double(bounds.getX(), bounds.getY(), 
				Math.max(d.getWidth(), DEFAULT_TOP_WIDTH), Math.max(d.getHeight(), DEFAULT_TOP_HEIGHT));

		aBottom = aContents.getBounds(pGraphics2D);
		Rectangle2D min = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT - DEFAULT_TOP_HEIGHT);
     	aBottom.add(min);
     	double width = Math.max(aTop.getWidth() + DEFAULT_WIDTH - DEFAULT_TOP_WIDTH, aBottom.getWidth());
     	double height = aTop.getHeight() + aBottom.getHeight();

     	List<Node> children = getChildren();
     	if(children.size() > 0)
     	{
     		Rectangle2D childBounds = new Rectangle2D.Double(bounds.getX(), bounds.getY(), 0, 0);
     		for(int i = 0; i < children.size(); i++)
     		{
     			Node child = children.get(i);
     			child.layout(pGraph, pGraphics2D, pGrid);
     			childBounds.add(child.getBounds());
     		}
     		width = Math.max(width, childBounds.getWidth() + XGAP);
     		height = Math.max(height, childBounds.getHeight() + YGAP);
     	}
     	Rectangle2D b = new Rectangle2D.Double(bounds.getX(), bounds.getY(), width, height);
     	pGrid.snap(b);
     	setBounds(b);
      
     	aTop = new Rectangle2D.Double(bounds.getX(), bounds.getY(), 
     			Math.max(d.getWidth() + 2 * NAME_GAP, DEFAULT_TOP_WIDTH), Math.max(d.getHeight(), DEFAULT_TOP_HEIGHT));
      
     	aBottom = new Rectangle2D.Double(bounds.getX(), bounds.getY() + aTop.getHeight(), bounds.getWidth(), bounds.getHeight() - aTop.getHeight());
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

	@Override
	public boolean addNode(Node pNode, Point2D pPoint)
	{
		if(pNode instanceof ClassNode || pNode instanceof InterfaceNode || pNode instanceof PackageNode)
		{
			addChild(pNode);
			return true;
		}
		else
		{
			return pNode instanceof NoteNode;
		}
   }
}
