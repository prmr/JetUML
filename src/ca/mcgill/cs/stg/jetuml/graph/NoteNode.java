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
import java.awt.geom.GeneralPath;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 *  A note node in a UML diagram.
 */
public class NoteNode extends RectangularNode
{
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 40;
	private static final Color DEFAULT_COLOR = new Color(0.9f, 0.9f, 0.6f); // pale yellow
	private static final int FOLD_X = 8;
	private static final int FOLD_Y = 8;
	
	private MultiLineString aText;

   /**
    *  Construct a note node with a default size and color.
    */
	public NoteNode()
	{
		setBounds(new Rectangle(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		aText = new MultiLineString();
		aText.setJustification(MultiLineString.LEFT);
	}

	@Override
	public void layout(Graph pGraph)
	{
		Rectangle b = aText.getBounds(); // getMultiLineBounds(name, g2);
		Rectangle bounds = getBounds();
		b = new Rectangle(bounds.getX(), bounds.getY(), Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid.snapped(b));
	}

	/**
     * Gets the value of the text property.
     * @return the text inside the note
	 */
	public MultiLineString getText()
	{
		return aText;
	}

	/**
     * Sets the value of the text property.
     * @param pText the text inside the note
	 */
	public void setText(MultiLineString pText)
	{
		aText = pText;
	}

	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		super.draw(pGraphics2D);
		Color oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(DEFAULT_COLOR);

		Shape path = getShape();
		pGraphics2D.fill(path);
		pGraphics2D.setColor(oldColor);
		pGraphics2D.draw(path);

		Rectangle bounds = getBounds();
		GeneralPath fold = new GeneralPath();
		fold.moveTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
		fold.lineTo((float)bounds.getMaxX() - FOLD_X, (float)bounds.getY() + FOLD_X);
		fold.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
		fold.closePath();
		oldColor = pGraphics2D.getColor();
		pGraphics2D.setColor(pGraphics2D.getBackground());
		pGraphics2D.fill(fold);
		pGraphics2D.setColor(oldColor);      
		pGraphics2D.draw(fold);      
      
		aText.draw(pGraphics2D, getBounds());
	}
   
	@Override
	public Shape getShape()
	{
		Rectangle bounds = getBounds();
		GeneralPath path = new GeneralPath();
		path.moveTo((float)bounds.getX(), (float)bounds.getY());
		path.lineTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
		path.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
		path.lineTo((float)bounds.getMaxX(), (float)bounds.getMaxY());
		path.lineTo((float)bounds.getX(), (float)bounds.getMaxY());
		path.closePath();
		return path;
	}

	@Override
	public NoteNode clone()
	{
		NoteNode cloned = (NoteNode)super.clone();
		cloned.aText = aText.clone();
		return cloned;
	}
}
