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
import java.awt.geom.Rectangle2D;
import java.awt.geom.GeneralPath;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

/**
 *   An actor node in a use case diagram.
 */
public class ActorNode extends HierarchicalNode
{
	// Bounding rectangle
	private static final int DEFAULT_WIDTH  = 48;
	private static final int DEFAULT_HEIGHT = 64;
	
	// Stick man
	// Height = HEAD_SIZE + BODY_SIZE + LEG_SIZE/sqrt(2)
	// CSOFF:
	private static final int GAP_ABOVE = 4;
	private static final int HEAD_SIZE = DEFAULT_WIDTH*4/12;
	private static final int BODY_SIZE = DEFAULT_WIDTH*5/12;
	private static final int LEG_SIZE  = DEFAULT_WIDTH*5/12;
	private static final int ARMS_SIZE = DEFAULT_WIDTH*6/12; 
	// CSON:
	
	private MultiLineString aName;

	/**
     * Construct an actor node with a default size and name.
	 */
	public ActorNode()
	{
		aName = new MultiLineString();
		aName.setText("Actor");
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
   
	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		Rectangle2D top = new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		Rectangle2D bot = aName.getBounds(pGraphics2D);
		Rectangle2D b = new Rectangle2D.Double(getBounds().getX(), getBounds().getY(),
            Math.max(top.getWidth(), bot.getWidth()), top.getHeight() + bot.getHeight());
		pGrid.snap(b);
		setBounds(b);
	}
    
	@Override
	public void draw(Graphics2D pGraphics2D)
	{	
		Rectangle2D bounds = getBounds();

		// Draw stick person
		GeneralPath path = new GeneralPath();
		float neckX = (float) (bounds.getX() + bounds.getWidth() / 2);
		float neckY = (float) (bounds.getY() + HEAD_SIZE + GAP_ABOVE);
		// head
		path.moveTo(neckX, neckY);
		path.quadTo(neckX + HEAD_SIZE / 2, neckY, neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE / 2);
		path.quadTo(neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX, neckY - HEAD_SIZE);
		path.quadTo(neckX - HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX-HEAD_SIZE / 2, neckY - HEAD_SIZE / 2);
		path.quadTo(neckX - HEAD_SIZE / 2, neckY, neckX, neckY);
		// body
		float hipX = neckX;
		float hipY = neckY + BODY_SIZE;
		path.lineTo(hipX, hipY);
		// arms
		path.moveTo(neckX - ARMS_SIZE / 2, neckY + BODY_SIZE / 3);
		path.lineTo(neckX + ARMS_SIZE / 2, neckY + BODY_SIZE / 3);
		// legs
		float dx = (float) (LEG_SIZE / Math.sqrt(2));
		float feetX1 = hipX - dx;
		float feetX2 = hipX + dx + 1;
		float feetY  = hipY + dx + 1;
		path.moveTo(feetX1, feetY);
		path.lineTo(hipX, hipY);
		path.lineTo(feetX2, feetY);

		pGraphics2D.draw(path);

		// Draw name
		Rectangle2D bot = aName.getBounds(pGraphics2D);

		Rectangle2D namebox = new Rectangle2D.Double(bounds.getX() + (bounds.getWidth() - bot.getWidth()) / 2, 
				bounds.getY() + DEFAULT_HEIGHT, bot.getWidth(), bot.getHeight());
		aName.draw(pGraphics2D, namebox);
	}

	/**
     * Sets the name property value.
     * @param pNewValue the new actor name
     */
	public void setName(MultiLineString pNewValue)
	{
		aName = pNewValue;
	}

	/**
     * Gets the name property value.
     * @return The name.
	 */
	public MultiLineString getName()
	{
		return aName;
	}
	
	@Override
	public ActorNode clone()
	{
		ActorNode cloned = (ActorNode) super.clone();
		cloned.aName = (MultiLineString) aName.clone();
		return cloned;
	} 
}
