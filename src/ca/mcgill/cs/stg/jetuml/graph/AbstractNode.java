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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;

import ca.mcgill.cs.stg.jetuml.framework.Grid;


/**
 * A class that supplies convenience implementations for 
 * a number of methods in the Node interface.
 */
public abstract class AbstractNode implements Node
{
	public static final int SHADOW_GAP = 4;
	
	private static final Color SHADOW_COLOR = Color.LIGHT_GRAY;

	/**
     * Constructs a node.
	 */
	public AbstractNode()
	{
	}

	@Override
	public AbstractNode clone()
	{
		try
		{
			AbstractNode cloned = (AbstractNode) super.clone();
			return cloned;
		}
		catch(CloneNotSupportedException exception)
		{
			return null;
		}
	}

	@Override
	public void translate(double pDeltaX, double pDeltaY)
	{
	}
	
	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Shape shape = getShape();
		if(shape == null)
		{
			return;
		}
		/*
      	Area shadow = new Area(shape);
      	shadow.transform(AffineTransform.getTranslateInstance(SHADOW_GAP, SHADOW_GAP));
      	shadow.subtract(new Area(shape));
		 */
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.translate(SHADOW_GAP, SHADOW_GAP);      
		pGraphics2D.setColor(SHADOW_COLOR);
		pGraphics2D.fill(shape);
		pGraphics2D.translate(-SHADOW_GAP, -SHADOW_GAP);
		pGraphics2D.setColor(pGraphics2D.getBackground());
		pGraphics2D.fill(shape);      
		pGraphics2D.setColor(oldColor);
	}
   
	/**
     *  @return the shape to be used for computing the drop shadow
    */
	public Shape getShape() 
	{ return null; }   
   
	/**
     *  Adds a persistence delegate to a given encoder.
     * @param pEncoder the encoder to which to add the delegate
     */
	public static void setPersistenceDelegate(Encoder pEncoder)
	{
      pEncoder.setPersistenceDelegate(AbstractNode.class, new DefaultPersistenceDelegate()
         {
            protected void initialize(Class<?> pType, Object pOldInstance, Object pNewInstance, Encoder pOut) 
            {
            	super.initialize(pType, pOldInstance, pNewInstance, pOut);
            }
         });
   }
}

