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

import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextBoundsType;

/**
 * A utility class to view strings with various decorations:
 * - underline
 * - bold
 * - different alignments.
 */
public final class StringViewer
{
	public static final Font FONT = Font.font("System", 12);
	private static final Font FONT_BOLD = Font.font(FONT.getFamily(), FontWeight.BOLD, FONT.getSize());
	
	private static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);
	private static final int HORIZONTAL_TEXT_PADDING = 3;
	private static final int VERTICAL_TEXT_PADDING = 7;
	
	/**
	 * How to align the text in this string.
	 */
	public enum Align
	{ LEFT, CENTER, RIGHT }
	
	private Align aAlignment = Align.CENTER;
	private final boolean aBold;
	private final boolean aUnderlined;
	
	/**
	 * Creates a new StringViewer.
	 * 
	 * @param pAlignment The alignment of the string.
	 * @param pBold True if the string is to be rendered bold.
	 * @param pUnderlined True if the string is to be rendered underlined.
	 * @pre pAlign != null.
	 */
	public StringViewer(Align pAlignment, boolean pBold, boolean pUnderlined) 
	{
		assert pAlignment != null;
		aAlignment = pAlignment;
		aBold = pBold;
		aUnderlined = pUnderlined;
	}
	
	private Font getFont()
	{
		if( aBold )
		{
			return FONT_BOLD;
		}
		else
		{
			return FONT;
		}
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
		Bounds bounds = getLabel(pString).getLayoutBounds(); 
		return new Rectangle(0, 0, (int) Math.round(bounds.getWidth() + HORIZONTAL_TEXT_PADDING*2), 
				(int) Math.round(bounds.getHeight() + VERTICAL_TEXT_PADDING*2));
	}
	
	private Text getLabel(String pString)
	{
		Text label = new Text();
		if (aUnderlined)
		{
			label.setUnderline(true);
		}
		label.setFont(getFont());
		label.setBoundsType(TextBoundsType.VISUAL);
		label.setText(pString);
		
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
		Text label = getLabel(pString);
		
		pGraphics.setTextAlign(label.getTextAlignment());
		
		int textX = 0;
		int textY = 0;
		if(aAlignment == Align.CENTER) 
		{
			textX = pRectangle.getWidth()/2;
			textY = pRectangle.getHeight()/2;
			pGraphics.setTextBaseline(VPos.CENTER);
		}
		else
		{
			pGraphics.setTextBaseline(VPos.TOP);
			textX = HORIZONTAL_TEXT_PADDING;
		}
		
		pGraphics.translate(pRectangle.getX(), pRectangle.getY());
		ViewUtils.drawText(pGraphics, textX, textY, pString.trim(), getFont());
		
		if(aUnderlined && pString.trim().length() > 0)
		{
			int xOffset = 0;
			int yOffset = 0;
			Bounds bounds = label.getLayoutBounds();
			if(aAlignment == Align.CENTER)
			{
				xOffset = (int) (bounds.getWidth()/2);
				yOffset = (int) (getFont().getSize()/2) + 1;
			}
			else if(aAlignment == Align.RIGHT)
			{
				xOffset = (int) bounds.getWidth();
			}
			
			ViewUtils.drawLine(pGraphics, textX-xOffset, textY+yOffset, (int) (textX-xOffset+bounds.getWidth()), textY+yOffset, LineStyle.SOLID);
		}
		pGraphics.translate(-pRectangle.getX(), -pRectangle.getY());
	}
}
