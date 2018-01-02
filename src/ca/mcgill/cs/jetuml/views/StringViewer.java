package ca.mcgill.cs.jetuml.views;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.StringTokenizer;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * A utility class to view strings with various decorations:
 * - underline
 * - bold
 * - different alignments.
 * 
 * @author Martin P. Robillard.
 */
public final class StringViewer
{
	private static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);
	private static final JLabel LABEL = new JLabel();
	
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
	public StringViewer(Align pAlignment, boolean pBold, boolean pUnderlined) 
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
		Dimension dimensions = getLabel(pString).getPreferredSize();       
		return new Rectangle(0, 0, (int) Math.round(dimensions.getWidth()), (int) Math.round(dimensions.getHeight()));
	}
	
	private JLabel getLabel(String pString)
	{
		JLabel label = LABEL;
		label.setBounds(0, 0, 0, 0);
		label.setText(convertToHtml(pString));
		
		if(aAlignment == Align.LEFT)
		{
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		else if(aAlignment == Align.CENTER)
		{
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
		else if(aAlignment == Align.RIGHT) 
		{
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return label;
	}
	
	/**
     * Draws the string inside a given rectangle.
     * @param pString The string to draw.
     * @param pGraphics2D the graphics context
     * @param pRectangle the rectangle into which to place the string
	 */
	public void draw(String pString, Graphics2D pGraphics2D, Rectangle pRectangle)
	{
		JLabel label = getLabel(pString);
		label.setFont(pGraphics2D.getFont());
		label.setBounds(0, 0, pRectangle.getWidth(), pRectangle.getHeight());
		pGraphics2D.translate(pRectangle.getX(), pRectangle.getY());
		label.paint(pGraphics2D);
		pGraphics2D.translate(-pRectangle.getX(), -pRectangle.getY());        
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
