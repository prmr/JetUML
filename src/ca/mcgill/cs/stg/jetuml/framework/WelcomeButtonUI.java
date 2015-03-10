package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;


public class WelcomeButtonUI extends BasicButtonUI
{

	    public WelcomeButtonUI()
	    {
	        super();
	    }

	    protected void installDefaults(AbstractButton b)
	    {
	        super.installDefaults(b);
	        b.setOpaque(false);
	        b.setBorderPainted(false);
	        b.setRolloverEnabled(true);
	        b.setBorder(new EmptyBorder(0, 0, 0, 0));
	    }

	    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text)
	    {

	        ButtonModel model = b.getModel();
	        if (model.isRollover())
	        {
	            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        }
	        else
	        {
	        }
	        super.paintText(g, b, textRect, text);
	    }
}
