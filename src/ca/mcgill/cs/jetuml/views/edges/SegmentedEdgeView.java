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
package ca.mcgill.cs.jetuml.views.edges;

import java.util.function.Supplier;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.geom.Conversions;
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import ca.mcgill.cs.jetuml.views.ToolGraphics;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.TextAlignment;

/**
 * Renders edges as a path consisting of straight line segments.
 */
public final class SegmentedEdgeView extends AbstractEdgeView
{
	private Supplier<LineStyle> aLineStyleSupplier;
	private Supplier<ArrowHead> aArrowStartSupplier;
	private Supplier<ArrowHead> aArrowEndSupplier;
	private Supplier<String> aStartLabelSupplier;
	private Supplier<String> aMiddleLabelSupplier;
	private Supplier<String> aEndLabelSupplier;
	private SegmentationStyle aStyle;
	
	/**
	 * @param pEdge The edge to wrap.
	 * @param pStyle The segmentation style.
	 * @param pLineStyle The line style.
	 * @param pStart The arrowhead at the start.
	 * @param pEnd The arrowhead at the start.
	 * @param pStartLabelSupplier Supplies the start label.
	 * @param pMiddleLabelSupplier Supplies the middle label.
	 * @param pEndLabelSupplier Supplies the end label
	 */
	public SegmentedEdgeView(Edge pEdge, SegmentationStyle pStyle, Supplier<LineStyle> pLineStyle, Supplier<ArrowHead> pStart, 
			Supplier<ArrowHead> pEnd, Supplier<String> pStartLabelSupplier, Supplier<String> pMiddleLabelSupplier, Supplier<String> pEndLabelSupplier)
	{
		super(pEdge);
		aStyle = pStyle;
		aLineStyleSupplier = pLineStyle;
		aArrowStartSupplier = pStart;
		aArrowEndSupplier = pEnd;
		aStartLabelSupplier = pStartLabelSupplier;
		aMiddleLabelSupplier = pMiddleLabelSupplier;
		aEndLabelSupplier = pEndLabelSupplier;
	}
	
	/**
	 * Draws a string.
	 * @param pGraphics the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private static void drawString(GraphicsContext pGraphics, Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrowHead, String pString, boolean pCenter)
	{
		if (pString == null || pString.length() == 0)
		{
			return;
		}
		Rectangle bounds = getStringBounds(pEndPoint1, pEndPoint2, pArrowHead, pString, pCenter);
		
		Paint oldFill = pGraphics.getFill();
		VPos oldVPos = pGraphics.getTextBaseline();
		TextAlignment oldAlign = pGraphics.getTextAlign();
		pGraphics.translate(bounds.getX(), bounds.getY());
		pGraphics.setFill(Color.BLACK);
		int textX = 0;
		int textY = 0;
		if(pCenter) 
		{
			textX = bounds.getWidth()/2;
			textY = (int) (bounds.getHeight() - textBounds(pString).getHeight()/2);
			pGraphics.setTextBaseline(VPos.CENTER);
			pGraphics.setTextAlign(TextAlignment.CENTER);
		}
		pGraphics.fillText(pString, textX, textY);
		pGraphics.translate(-bounds.getX(), -bounds.getY()); 
		pGraphics.setFill(oldFill);
		pGraphics.setTextBaseline(oldVPos);
		pGraphics.setTextAlign(oldAlign);
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		Point2D[] points = getPoints();		
		ToolGraphics.strokeSharpPath(pGraphics, getSegmentPath(), aLineStyleSupplier.get());
		aArrowStartSupplier.get().view().draw(pGraphics, points[1], points[0]);
		aArrowEndSupplier.get().view().draw(pGraphics, points[points.length - 2], points[points.length - 1]);

		drawString(pGraphics, points[1], points[0], aArrowStartSupplier.get(), aStartLabelSupplier.get(), false);
		drawString(pGraphics, points[points.length / 2 - 1], points[points.length / 2], null, aMiddleLabelSupplier.get(), true);
		drawString(pGraphics, points[points.length - 2], points[points.length - 1], aArrowEndSupplier.get(), aEndLabelSupplier.get(), false);
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
			ArrowHead pArrow, Rectangle pDimension, boolean pCenter)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.getHeight();
		Point2D attach = pEndPoint2;
		if (pCenter)
		{
			if (pEndPoint1.getX() > pEndPoint2.getX()) 
			{ 
				return getAttachmentPoint(pEndPoint2, pEndPoint1, pArrow, pDimension, pCenter); 
			}
			attach = new Point2D((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
					(pEndPoint1.getY() + pEndPoint2.getY()) / 2);
			if (pEndPoint1.getY() < pEndPoint2.getY())
			{
				yoff =  -gap-pDimension.getHeight();
			}
			else if (pEndPoint1.getY() == pEndPoint2.getY())
			{
				xoff = -pDimension.getWidth() / 2;
			}
			else
			{
				yoff = gap;
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
			if(pArrow != null)
			{
				Bounds arrowBounds = pArrow.view().getPath(pEndPoint1, pEndPoint2).getBoundsInLocal();
				if(pEndPoint1.getX() < pEndPoint2.getX())
				{
					xoff -= arrowBounds.getWidth();
				}
				else
				{
					xoff += arrowBounds.getWidth();
				}
			}
		}
		return new Point2D(attach.getX() + xoff, attach.getY() + yoff);
	}
	
	private Point2D[] getPoints()
	{
		return aStyle.getPath(edge(), edge().getDiagram());
	}

	@Override
	public Line getConnectionPoints()
	{
		Point2D[] points = getPoints();
		return new Line(Conversions.toPoint(points[0]), 
				Conversions.toPoint(points[points.length - 1]));
	}
	
	@Override
	protected Shape getShape()
	{
		Path path = getSegmentPath();
		Point2D[] points = getPoints();
		path.getElements().addAll(aArrowStartSupplier.get().view().getPath(points[1], points[0]).getElements());
		path.getElements().addAll(aArrowEndSupplier.get().view().getPath(points[points.length - 2], points[points.length - 1]).getElements());
		return path;
	}

	private Path getSegmentPath()
	{
		Point2D[] points = getPoints();
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
			ArrowHead pArrow, String pString, boolean pCenter)
	{
		if (pString == null || pString.equals(""))
		{
			return new Rectangle((int)Math.round(pEndPoint2.getX()), 
					(int)Math.round(pEndPoint2.getY()), 0, 0);
		}
		
		Bounds bounds = textBounds(pString);
		int width = (int) Math.round(bounds.getWidth());
		int height = (int) Math.round(bounds.getHeight());
		Rectangle stringDimensions = new Rectangle(0, 0, width, height);
		Point2D a = getAttachmentPoint(pEndPoint1, pEndPoint2, pArrow, stringDimensions, pCenter);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				(int) Math.round(stringDimensions.getWidth()), (int)Math.round(stringDimensions.getHeight()));
	}
	
	@Override
	public Rectangle getBounds()
	{
		Point2D[] points = getPoints();
		Rectangle bounds = super.getBounds();
		bounds = bounds.add(getStringBounds(points[1], points[0], aArrowStartSupplier.get(), aStartLabelSupplier.get(), false));
		bounds = bounds.add(getStringBounds(points[points.length / 2 - 1], points[points.length / 2], null, aMiddleLabelSupplier.get(), true));
		bounds = bounds.add(getStringBounds(points[points.length - 2], points[points.length - 1], 
				aArrowEndSupplier.get(), aEndLabelSupplier.get(), false));
		return bounds;
	}
}
