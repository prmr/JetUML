package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

public class ExtendedOptionalToolBar extends JPanel
{
	private static final int FONT_SIZE = 15;
	private static final Color FONT_COLOR = new Color(77, 115, 153);
	private ResourceBundle aToolBarResources;
	private ImageIcon copyToClipBoardIcon;
	private GraphFrame aGraphFrame;
	private static final int MARGIN_IMAGE = 2; // Number of pixels to leave around the graph when exporting it as an image

	public ExtendedOptionalToolBar(GraphFrame pGraphFrame)
	{
		aGraphFrame = pGraphFrame;
		setLayout(new GridLayout(1,2));
		aToolBarResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		copyToClipBoardIcon = new ImageIcon(getClass().getClassLoader().getResource(aToolBarResources.getString("toolbar.copyToClipBoard")));
        addCopyToClipboard("Copy To Clipboard");
        setMaximumSize(new Dimension(430,50));
        setMinimumSize(new Dimension(430,50));
	}	
	
	/**
     * Adds the CopyToClipboard button
     * @param pTip the tool tip
	 */
	public void addCopyToClipboard(String pTip)
	{
         final JButton button = new JButton(copyToClipBoardIcon);
         button.setPreferredSize(new Dimension(25,35));
         button.setAlignmentX(CENTER_ALIGNMENT);
         add(button);
         button.addActionListener(new ActionListener()
         {
        	 public void actionPerformed(ActionEvent pEvent)
        	 {
        		copyToClipboard();
        	 }
         });
         JLabel aLabel = new JLabel(pTip, SwingConstants.CENTER);
         Font font = aLabel.getFont();
 		 Font boldFont = new Font(font.getFontName(), Font.BOLD, FONT_SIZE);
 		 aLabel.setFont(boldFont);
 		 aLabel.setForeground(FONT_COLOR);
         add(aLabel);
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
