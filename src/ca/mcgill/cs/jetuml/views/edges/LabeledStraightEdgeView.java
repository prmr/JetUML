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
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;

/**
 * Can draw a straight edge with a label than can be obtained dynamically. 
 */
public final class LabeledStraightEdgeView extends StraightEdgeView
{	
	private final Supplier<String> aLabelSupplier;
	
	/**
	 * Creates a new view with the required LineStyle and ArrowHead and label provider.
	 * 
	 * @param pEdge The edge to wrap.
	 * @param pLineStyle The line style for the edge.
	 * @param pArrowHead The arrow head for the end of the arrow. The start is always NONE.
	 * @param pLabelSupplier A supplier for the edge's label.
	 */
	public LabeledStraightEdgeView(Edge pEdge, LineStyle pLineStyle, ArrowHead pArrowHead,
			Supplier<String> pLabelSupplier)
	{
		super(pEdge, pLineStyle, pArrowHead);
		aLabelSupplier = pLabelSupplier;
	}
	
	@Override
	public void draw(GraphicsContext pGraphics)
	{
		super.draw(pGraphics);
		Line connectionPoints = getConnectionPoints();
		drawString(pGraphics, Conversions.toPoint2D(connectionPoints.getPoint1()), 
				Conversions.toPoint2D(connectionPoints.getPoint2()), aLabelSupplier.get());
	}
	
	/**
	 * Draws a string.
	 * @param pGraphics the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private static void drawString(GraphicsContext pGraphics, Point2D pEndPoint1, Point2D pEndPoint2, String pString)
	{
		if (pString == null || pString.length() == 0)
		{
			return;
		}
		Rectangle bounds = getStringBounds(pEndPoint1, pEndPoint2, pString);
		
		Paint oldFill = pGraphics.getFill();
		VPos oldVPos = pGraphics.getTextBaseline();
		TextAlignment oldAlign = pGraphics.getTextAlign();
		pGraphics.translate(bounds.getX(), bounds.getY());
		pGraphics.setFill(Color.BLACK);
		int textX = 0;
		int textY = 0;
		
		textX = bounds.getWidth()/2;
		textY = (int) (bounds.getHeight() - textBounds(pString).getHeight()/2);
		pGraphics.setTextBaseline(VPos.CENTER);
		pGraphics.setTextAlign(TextAlignment.CENTER);

		pGraphics.fillText(pString, textX, textY);
		pGraphics.translate(-bounds.getX(), -bounds.getY()); 
		pGraphics.setFill(oldFill);
		pGraphics.setTextBaseline(oldVPos);
		pGraphics.setTextAlign(oldAlign);
	}
	
	/*
	 * Computes the extent of a string that is drawn along a line segment.
	 * @param p an endpoint of the segment along which to draw the string
	 * @param q the other endpoint of the segment along which to draw the string
	 * @param s the string to draw
	 * @param center true if the string should be centered along the segment
	 * @return the rectangle enclosing the string
	 */
	private static Rectangle getStringBounds(Point2D pEndPoint1, Point2D pEndPoint2, String pString)
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
		Point2D a = getAttachmentPoint(pEndPoint1, pEndPoint2, stringDimensions);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				(int) Math.round(stringDimensions.getWidth()), (int)Math.round(stringDimensions.getHeight()));
	}
	
	/**
	 * Computes the attachment point for drawing a string.
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param b the bounds of the string to draw
	 * @return the point at which to draw the string
	 */
	private static Point2D getAttachmentPoint(Point2D pEndPoint1, Point2D pEndPoint2, Rectangle pDimension)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.getHeight();
		Point2D attach = pEndPoint2;
		
		if (pEndPoint1.getX() > pEndPoint2.getX()) 
		{ 
			return getAttachmentPoint(pEndPoint2, pEndPoint1, pDimension); 
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
		return new Point2D(attach.getX() + xoff, attach.getY() + yoff);
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = super.getBounds();
		Line connectionPoints = getConnectionPoints();
		bounds = bounds.add(getStringBounds(Conversions.toPoint2D(connectionPoints.getPoint1()), 
				Conversions.toPoint2D(connectionPoints.getPoint2()), aLabelSupplier.get()));
		return bounds;
	}	
}