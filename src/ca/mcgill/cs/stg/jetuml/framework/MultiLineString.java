/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ca.mcgill.cs.stg.jetuml.geom.Rectangle;

/**
 *  A string that can extend over multiple lines.
 */
public class MultiLineString implements Cloneable
{
	private enum Align
	{ LEFT, CENTER, RIGHT }
	
	// Eventually these should be ported to the enum as well
	// For the moment they are kept as ints to preserve the 
	// backward compatibility with the serialized version of 
	// the test graphs.
	public static final int LEFT = 0;
	public static final int CENTER = 1;
	public static final int RIGHT = 2;
	
	private String aText = "";
	private Align aJustification = Align.CENTER;
	private boolean aBold = false;
	private boolean aUnderlined = false;
	
	/**
     * Constructs an empty, centered, normal size multi-line
     * string that is not underlined and not bold.
	 */
	public MultiLineString() 
	{}
	
	/**
     * Constructs an empty, centered, normal size multi-line
     * string that is not underlined. pBold determines if it is bold.
     * @param pBold True if the string should be bold.
	 */
	public MultiLineString(boolean pBold) 
	{ 
		aBold = pBold;
	}
	
	/**
     * Sets the value of the text property.
     * @param pText the text of the multiline string
	 */
	public void setText(String pText)
	{ 
		aText = pText; 
		getLabel(); 
	}
   
	/**
     * Gets the value of the text property.
     * @return the text of the multi-line string
	 */
	public String getText() 
	{ 
		return aText; 
	}
   
	/**
     * Sets the value of the justification property.
     * @param pJustification the justification, one of LEFT, CENTER, RIGHT
	 */
	public void setJustification(int pJustification) 
	{ 
		assert pJustification >= 0 && pJustification < Align.values().length;
		aJustification = Align.values()[pJustification]; 
	}
   
	/**
     * Gets the value of the justification property.
     * @return the justification, one of LEFT, CENTER, RIGHT
	 */
	public int getJustification() 
	{ 
		return aJustification.ordinal();
	}
   
	/**
     * Gets the value of the underlined property.
     * @return true if the text is underlined
	 */
	public boolean isUnderlined() 
	{ 
		return aUnderlined;
	}
	
	/**
	 * @return The value of the bold property.
	 */
	public boolean isBold()
	{
		return aBold;
	}
  
	/**
     * Sets the value of the underlined property.
     * @param pUnderlined true to underline the text
	 */
	public void setUnderlined(boolean pUnderlined) 
	{ 
		aUnderlined = pUnderlined; 
	}
   
	@Override
	public String toString()
	{
		return aText.replace('\n', '|');
	}

	private JLabel getLabel()
	{
		JLabel label = new JLabel(convertToHtml().toString());
		
		if(aJustification == Align.LEFT)
		{
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		else if(aJustification == Align.CENTER)
		{
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
		else if(aJustification == Align.RIGHT) 
		{
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return label;
	}

	/*
	 * @return an HTML version of the text of the string,
	 * taking into account the properties (underline, bold, etc.)
	 */
	StringBuffer convertToHtml()
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

		htmlText.append("<html><p align=\"" + aJustification.toString().toLowerCase() + "\">");
		StringTokenizer tokenizer = new StringTokenizer(aText, "\n");
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
		return htmlText;
	}
   
	/**
     * Gets the bounding rectangle for this multiline string.
     * @return the bounding rectangle (with top left corner (0,0))
	 */
	public Rectangle getBounds()
	{
		if(aText.length() == 0) 
		{
			return new Rectangle(0, 0, 0, 0);
		}
		Dimension dimensions = getLabel().getPreferredSize();       
		return new Rectangle(0, 0, (int) Math.round(dimensions.getWidth()), (int) Math.round(dimensions.getHeight()));
	}

	/**
     * Draws this multi-line string inside a given rectangle.
     * @param pGraphics2D the graphics context
     * @param pRectangle the rectangle into which to place this multi-line string
	 */
	public void draw(Graphics2D pGraphics2D, Rectangle pRectangle)
	{
		JLabel label = getLabel();
		label.setFont(pGraphics2D.getFont());
		label.setBounds(0, 0, pRectangle.getWidth(), pRectangle.getHeight());
		pGraphics2D.translate(pRectangle.getX(), pRectangle.getY());
		label.paint(pGraphics2D);
		pGraphics2D.translate(-pRectangle.getX(), -pRectangle.getY());        
	}
	
	@Override
	public boolean equals(Object pObject)
	{
		if( this == pObject )
		{
			return true;
		}
		if( pObject == null )
		{
			return false;
		}
		if( pObject.getClass() != getClass() )
		{
			return false;
		}
		MultiLineString theString = (MultiLineString)pObject;
		return aText.equals(theString.aText) && aJustification == theString.aJustification &&
				aBold == theString.aBold && aUnderlined == theString.aUnderlined;
	}
	
	@Override
	public int hashCode()
	{
		return convertToHtml().toString().hashCode();
	}
	
	@Override
	public MultiLineString clone()
	{
		try
		{
			return (MultiLineString) super.clone();
		}
		catch (CloneNotSupportedException exception)
		{
			return null;
		}
	}
}
