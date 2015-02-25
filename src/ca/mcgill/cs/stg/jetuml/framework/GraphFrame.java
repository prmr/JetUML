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

import java.awt.BorderLayout;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.File;
import java.util.ResourceBundle;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 *A frame for showing a graphical editor.
 */
@SuppressWarnings("serial")
public class GraphFrame extends JInternalFrame
{
	private Graph aGraph;
	private GraphPanel aPanel;
	private ToolBar aToolBar;
	private File aFile; // The file associated with this graph
	
	/**
     * Constructs a graph frame with an empty tool bar.
     * @param pGraph the initial graph
	 */
	public GraphFrame(Graph pGraph)
	{
		aGraph = pGraph;
		aToolBar = new ToolBar(aGraph);
		aPanel = new GraphPanel(aToolBar);
		Container contentPane = getContentPane();
		contentPane.add(aToolBar, BorderLayout.NORTH);
		contentPane.add(new JScrollPane(aPanel), BorderLayout.CENTER);
      
		// add listener to confirm frame closing
		addVetoableChangeListener(new VetoableChangeListener()
		{
			public void vetoableChange(PropertyChangeEvent pEvent) throws PropertyVetoException
            {  
				String name = pEvent.getPropertyName();
				Object value = pEvent.getNewValue();

				// we only want to check attempts to close a frame
				if(name.equals("closed") && value.equals(Boolean.TRUE) && aPanel.isModified())
				{  
					ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");                  
                  
					// ask user if it is ok to close
					int result = JOptionPane.showInternalConfirmDialog(GraphFrame.this, 
							editorResources.getString("dialog.close.ok"), null, JOptionPane.YES_NO_OPTION);

					// if the user doesn't agree, veto the close
					if(result != JOptionPane.YES_OPTION) 
					{
						throw new PropertyVetoException("User canceled close", pEvent);
					}
				}
            }           
		});
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
}
