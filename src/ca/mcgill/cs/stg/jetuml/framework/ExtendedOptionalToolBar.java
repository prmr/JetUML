/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 by the contributors of the JetUML project.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * @author JoelChev
 * An ExtendedOptionalToolBar that has labels for each optional tool. For now, that is simply copying to the Clipboard.
 *
 */
@SuppressWarnings("serial")
public class ExtendedOptionalToolBar extends JPanel
{
	private static final int FONT_SIZE = 15;
	private static final int HORIZONTAL_SPACING = 10;
	private static final Color FONT_COLOR = new Color(77, 115, 153);
	private static final int MARGIN_IMAGE = 2; // Number of pixels to leave around the graph when exporting it as an image
	private ResourceBundle aToolBarResources;
	private ImageIcon aCopyToClipBoardIcon;
	private JPanel aNorthPanel;
	private GraphFrame aGraphFrame;
	

	/**
	 * Constructs an ExtendedOptionalToolBar.
	 * @param pGraphFrame the GraphFrame associated with this ExtendedOptionalToolBar
	 */
	public ExtendedOptionalToolBar(GraphFrame pGraphFrame)
	{
		aGraphFrame = pGraphFrame;
		aNorthPanel = new JPanel(new GridLayout(1, 1));
		aToolBarResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		aCopyToClipBoardIcon = new ImageIcon(getClass().getClassLoader().getResource(aToolBarResources.getString("toolbar.copyToClipBoard")));
        addCopyToClipboard("Clipboard  Copy");
        add(aNorthPanel, BorderLayout.NORTH);
	}	
	
	/**
     * Adds the CopyToClipboard button.
     * @param pTip the tool tip
	 */
	public void addCopyToClipboard(String pTip)
	{
		 JPanel innerPanel = new JPanel();
		 innerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, HORIZONTAL_SPACING, 0));
         final JButton button = new JButton(aCopyToClipBoardIcon);
         innerPanel.add(button);
         button.addActionListener(new ActionListener()
         {
        	 public void actionPerformed(ActionEvent pEvent)
        	 {
        		copyToClipboard();
        	 }
         });
         JLabel aLabel = new JLabel(pTip);
         Font font = aLabel.getFont();
 		 Font boldFont = new Font(font.getFontName(), Font.BOLD, FONT_SIZE);
 		 aLabel.setFont(boldFont);
 		 aLabel.setForeground(FONT_COLOR);
         innerPanel.add(aLabel);
         aNorthPanel.add(innerPanel);
	}
	
	/*
     * Return the image corresponding to the graph.
     * 
     * @param pGraph The graph to convert to an image.
     * @return bufferedImage. To convert it into an image, use the syntax :
     *         Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
     */
    private static BufferedImage getImage(Graph pGraph)
    {
        Rectangle2D bounds = pGraph.getBounds();
        BufferedImage image = new BufferedImage((int) (bounds.getWidth() + MARGIN_IMAGE*2), 
        		(int) (bounds.getHeight() + MARGIN_IMAGE*2), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.translate(-bounds.getX(), -bounds.getY());
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth() + MARGIN_IMAGE*2, bounds.getHeight() + MARGIN_IMAGE*2));
        g2.translate(MARGIN_IMAGE, MARGIN_IMAGE);
        g2.setColor(Color.BLACK);
        g2.setBackground(Color.WHITE);
        pGraph.draw(g2, null);
        return image;
    }
    
	/**
   	 * Copies the current image to the clipboard.
   	 */
   	public void copyToClipboard()
   	{
   		GraphFrame frame = aGraphFrame;
   		if( frame == null )
   		{
   			return;
   		}
   		final BufferedImage image = getImage(frame.getGraph());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable()
		{
			@Override
			public boolean isDataFlavorSupported(DataFlavor pFlavor)
			{
				return DataFlavor.imageFlavor.equals(pFlavor);
			}
			
			@Override
			public DataFlavor[] getTransferDataFlavors()
			{
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}
			
			@Override
			public Object getTransferData(DataFlavor pFlavor) throws UnsupportedFlavorException, IOException
			{
				if(DataFlavor.imageFlavor.equals(pFlavor))
		        {
		            return image;
		        }
		        else
		        {
		            throw new UnsupportedFlavorException(pFlavor);
		        }
			}
		}, null);
   		JOptionPane.showInternalMessageDialog(aGraphFrame.getJTabbedPane(), aToolBarResources.getString("dialog.to_clipboard.message"), 
   				aToolBarResources.getString("dialog.to_clipboard.title"), JOptionPane.INFORMATION_MESSAGE);
   	}
	
}
