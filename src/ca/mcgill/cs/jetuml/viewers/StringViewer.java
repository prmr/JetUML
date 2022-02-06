/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
package ca.mcgill.cs.jetuml.viewers;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import ca.mcgill.cs.jetuml.annotations.Flyweight;
import ca.mcgill.cs.jetuml.annotations.Immutable;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreferenceChangeHandler;
import ca.mcgill.cs.jetuml.geom.Dimension;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * A utility class to view strings with various decorations:
 * - underline
 * - bold
 * - different alignments.
 */
@Immutable 
@Flyweight
public final class StringViewer
{
	private static final CanvasFont CANVAS_FONT = new CanvasFont();
	
	private static final Dimension EMPTY = new Dimension(0, 0);
	private static final int DEFAULT_HORIZONTAL_TEXT_PADDING = 7;
	private static final int DEFAULT_VERTICAL_TEXT_PADDING = 7;
	
	private static final Map<Alignment, Map<EnumSet<TextDecoration>, StringViewer>> STORE = new HashMap<>();
	
	/**
	 * How to align the text in this string.
	 */
	public enum Alignment
	{ TOP_LEFT, TOP_CENTER, TOP_RIGHT,
	  CENTER_LEFT, CENTER_CENTER, CENTER_RIGHT,
	  BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT;
	  
	  private boolean isTop() 
	  { 
		  return this == TOP_LEFT || this == TOP_CENTER || this == TOP_RIGHT; 
	  } 
		
	  private boolean isVerticallyCentered() 
	  { 
		  return this == CENTER_LEFT || this == CENTER_CENTER || this == CENTER_RIGHT; 
	  } 
	  
	  private boolean isBottom() 
	  { 
		  return this == BOTTOM_LEFT || this == BOTTOM_CENTER || this == BOTTOM_RIGHT; 
	  }
	  
	  private boolean isLeft() 
	  { 
		  return this == TOP_LEFT || this == CENTER_LEFT || this == BOTTOM_LEFT; 
	  }
	  
	  private boolean isHorizontallyCentered() 
	  { 
		  return this == TOP_CENTER || this == CENTER_CENTER || this == BOTTOM_CENTER; 
	  }
	  
	  private boolean isRight() 
	  { 
		  return this == TOP_RIGHT || this == CENTER_RIGHT || this == BOTTOM_RIGHT; 
	  }
	  
	}
	
	/**
	 * Various text decorations.
	 */
	public enum TextDecoration
	{ BOLD, UNDERLINED, PADDED }
	
	private Alignment aAlign = Alignment.CENTER_CENTER;
	private final boolean aBold;
	private final boolean aUnderlined;
	private int aHorizontalPadding = DEFAULT_HORIZONTAL_TEXT_PADDING;
	private int aVerticalPadding = DEFAULT_VERTICAL_TEXT_PADDING;
	
	private StringViewer(Alignment pAlign, EnumSet<TextDecoration> pDecorations) 
	{
		if ( !pDecorations.contains(TextDecoration.PADDED) )
		{
			aHorizontalPadding = 0;
			aVerticalPadding = 0;
		}
		aAlign = pAlign;
		aBold = pDecorations.contains(TextDecoration.BOLD);
		aUnderlined = pDecorations.contains(TextDecoration.UNDERLINED);
	}
	
	/**
	 * Lazily creates or retrieves an instance of StringViewer.
	 * @param pAlign The alignment to use.
	 * @param pDecorations The decorations to apply.
	 * @pre pAlign != null
	 * @return The StringViewer instance with the requested properties.
	 */
	public static StringViewer get(Alignment pAlign, TextDecoration... pDecorations)
	{
		assert pAlign != null;
		
		EnumSet<TextDecoration> decorationSet = EnumSet.noneOf(TextDecoration.class);
		Collections.addAll(decorationSet, pDecorations);
		
		Map<EnumSet<TextDecoration>, StringViewer> innerMap = STORE.computeIfAbsent(pAlign, k -> new HashMap<>());
		return innerMap.computeIfAbsent(decorationSet, k -> new StringViewer(pAlign, decorationSet));
	}
	
	/**
     * Gets the width and height required to show pString, including
     * padding around the string.
     * @param pString The input string. 
     * @return The dimension pString will use on the screen.
     * @pre pString != null.
	 */
	public Dimension getDimension(String pString)
	{
		assert pString != null;
		if(pString.length() == 0) 
		{
			return EMPTY;
		}
		Dimension dimension = CANVAS_FONT.getDimension(pString, aBold);
		return new Dimension(Math.round(dimension.width() + aHorizontalPadding*2), 
				Math.round(dimension.height() + aVerticalPadding*2));
	}

	/**
	 * Breaks up a string such that each multi-word line has at most
	 * pWidth characters.
	 * @param pString The string to wrap.
	 * @param pWidth The maximum number of characters on a line.
	 * @return The new string.
	 */
	public static String wrapString(String pString, int pWidth)
	{
		int remainingEmptySpace = pWidth;
        final int spaceLength = 1;
        String[] words = pString.split(" ");
        StringBuilder formattedString = new StringBuilder();

        for ( String word : words )
        {
        	// Replace last space with newline (if last space exists)
        	if ( word.length() > remainingEmptySpace && formattedString.length() > 0 )
        	{
        		formattedString.deleteCharAt(formattedString.length() - 1);
        		formattedString.append('\n');
        		remainingEmptySpace = pWidth;
        	}

    		remainingEmptySpace = remainingEmptySpace - word.length() - spaceLength;
    		formattedString.append(word);
    		formattedString.append(' ');
        }

        // Remove extraneous space
        formattedString.deleteCharAt(formattedString.length() - 1);
    	return formattedString.toString();
	}

	private TextAlignment getTextAlignment()
	{		
		if ( aAlign.isLeft() )
		{
			return TextAlignment.LEFT;
		}
		else if ( aAlign.isHorizontallyCentered() )
		{
			return TextAlignment.CENTER;
		}
		return TextAlignment.RIGHT;
	}
	
	private VPos getTextBaseline()
	{
		if ( aAlign.isBottom() )
		{
			return VPos.BASELINE;
		}
		else if ( aAlign.isTop() )
		{
			return VPos.TOP;
		}
		return VPos.CENTER;
	}
	
	/**
     * Draws the string inside a given rectangle.
     * @param pString The string to draw.
     * @param pGraphics the graphics context
     * @param pRectangle the rectangle into which to place the string
	 */
	public void draw(String pString, GraphicsContext pGraphics, Rectangle pRectangle)
	{
		final VPos oldVPos = pGraphics.getTextBaseline();
		final TextAlignment oldAlign = pGraphics.getTextAlign();
		
		pGraphics.setTextAlign(getTextAlignment());
		pGraphics.setTextBaseline(getTextBaseline());
		
		
		int textX = 0;
		int textY = 0;
		if( aAlign.isHorizontallyCentered() ) 
		{
			textX = pRectangle.getWidth()/2;
		}
		else
		{
			textX = aHorizontalPadding;
		}
		
		if ( aAlign.isVerticallyCentered() )
		{
			textY = pRectangle.getHeight()/2;
		}
		
		pGraphics.translate(pRectangle.getX(), pRectangle.getY());
		CANVAS_FONT.drawString(pGraphics, textX, textY, pString.trim(), aBold);
		
		if(aUnderlined && pString.trim().length() > 0)
		{
			int xOffset = 0;
			int yOffset = 0;
			Dimension dimension = CANVAS_FONT.getDimension(pString, aBold);
			if( aAlign.isHorizontallyCentered() )
			{
				xOffset = dimension.width()/2;
				yOffset = CANVAS_FONT.fontSize()/2 + 1;
			}
			else if( aAlign.isRight() )
			{
				xOffset = dimension.width();
			}
			
			ViewerUtils.drawLine(pGraphics, textX-xOffset, textY+yOffset, 
					textX-xOffset+dimension.width(), textY+yOffset, LineStyle.SOLID);
		}
		pGraphics.translate(-pRectangle.getX(), -pRectangle.getY());
		pGraphics.setTextBaseline(oldVPos);
		pGraphics.setTextAlign(oldAlign);
	}
	
	/**
	 * Responsible for performing more rudimentary operations involving font,
	 * as well as being synchronized with the user's current font.
	 */
	private static final class CanvasFont implements IntegerPreferenceChangeHandler
	{

		private Font aFont;
		private Font aFontBold;
		private FontMetrics aFontMetrics;
		private FontMetrics aFontBoldMetrics;

		private CanvasFont()
		{
			refreshAttributes();
			UserPreferences.instance().addIntegerPreferenceChangeHandler(this);
		}

		private Font getFont(boolean pBold)
		{
			if ( pBold )
			{
				return aFontBold;
			}
			return aFont;
		}

		private FontMetrics getFontMetrics(boolean pBold)
		{
			if ( pBold )
			{
				return aFontBoldMetrics;
			}
			return aFontMetrics;
		}

		/**
		 * Returns the dimension of a given string.
		 * @param pString The string to which the bounds pertain.
		 * @return The dimension of the string
		 */
		public Dimension getDimension(String pString, boolean pBold)
		{
			return getFontMetrics(pBold).getDimension(pString);
		}

		/**
		 * Draws the string on the graphics context at the specified position.
		 * @param pGraphics The graphics context
		 * @param pTextX The x-position of the string
		 * @param pTextY The y-position of the string
		 * @param pString The canvas on which to draw the string
		 * @param pBold If the text should be bold
		 */
		public void drawString(GraphicsContext pGraphics, int pTextX, int pTextY, String pString, boolean pBold)
		{
			ViewerUtils.drawText(pGraphics, pTextX, pTextY, pString, getFont(pBold));
		}

		/**
		 * Returns the font size the user currently specifies.
		 * @return The font size
		 */
		public int fontSize()
		{
			return (int) Math.round(aFont.getSize());
		}

		@Override
		public void integerPreferenceChanged(IntegerPreference pPreference) 
		{
			if ( pPreference == IntegerPreference.fontSize && aFont.getSize() != UserPreferences.instance().getInteger(pPreference) )
			{
				refreshAttributes();
			}

		}

		private void refreshAttributes()
		{
			aFont = Font.font("System", UserPreferences.instance().getInteger(IntegerPreference.fontSize));
			aFontBold = Font.font(aFont.getFamily(), FontWeight.BOLD, aFont.getSize());
			aFontMetrics = new FontMetrics(aFont);
			aFontBoldMetrics = new FontMetrics(aFontBold);
		}

	}
}
