package org.jetuml.rendering;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.jetuml.geom.Alignment;
import org.jetuml.geom.Rectangle;

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
	/* For drop shadows, we use an overlay with a Gaussian filter because
	 * the dropShadow SVG filter is not uniformly supported. When it becomes
	 * more widely supported to convert SVG images to PDF, for example, this
	 * class can be revised to use the feDropShadow filter instead. 
	 */
	
	/* Margin of white space around the diagram, in pixels. */
	private static final int MARGIN = 7;
	
	/* Amount of pixels to subtract from the font size, to make sure it fits. */
	private static final float FONT_ADJUSTMENT = 0.25f;
	
	private final StringJoiner aSvg = new StringJoiner("\n");
	
	/**
	 * Creates an SVG image using pViewport as the viewport area. The viewport is the area to 
	 * render. The viewport effectively translates the coordinates of the diagram to render
	 * in the final SVG image.
	 * @param pViewport A rectangle describing the coordinate area to use as SVG viewport.
	 */
	public SvgRenderingContext(Rectangle pViewport)
	{
		final String rootStartTemplate = "<svg "
				+ "viewBox=\"%d %d %d %d\" "
				+ "xmlns=\"http://www.w3.org/2000/svg\">\n"
				+ "<defs><filter id=\"shadow\" x=\"-10%%\" y=\"-10%%\">\n"
				+ "  <feGaussianBlur in=\"SourceGraphic\" stdDeviation=\"1\" />\n"
				+ "</filter></defs>"
				+ "<g transform=\"translate(0.5,0.5)\" stroke-width=\"0.75\">";
		
		aSvg.add(String.format(rootStartTemplate, 
				pViewport.x() - MARGIN, 
				pViewport.y() - MARGIN, 
				pViewport.width() + MARGIN * 2,
				pViewport.height() + MARGIN * 2,
				(pViewport.width() + MARGIN) * 2,
				(pViewport.width() + MARGIN) * 2));
	}
	
	@Override
	public void strokeLine(int pX1, int pY1, int pX2, int pY2, Color pColor, LineStyle pStyle)
	{
		final String templateLine = "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" stroke=\"black\"%s/>";
		aSvg.add(String.format(templateLine, pX1, pY1, pX2, pY2, lineStyle(pStyle)));
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
		final String templateRectangle = "<rect width=\"%d\" height=\"%d\" x=\"%d\" y=\"%d\""
				+ " stroke=\"black\" fill=\"white\"/>";
		final String templateRectangleShadow = "<rect width=\"%d\" height=\"%d\" x=\"%d\" y=\"%d\""
				+ " stroke=\"none\" fill=\"lightgray\" style=\"filter:url(#shadow);\"/>";
		pDropShadow.ifPresent(dropShadow -> 
			aSvg.add(String.format(templateRectangleShadow, 
					pRectangle.width(), pRectangle.height(), pRectangle.x()+2, pRectangle.y()+2)));
		aSvg.add(String.format(templateRectangle, pRectangle.width(), pRectangle.height(), pRectangle.x(), pRectangle.y()));
	}

	@Override
	public void drawOval(int pX, int pY, int pWidth, int pHeight, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pShadow)
	{
		final String templateOval = "<ellipse rx=\"%d\" ry=\"%d\" cx=\"%d\" cy=\"%d\"" 
				+ " stroke=\"black\" fill=\"%s\"/>";
		final String templateOvalShadow = "<ellipse rx=\"%d\" ry=\"%d\" cx=\"%d\" cy=\"%d\"" 
				+ " stroke=\"none\" fill=\"lightgray\" style=\"filter:url(#shadow);\"/>";
		
		String color = "white";
		if (pFillColor != Color.WHITE)
		{
			color = "black";
		}
		if (pShadow.isPresent())
		{
			aSvg.add(String.format(templateOvalShadow, pWidth/2, pHeight/2, pX+pWidth/2+2, pY+pHeight/2+2, color));
		}
		aSvg.add(String.format(templateOval, pWidth/2, pHeight/2, pX+pWidth/2, pY+pHeight/2, color));
	}

	@Override
	public void strokeArc(int pCenterX, int pCenterY, int pRadius, int pStartAngle, int pLength, Color pStrokeColor)
	{
		final int fullCircle = 360; // Degrees
		double startAngle = Math.toRadians(pStartAngle);
		double endAngle = Math.toRadians((pStartAngle - pLength) % fullCircle);
		int x1 = (int) (pCenterX + Math.round(Math.sin(startAngle) * pRadius));
		int y1 = (int) (pCenterY + Math.round(Math.cos(startAngle) * pRadius));
		int x2 = (int) (pCenterX + Math.round(Math.sin(endAngle) * pRadius));
		int y2 = (int) (pCenterY + Math.round(Math.cos(endAngle) * pRadius));
		final String templateArc = "<path d=\"M %d %d A %d %d 0 1 1 %d %d\" stroke=\"black\" fill=\"none\"/>";
		aSvg.add(String.format(templateArc, x1, y1, pRadius, pRadius, x2, y2));
	}

	@Override
	public void strokePath(Path pPath, Color pStrokeColor, LineStyle pStyle)
	{
		strokePath(pPath, pStyle, "none", false);
	}
	
	private void strokePath(Path pPath, LineStyle pStyle, String pFill, boolean pShadow)
	{
		StringJoiner path = new StringJoiner(" ", "<path d=\"", 
				String.format("\" stroke=\"black\" fill=\"%s\"%s/>", pFill, lineStyle(pStyle)));
		if (pShadow)
		{
			path = new StringJoiner(" ", "<path d=\"", 
					String.format("\" stroke=\"none\" fill=\"lightGray\"  "
							+ "transform=\"translate(2 2)\" style=\"filter:url(#shadow);\"/>"));
		}
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
		if (pFillColor == Color.WHITE)
		{
			color = "white";
		}
		else if (pFillColor == Color.BLACK)
		{
			color = "black";
		}
		else 
		{
			color = "rgb(90%, 90%, 60%)"; // The only other color is for notes.
		}
		if (pDropShadow.isPresent())
		{
			strokePath(pPath, LineStyle.SOLID, color, true);
		}
		strokePath(pPath, LineStyle.SOLID, color, false);
	}

	@Override
	public void drawRoundedRectangle(Rectangle pRectangle, Color pFillColor, Color pStrokeColor,
			Optional<DropShadow> pDropShadow)
	{
		final String templateRoundedRectangle = "<rect width=\"%d\" height=\"%d\" x=\"%d\" y=\"%d\" rx=\"10\" ry=\"10\"" 
				+ " stroke=\"black\" fill=\"white\"/>";
		final String templateRoundedRectangleShadow = "<rect width=\"%d\" height=\"%d\" x=\"%d\" y=\"%d\" rx=\"10\" ry=\"10\"" 
				+ " stroke=\"none\" fill=\"lightGray\" style=\"filter:url(#shadow);\"/>";		
		pDropShadow.ifPresent(shadow -> aSvg.add(String.format(templateRoundedRectangleShadow, 
				pRectangle.width(), pRectangle.height(), pRectangle.x() + 2, pRectangle.y() +2)));
		aSvg.add(String.format(templateRoundedRectangle, pRectangle.width(), pRectangle.height(), pRectangle.x(), pRectangle.y()));
	}

	@Override
	public void drawText(String pText, Rectangle pBounds, Alignment pTextPosition, 
			Color pTextColor, Font pFont, FontDimension pDimension)
	{
		// SVG positions the text from the bottom coordinate.
		int anchorX = pBounds.x();
		int anchorY = pBounds.maxY() - pDimension.baselineOffset();
		String anchor = "start";
		if( pTextPosition == Alignment.CENTER )
		{
			anchorX = pBounds.center().x();
			anchor = "middle";
		}
		String weight = "normal";
		if (pFont.getStyle().toLowerCase().contains("bold"))
		{
			weight = "bold";
		}
		String style = "normal";
		if (pFont.getStyle().toLowerCase().contains("italic"))
		{
			style = "italic";
		}
		
		final String templateText = "<text x=\"%d\" y=\"%d\" "
				+ "font-size=\"%.2fpx\" "
				+ "font-family=\"Arial, Helvetica, sans-serif\" "
				+ "font-weight=\"%s\" "
				+ "font-style=\"%s\" "
				+ "text-anchor=\"%s\">%s</text>";

		aSvg.add(String.format(templateText, anchorX, anchorY, pFont.getSize() - FONT_ADJUSTMENT, weight, style, anchor, escapeText(pText)));
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
		final String rootEnd = "</g></svg>";
		return aSvg.add(rootEnd).toString();
	}
}
