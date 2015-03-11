/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 *A frame for showing a graphical editor.
 */
@SuppressWarnings("serial")
public class GraphFrame extends JInternalFrame
{
	private JTabbedPane aPane;
	private Graph aGraph;
	private GraphPanel aPanel;
	private ToolBar aToolBar;
	private File aFile; // The file associated with this graph
	
	/**
     * Constructs a graph frame with an empty tool bar.
     * @param pGraph the initial graph
	 */
	public GraphFrame(Graph pGraph, JTabbedPane pPane)
	{
		aPane = pPane;
		aGraph = pGraph;
		aToolBar = new ToolBar(aGraph);
		aPanel = new GraphPanel(aToolBar);
		//tab button
		JLabel test = new JLabel("Test");
		add(test);
        JButton button = new TabButton();
        add(button);
		Container contentPane = getContentPane();
		contentPane.add(aToolBar, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(aPanel), BorderLayout.CENTER);
		aPanel.setGraph(aGraph);
	}

	/**
     * Gets the graph that is being edited in this frame.
     * @return the graph
	 */
	public Graph getGraph()
	{
		return aGraph;
	}

	/**
     * Gets the graph panel that is contained in this frame.
     * @return the graph panel
	 */
	public GraphPanel getGraphPanel()
   	{
		return aPanel;
   	}
	
	/**
	 * Sets the title of the frame as the file name if there
	 * is a file name. 
	 * 
	 * @param pModified If the file is in modified (unsaved) state,
	 * appends an asterisk to the frame title.
	 */
	public void setTitle(boolean pModified)
	{
		if(aFile != null)
		{
			String title = aFile.getName();
			if(pModified)
			{
				if(!getTitle().endsWith("*"))
				{
					setTitle(title + "*");
				}
			}
			else
			{
				setTitle(title);
			}
		}
	}

	/**
     * Gets the file property.
     * @return the file associated with this graph
	 */
	public File getFileName()
	{
		return aFile;
	}

	/**
     * Sets the file property.
     * @param pFile The file associated with this graph
	 */
	public void setFile(File pFile)
	{
		aFile = pFile;
		setTitle(aFile.getName());
	}
		
	 private class TabButton extends JButton implements ActionListener {
	        public TabButton() {
	            int size = 17;
	            setPreferredSize(new Dimension(size, size));
	            setToolTipText("Close This Diagram");
	            //Make the button looks the same for all Laf's
	            setUI(new BasicButtonUI());
	            //Make it transparent
	            setContentAreaFilled(false);
	            //No need to be focusable
	            setFocusable(false);
	            setBorder(BorderFactory.createEtchedBorder());
	            setBorderPainted(false);
	            //Making nice rollover effect
	            //we use the same listener for all buttons
	            addMouseListener(buttonMouseListener);
	            setRolloverEnabled(true);
	            //Close the proper tab by clicking the button
	            addActionListener(this);
	        }
	        
	        public void actionPerformed(ActionEvent e) {
	            int i = aPane.indexOfTabComponent(GraphFrame.this);
	            if (i != -1) 
	            {
	                EditorFrame aEditorFrame = (EditorFrame) aPane.getParent();
	                aEditorFrame.close(GraphFrame.this);
	            }
	        }
	        
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2 = (Graphics2D) g.create();
	            //shift the image for pressed buttons
	            if (getModel().isPressed()) 
	            {
	                g2.translate(1, 1);
	            }
	            g2.setStroke(new BasicStroke(2));
	            g2.setColor(Color.BLACK);
	            if (getModel().isRollover()) {
	                g2.setColor(Color.MAGENTA);
	            }
	            int delta = 6;
	            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
	            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);
	            g2.dispose();
	        }
	 }
	 private final static MouseListener buttonMouseListener = new MouseAdapter() {
	        public void mouseEntered(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) 
	            {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(true);
	            }
	        }
	 
	        public void mouseExited(MouseEvent e) {
	            Component component = e.getComponent();
	            if (component instanceof AbstractButton) 
	            {
	                AbstractButton button = (AbstractButton) component;
	                button.setBorderPainted(false);
	            }
	        }
	 };
}	        
