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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ca.mcgill.cs.stg.jetuml.framework.Grid;
import ca.mcgill.cs.stg.jetuml.framework.MultiLineString;

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
//	private Color aColor;

   /**
    *  Construct a note node with a default size and color.
    */
	public NoteNode()
	{
		setBounds(new Rectangle2D.Double(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT));
		aText = new MultiLineString();
		aText.setJustification(MultiLineString.LEFT);
//		aColor = DEFAULT_COLOR;
	}

	@Override
	public boolean addEdge(Edge pEdge, Point2D pPoint1, Point2D pPoint2)
	{
		PointNode end = new PointNode();
		end.translate(pPoint2.getX(), pPoint2.getY());
		pEdge.connect(this, end);
		return super.addEdge(pEdge, pPoint1, pPoint2);
	}

	@Override
	public void removeEdge(Graph pGraph, Edge pEdge)
	{
		if(pEdge.getStart() == this)
		{
			pGraph.removeNode(pEdge.getEnd());
		}
	}

	@Override
	public void layout(Graph pGraph, Graphics2D pGraphics2D, Grid pGrid)
	{
		Rectangle2D b = aText.getBounds(pGraphics2D); // getMultiLineBounds(name, g2);
		Rectangle2D bounds = getBounds();
		b = new Rectangle2D.Double(bounds.getX(), bounds.getY(), Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(), DEFAULT_HEIGHT));
		pGrid.snap(b);
		setBounds(b);
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

		Rectangle2D bounds = getBounds();
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
		Rectangle2D bounds = getBounds();
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
		cloned.aText = (MultiLineString) aText.clone();
		return cloned;
	}
}
