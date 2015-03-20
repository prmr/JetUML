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

import java.io.File;

import javax.swing.JInternalFrame;
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
		contentPane.add(aToolBar, BorderLayout.EAST);
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
}	        
