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
package ca.mcgill.cs.jetuml.viewers.edges;

import java.util.function.Function;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.ArrowHead;
import ca.mcgill.cs.jetuml.viewers.LineStyle;
import ca.mcgill.cs.jetuml.viewers.StringViewer;
import ca.mcgill.cs.jetuml.viewers.ToolGraphics;
import ca.mcgill.cs.jetuml.viewers.StringViewer.Alignment;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * Renders edges as a path consisting of straight line segments.
 */
public class SegmentedEdgeViewer extends AbstractEdgeViewer
{
	private static final StringViewer TOP_CENTERED_STRING_VIEWER = StringViewer.get(Alignment.TOP_CENTER);
	private static final StringViewer BOTTOM_CENTERED_STRING_VIEWER = StringViewer.get(Alignment.BOTTOM_CENTER);
	private static final StringViewer LEFT_JUSTIFIED_STRING_VIEWER = StringViewer.get(Alignment.TOP_LEFT);
			
	private Function<Edge, LineStyle> aLineStyleExtractor;
	private Function<Edge, ArrowHead> aArrowStartExtractor;
	private Function<Edge, ArrowHead> aArrowEndExtractor;
	private Function<Edge, String> aStartLabelExtractor;
	private Function<Edge, String> aMiddleLabelExtractor;
	private Function<Edge, String> aEndLabelExtractor;
	private SegmentationStyle aStyle;
	
	/**
	 * @param pStyle The segmentation style.
	 * @param pLineStyle The line style.
	 * @param pStart The arrowhead at the start.
	 * @param pEnd The arrowhead at the start.
	 * @param pStartLabelExtractor Extracts the start label from the edge
	 * @param pMiddleLabelExtractor Extracts the middle label from the edge
	 * @param pEndLabelExtractor Extracts the end label from the edge
	 */
	public SegmentedEdgeViewer(SegmentationStyle pStyle, Function<Edge, LineStyle> pLineStyle, Function<Edge, ArrowHead> pStart, 
			Function<Edge, ArrowHead> pEnd, Function<Edge, String> pStartLabelExtractor, 
			Function<Edge, String> pMiddleLabelExtractor, Function<Edge, String> pEndLabelExtractor)
	{
		aStyle = pStyle;
		aLineStyleExtractor = pLineStyle;
		aArrowStartExtractor = pStart;
		aArrowEndExtractor = pEnd;
		aStartLabelExtractor = pStartLabelExtractor;
		aMiddleLabelExtractor = pMiddleLabelExtractor;
		aEndLabelExtractor = pEndLabelExtractor;
	}
	
	/**
	 * Draws a string.
	 * @param pGraphics the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private void drawString(GraphicsContext pGraphics, Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrowHead, String pString, boolean pCenter, boolean pIsStepUp)
	{
		if (pString == null || pString.length() == 0)
		{
			return;
		}
		String label = wrapLabel(pString, pEndPoint1, pEndPoint2);
		Rectangle bounds = getStringBounds(pEndPoint1, pEndPoint2, pArrowHead, label, pCenter, pIsStepUp);
		if(pCenter) 
		{
			if ( pEndPoint2.getY() >= pEndPoint1.getY() )
			{
				TOP_CENTERED_STRING_VIEWER.draw(label, pGraphics, bounds);
			}
			else
			{
				BOTTOM_CENTERED_STRING_VIEWER.draw(label, pGraphics, bounds);
			}
		}
		else
		{
			LEFT_JUSTIFIED_STRING_VIEWER.draw(label, pGraphics, bounds);
		}
	}
	
	private String wrapLabel(String pString, Point2D pEndPoint1, Point2D pEndPoint2) 
	{
		int distanceInX = (int)Math.abs(pEndPoint1.getX() - pEndPoint2.getX());
		int distanceInY = (int)Math.abs(pEndPoint1.getY() - pEndPoint2.getY());
		return super.wrapLabel(pString, distanceInX, distanceInY);
	}

	@Override
	public void draw(Edge pEdge, GraphicsContext pGraphics)
	{
		Point2D[] points = getPoints(pEdge);		
		ToolGraphics.strokeSharpPath(pGraphics, getSegmentPath(pEdge), aLineStyleExtractor.apply(pEdge));
		aArrowStartExtractor.apply(pEdge).view().draw(pGraphics, 
				Conversions.toPoint(points[1]), 
				Conversions.toPoint(points[0]));
		
		aArrowEndExtractor.apply(pEdge).view().draw(pGraphics, 
				Conversions.toPoint(points[points.length - 2]), 
				Conversions.toPoint(points[points.length - 1]));
		drawString(pGraphics, points[1], points[0], aArrowStartExtractor.apply(pEdge), 
				aStartLabelExtractor.apply(pEdge), false, isStepUp(pEdge));
		drawString(pGraphics, points[points.length / 2 - 1], points[points.length / 2], null, 
				aMiddleLabelExtractor.apply(pEdge), true, isStepUp(pEdge));
		drawString(pGraphics, points[points.length - 2], points[points.length - 1], 
				aArrowEndExtractor.apply(pEdge), aEndLabelExtractor.apply(pEdge), false, isStepUp(pEdge));
	}
	
	/**
	 * Computes the attachment point for drawing a string.
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param b the bounds of the string to draw
	 * @param pCenter true if the string should be centered along the segment
	 * @return the point at which to draw the string
	 */
	private static Point2D getAttachmentPoint(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, Rectangle pDimension, boolean pCenter, boolean pIsStepUp)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.getHeight();
		Point2D attach = pEndPoint2;
		if (pCenter)
		{
			if (pEndPoint1.getX() > pEndPoint2.getX()) 
			{ 
				return getAttachmentPoint(pEndPoint2, pEndPoint1, pArrow, pDimension, pCenter, pIsStepUp); 
			}
			attach = new Point2D((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
					(pEndPoint1.getY() + pEndPoint2.getY()) / 2);
			if (pEndPoint1.getX() == pEndPoint2.getX() && pIsStepUp)
			{
				yoff = gap;
			}
			else if (pEndPoint1.getX() == pEndPoint2.getX() && !pIsStepUp)
			{
				yoff =  -gap-pDimension.getHeight();
			}
			else if (pEndPoint1.getY() == pEndPoint2.getY())
			{
				if (pDimension.getWidth() > Math.abs(pEndPoint1.getX() - pEndPoint2.getX()))
				{
					attach = new Point2D(pEndPoint2.getX() + (pDimension.getWidth() / 2) + gap, 
							(pEndPoint1.getY() + pEndPoint2.getY()) / 2);
				}
				xoff = -pDimension.getWidth() / 2;
			}
		}
		else 
		{
			if(pEndPoint1.getX() < pEndPoint2.getX())
			{
				xoff = -gap - pDimension.getWidth();
			}
			if(pEndPoint1.getY() > pEndPoint2.getY())
			{
				yoff = gap;
			}
			if(pArrow != null && pArrow != ArrowHead.NONE)
			{
				Bounds arrowBounds = pArrow.view().getPath(
						Conversions.toPoint(pEndPoint1), 
						Conversions.toPoint(pEndPoint2)).getBoundsInLocal();
				if(pEndPoint1.getY() == pEndPoint2.getY())
				{
					yoff -= arrowBounds.getHeight() / 2;
				}
				else if(pEndPoint1.getX() == pEndPoint2.getX())
				{
					xoff += arrowBounds.getWidth() / 2;
				}
			}
		}
		return new Point2D(attach.getX() + xoff, attach.getY() + yoff);
	}
	
	private Point2D[] getPoints(Edge pEdge)
	{
		return aStyle.getPath(pEdge);
	}

	@Override
	public Line getConnectionPoints(Edge pEdge)
	{
		Point2D[] points = getPoints(pEdge);
		return new Line(Conversions.toPoint(points[0]), 
				Conversions.toPoint(points[points.length - 1]));
	}
	
	@Override
	protected Shape getShape(Edge pEdge)
	{
		Path path = getSegmentPath(pEdge);
		Point2D[] points = getPoints(pEdge);
		path.getElements().addAll(aArrowStartExtractor.apply(pEdge).view().getPath(
				Conversions.toPoint(points[1]),
				Conversions.toPoint(points[0])).getElements());
		path.getElements().addAll(aArrowEndExtractor.apply(pEdge).view().getPath(
				Conversions.toPoint(points[points.length - 2]), 
				Conversions.toPoint(points[points.length - 1])).getElements());
		return path;
	}

	private Path getSegmentPath(Edge pEdge)
	{
		Point2D[] points = getPoints(pEdge);
		Path path = new Path();
		Point2D p = points[points.length - 1];
		MoveTo moveTo = new MoveTo((float) p.getX(), (float) p.getY());
		path.getElements().add(moveTo);
		for(int i = points.length - 2; i >= 0; i--)
		{
			p = points[i];
			LineTo lineTo = new LineTo((float) p.getX(), (float) p.getY());
			path.getElements().add(lineTo);
		}
		return path;
	}
	
	/*
	 * Computes the extent of a string that is drawn along a line segment.
	 * @param p an endpoint of the segment along which to draw the string
	 * @param q the other endpoint of the segment along which to draw the string
	 * @param s the string to draw
	 * @param center true if the string should be centered along the segment
	 * @return the rectangle enclosing the string
	 */
	private static Rectangle getStringBounds(Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrow, String pString, boolean pCenter, boolean pIsStepUp)
	{
		if (pString == null || pString.isEmpty())
		{
			return new Rectangle((int)Math.round(pEndPoint2.getX()), 
					(int)Math.round(pEndPoint2.getY()), 0, 0);
		}
		
		Dimension textDimensions = textDimensions(pString);
		Rectangle stringDimensions = new Rectangle(0, 0, textDimensions.width(), textDimensions.height());
		Point2D a = getAttachmentPoint(pEndPoint1, pEndPoint2, pArrow, stringDimensions, pCenter, pIsStepUp);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				Math.round(stringDimensions.getWidth()), Math.round(stringDimensions.getHeight()));
	}
	
	@Override
	public Rectangle getBounds(Edge pEdge)
	{
		Point2D[] points = getPoints(pEdge);
		Rectangle bounds = super.getBounds(pEdge);
		bounds = bounds.add(getStringBounds(points[1], points[0], 
				aArrowStartExtractor.apply(pEdge), aStartLabelExtractor.apply(pEdge), false, isStepUp(pEdge)));
		bounds = bounds.add(getStringBounds(points[points.length / 2 - 1], 
				points[points.length / 2], null, aMiddleLabelExtractor.apply(pEdge), true, isStepUp(pEdge)));
		bounds = bounds.add(getStringBounds(points[points.length - 2], points[points.length - 1], 
				aArrowEndExtractor.apply(pEdge), aEndLabelExtractor.apply(pEdge), false, isStepUp(pEdge)));
		return bounds;
	}
	
	private static boolean isStepUp(Edge pEdge) 
	{
		Point point1 = EdgeViewerRegistry.getConnectionPoints(pEdge).getPoint1();
		Point point2 = EdgeViewerRegistry.getConnectionPoints(pEdge).getPoint2();
		return point1.getX() < point2.getX() && point1.getY() > point2.getY() || 
				point1.getX() > point2.getX() && point1.getY() < point2.getY();
	}

	@Override
	public Canvas createIcon(Edge pEdge) 
	{
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(OFFSET, OFFSET), new LineTo(BUTTON_SIZE-OFFSET, BUTTON_SIZE-OFFSET));
		ToolGraphics.strokeSharpPath(canvas.getGraphicsContext2D(), path, aLineStyleExtractor.apply(pEdge));
		aArrowEndExtractor.apply(pEdge).view().draw(canvas.getGraphicsContext2D(), 
				new Point(OFFSET, OFFSET), new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET));
		aArrowStartExtractor.apply(pEdge).view().draw(canvas.getGraphicsContext2D(), 
				new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET), new Point(OFFSET, OFFSET));
		return canvas;
	}
}
