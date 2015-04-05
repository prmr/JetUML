package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Dimension;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;



import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * The component used for extended functionality in the SideBar. For now, this only includes copying to the clipboard.
 * 
 * @author JoelChev
 *
 */
@SuppressWarnings("serial")
public class OptionalToolBar extends JPanel
{
	private static final int COMPONENT_WIDTH = 55;
	private static final int COMPONENT_HEIGHT = 50;
	private static final int BUTTON_WIDTH = 25;
	private static final int BUTTON_HEIGHT = 35;
	private static final int MARGIN_IMAGE = 2; // Number of pixels to leave around the graph when exporting it as an image
	private ResourceBundle aToolBarResources;
	private ImageIcon aCopyToClipBoardIcon;
	private GraphFrame aGraphFrame;

	/**
	 * Constructs an OptionalToolBar.
	 * @param pGraphFrame the GraphFrame associated with this OptionalToolBar
	 */
	public OptionalToolBar(GraphFrame pGraphFrame)
	{
		aGraphFrame = pGraphFrame;
		setLayout(new GridLayout(0, 1));
		aToolBarResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		aCopyToClipBoardIcon = new ImageIcon(getClass().getClassLoader().getResource(aToolBarResources.getString("toolbar.copyToClipBoard")));
        addCopyToClipboard("Copy To Clipboard");
        setMaximumSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));
        setMinimumSize(new Dimension(COMPONENT_WIDTH, COMPONENT_HEIGHT));
	}	
	
	/**
     * Adds the CopyToClipboard button.
     * @param pTip the tool tip
	 */
	public void addCopyToClipboard(String pTip)
	{
         final JButton button = new JButton(aCopyToClipBoardIcon);
         button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT));
         button.setToolTipText(pTip);
         button.setAlignmentX(CENTER_ALIGNMENT);
         add(button);
         button.addActionListener(new ActionListener()
         {
        	 public void actionPerformed(ActionEvent pEvent)
        	 {
        		copyToClipboard();
        	 }
         });
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