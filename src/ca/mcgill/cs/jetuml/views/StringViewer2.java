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
package ca.mcgill.cs.jetuml.views;

import java.util.StringTokenizer;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.text.TextAlignment;

/**
 * A utility class to view strings with various decorations:
 * - underline
 * - bold
 * - different alignments.
 * 
 * @author Martin P. Robillard.
 */
public final class StringViewer2
{
	private static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);
	private static final Label LABEL = new Label();
	
	/**
	 * How to align the text in this string.
	 */
	public enum Align
	{ LEFT, CENTER, RIGHT }
	
	private Align aAlignment = Align.CENTER;
	private boolean aBold = false;
	private boolean aUnderlined = false;
	
	/**
	 * Creates a new StringViewer.
	 * 
	 * @param pAlignment The alignment of the string.
	 * @param pBold True if the string is to be rendered bold.
	 * @param pUnderlined True if the string is to be rendered underlined.
	 * @pre pAlign != null.
	 */
	public StringViewer2(Align pAlignment, boolean pBold, boolean pUnderlined) 
	{
		assert pAlignment != null;
		aAlignment = pAlignment;
		aBold = pBold;
		aUnderlined = pUnderlined;
	}
	
	/**
     * Gets the bounding rectangle for pString.
     * @param pString The input string. Cannot be null.
     * @return the bounding rectangle (with top left corner (0,0))
     * @pre pString != null.
	 */
	public Rectangle getBounds(String pString)
	{
		assert pString != null;
		if(pString.length() == 0) 
		{
			return EMPTY;
		}
		return new Rectangle(0, 0, (int) Math.round(getLabel(pString).getWidth()), (int) Math.round(getLabel(pString).getHeight()));
	}
	
	private Label getLabel(String pString)
	{
		Label label = LABEL;
//		label.setBounds(0, 0, 0, 0);
		label.setText(convertToHtml(pString));
		
		if(aAlignment == Align.LEFT)
		{
			label.setTextAlignment(TextAlignment.LEFT);
		}
		else if(aAlignment == Align.CENTER)
		{
			label.setTextAlignment(TextAlignment.CENTER);
		}
		else if(aAlignment == Align.RIGHT) 
		{
			label.setTextAlignment(TextAlignment.RIGHT);
		}
		return label;
	}
	
	/**
     * Draws the string inside a given rectangle.
     * @param pString The string to draw.
     * @param pGraphics the graphics context
     * @param pRectangle the rectangle into which to place the string
	 */
	public void draw(String pString, GraphicsContext pGraphics, Rectangle pRectangle)
	{
		Label label = getLabel(pString);
		pGraphics.setTextAlign(label.getTextAlignment());
		pGraphics.translate(pRectangle.getX(), pRectangle.getY());
		pGraphics.fillText(label.getText(), 0, 0);	// check to see if rendering properly
		pGraphics.translate(-pRectangle.getX(), -pRectangle.getY());        
	}
	
	/**
	 * @return an HTML version of the text of the string,
	 * taking into account the properties (underline, bold, etc.)
	 */
	private String convertToHtml(String pString)
	{
		StringBuffer prefix = new StringBuffer();
		StringBuffer suffix = new StringBuffer();
		StringBuffer htmlText = new StringBuffer();
		
		// Add some spacing before and after the text.
		prefix.append("&nbsp;");
		suffix.insert(0, "&nbsp;");
		if(aUnderlined) 
		{
			prefix.append("<u>");
			suffix.insert(0, "</u>");
		}
		if(aBold)
		{
			prefix.append("<b>");
			suffix.insert(0, "</b>");
		}

		htmlText.append("<html><p align=\"" + aAlignment.toString().toLowerCase() + "\">");
		StringTokenizer tokenizer = new StringTokenizer(pString, "\n");
		boolean first = true;
		while(tokenizer.hasMoreTokens())
		{
			if(first) 
			{
				first = false;
			}
			else 
			{
				htmlText.append("<br>");
			}
			htmlText.append(prefix);
			String next = tokenizer.nextToken();
			String next0 = next.replaceAll("&", "&amp;");
			String next1 = next0.replaceAll("<", "&lt;");
			String next2 = next1.replaceAll(">", "&gt;");
			htmlText.append(next2);
			htmlText.append(suffix);
		}      
		htmlText.append("</p></html>");
		return htmlText.toString();
	}
}
