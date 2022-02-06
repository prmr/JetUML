/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package ca.mcgill.cs.jetuml.viewers.nodes;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.NoteNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * An object to render a NoteNode.
 */
public final class NoteNodeViewer extends AbstractNodeViewer
{
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 40;
	private static final int FOLD_LENGTH = 8;
	private static final Color NOTE_COLOR = Color.color(0.9f, 0.9f, 0.6f); // Pale yellow
	private static final StringViewer NOTE_VIEWER = StringViewer.get(Alignment.TOP_LEFT, TextDecoration.PADDED);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		ToolGraphics.strokeAndFillSharpPath(pGraphics, createNotePath(pNode), NOTE_COLOR, true);
		ToolGraphics.strokeAndFillSharpPath(pGraphics, createFoldPath(pNode), Color.WHITE, false);
		NOTE_VIEWER.draw(((NoteNode)pNode).getName(), pGraphics, 
				new Rectangle(pNode.position().getX(), pNode.position().getY(), DEFAULT_WIDTH, DEFAULT_HEIGHT));
	}
	
	private Path createNotePath(Node pNode)
	{
		Path path = new Path();
		Rectangle bounds = getBounds(pNode);		
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
	private Path createFoldPath(Node pNode)
	{
		Rectangle bounds = getBounds(pNode);
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
	protected Rectangle internalGetBounds(Node pNode)
	{
		Dimension textBounds = NOTE_VIEWER.getDimension(((NoteNode)pNode).getName()); 
		return new Rectangle(pNode.position().getX(), pNode.position().getY(), 
				Math.max(textBounds.width() + FOLD_LENGTH, DEFAULT_WIDTH), Math.max(textBounds.height(), DEFAULT_HEIGHT));
	}
}
