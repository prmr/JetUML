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

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 * An interface node in a class diagram that can be composed
 * of three compartments: top (for the name), middle (for attributes,
 * normally unused), and bottom (for methods).
 */
public class InterfaceNode extends RectangularNode implements ChildNode
{
	protected static final int DEFAULT_COMPARTMENT_HEIGHT = 20;
	protected static final int DEFAULT_WIDTH = 100;
	protected static final int DEFAULT_HEIGHT = 60;
	
	protected MultiLineString aName;
	protected MultiLineString aMethods;   
	
	private ParentNode aContainer;
	

	/**
     * Construct an interface node with a default size and
     * the text <<interface>>.
	 */
	public InterfaceNode()
	{
		aName = new MultiLineString(true);
		aName.setText("\u00ABinterface\u00BB\n");
		aName.setJustification(MultiLineString.CENTER);
		aMethods = new MultiLineString();
		aMethods.setJustification(MultiLineString.LEFT);
		setBounds(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		int midHeight = computeMiddle().getHeight();
		double bottomHeight = computeBottom().getHeight();
		Rectangle2D top = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), getBounds().getHeight() - midHeight - bottomHeight);
		pGraphics2D.draw(top);
		aName.draw(pGraphics2D, Conversions.toRectangle(top));
		Rectangle2D mid = new Rectangle2D.Double(top.getX(), top.getMaxY(), top.getWidth(), midHeight);
		pGraphics2D.draw(mid);
		Rectangle2D bot = new Rectangle2D.Double(top.getX(), mid.getMaxY(), top.getWidth(), bottomHeight);
		pGraphics2D.draw(bot);
		aMethods.draw(pGraphics2D, Conversions.toRectangle(bot));
	}
	
	/**
	 * The top is computed to be at least the default
	 * node size.
	 * @return The area of the top compartment
	 */
	protected Rectangle computeTop()
	{
		Rectangle top = aName.getBounds(); 
		
		int minHeight = DEFAULT_COMPARTMENT_HEIGHT;
		if(!needsMiddleCompartment() && !needsBottomCompartment() )
		{
			minHeight = DEFAULT_HEIGHT;
		}
		else if( needsMiddleCompartment() ^ needsBottomCompartment() )
		{
			minHeight = 2 * DEFAULT_COMPARTMENT_HEIGHT;
		}
		top = top.add(new Rectangle(0, 0, DEFAULT_WIDTH, minHeight));

		return top;
	}
	
	/**
	 * @return The area of the middle compartment. The x and y values
	 * are meaningless.
	 */
	protected Rectangle computeMiddle()
	{
		return new Rectangle(0, 0, 0, 0);
	}
	
	/**
	 * @return The area of the bottom compartment. The x and y values
	 * are meaningless.
	 */
	protected Rectangle computeBottom()
	{
		if( !needsBottomCompartment() )
		{
			return new Rectangle(0, 0, 0, 0);
		}
			
		Rectangle bottom = aMethods.getBounds();
		bottom = bottom.add(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_COMPARTMENT_HEIGHT));
		return bottom;
	}
	

	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsMiddleCompartment()
	{
		return false;
	}
	
	/**
	 * @return True if the node requires a bottom compartment.
	 */
	protected boolean needsBottomCompartment()
	{
		return !aMethods.getText().isEmpty();
	}
	

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D)
	{
		Rectangle top = computeTop();
		Rectangle middle = computeMiddle();
		Rectangle bottom = computeBottom();

		Rectangle bounds = new Rectangle(getBounds().getX(), getBounds().getY(), 
				Math.max(Math.max(top.getWidth(), middle.getWidth()), bottom.getWidth()), top.getHeight() + middle.getHeight() + bottom.getHeight());
		setBounds(Grid.snapped(bounds));
	}

	/**
     * Sets the name property value.
     * @param pName the interface name
	 */
	public void setName(MultiLineString pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the interface name
	 */
	public MultiLineString getName()
	{
		return aName;
	}
	
	/**
     * Sets the methods property value.
     * @param pMethods the methods of this interface
	 */
	public void setMethods(MultiLineString pMethods)
	{
		aMethods = pMethods;
	}
	
	/**
     * Gets the methods property value.
     * @return the methods of this interface
	 */
	public MultiLineString getMethods()
	{
		return aMethods;
	}

	@Override
	public InterfaceNode clone()
	{
		InterfaceNode cloned = (InterfaceNode)super.clone();
		cloned.aName = aName.clone();
		cloned.aMethods = aMethods.clone();
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
	public boolean requiresParent()
	{
		return false;
	}
}
