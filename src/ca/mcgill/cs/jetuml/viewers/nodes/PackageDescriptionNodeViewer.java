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

import static ca.mcgill.cs.jetuml.geom.GeomUtils.max;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.nodes.AbstractPackageNode;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageDescriptionNode;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import ca.mcgill.cs.jetuml.viewers.StringViewer.TextDecoration;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * An object to render a package in a class diagram.
 */
public final class PackageDescriptionNodeViewer extends AbstractPackageNodeViewer
{
	private static final StringViewer CONTENTS_VIEWER = StringViewer.get(Alignment.CENTER_CENTER, TextDecoration.PADDED);
	
	@Override
	public void draw(Node pNode, GraphicsContext pGraphics)
	{
		super.draw(pNode, pGraphics);
		Rectangle bottomBounds = getBottomBounds((AbstractPackageNode)pNode);
		CONTENTS_VIEWER.draw(((PackageDescriptionNode)pNode).getContents(), pGraphics, new Rectangle(bottomBounds.getX() + NAME_GAP, 
				bottomBounds.getY(), bottomBounds.getWidth(), bottomBounds.getHeight()));
	}
	
	@Override
	protected Rectangle getBottomBounds(AbstractPackageNode pNode)
	{
		Dimension contentsBounds = CONTENTS_VIEWER.getDimension(((PackageDescriptionNode)pNode).getContents());
		int width = max(contentsBounds.width() + 2 * PADDING, DEFAULT_WIDTH);
		int height = max(contentsBounds.height() + 2 * PADDING, DEFAULT_BOTTOM_HEIGHT);
		
		Dimension topDimension = getTopDimension(pNode);
		width = max( width, topDimension.width()+ (DEFAULT_WIDTH - DEFAULT_TOP_WIDTH));
		
		return new Rectangle(pNode.position().getX(), pNode.position().getY() + topDimension.height(), 
				width, height);
	}
	
	/*
	 * Custom version to distinguish package descriptions from package nodes.
	 */
	@Override
	public Canvas createIcon(Node pNode)
	{
		assert pNode instanceof AbstractPackageNode;
		Canvas icon = super.createIcon(pNode);
		CONTENTS_VIEWER.draw("description", icon.getGraphicsContext2D(), getBottomBounds((AbstractPackageNode)pNode));
		return icon;
	}
}
