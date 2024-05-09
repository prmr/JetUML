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
package org.jetuml.rendering.nodes;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.ToolGraphics;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.StringRenderer.TextDecoration;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

/**
 * An object to render an actor in a use case diagram.
 */
public final class ActorNodeRenderer extends AbstractNodeRenderer
{
	private static final StringRenderer NAME_VIEWER = StringRenderer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	
	private static final int PADDING = 4;
	private static final int HEAD_SIZE = 16;
	private static final int BODY_SIZE = 20;
	private static final int LEG_SIZE  = 20;
	private static final int ARMS_SIZE = 24;
	private static final int WIDTH = ARMS_SIZE * 2;
	private static final int HEIGHT = HEAD_SIZE + BODY_SIZE + LEG_SIZE + PADDING * 2;
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public ActorNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public Dimension getDefaultDimension(Node pNode)
	{
		return new Dimension(WIDTH, HEIGHT);
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Dimension nameBounds = NAME_VIEWER.getDimension(((ActorNode)pNode).getName());
		return new Rectangle(
				pNode.position().x() + Math.min(0, (WIDTH - nameBounds.width()) / 2), 
				pNode.position().y(),
				Math.max(WIDTH, nameBounds.width()),
				HEIGHT + nameBounds.height()
		);
	}

	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics)
	{
		Rectangle bounds = getBounds(pElement);
		Node node = (Node) pElement;
		Dimension nameBounds = NAME_VIEWER.getDimension(((ActorNode)node).getName());
		Rectangle nameBox = new Rectangle(node.position().x() + (WIDTH - nameBounds.width()) / 2, 
				bounds.y() + HEIGHT, nameBounds.width(), nameBounds.height());
		NAME_VIEWER.draw(((ActorNode)node).getName(), pGraphics, nameBox);
		ToolGraphics.strokeSharpPath(pGraphics, createStickManPath(node), LineStyle.SOLID);
	}
	
	private static Path createStickManPath(Node pNode)
	{
		Path path = new Path();
		
		int neckX = pNode.position().x() + WIDTH / 2;
		int neckY = pNode.position().y() + HEAD_SIZE + PADDING;
		int hipX = neckX;
		int hipY = neckY + BODY_SIZE;
		float dx = (float) (LEG_SIZE / Math.sqrt(2));
		float feetX1 = hipX - dx;
		float feetX2 = hipX + dx + 1;
		float feetY  = hipY + dx + 1;
		
		path.getElements().addAll(
				new MoveTo(neckX, neckY),
				new QuadCurveTo(neckX + HEAD_SIZE / 2, neckY, neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE / 2),
				new QuadCurveTo(neckX + HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX, neckY - HEAD_SIZE),
				new QuadCurveTo(neckX - HEAD_SIZE / 2, neckY - HEAD_SIZE, neckX-HEAD_SIZE / 2, neckY - HEAD_SIZE / 2),
				new QuadCurveTo(neckX - HEAD_SIZE / 2, neckY, neckX, neckY),
				new LineTo(hipX, hipY),
				new MoveTo(neckX - ARMS_SIZE / 2, neckY + BODY_SIZE / 3),
				new LineTo(neckX + ARMS_SIZE / 2, neckY + BODY_SIZE / 3),
				new MoveTo(feetX1, feetY),
				new LineTo(hipX, hipY),
				new LineTo(feetX2, feetY));	
		return path;
	}
}
