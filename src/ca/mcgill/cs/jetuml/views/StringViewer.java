package ca.mcgill.cs.jetuml.views;

import java.awt.Dimension;
import java.awt.Graphics2D;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import ca.mcgill.cs.jetuml.application.MultiLineString;
import ca.mcgill.cs.jetuml.application.MultiLineString.Align;
import ca.mcgill.cs.jetuml.geom.Rectangle;

/**
 * A utility class to view multi-line strings.
 * 
 * @author Martin P. Robillard.
 */
public final class StringViewer
{
	private static final Rectangle EMPTY = new Rectangle(0, 0, 0, 0);
	private static final JLabel LABEL = new JLabel();
	
	private StringViewer() {}
	
	/**
     * Gets the bounding rectangle for pString.
     * @param pString The input string. Cannot be null.
     * @return the bounding rectangle (with top left corner (0,0))
     * @pre pString != null.
	 */
	public static Rectangle getBounds(MultiLineString pString)
	{
		assert pString != null;
		if(pString.getText().length() == 0) 
		{
			return EMPTY;
		}
		Dimension dimensions = getLabel(pString).getPreferredSize();       
		return new Rectangle(0, 0, (int) Math.round(dimensions.getWidth()), (int) Math.round(dimensions.getHeight()));
	}
	
	private static JLabel getLabel(MultiLineString pString)
	{
		JLabel label = LABEL;
		label.setBounds(0, 0, 0, 0);
		label.setText(pString.convertToHtml());
		
		if(pString.obtainJustification() == Align.LEFT)
		{
			label.setHorizontalAlignment(SwingConstants.LEFT);
		}
		else if(pString.obtainJustification() == Align.CENTER)
		{
			label.setHorizontalAlignment(SwingConstants.CENTER);
		}
		else if(pString.obtainJustification() == Align.RIGHT) 
		{
			label.setHorizontalAlignment(SwingConstants.RIGHT);
		}
		return label;
	}
	
	/**
     * Draws this multi-line string inside a given rectangle.
     * @param pString The string to draw.
     * @param pGraphics2D the graphics context
     * @param pRectangle the rectangle into which to place this multi-line string
	 */
	public static void draw(MultiLineString pString, Graphics2D pGraphics2D, Rectangle pRectangle)
	{
		JLabel label = getLabel(pString);
		label.setFont(pGraphics2D.getFont());
		label.setBounds(0, 0, pRectangle.getWidth(), pRectangle.getHeight());
		pGraphics2D.translate(pRectangle.getX(), pRectangle.getY());
		label.paint(pGraphics2D);
		pGraphics2D.translate(-pRectangle.getX(), -pRectangle.getY());        
	}
}
