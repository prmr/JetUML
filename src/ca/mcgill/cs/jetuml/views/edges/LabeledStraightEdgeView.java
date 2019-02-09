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
import ca.mcgill.cs.jetuml.geom.Line;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.views.ArrowHead;
import ca.mcgill.cs.jetuml.views.LineStyle;
import javafx.geometry.Bounds;
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
		String label = aLabelSupplier.get();
		if( label.length() > 0 )
		{
			drawString(pGraphics, connectionPoints.getPoint1(), connectionPoints.getPoint2(), label);
		}
	}
	
	private static void drawString(GraphicsContext pGraphics, Point pStart, Point pEnd, String pString)
	{
		assert pString != null && pString.length() > 0;

		Rectangle bounds = getStringBounds(pStart, pEnd, pString);
		
		Paint oldFill = pGraphics.getFill();
		VPos oldVPos = pGraphics.getTextBaseline();
		TextAlignment oldAlign = pGraphics.getTextAlign();
		pGraphics.translate(bounds.getX(), bounds.getY());
		pGraphics.setFill(Color.BLACK);
			
		int textX = bounds.getWidth()/2;
		int textY = (int) (bounds.getHeight() - textBounds(pString).getHeight()/2);
		pGraphics.setTextBaseline(VPos.CENTER);
		pGraphics.setTextAlign(TextAlignment.CENTER);

		pGraphics.fillText(pString, textX, textY);
		pGraphics.translate(-bounds.getX(), -bounds.getY()); 
		pGraphics.setFill(oldFill);
		pGraphics.setTextBaseline(oldVPos);
		pGraphics.setTextAlign(oldAlign);
	}
	
	private static Rectangle getStringBounds(Point pEndPoint1, Point pEndPoint2, String pString)
	{
		assert pString != null && pString.length() > 0;
		Bounds bounds = textBounds(pString);
		int width = (int) Math.round(bounds.getWidth());
		int height = (int) Math.round(bounds.getHeight());
		Rectangle stringDimensions = new Rectangle(0, 0, width, height);
		Point a = getAttachmentPoint(pEndPoint1, pEndPoint2, stringDimensions);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				(int) Math.round(stringDimensions.getWidth()), (int)Math.round(stringDimensions.getHeight()));
	}
	
	private static Point getAttachmentPoint(Point pEndPoint1, Point pEndPoint2, Rectangle pDimension)
	{    
		final int gap = 3;
		int xoff = gap;
		int yoff = -gap - pDimension.getHeight();
		Point attach = pEndPoint2;
		
		if (pEndPoint1.getX() > pEndPoint2.getX()) 
		{ 
			return getAttachmentPoint(pEndPoint2, pEndPoint1, pDimension); 
		}
		attach = new Point((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
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
		return new Point(attach.getX() + xoff, attach.getY() + yoff);
	}
	
	@Override
	public Rectangle getBounds()
	{
		Rectangle bounds = super.getBounds();
		Line connectionPoints = getConnectionPoints();
		String label = aLabelSupplier.get();
		if( label.length() > 0 )
		{
			bounds = bounds.add(getStringBounds(connectionPoints.getPoint1(), 
					connectionPoints.getPoint2(), label));
		}
		return bounds;
	}	
}