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

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.Node;
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.GeomUtils;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.Alignment;
import org.jetuml.gui.ColorScheme;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurveTo;

/**
 * An object to render an actor in a use case diagram.
 */
public final class ActorNodeRenderer extends AbstractNodeRenderer
{
	private static final StringRenderer LABEL_RENDERER = new StringRenderer(Alignment.CENTER);
	
	private static final int HEAD_SIZE = 16;
	private static final int BODY_SIZE = 20;
	private static final int LEG_SIZE  = 20;
	private static final int ARMS_SIZE = 24;
	private static final int WIDTH = GeomUtils.round(LEG_SIZE / Math.sqrt(2)) * 2;
	private static final int HEIGHT = HEAD_SIZE + BODY_SIZE + GeomUtils.round(LEG_SIZE / Math.sqrt(2));
	
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
		Rectangle bounds = internalGetBounds(pNode);
		return new Dimension(bounds.width(), bounds.height());
	}
	
	@Override
	protected Rectangle internalGetBounds(Node pNode)
	{
		Dimension textDimension = LABEL_RENDERER.getDimension(((ActorNode)pNode).getName());
		Rectangle bounds = 
		new Rectangle(
				pNode.position().x() + Math.min(0, (WIDTH - textDimension.width()) / 2), 
				pNode.position().y(),
				Math.max(WIDTH, textDimension.width()),
				HEIGHT + textDimension.height());
		return bounds;
	}

	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		Rectangle bounds = getBounds(pElement);
		Node node = (Node) pElement;
		Dimension textDimension = LABEL_RENDERER.getDimension(((ActorNode)node).getName());
		Rectangle nameBox = new Rectangle(node.position().x() + (WIDTH - textDimension.width()) / 2, 
				bounds.y() + HEIGHT, textDimension.width(), textDimension.height());
		LABEL_RENDERER.draw(((ActorNode)node).getName(), nameBox, pContext);
		pContext.strokePath(createStickManPath(node), ColorScheme.get().stroke(), LineStyle.SOLID);
	}
	
	private static Path createStickManPath(Node pNode)
	{
		Path path = new Path();
		
		int neckX = pNode.position().x() + WIDTH / 2;
		int neckY = pNode.position().y() + HEAD_SIZE;
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
