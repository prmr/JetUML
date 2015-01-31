/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2007 Cay S. Horstmann (http://horstmann.com)
 Alexandre de Pellegrin (http://alexdp.free.fr);

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * Utilities to convert a Graph object into an image.
 * 
 * @author Alexandre de Pellegrin
 * @author Martin P. Robillard
 */
public final class FileExportService
{
	private FileExportService()
	{}
	

    /**
     * Return the image corresponding to the graph.
     * 
     * @param pGraph
     * @return bufferedImage. To convert it into an image, use the syntax :
     *         Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
     */
    private static BufferedImage getImage(Graph pGraph)
    {
        Rectangle2D bounds = pGraph.getBounds();
        BufferedImage image = new BufferedImage((int) bounds.getWidth() + 1, (int) bounds.getHeight() + 1,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.translate(-bounds.getX(), -bounds.getY());
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth() + 1, bounds.getHeight() + 1));
        g2.setColor(Color.BLACK);
        g2.setBackground(Color.WHITE);
        pGraph.draw(g2, new Grid());
        return image;
    }

    /**
     * Export a graph to the clipboard.
     * 
     * @param pGraph The graph to export.
     */
    public static void exportToclipBoard(Graph pGraph)
    {
        final BufferedImage image = getImage(pGraph);
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
    }
}
