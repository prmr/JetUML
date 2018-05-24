/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2018 by the contributors of the JetUML project.
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

package ca.mcgill.cs.jetuml.gui;

import java.io.File;

import ca.mcgill.cs.jetuml.diagram.ClassDiagram;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.ObjectDiagram;
import ca.mcgill.cs.jetuml.diagram.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagram.StateDiagram;
import ca.mcgill.cs.jetuml.diagram.UseCaseDiagram;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

/**
 *A tab holding a single diagram.
 */
public class DiagramTab extends Tab
{	
	private TabPane aTabbedPane;
	private GraphPanel aPanel;
	private File aFile; // The file associated with this graph
	
	/**
     * Constructs a graph frame with an empty tool bar.
     * @param pGraph the initial graph
     * @param pTabbedPane the TabPane associated with this DiagramTab.
	 */
	public DiagramTab(Diagram pGraph, TabPane pTabbedPane)
	{
		aTabbedPane = pTabbedPane;
		DiagramFrameToolBar sideBar = new DiagramFrameToolBar(pGraph);
		aPanel = new GraphPanel(pGraph, sideBar, Screen.getPrimary().getVisualBounds());
		aPanel.paintPanel();
		
		BorderPane layout = new BorderPane();
		layout.setRight(sideBar);
		ScrollPane scroll = new ScrollPane(aPanel);
		
		// The call below is necessary to removes the focus highlight around the Canvas
		// See issue #250
		scroll.setStyle("-fx-focus-color: transparent; -fx-faint-focus-color: transparent;"); 
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollBarPolicy.NEVER);
		layout.setCenter(scroll);
		
		setTitle(false);
		setContent(layout);

		setOnCloseRequest(pEvent -> 
		{
			pEvent.consume();
			EditorFrame editorFrame = (EditorFrame) getTabPane().getParent();
			editorFrame.close(this);
		});
	}

	/**
     * Gets the graph that is being edited in this frame.
     * @return the graph
	 */
	public Diagram getGraph()
	{
		return aPanel.getGraph();
	}
	
	/**
	 * Shoes or hides the textual description of the tools and commands.
	 * 
	 * @param pShow True if the labels are to be shown
	 */
	public void showToolbarButtonLabels(boolean pShow)
	{
		((DiagramFrameToolBar)((BorderPane)getContent()).getRight()).showButtonLabels(pShow);
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
	 * This association and getter method are needed to display messages using the copy to clipboard
	 * functionality of the Optional DiagramFrameToolBar.
	 * @return aTabbedPane the TabPane associated with this DiagramTab.
	 */
	public TabPane getTabbedPane()
	{
		return aTabbedPane;
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
				if(!getText().endsWith("*"))
				{
					setText(title + "*");
				}
			}
			else
			{
				setText(title);
			}
		}
		else
		{
			Diagram graphType = getGraph();
			if (graphType instanceof UseCaseDiagram)
			{
				setText("Use Case Diagram");
			} 
			else if (graphType instanceof StateDiagram)
			{
				setText("State Diagram");
			} 
			else if (graphType instanceof ClassDiagram)
			{
				setText("Class Diagram");
			} 
			else if (graphType instanceof SequenceDiagram)
			{
				setText("Sequence Diagram");
			} 
			else if (graphType instanceof ObjectDiagram)
			{
				setText("Object Diagram");
			} 
			else 
			{
				setText("Not supported in JavaFX");
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
		setTitle(false);
	}
}	        
