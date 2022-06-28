/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.viewers.edges;

import static org.jetuml.viewers.EdgePriority.priorityOf;

import java.util.Optional;

import org.jetuml.diagram.DiagramElement;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.AggregationEdge;
import org.jetuml.diagram.edges.AggregationEdge.Type;
import org.jetuml.diagram.edges.AssociationEdge;
import org.jetuml.diagram.edges.AssociationEdge.Directionality;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.GeneralizationEdge;
import org.jetuml.diagram.edges.SingleLabelEdge;
import org.jetuml.diagram.edges.ThreeLabelEdge;
import org.jetuml.geom.Dimension;
import org.jetuml.geom.EdgePath;
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.RenderingFacade;
import org.jetuml.viewers.ArrowHead;
import org.jetuml.viewers.EdgePriority;
import org.jetuml.viewers.LineStyle;
import org.jetuml.viewers.StringViewer;
import org.jetuml.viewers.StringViewer.Alignment;
import org.jetuml.viewers.ToolGraphics;

import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * Renders the path of stored class diagram edges using EdgeStorage.
 */
public class StoredEdgeViewer extends AbstractEdgeViewer
{
	private static final int BUTTON_SIZE = 25;
	private static final int OFFSET = 3;
	private static final int MAX_DISTANCE = 3;
	
	private static final StringViewer TOP_CENTERED_STRING_VIEWER = StringViewer.get(Alignment.TOP_CENTER);
	private static final StringViewer BOTTOM_CENTERED_STRING_VIEWER = StringViewer.get(Alignment.BOTTOM_CENTER);
	private static final StringViewer LEFT_JUSTIFIED_STRING_VIEWER = StringViewer.get(Alignment.TOP_LEFT);
	private static final int SINGLE_CHAR_WIDTH = LEFT_JUSTIFIED_STRING_VIEWER.getDimension(" ").width();
	private static final int SIGLE_CHAR_HEIGHT = LEFT_JUSTIFIED_STRING_VIEWER.getDimension(" ").height();
	private static final int MAX_LENGTH_FOR_NORMAL_FONT = 15;
	private static final int DEGREES_180 = 180;
	
	/**
	 * Gets the line style for pEdge.
	 * @param pEdge the edge of interest
	 * @return the LineStyle of pEdge
	 * @pre pEdge !=null
	 */
	private static LineStyle getLineStyle(Edge pEdge)
	{
		assert pEdge !=null;
		if(priorityOf(pEdge) == EdgePriority.IMPLEMENTATION || priorityOf(pEdge) == EdgePriority.DEPENDENCY)
		{
			return LineStyle.DOTTED;
		}
		else
		{
			return LineStyle.SOLID;
		}
	}
	
	/**
	 * Gets the start arrow for pEdge.
	 * @param pEdge the edge of interest
	 * @return the start arrow for pEdge
	 * @pre pEdge !=null
	 */
	private static ArrowHead getArrowStart(Edge pEdge)
	{
		assert pEdge !=null;
		if (pEdge instanceof AggregationEdge)
		{
			AggregationEdge edge = (AggregationEdge) pEdge;
			if (edge.getType() == Type.Composition)
			{
				return ArrowHead.BLACK_DIAMOND;
			}
			else
			{
				return ArrowHead.DIAMOND;
			}
		}
		else if (pEdge instanceof AssociationEdge)
		{
			if (((AssociationEdge) pEdge).getDirectionality() == Directionality.Bidirectional)
			{
				return ArrowHead.V;
			}
		}
		else if (pEdge instanceof DependencyEdge)
		{
			DependencyEdge edge = (DependencyEdge) pEdge;
			if (edge.getDirectionality() == DependencyEdge.Directionality.Bidirectional)
			{
				return ArrowHead.V;
			}
		}
		return ArrowHead.NONE;
	}
	
	/**
	 * Gets the end arrow for pEdge.
	 * @param pEdge the edge of interest
	 * @return the end arrow for pEdge
	 * @pre pEdge !=null
	 */
	private static ArrowHead getArrowEnd(Edge pEdge)
	{
		assert pEdge !=null;
		if (pEdge instanceof GeneralizationEdge)
		{
			return ArrowHead.TRIANGLE;
		}
		else if( pEdge instanceof AggregationEdge)
		{
			return ArrowHead.NONE;
		}
		else if (pEdge instanceof DependencyEdge)
		{
			return ArrowHead.V;
		}
		else if (pEdge instanceof AssociationEdge)
		{
			AssociationEdge edge = (AssociationEdge) pEdge;
			if (edge.getDirectionality() == AssociationEdge.Directionality.Unidirectional || 
					edge.getDirectionality() == AssociationEdge.Directionality.Bidirectional)
			{
				return ArrowHead.V;
			}	
		}
		return ArrowHead.NONE;
		
	}

	/**
	 * Uses the stored EdgePath of pEdge to create Path representation of its trajectory. 
	 * @param pEdge the edge of interest
	 * @return a Path representing the path of pEdge
	 * @pre pEdge!=null;
	 */
	private Path getSegmentPath(Edge pEdge) 
	{
		assert pEdge != null;
		assert pEdge.getDiagram().getType() == DiagramType.CLASS;
		Path shape = new Path();
		EdgePath path = getStoredEdgePath(pEdge);
		shape.getElements().add(new MoveTo(path.getStartPoint().getX(), path.getStartPoint().getY()));
		for (int i = 1; i < path.size(); i++)
		{
			Point point = path.getPointByIndex(i);
			shape.getElements().add(new LineTo(point.getX(), point.getY()));
		}
		return shape;
	}

	/**
	 * Returns whether an edge is segmented and is a step up. 
	 * @param pEdge the edge of interest
	 * @return true if edge is a step up, false otherwise.
	 */
	private boolean isStepUp(Edge pEdge) 
	{
		Point point1 = getStoredEdgePath(pEdge).getStartPoint();
		Point point2 = getStoredEdgePath(pEdge).getEndPoint();
		return point1.getX() < point2.getX() && point1.getY() > point2.getY() || 
				point1.getX() > point2.getX() && point1.getY() < point2.getY();
	}
	
	/**
	 * Draws a string.
	 * @param pGraphics the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private void drawString(GraphicsContext pGraphics, Point pEndPoint1, Point pEndPoint2, 
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
	
	private String wrapLabel(String pString, Point pEndPoint1, Point pEndPoint2) 
	{
		int distanceInX = (int)Math.abs(pEndPoint1.getX() - pEndPoint2.getX());
		int distanceInY = (int)Math.abs(pEndPoint1.getY() - pEndPoint2.getY());
		int lineLength = MAX_LENGTH_FOR_NORMAL_FONT;
		double distanceInXPerChar = distanceInX / SINGLE_CHAR_WIDTH;
		double distanceInYPerChar = distanceInY / SIGLE_CHAR_HEIGHT;
		if (distanceInX > 0)
		{
			double angleInDegrees = Math.toDegrees(Math.atan(distanceInYPerChar/distanceInXPerChar));
			lineLength = Math.max(MAX_LENGTH_FOR_NORMAL_FONT, (int)((distanceInX / 4) * (1 - angleInDegrees / DEGREES_180)));
		}
		return StringViewer.wrapString(pString, lineLength);
	}
	
	/**
	 * Computes the extent of a string that is drawn along a line segment.
	 * @param p an endpoint of the segment along which to draw the string
	 * @param q the other endpoint of the segment along which to draw the string
	 * @param s the string to draw
	 * @param center true if the string should be centered along the segment
	 * @return the rectangle enclosing the string
	*/
	private static Rectangle getStringBounds(Point pEndPoint1, Point pEndPoint2, 
			ArrowHead pArrow, String pString, boolean pCenter, boolean pIsStepUp)
	{
		if (pString == null || pString.isEmpty())
		{
			return new Rectangle((int)Math.round(pEndPoint2.getX()), 
					(int)Math.round(pEndPoint2.getY()), 0, 0);
		}
		Dimension textDimensions = TOP_CENTERED_STRING_VIEWER.getDimension(pString);
		Rectangle stringDimensions = new Rectangle(0, 0, textDimensions.width(), textDimensions.height());
		Point a = getAttachmentPoint(pEndPoint1, pEndPoint2, pArrow, stringDimensions, pCenter, pIsStepUp);
		return new Rectangle((int)Math.round(a.getX()), (int)Math.round(a.getY()),
				Math.round(stringDimensions.getWidth()), Math.round(stringDimensions.getHeight()));
	}

	/**
	 * Computes the attachment point for drawing a string.
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param b the bounds of the string to draw
	 * @param pCenter true if the string should be centered along the segment
	 * @return the point at which to draw the string
	 */
	private static Point getAttachmentPoint(Point pEndPoint1, Point pEndPoint2, 
			ArrowHead pArrow, Rectangle pDimension, boolean pCenter, boolean pIsStepUp)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.getHeight();
		Point attach = pEndPoint2;
		if (pCenter)
		{
			if (pEndPoint1.getX() > pEndPoint2.getX()) 
			{ 
				return getAttachmentPoint(pEndPoint2, pEndPoint1, pArrow, pDimension, pCenter, pIsStepUp); 
			}
			attach = new Point((pEndPoint1.getX() + pEndPoint2.getX()) / 2, 
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
					attach = new Point(pEndPoint2.getX() + (pDimension.getWidth() / 2) + gap, 
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
				Bounds arrowBounds = pArrow.view().getPath(pEndPoint1, pEndPoint2).getBoundsInLocal();
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
		return new Point((int) (attach.getX() + xoff), (int) (attach.getY() + yoff));
	}
	
	/**
	 * Gets the start label for pEdge.
	 * @param pEdge the edge of interest
	 * @return the string start label for pEdge
	 * @pre pEdge != null
	 */
	private String getStartLabel(Edge pEdge)
	{
		assert pEdge !=null;
		if (pEdge instanceof ThreeLabelEdge)
		{
			ThreeLabelEdge threeLabelEdge = (ThreeLabelEdge) pEdge;
			return threeLabelEdge.getStartLabel();
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * Gets the middle label for pEdge.
	 * @param pEdge the edge of interest
	 * @return the String middle label for pEdge
	 * @pre pEdge != null
	 */
	private String getMiddleLabel(Edge pEdge)
	{
		assert pEdge !=null;
		if (pEdge instanceof ThreeLabelEdge)
		{
			ThreeLabelEdge threeLabelEdge = (ThreeLabelEdge) pEdge;
			return threeLabelEdge.getMiddleLabel();
		}
		else if (pEdge instanceof SingleLabelEdge)
		{
			SingleLabelEdge singleLabelEdge = (SingleLabelEdge) pEdge;
			return singleLabelEdge.getMiddleLabel();
		}
		else
		{
			return "";
		}
	}
	
	/**
	 * Gets the end label for pEdge.
	 * @param pEdge the edge of interest
	 * @return the String end label for pEdge
	 * @pre pEdge != null
	 */
	private String getEndLabel(Edge pEdge)
	{
		assert pEdge !=null;
		if (pEdge instanceof ThreeLabelEdge)
		{
			ThreeLabelEdge threeLabelEdge = (ThreeLabelEdge) pEdge;
			return threeLabelEdge.getEndLabel();
		}
		else
		{
			return "";
		}
	}

	@Override
	public Rectangle getBounds(DiagramElement pElement) 
	{
		Edge edge = (Edge) pElement;
		EdgePath path = getStoredEdgePath(edge);
		Rectangle bounds = super.getBounds(edge);
		bounds = bounds.add(getStringBounds(path.getPointByIndex(1), path.getStartPoint(), 
				getArrowStart(edge), getStartLabel(edge), false, isStepUp(edge)));
		bounds = bounds.add(getStringBounds(path.getPointByIndex(path.size() / 2 - 1), 
				path.getPointByIndex(path.size() / 2), null, getMiddleLabel(edge), true, isStepUp(edge)));
		bounds = bounds.add(getStringBounds(path.getPointByIndex(path.size() - 2), path.getEndPoint(), 
				getArrowEnd(edge), getEndLabel(edge), false, isStepUp(edge)));
		return bounds;
	}

	@Override
	protected Shape getShape(Edge pEdge) 
	{
		assert pEdge != null;
		return getSegmentPath(pEdge);
	}

	@Override
	public void draw(DiagramElement pElement, GraphicsContext pGraphics) 
	{
		assert pElement !=null && pGraphics != null;
		Edge edge = (Edge) pElement;
		EdgePath path = getStoredEdgePath(edge);
		ToolGraphics.strokeSharpPath(pGraphics, getSegmentPath(edge), getLineStyle(edge));
		getArrowStart(edge).view().draw(pGraphics, path.getPointByIndex(1), path.getStartPoint());
		getArrowEnd(edge).view().draw(pGraphics, path.getPointByIndex(path.size()-2), path.getEndPoint());
		drawString(pGraphics, path.getPointByIndex(1), path.getStartPoint(), getArrowStart(edge), getStartLabel(edge), 
			false, isStepUp(edge));
		drawString(pGraphics, path.getPointByIndex(path.size() / 2 - 1) , path.getPointByIndex(path.size() / 2), null, 
			getMiddleLabel(edge), true, isStepUp(edge));
		drawString(pGraphics, path.getPointByIndex(path.size()-2), path.getPointByIndex(path.size()-1), 
			getArrowEnd(edge), getEndLabel(edge), false, isStepUp(edge));
	}

	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement) 
	{
		Edge edge = (Edge)pElement;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(OFFSET, OFFSET), new LineTo(BUTTON_SIZE-OFFSET, BUTTON_SIZE-OFFSET));
		ToolGraphics.strokeSharpPath(canvas.getGraphicsContext2D(), path, getLineStyle(edge));
		getArrowEnd(edge).view().draw(canvas.getGraphicsContext2D(), 
				new Point(OFFSET, OFFSET), new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET));
		getArrowStart(edge).view().draw(canvas.getGraphicsContext2D(), 
				new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET), new Point(OFFSET, OFFSET));
		return canvas;
	}

	@Override
	public void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics) 
	{
		EdgePath path = getStoredEdgePath((Edge)pElement);
		if (path != null) 
		{
			ToolGraphics.drawHandles(pGraphics, new Line(path.getStartPoint(), path.getEndPoint()));
		}
	}

	@Override
	public boolean contains(DiagramElement pElement, Point pPoint) 
	{
		// Purposefully does not include the arrow head and labels, which create large bounds.
		EdgePath path = getStoredEdgePath((Edge)pElement);
		if (path == null)
		{
			return false;
		}
		else
		{
			if(pPoint.distance(path.getStartPoint()) <= MAX_DISTANCE || pPoint.distance(path.getEndPoint()) <= MAX_DISTANCE)
			{
				return true;
			}
			Shape fatPath = getShape((Edge)pElement);
			fatPath.setStrokeWidth(2 * MAX_DISTANCE);
			return fatPath.contains(pPoint.getX(), pPoint.getY());
		}
	}

	@Override
	public Line getConnectionPoints(Edge pEdge) 
	{
		return new Line(getStoredEdgePath(pEdge).getStartPoint(), 
				getStoredEdgePath(pEdge).getEndPoint());
	}
	
	/**
	 * Gets the EdgePath of pEdge from EdgeStorage.
	 * @param pEdge the edge of interest
	 * @return the EdgePath of pEdge from storage
	 * @pre pEdge is present in EdgeStorage
	 */
	private EdgePath getStoredEdgePath(Edge pEdge)
	{
		Optional<EdgePath> edgePath = RenderingFacade.classDiagramRenderer().getStoredEdgePath(pEdge);
		assert edgePath.isPresent();
		return edgePath.get();
	}
}