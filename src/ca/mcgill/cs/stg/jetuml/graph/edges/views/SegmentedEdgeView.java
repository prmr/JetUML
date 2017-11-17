package ca.mcgill.cs.stg.jetuml.graph.edges.views;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.function.Supplier;

import javax.swing.JLabel;

import ca.mcgill.cs.stg.jetuml.framework.ArrowHead;
import ca.mcgill.cs.stg.jetuml.framework.LineStyle;
import ca.mcgill.cs.stg.jetuml.framework.SegmentationStyle;
import ca.mcgill.cs.stg.jetuml.geom.Conversions;
import ca.mcgill.cs.stg.jetuml.geom.Line;
import ca.mcgill.cs.stg.jetuml.geom.Rectangle;
import ca.mcgill.cs.stg.jetuml.graph.edges.Edge;

/**
 * Renders edges as a straight line connected to center of nodes.
 */
public class SegmentedEdgeView extends AbstractEdgeView
{
	private static JLabel label = new JLabel();
	
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
	 * @param pGraphics2D the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private static void drawString(Graphics2D pGraphics2D, Point2D pEndPoint1, Point2D pEndPoint2, 
			ArrowHead pArrowHead, String pString, boolean pCenter)
	{
		if (pString == null || pString.length() == 0)
		{
			return;
		}
		label.setText(toHtml(pString));
		label.setFont(pGraphics2D.getFont());
		Dimension dimensions = label.getPreferredSize();      
		label.setBounds(0, 0, dimensions.width, dimensions.height);

		Rectangle bounds = getStringBounds(pEndPoint1, pEndPoint2, pArrowHead, pString, pCenter);

		pGraphics2D.translate(bounds.getX(), bounds.getY());
		label.paint(pGraphics2D);
		pGraphics2D.translate(-bounds.getX(), -bounds.getY());        
	}
	
	@Override
	public void draw(Graphics2D pGraphics2D)
	{
		Point2D[] points = getPoints();

		Stroke oldStroke = pGraphics2D.getStroke();
		pGraphics2D.setStroke(aLineStyleSupplier.get().getStroke());
		pGraphics2D.draw(getSegmentPath());
		pGraphics2D.setStroke(oldStroke);
		aArrowStartSupplier.get().draw(pGraphics2D, points[1], points[0]);
		aArrowEndSupplier.get().draw(pGraphics2D, points[points.length - 2], points[points.length - 1]);

		drawString(pGraphics2D, points[1], points[0], aArrowStartSupplier.get(), aStartLabelSupplier.get(), false);
		drawString(pGraphics2D, points[points.length / 2 - 1], points[points.length / 2], null, aMiddleLabelSupplier.get(), true);
		drawString(pGraphics2D, points[points.length - 2], points[points.length - 1], aArrowEndSupplier.get(), aEndLabelSupplier.get(), false);
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
			ArrowHead pArrow, Dimension pDimension, boolean pCenter)
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
			attach = new Point2D.Double((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
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
				Rectangle2D arrowBounds = pArrow.getPath(pEndPoint1, pEndPoint2).getBounds2D();
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
		return new Point2D.Double(attach.getX() + xoff, attach.getY() + yoff);
	}
	
	private Point2D[] getPoints()
	{
		return aStyle.getPath(edge(), edge().getGraph());
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
		GeneralPath path = getSegmentPath();
		Point2D[] points = getPoints();
		path.append(aArrowStartSupplier.get().getPath(points[1], points[0]), false);
		path.append(aArrowEndSupplier.get().getPath(points[points.length - 2], points[points.length - 1]), false);
		return path;
	}

	private GeneralPath getSegmentPath()
	{
		Point2D[] points = getPoints();
		GeneralPath path = new GeneralPath();
		Point2D p = points[points.length - 1];
		path.moveTo((float) p.getX(), (float) p.getY());
		for(int i = points.length - 2; i >= 0; i--)
		{
			p = points[i];
			path.lineTo((float) p.getX(), (float) p.getY());
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
		label.setText(toHtml(pString));
		Dimension d = label.getPreferredSize();
		Point2D a = getAttachmentPoint(pEndPoint1, pEndPoint2, pArrow, d, pCenter);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				(int)Math.round(d.getWidth()), (int)Math.round(d.getHeight()));
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
