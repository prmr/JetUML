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

import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.StringViewer;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * An object to render a NoteNode.
 */
public final class NoteNodeView extends AbstractNodeView
{
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 40;
	private static final int FOLD_LENGTH = 8;
	private static final Color NOTE_COLOR = Color.color(0.9f, 0.9f, 0.6f); // Pale yellow
	private static final StringViewer NOTE_VIEWER = new StringViewer(StringViewer.Align.LEFT, false, false);
	
	/**
	 * @param pNode The node to wrap.
	 */
	public NoteNodeView(NoteNode pNode)
	{
		super(pNode);
	}
	
	private String name()
	{
		return ((NoteNode)node()).getName();
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		ToolGraphics.strokeAndFillSharpPath(pGraphics, createNotePath(), NOTE_COLOR, true);
		ToolGraphics.strokeAndFillSharpPath(pGraphics, createFoldPath(), Color.WHITE, false);
		NOTE_VIEWER.draw(name(), pGraphics, new Rectangle(node().position().getX(), node().position().getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	private Path createNotePath()
	{
		Path path = new Path();
		Rectangle bounds = getBounds();		
		path.getElements().addAll(
				new MoveTo(bounds.getX(), bounds.getY()),
				new LineTo(bounds.getMaxX() - FOLD_LENGTH, bounds.getY()),
				new LineTo(bounds.getMaxX(), bounds.getY() + FOLD_LENGTH),
				new LineTo(bounds.getMaxX(), bounds.getMaxY()),
				new LineTo(bounds.getX(), bounds.getMaxY()),
				new LineTo(bounds.getX(), bounds.getY()));
		return path;
	}
	
	/**
	 * Fills in note fold.
	 * @param pGraphics GraphicsContext in which to fill the fold
	 */
	private Path createFoldPath()
	{
		Rectangle bounds = getBounds();
		Path path = new Path();
		path.getElements().addAll(
				new MoveTo(bounds.getMaxX() - FOLD_LENGTH, bounds.getY()),
				new LineTo(bounds.getMaxX() - FOLD_LENGTH, bounds.getY() + FOLD_LENGTH),
				new LineTo(bounds.getMaxX(), bounds.getY() + FOLD_LENGTH),
				new LineTo(bounds.getMaxX() - FOLD_LENGTH, bounds.getY())
		);
		return path;
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle textBounds = NOTE_VIEWER.getBounds(name()); 
		return new Rectangle(node().position().getX(), node().position().getY(), 
				Math.max(textBounds.getWidth() + FOLD_LENGTH, DEFAULT_WIDTH), Math.max(textBounds.getHeight(), DEFAULT_HEIGHT));
	}
}
