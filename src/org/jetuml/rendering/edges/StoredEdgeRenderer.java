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
package org.jetuml.rendering.edges;

import static org.jetuml.rendering.EdgePriority.priorityOf;

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
import org.jetuml.geom.Line;
import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.rendering.ArrowHead;
import org.jetuml.rendering.ClassDiagramRenderer;
import org.jetuml.rendering.DiagramRenderer;
import org.jetuml.rendering.EdgePath;
import org.jetuml.rendering.EdgePriority;
import org.jetuml.rendering.LineStyle;
import org.jetuml.rendering.StringRenderer;
import org.jetuml.rendering.StringRenderer.Alignment;
import org.jetuml.rendering.ToolGraphics;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;

/**
 * Renders the path of stored class diagram edges using EdgeStorage.
 */
public class StoredEdgeRenderer extends AbstractEdgeRenderer
{
	private static final StringRenderer TOP_CENTERED_STRING_VIEWER = StringRenderer.get(Alignment.TOP_CENTER);
	private static final StringRenderer BOTTOM_CENTERED_STRING_VIEWER = StringRenderer.get(Alignment.BOTTOM_CENTER);
	private static final StringRenderer LEFT_JUSTIFIED_STRING_VIEWER = StringRenderer.get(Alignment.TOP_LEFT);
	private static final int SINGLE_CHAR_WIDTH = LEFT_JUSTIFIED_STRING_VIEWER.getDimension(" ").width();
	private static final int SIGLE_CHAR_HEIGHT = LEFT_JUSTIFIED_STRING_VIEWER.getDimension(" ").height();
	private static final int DEGREES_180 = 180;
	
	/**
	 * @param pParent The renderer for the parent diagram.
	 */
	public StoredEdgeRenderer(DiagramRenderer pParent)
	{
		super(pParent);
	}
	
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
		if(pEdge instanceof AggregationEdge edge)
		{
			if(edge.getType() == Type.Composition)
			{
				return ArrowHead.BLACK_DIAMOND;
			}
			else
			{
				return ArrowHead.DIAMOND;
			}
		}
		else if(pEdge instanceof AssociationEdge edge)
		{
			if(edge.getDirectionality() == Directionality.Bidirectional)
			{
				return ArrowHead.V;
			}
		}
		else if(pEdge instanceof DependencyEdge edge)
		{
			if(edge.getDirectionality() == DependencyEdge.Directionality.Bidirectional)
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
		if(pEdge instanceof GeneralizationEdge)
		{
			return ArrowHead.TRIANGLE;
		}
		else if( pEdge instanceof AggregationEdge)
		{
			return ArrowHead.NONE;
		}
		else if(pEdge instanceof DependencyEdge)
		{
			return ArrowHead.V;
		}
		else if(pEdge instanceof AssociationEdge edge)
		{
			if(edge.getDirectionality() == AssociationEdge.Directionality.Unidirectional || 
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
		Path shape = new Path();
		EdgePath path = getStoredEdgePath(pEdge);
		shape.getElements().add(new MoveTo(path.getStartPoint().x(), path.getStartPoint().y()));
		for(int i = 1; i < path.size(); i++)
		{
			Point point = path.getPointByIndex(i);
			shape.getElements().add(new LineTo(point.x(), point.y()));
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
		return point1.x() < point2.x() && point1.y() > point2.y() || 
				point1.x() > point2.x() && point1.y() < point2.y();
	}
	
	/*
	 * Draws a label for an edge.
	 * 
	 * @param pGraphics the graphics context
	 * @param pEndPoint1 an endpoint of the segment along which to draw the string
	 * @param pEndPoint2 the other endpoint of the segment along which to draw the string
	 * @param pString the string to draw 
	 * @param pCenter true if the string should be centered along the segment
	 */
	private static void drawLabel(GraphicsContext pGraphics, Line pSegment, 
			ArrowHead pArrowHead, String pString, boolean pCenter, boolean pIsStepUp)
	{
		if(pString == null || pString.length() == 0)
		{
			return;
		}
		String label = wrapLabel(pString, pSegment); 
		Rectangle bounds = getLabelBounds(pSegment, pArrowHead, label, pCenter, pIsStepUp);
		if(pCenter) 
		{
			if( pSegment.y2() >= pSegment.y1() )
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
	
	private static String wrapLabel(String pLabel, Line pSegment) 
	{
		Dimension distances = pSegment.distanceBetweenPoints();
		int lineLength = MAX_LENGTH_FOR_NORMAL_FONT;
		double distanceInXPerChar = distances.width() / SINGLE_CHAR_WIDTH;
		double distanceInYPerChar = distances.height() / SIGLE_CHAR_HEIGHT;
		if( distances.width() > 0)
		{
			double angleInDegrees = Math.toDegrees(Math.atan(distanceInYPerChar/distanceInXPerChar));
			lineLength = Math.max(MAX_LENGTH_FOR_NORMAL_FONT, (int)((distances.width() / 4) * (1 - angleInDegrees / DEGREES_180)));
		}
		return StringRenderer.wrapString(pLabel, lineLength);
	}
	
	/*
	 * Computes the extent of a string that is drawn along a line segment.
	 * @param pSegment The segment to label
	 * @param pArrow The line decoration
	 * @param pLabel The label
	 * @param pCenter true if the string should be centered along the segment
	 * @return the rectangle enclosing the string
	*/
	private static Rectangle getLabelBounds(Line pSegment, ArrowHead pArrow, 
			String pLabel, boolean pCenter, boolean pIsStepUp)
	{
		if(pLabel == null || pLabel.isEmpty())
		{
			return new Rectangle(pSegment.x2(), pSegment.y2(), 0, 0);
		}
		Dimension textDimensions = TOP_CENTERED_STRING_VIEWER.getDimension(pLabel);
		Point attachmentPoint = getAttachmentPoint(pSegment, pArrow, textDimensions, pCenter, pIsStepUp);
		return new Rectangle(attachmentPoint.x(), attachmentPoint.y(), textDimensions.width(), textDimensions.height());
	}

	/*
	 * Computes the attachment point for drawing a string.
	 */
	private static Point getAttachmentPoint(Line pSegment, 
			ArrowHead pArrow, Dimension pDimension, boolean pCenter, boolean pIsStepUp)
	{    
		final int gap = 3;
		double xoff = gap;
		double yoff = -gap - pDimension.height();
		Point attach = pSegment.point2();
		if( pCenter )
		{
			if( pSegment.x1() > pSegment.x2()) 
			{ 
				return getAttachmentPoint(pSegment.reversed(), pArrow, pDimension, pCenter, pIsStepUp); 
			}
			attach = pSegment.center();
			if( pSegment.isVertical() && pIsStepUp)
			{
				yoff = gap;
			}
			else if( pSegment.isVertical() && !pIsStepUp)
			{
				yoff =  -gap-pDimension.height();
			}
			else if( pSegment.isHorizontal())
			{
				if(pDimension.width() > pSegment.distanceBetweenPoints().width())
				{
					attach = new Point(pSegment.x2() + (pDimension.width() / 2) + gap, 
							(pSegment.y1() + pSegment.y2()) / 2);
				}
				xoff = -pDimension.width() / 2;
			}
		}
		else 
		{
			if(pSegment.x1() < pSegment.x2())
			{
				xoff = -gap - pDimension.width();
			}
			if(pSegment.y1() > pSegment.y2())
			{
				yoff = gap;
			}
			if(pArrow != ArrowHead.NONE)
			{
				Rectangle arrowBounds = ArrowHeadRenderer.getBounds(pArrow, pSegment); 
				if(pSegment.isHorizontal())
				{
					yoff -= arrowBounds.height() / 2;
				}
				else if(pSegment.isVertical())
				{
					xoff += arrowBounds.width() / 2;
				}
			}
		}
		return new Point((int) (attach.x() + xoff), (int) (attach.y() + yoff));
	}
	
	/**
	 * Gets the start label for pEdge.
	 * @param pEdge the edge of interest
	 * @return the string start label for pEdge
	 * @pre pEdge != null
	 */
	private static String getStartLabel(Edge pEdge)
	{
		assert pEdge !=null;
		if(pEdge instanceof ThreeLabelEdge threeLabelEdge)
		{
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
	private static String getMiddleLabel(Edge pEdge)
	{
		assert pEdge !=null;
		if(pEdge instanceof ThreeLabelEdge threeLabelEdge)
		{
			return threeLabelEdge.getMiddleLabel();
		}
		else if(pEdge instanceof SingleLabelEdge singleLabelEdge)
		{
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
	private static String getEndLabel(Edge pEdge)
	{
		assert pEdge !=null;
		if(pEdge instanceof ThreeLabelEdge threeLabelEdge)
		{
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
		bounds = bounds.add(getLabelBounds(segmentForStartLabel(path), getArrowStart(edge), getStartLabel(edge), false, isStepUp(edge)));
		bounds = bounds.add(getLabelBounds(segmentForMiddleLabel(path), ArrowHead.NONE, getMiddleLabel(edge), true, isStepUp(edge))); 
		bounds = bounds.add(getLabelBounds(segmentForEndLabel(path), getArrowEnd(edge), getEndLabel(edge), false, isStepUp(edge)));
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
		ArrowHeadRenderer.draw(pGraphics, getArrowStart(edge), path.getPointByIndex(1), path.getStartPoint());
		ArrowHeadRenderer.draw(pGraphics, getArrowEnd(edge), path.getPointByIndex(path.size()-2), path.getEndPoint());

		drawLabel(pGraphics, segmentForStartLabel(path), getArrowStart(edge), getStartLabel(edge), false, isStepUp(edge));
		drawLabel(pGraphics, segmentForMiddleLabel(path), ArrowHead.NONE, getMiddleLabel(edge), true, isStepUp(edge));
		drawLabel(pGraphics, segmentForEndLabel(path), getArrowEnd(edge), getEndLabel(edge), false, isStepUp(edge));
	}
	
	/*
	 * @return The line segment used to position
	 */
	private static Line segmentForStartLabel(EdgePath pPath)
	{
		return new Line(pPath.getPointByIndex(1), pPath.getStartPoint());
	}
	
	private static Line segmentForMiddleLabel(EdgePath pPath)
	{
		// If any point is the same we consider this is a straight path
		if( pPath.size() == 4 && (pPath.getPointByIndex(0).equals(pPath.getPointByIndex(1)) || 
				pPath.getPointByIndex(1).equals(pPath.getPointByIndex(2)) || 
				pPath.getPointByIndex(2).equals(pPath.getPointByIndex(3))))
		{
			return new Line(pPath.getStartPoint(), pPath.getEndPoint());
		}
		return new Line( pPath.getPointByIndex(pPath.size() / 2 - 1) , pPath.getPointByIndex(pPath.size() / 2));
	}
	
	private static Line segmentForEndLabel(EdgePath pPath)
	{
		return new Line(pPath.getPointByIndex(pPath.size()-2), pPath.getPointByIndex(pPath.size()-1));
	}

	@Override
	public Canvas createIcon(DiagramType pDiagramType, DiagramElement pElement) 
	{
		Edge edge = (Edge)pElement;
		Canvas canvas = new Canvas(BUTTON_SIZE, BUTTON_SIZE);
		Path path = new Path();
		path.getElements().addAll(new MoveTo(OFFSET, OFFSET), new LineTo(BUTTON_SIZE-OFFSET, BUTTON_SIZE-OFFSET));
		ToolGraphics.strokeSharpPath(canvas.getGraphicsContext2D(), path, getLineStyle(edge));
		
		ArrowHeadRenderer.draw(canvas.getGraphicsContext2D(), getArrowEnd(edge), 
				new Point(OFFSET, OFFSET), new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET));
		ArrowHeadRenderer.draw(canvas.getGraphicsContext2D(), getArrowStart(edge), 
				new Point(BUTTON_SIZE-OFFSET, BUTTON_SIZE - OFFSET), new Point(OFFSET, OFFSET));
		return canvas;
	}

	@Override
	public void drawSelectionHandles(DiagramElement pElement, GraphicsContext pGraphics) 
	{
		EdgePath path = getStoredEdgePath((Edge)pElement);
		if(path != null) 
		{
			ToolGraphics.drawHandles(pGraphics, new Line(path.getStartPoint(), path.getEndPoint()));
		}
	}

	@Override
	public boolean contains(DiagramElement pElement, Point pPoint) 
	{
		// Purposefully does not include the arrow head and labels, which create large bounds.
		EdgePath path = getStoredEdgePath((Edge)pElement);
		if(path == null)
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
			return fatPath.contains(pPoint.x(), pPoint.y());
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
		Optional<EdgePath> edgePath = ((ClassDiagramRenderer)parent()).getStoredEdgePath(pEdge);
		assert edgePath.isPresent();
		return edgePath.get();
	}
}