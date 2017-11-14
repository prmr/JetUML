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
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
   A node in a state diagram.
*/
public class StateNode extends RectangularNode
{
	private static final int ARC_SIZE = 20;
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	
	private MultiLineString aName;

	/**
     * Construct a state node with a default size.
	 */
	public StateNode()
	{
		aName = new MultiLineString();
		setBounds(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		pGraphics2D.draw(getShape());
		aName.draw(pGraphics2D, Conversions.toRectangle2D(getBounds()));
	}
   
	@Override
	public Shape getShape()
	{       
		return new RoundRectangle2D.Double(getBounds().getX(), getBounds().getY(), 
				getBounds().getWidth(), getBounds().getHeight(), ARC_SIZE, ARC_SIZE);
   }

	@Override	
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		Rectangle2D b = aName.getBounds(pGraphics2D);
		b = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(), 
				Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid.snapped(Conversions.toRectangle(b)));
	}

	/**
     * Sets the name property value.
     * @param pName the new state name
	 */
	public void setName(MultiLineString pName)
	{
		aName = pName;
	}

	/**
     * Gets the name property value.
     * @return the state name
	 */
	public MultiLineString getName()
	{
		return aName;
	}

	@Override
	public StateNode clone()
	{
		StateNode cloned = (StateNode)super.clone();
		cloned.aName = aName.clone();
		return cloned;
	}
}
