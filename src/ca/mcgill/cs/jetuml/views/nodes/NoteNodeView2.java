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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph2;
import ca.mcgill.cs.jetuml.graph.nodes.NoteNode;
import ca.mcgill.cs.jetuml.views.Grid;
import ca.mcgill.cs.jetuml.views.StringViewer2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * An object to render a NoteNode.
 * 
 * @author Martin P. Robillard
 *
 */
public class NoteNodeView2 extends RectangleBoundedNodeView2
{
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 40;
	private static final int FOLD_X = 8;
	private static final int FOLD_Y = 8;
	private static final Color DEFAULT_COLOR = Color.color(0.9f, 0.9f, 0.6f); // Pale yellow
	private static final StringViewer2 NOTE_VIEWER = new StringViewer2(StringViewer2.Align.LEFT, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public NoteNodeView2(NoteNode pNode)
	{
		super(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	private String name()
	{
		return ((NoteNode)node()).getName();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);
		double oldLineWidth = pGraphics.getLineWidth();
		pGraphics.setLineWidth(STROKE_WIDTH);
		fillFold(pGraphics);   
		pGraphics.setLineWidth(oldLineWidth);
      
		NOTE_VIEWER.draw(name(), pGraphics, getBounds());
	}
	
	@Override
	public void fillShape(GraphicsContext pGraphics, boolean pShadow) 
	{
		Rectangle bounds = getBounds();		
		if (pShadow) 
		{
			pGraphics.beginPath();
			pGraphics.setFill(SHADOW_COLOR);
			pGraphics.moveTo((float)bounds.getX(), (float)bounds.getY());
			pGraphics.lineTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
			pGraphics.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
			pGraphics.lineTo((float)bounds.getMaxX(), (float)bounds.getMaxY());
			pGraphics.lineTo((float)bounds.getX(), (float)bounds.getMaxY());
			pGraphics.closePath();
			pGraphics.fill();	
		}
		else 
		{
			pGraphics.beginPath();
			pGraphics.setFill(DEFAULT_COLOR);
			pGraphics.moveTo((float)bounds.getX(), (float)bounds.getY());
			pGraphics.lineTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
			pGraphics.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
			pGraphics.lineTo((float)bounds.getMaxX(), (float)bounds.getMaxY());
			pGraphics.lineTo((float)bounds.getX(), (float)bounds.getMaxY());
			pGraphics.closePath();
			pGraphics.fill();
			pGraphics.stroke();
		}	
	}
	
	/**
	 * Fills in note fold.
	 * @param pGraphics GraphicsContext in which to fill the fold
	 */
	public void fillFold(GraphicsContext pGraphics)
	{
		final Rectangle bounds = getBounds();
		pGraphics.beginPath();
		pGraphics.setFill(Color.WHITE);
		pGraphics.moveTo((float)(bounds.getMaxX() - FOLD_X), (float)bounds.getY());
		pGraphics.lineTo((float)bounds.getMaxX() - FOLD_X, (float)bounds.getY() + FOLD_X);
		pGraphics.lineTo((float)bounds.getMaxX(), (float)(bounds.getY() + FOLD_Y));
		pGraphics.closePath();
		pGraphics.fill();
		pGraphics.stroke();
	}
	
	@Override
	public void layout(Graph2 pGraph)
	{
		Rectangle b = NOTE_VIEWER.getBounds(name()); 
		Rectangle bounds = getBounds();
		b = new Rectangle(bounds.getX(), bounds.getY(), Math.max(b.getWidth(), DEFAULT_WIDTH), Math.max(b.getHeight(), DEFAULT_HEIGHT));
		setBounds(Grid.snapped(b));
	}
}
