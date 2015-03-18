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



/**
 * @author JoelChev
 * This class will be used to build the buttons on the Welcome Tab.
 * It allows a User to click on the text on the Welcome Tab to launch previous files and to create new diagrams.
 * It also allows for text to change colour when a user goes over them with their mouse.
 *
 */
public class WelcomeButtonUI extends BasicButtonUI
{
	private static final Color HOVER_COLOR = new Color(255, 210, 90);
	private static final int FONT_SIZE = 30;
	
	    /**
	     * Constructor for WelcomeButtonUI class.
	     */
	    public WelcomeButtonUI()
	    {
	        super();
	    }


	    @Override
	    protected void installDefaults(AbstractButton pButton)
	    {
	        super.installDefaults(pButton);
	        pButton.setOpaque(false);
	        pButton.setBorderPainted(false);
	        pButton.setRolloverEnabled(true);
	        pButton.setFont(new Font("Arial", Font.PLAIN, FONT_SIZE));
	        pButton.setBorder(new EmptyBorder(4, 0, 0, 4));
	    }

	    @Override
	    protected void paintText(Graphics pGraphic, AbstractButton pButton, Rectangle pTextRect, String pText)
	    {

	        ButtonModel model = pButton.getModel();
	        if (model.isRollover())
	        {
	        	pButton.setForeground(HOVER_COLOR);
	            pButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
	        }
	        else
	        {
	        	pButton.setForeground(Color.BLACK);
	        }
	        super.paintText(pGraphic, pButton, pTextRect, pText);
	    }
}
