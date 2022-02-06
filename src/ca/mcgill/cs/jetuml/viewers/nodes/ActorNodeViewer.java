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
import ca.mcgill.cs.jetuml.diagram.nodes.ActorNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

/**
 * An object to render an actor in a use case diagram.
 */
public final class ActorNodeViewer extends AbstractNodeViewer
{
	private static final StringViewer NAME_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	
	private static final int PADDING = 4;
	private static final int HEAD_SIZE = 16;
	private static final int BODY_SIZE = 20;
	private static final int LEG_SIZE  = 20;
	private static final int ARMS_SIZE = 24;
	private static final int WIDTH = ARMS_SIZE * 2;
	private static final int HEIGHT = HEAD_SIZE + BODY_SIZE + LEG_SIZE + PADDING * 2;
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Dimension nameBounds = NAME_VIEWER.getDimension(((ActorNode)pNode).getName());
		return new Rectangle(
				pNode.position().getX() + Math.min(0, (WIDTH - nameBounds.width()) / 2), 
				pNode.position().getY(),
				Math.max(WIDTH, nameBounds.width()),
				HEIGHT + nameBounds.height()
		);
	}

	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{	
		Rectangle bounds = getBounds(pNode);
		Dimension nameBounds = NAME_VIEWER.getDimension(((ActorNode)pNode).getName());
		Rectangle nameBox = new Rectangle(pNode.position().getX() + (WIDTH - nameBounds.width()) / 2, 
				bounds.getY() + HEIGHT, nameBounds.width(), nameBounds.height());
		NAME_VIEWER.draw(((ActorNode)pNode).getName(), pGraphics, nameBox);
		ToolGraphics.strokeSharpPath(pGraphics, createStickManPath(pNode), LineStyle.SOLID);
	}
	
	private static Path createStickManPath(Node pNode)
	{
		Path path = new Path();
		
		int neckX = pNode.position().getX() + WIDTH / 2;
		int neckY = pNode.position().getY() + HEAD_SIZE + PADDING;
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
