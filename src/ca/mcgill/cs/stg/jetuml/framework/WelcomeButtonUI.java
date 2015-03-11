package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;



public class WelcomeButtonUI extends BasicButtonUI
{
	private static Color hoverColor = new Color(255, 210, 90);
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
	        b.setFont(new Font("Arial", Font.PLAIN, 30));
	        b.setBorder(new EmptyBorder(4, 0, 0, 4));
	    }

	    protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text)
	    {

	        ButtonModel model = b.getModel();
	        if (model.isRollover())
	        {
	        	b.setForeground(hoverColor);
	            b.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        }
	        else
	        {
	        	b.setForeground(Color.BLACK);
	        }
	        super.paintText(g, b, textRect, text);
	    }
}
