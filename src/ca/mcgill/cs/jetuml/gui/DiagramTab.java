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

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.io.File;

import ca.mcgill.cs.jetuml.diagram.Diagram;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;

/**
 *A tab holding a single diagram.
 */
public class DiagramTab extends Tab
{	
	private DiagramCanvas aPanel;
	private File aFile; // The file associated with this diagram
	
	/**
     * Constructs a diagram tab initialized with pDiagram.
     * @param pDiagram The initial diagram
	 */
	public DiagramTab(Diagram pDiagram)
	{
		DiagramTabToolBar sideBar = new DiagramTabToolBar(pDiagram);
		aPanel = new DiagramCanvas(pDiagram, sideBar, Screen.getPrimary().getVisualBounds());
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
     * @return The diagram being edited within this tab.
	 */
	public Diagram getDiagram()
	{
		return aPanel.getDiagram();
	}
	
	/**
	 * Shoes or hides the textual description of the tools and commands.
	 * 
	 * @param pShow True if the labels are to be shown
	 */
	public void showToolbarButtonLabels(boolean pShow)
	{
		((DiagramTabToolBar)((BorderPane)getContent()).getRight()).showButtonLabels(pShow);
	}

	/**
     * Gets the graph panel that is contained in this frame.
     * @return the graph panel
	 */
	public DiagramCanvas getGraphPanel()
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
			setText(RESOURCES.getString(getDiagram().getClass().getSimpleName().toLowerCase() + ".text"));
		}
	}

	/**
     * Gets the file property.
     * @return the file associated with this graph
	 */
	public File getFile()
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
