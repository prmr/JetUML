package org.jetuml.rendering;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.jetuml.geom.Point;
import org.jetuml.geom.Rectangle;
import org.jetuml.geom.Alignment;

import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.text.Font;

/**
 * Represents an object that can build an SVG image.
 */
public class SvgRenderingContext implements RenderingContext
{
	private static final String ROOT_START_TEMPLATE = "<svg width=\"%d\" height=\"%d\" xmlns=\"http://www.w3.org/2000/svg\">";
	private static final String ROOT_END = "</svg>";
	
	private static final String TEMPLATE_LINE = "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"black\"%s/>";
	private static final String TEMPLATE_RECTANGLE = "<rect width=\"%d\" height=\"%d\" x=\"%d\" y=\"%d\""
			+ " stroke=\"black\" fill=\"white\"/>";
	private static final String TEMPLATE_OVAL = "<ellipse rx=\"%d\" ry=\"%d\" cx=\"%d\" cy=\"%d\"" 
			+ " stroke=\"black\" fill=\"%s\"/>";
	private static final String TEMPLATE_ROUNDED_RECTANGLE = "<rect width=\"%d\" height=\"%d\" x=\"%d\" y=\"%d\" rx=\"10\" ry=\"10\"" 
			+ " stroke=\"black\" fill=\"white\"/>";
	private static final String TEMPLATE_ARC = "<path d=\"M %d %d A %d %d 0 1 1 %d %d\" stroke=\"black\" fill=\"none\"/>";
	private static final String TEMPLATE_TEXT_LEFT = "<text x=\"%d\" y=\"%d\" font-size=\"10pt\" "
			+ "font-family=\"Arial, Helvetica, sans-serif\" "
			+ "text-anchor=\"start\">%s</text>";
	private static final String TEMPLATE_TEXT_CENTER = "<text x=\"%d\" y=\"%d\" font-size=\"10pt\" "
			+ "font-family=\"Arial, Helvetica, sans-serif\" "
			+ "text-anchor=\"middle\">%s</text>";
	private static final int DEGREES_360 = 360;
	
	private final StringJoiner aSvg = new StringJoiner("\n");
	
	/**
	 * Creates an SVG image of the specified dimension.
	 * 
	 * @param pDimension The dimension of the SVG image, in pixels.
	 */
	public SvgRenderingContext(int pWidth, int pHeight)
	{
		aSvg.add(String.format(ROOT_START_TEMPLATE, pWidth, pHeight));
	}
	
	@Override
	public void strokeLine(int pX1, int pY1, int pX2, int pY2, Color pColor, LineStyle pStyle)
	{
		aSvg.add(String.format(TEMPLATE_LINE, pX1, pY1, pX2, pY2, lineStyle(pStyle)));
	}
	
	private static String lineStyle(LineStyle pLineStyle)
	{
		if (pLineStyle == LineStyle.DOTTED)
		{
			String dashes = Arrays.stream(pLineStyle.getLineDashes())
					.mapToObj(d -> Double.toString(d))
					.collect(Collectors.joining(" "));
			return String.format(" stroke-dasharray=\"%s\"", dashes);
		}
		return "";
	}

	@Override
	public void drawRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pDropShadow)
	{
		// TODO Add drop shadow
		aSvg.add(String.format(TEMPLATE_RECTANGLE, pRectangle.width(), pRectangle.height(), pRectangle.x(), pRectangle.y()));
	}

	@Override
	public void drawOval(int pX, int pY, int pWidth, int pHeight, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pShadow)
	{
		// TODO Add drop shadow
		String color = "white";
		if (pFillColor != Color.WHITE)
		{
			color = "black";
		}
		aSvg.add(String.format(TEMPLATE_OVAL, pWidth/2, pHeight/2, pX+pWidth/2, pY+pHeight/2, color));
	}

	@Override
	public void strokeArc(int pCenterX, int pCenterY, int pRadius, int pStartAngle, int pLength, Color pStrokeColor)
	{
		double startAngle = Math.toRadians(pStartAngle);
		double endAngle = Math.toRadians((pStartAngle - pLength) % DEGREES_360);
		int x1 = (int) (pCenterX + Math.round(Math.sin(startAngle) * pRadius));
		int y1 = (int) (pCenterY + Math.round(Math.cos(startAngle) * pRadius));
		int x2 = (int) (pCenterX + Math.round(Math.sin(endAngle) * pRadius));
		int y2 = (int) (pCenterY + Math.round(Math.cos(endAngle) * pRadius));
		aSvg.add(String.format(TEMPLATE_ARC, x1, y1, pRadius, pRadius, x2, y2));
	}

	@Override
	public void strokePath(Path pPath, Color pStrokeColor, LineStyle pStyle)
	{
		strokePath(pPath, pStyle, "none");
	}
	
	private void strokePath(Path pPath, LineStyle pStyle, String pFill)
	{
		StringJoiner path = new StringJoiner(" ", "<path d=\"", 
				String.format("\" stroke=\"black\" fill=\"%s\"%s/>", pFill, lineStyle(pStyle)));
		for(PathElement element : pPath.getElements())
		{
			if (element instanceof MoveTo moveTo)
			{
				path.add("M " + Math.round(moveTo.getX()) + " " + Math.round(moveTo.getY()));
			}
			else if (element instanceof LineTo lineTo)
			{
				path.add("L " + Math.round(lineTo.getX()) + " " + Math.round(lineTo.getY()));
			}
			else if (element instanceof QuadCurveTo curve)
			{
				path.add("Q " + Math.round(curve.getControlX()) + " " + 
						Math.round(curve.getControlY()) + " " + 
						Math.round(curve.getX()) + " " + Math.round(curve.getY()));
			}
		}
		aSvg.add(path.toString());
	}

	@Override
	public void drawClosedPath(Path pPath, Color pFillColor, Color pStrokeColor, Optional<DropShadow> pDropShadow)
	{
		String color = "white";
		if (pFillColor != Color.WHITE)
		{
			color = "black";
		}
		strokePath(pPath, LineStyle.SOLID, color);
	}

	@Override
	public void drawRoundedRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pDropShadow)
	{
		// TODO Add drop shadow
		aSvg.add(String.format(TEMPLATE_ROUNDED_RECTANGLE, pRectangle.width(), pRectangle.height(), pRectangle.x(), pRectangle.y()));
	}

	@Override
	public void drawText(String pText, Rectangle pBounds, Alignment pTextPosition, 
			Color pTextColor, Font pFont, Point pAnchor)
	{
		/*
		 * SVG positions the text from the bottom coordinate.
		 */
		if (pTextPosition == Alignment.LEFT)
		{
			aSvg.add(String.format(TEMPLATE_TEXT_LEFT, pBounds.x(), pBounds.maxY(), escapeText(pText)));
		}
		else
		{
			aSvg.add(String.format(TEMPLATE_TEXT_CENTER, pBounds.center().x(), 
					pBounds.maxY(), escapeText(pText)));
		}
	}
	
	private static String escapeText(String pText)
	{
		return pText.replace("<", "&lt;").replace(">", "&gt;");
	}
	
	/**
	 * @return The completed svg file. Should only be called once.
	 */
	public String create()
	{
		return aSvg.add(ROOT_END).toString();
	}
}
