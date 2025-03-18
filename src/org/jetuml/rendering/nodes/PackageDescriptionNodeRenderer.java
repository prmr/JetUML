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

import static org.jetuml.geom.GeomUtils.max;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.AbstractPackageNode;
import org.jetuml.diagram.nodes.PackageDescriptionNode;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.TextPosition;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.GraphicsRenderingContext;
import org.jetuml.rendering.RenderingContext;
import org.jetuml.rendering.StringRenderer;

import javafx.scene.canvas.Canvas;

/**
 * An object to render a package in a class diagram.
 */
public final class PackageDescriptionNodeRenderer extends AbstractPackageNodeRenderer
{
	private static final StringRenderer CONTENT_RENDERER = new StringRenderer(TextPosition.TOP_CENTER);
	
	/**
	 * @param pParent Renderer of the parent diagram.
	 */
	public PackageDescriptionNodeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
	@Override
	public void draw(DiagramElement pElement, RenderingContext pContext)
	{
		super.draw(pElement, pContext);
		Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pElement);
		String content = ((PackageDescriptionNode)pElement).getContents();
		CONTENT_RENDERER.draw(((PackageDescriptionNode)pElement).getContents(), 
				bottomBounds.centerSlice(CONTENT_RENDERER.getDimension(content).height()), 
				pContext);
	}
	
	@Override
	protected Rectangle getBottomBounds(AbstractPackageNode pNode)
	{
		Dimension contentsBounds = CONTENT_RENDERER.getDimension(((PackageDescriptionNode)pNode).getContents());
		int width = max(contentsBounds.width() + 2 * PADDING, DEFAULT_WIDTH);
		int height = max(contentsBounds.height() + 2 * PADDING, DEFAULT_BOTTOM_HEIGHT);
		
		Dimension topDimension = getTopDimension(pNode);
		width = max( width, topDimension.width()+ (DEFAULT_WIDTH - DEFAULT_TOP_WIDTH));
		
		return new Rectangle(pNode.position().x(), pNode.position().y() + topDimension.height(), 
				width, height);
	}
	
	/*
	 * Custom version to distinguish package descriptions from package nodes.
	 */
	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement)
	{
		assert pElement instanceof AbstractPackageNode;
		Canvas icon = super.createIcon(pDiagramType, pElement);
		CONTENT_RENDERER.draw("description", 
				getBottomBounds((AbstractPackageNode)pElement).centerSlice(CONTENT_RENDERER.lineHeight()), 
				new GraphicsRenderingContext(icon.getGraphicsContext2D()));
		return icon;
	}
}
