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

import com.sun.javafx.tk.FontLoader;
import com.sun.javafx.tk.FontMetrics;
import com.sun.javafx.tk.Toolkit;

import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
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
	private static final int TEXT_PADDING = 5;
	
	/**
	 * How to align the text in this string.
	 */
	public enum Align
	{ LEFT, CENTER, RIGHT }
	
	private Align aAlignment = Align.CENTER;
	private boolean aBold = false;
	private boolean aUnderlined = false;
	private Font aFont = Font.getDefault();
	
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
		Text text = new Text(pString.trim());
		text.setFont(aFont);
		String[] lines = pString.split("\n");
		int width = (int) Math.round(text.getLayoutBounds().getWidth() + 2*TEXT_PADDING);
		int height = (int) Math.round(text.getLayoutBounds().getHeight() + 2*TEXT_PADDING + lines.length);
		return new Rectangle(0, 0, width, height);
	}
	
	/**
     * Draws the string inside a given rectangle.
     * @param pString The string to draw.
     * @param pGraphics the graphics context
     * @param pRectangle the rectangle into which to place the string
	 */
	public void draw(String pString, GraphicsContext pGraphics, Rectangle pRectangle)
	{
		Font oldFont = pGraphics.getFont();
		if (aBold) 
		{
			pGraphics.setFont(Font.font(aFont.getFamily(), FontWeight.BOLD, aFont.getSize()));
		}
		else
		{
			pGraphics.setFont(aFont);
		}

		int textX = TEXT_PADDING;
		FontLoader fontLoader = Toolkit.getToolkit().getFontLoader();
		FontMetrics fontMetrics = fontLoader.getFontMetrics(aFont);
		int textY = (int) Math.round(fontMetrics.getLineHeight());
		if(aAlignment == Align.LEFT)
		{
			pGraphics.setTextAlign(TextAlignment.LEFT);
		}
		else if(aAlignment == Align.CENTER)
		{
			pGraphics.setTextAlign(TextAlignment.CENTER);
			textX = pRectangle.getWidth()/2;
			if (pString.trim().contains("\n"))
			{
				textY = (int) Math.round(fontMetrics.getLineHeight() + TEXT_PADDING);
			}
			else 
			{
				textY = (int) (pRectangle.getHeight()/2 + (fontMetrics.getAscent()-fontMetrics.getDescent())/2);
			}
		}
		else if(aAlignment == Align.RIGHT) 
		{
			pGraphics.setTextAlign(TextAlignment.RIGHT);
			textX = pRectangle.getWidth();
		}
		
		Paint oldFill = pGraphics.getFill();
		pGraphics.translate(pRectangle.getX(), pRectangle.getY());
		pGraphics.setFill(Color.BLACK);	
		pGraphics.fillText(pString.trim(), textX, textY);
		  
		if (aUnderlined && pString.trim().length() > 0)
		{
			Rectangle stringBounds = getBounds(pString);
			int xOffset = 0;
			if (aAlignment == Align.CENTER)
			{
				xOffset = stringBounds.getWidth()/2;
			}
			else if (aAlignment == Align.RIGHT)
			{
				xOffset = stringBounds.getWidth();
			}
			
			if (aBold) 
			{
				double oldWidth = pGraphics.getLineWidth();
				pGraphics.setLineWidth(2);
				pGraphics.strokeLine(textX-xOffset, textY+1, textX-xOffset+stringBounds.getWidth(), textY+1);
				pGraphics.setLineWidth(oldWidth);
			}
			else
			{
				pGraphics.strokeLine(textX-xOffset, textY+1, textX-xOffset+stringBounds.getWidth(), textY+1);
			}
		}
		pGraphics.translate(-pRectangle.getX(), -pRectangle.getY()); 
		pGraphics.setFill(oldFill);
		pGraphics.setFont(oldFont);
	}
}
