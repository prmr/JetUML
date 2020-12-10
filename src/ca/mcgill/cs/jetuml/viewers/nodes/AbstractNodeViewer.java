/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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

import java.util.Optional;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Direction;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Basic services for drawing nodes.
 */
public abstract class AbstractNodeViewer implements NodeViewer
{
	public static final int BUTTON_SIZE = 25;
	public static final int OFFSET = 3;
	
	/* 
	 * The default behavior for containment is to return true if the point is
	 * within the bounding box of the node view.
	 * @see ca.mcgill.cs.jetuml.views.DiagramElementView#contains(ca.mcgill.cs.jetuml.geom.Point)
	 */
	@Override
	public boolean contains(Node pNode, Point pPoint)
	{
		return getBounds(pNode).contains(pPoint);
	}
	
	private static Optional<Point> computeConnectionPointForCanonicalDirection(Rectangle pBounds, Direction pDirection)
	{
		Optional<Point> result = Optional.empty();
		if( pDirection == Direction.NORTH )
		{
			result = Optional.of(new Point(pBounds.getCenter().getX(), pBounds.getY()));
		}
		else if( pDirection == Direction.SOUTH )
		{
			result =  Optional.of(new Point(pBounds.getCenter().getX(), pBounds.getMaxY()));
		}
		else if( pDirection == Direction.EAST)
		{
			result = Optional.of(new Point(pBounds.getMaxX(), pBounds.getCenter().getY()));
		}
		else if( pDirection == Direction.WEST )
		{
			result = Optional.of(new Point(pBounds.getX(), pBounds.getCenter().getY()));
		}
		return result;
	}
	
	/* 
	 * The default behavior is to returns a point on the bounds of the node that intersects
	 * the side of the node at the point where a line in pDirection originating from the center
	 * intersects it.
	 */
	@Override
	public Point getConnectionPoint(Node pNode, Direction pDirection)
	{
		final Rectangle bounds = getBounds(pNode);
		
		Optional<Point> result = computeConnectionPointForCanonicalDirection(bounds, pDirection);
		if( result.isPresent() )
		{
			return result.get();
		}
		
		Direction diagonalNE = Direction.fromLine(bounds.getCenter(), new Point(bounds.getMaxX(), bounds.getY()));
		Direction diagonalSE = Direction.fromLine(bounds.getCenter(), new Point(bounds.getMaxX(), bounds.getMaxY()));
		Direction diagonalSW = diagonalNE.mirrored();
		Direction diagonalNW = diagonalSE.mirrored();
		
		if( pDirection.isBetween(diagonalNE, diagonalSE))
		{
			int offset = length(pDirection.asAngle() - Direction.EAST.asAngle(), bounds.getWidth()/2);
			return new Point(bounds.getMaxX(), bounds.getCenter().getY() + offset);
		}
		else if( pDirection.isBetween(diagonalSE, diagonalSW))
		{
			int offset = length(pDirection.asAngle() - Direction.SOUTH.asAngle(), bounds.getHeight()/2);
			return new Point(bounds.getCenter().getX() - offset, bounds.getMaxY());
		}
		else if( pDirection.isBetween(diagonalSW, diagonalNW))
		{
			int offset = length(pDirection.asAngle() - Direction.WEST.asAngle(), bounds.getWidth()/2);
			return new Point(bounds.getX(), bounds.getCenter().getY() - offset);
		}
		else
		{
			final int angleS = 360;
			int offset = length(pDirection.asAngle() - angleS, bounds.getHeight()/2);
			return new Point(bounds.getCenter().getX() + offset, bounds.getY());
		}
	}
	
	@Override
	public void drawSelectionHandles(Node pNode, GraphicsContext pGraphics)
	{
		ToolGraphics.drawHandles(pGraphics, getBounds(pNode));		
	}
	
	@Override
	public Canvas createIcon(Node pNode)
	{
		Rectangle bounds = getBounds(pNode);
		int width = bounds.getWidth();
		int height = bounds.getHeight();
		double scaleX = (BUTTON_SIZE - OFFSET)/ (double) width;
		double scaleY = (BUTTON_SIZE - OFFSET)/ (double) height;
		double scale = Math.min(scaleX, scaleY);
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		GraphicsContext graphics = canvas.getGraphicsContext2D();
		graphics.scale(scale, scale);
		graphics.translate(Math.max((height - width) / 2, 0), Math.max((width - height) / 2, 0));
		graphics.setFill(Color.WHITE);
		graphics.setStroke(Color.BLACK);
		draw(pNode, canvas.getGraphicsContext2D());
		return canvas;
	}
	
	private static int length(int pAngleInDegrees, int pOpposingSide)
	{
		return (int) Math.round(pOpposingSide * Math.tan(Math.toRadians(pAngleInDegrees)));
	}
}
