/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2025 by McGill University.
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
package org.jetuml.rendering.nodes;

import java.util.Optional;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.Alignment;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;

import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

/**
 * An object to render a NoteNode.
 */
public final class NoteNodeRenderer extends AbstractNodeRenderer
{
	private static final int DEFAULT_WIDTH = 60;
	private static final int DEFAULT_HEIGHT = 40;
	private static final int FOLD_LENGTH = 8;
	private static final int PADDING = 3;
	private static final StringRenderer NOTE_VIEWER = new StringRenderer(Alignment.LEFT);
	
	/**
	 * @param pParent Renderer for the parent diagram.
	 */
	public NoteNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		Node node = (Node) pElement;
		pContext.drawClosedPath(createNotePath(node), ColorScheme.get().note(), ColorScheme.get().stroke(),  
				Optional.of(ColorScheme.get().dropShadow()));
		pContext.drawClosedPath(createFoldPath(node), Color.WHITE, ColorScheme.get().stroke(), Optional.empty());
		NOTE_VIEWER.draw(((NoteNode)node).getName(), 
				new Rectangle(node.position().x() + PADDING, 
						      node.position().y() + PADDING, 
						      DEFAULT_WIDTH - FOLD_LENGTH - PADDING * 2, 
						      DEFAULT_HEIGHT - FOLD_LENGTH - PADDING -2), pContext);
	}
	
	private Path createNotePath(Node pNode)
	{
		Path path = new Path();
		Rectangle bounds = getBounds(pNode);		
		path.getElements().addAll(
				new MoveTo(bounds.x(), bounds.y()),
				new LineTo(bounds.maxX() - FOLD_LENGTH, bounds.y()),
				new LineTo(bounds.maxX(), bounds.y() + FOLD_LENGTH),
				new LineTo(bounds.maxX(), bounds.maxY()),
				new LineTo(bounds.x(), bounds.maxY()),
				new LineTo(bounds.x(), bounds.y()));
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
				new MoveTo(bounds.maxX() - FOLD_LENGTH, bounds.y()),
				new LineTo(bounds.maxX() - FOLD_LENGTH, bounds.y() + FOLD_LENGTH),
				new LineTo(bounds.maxX(), bounds.y() + FOLD_LENGTH),
				new LineTo(bounds.maxX() - FOLD_LENGTH, bounds.y())
		);
		return path;
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Dimension textDimension = NOTE_VIEWER.getDimension(((NoteNode)pNode).getName());
		return new Rectangle(pNode.position().x(), pNode.position().y(), 
				Math.max(textDimension.width() + FOLD_LENGTH + PADDING * 2, DEFAULT_WIDTH), 
				Math.max(textDimension.height() + PADDING * 2, DEFAULT_HEIGHT));
	}
}
