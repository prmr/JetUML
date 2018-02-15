/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015-2017 by the contributors of the JetUML project.
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

import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.graph.Edge;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.graph.GraphElement;
import ca.mcgill.cs.jetuml.graph.Node;
import ca.mcgill.cs.jetuml.views.IconCreator;
import ca.mcgill.cs.jetuml.views.ImageCreator;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;

/**
 *  A collapsible tool bar than contains various tools and optional
 *  command shortcut buttons. Only one tool can be selected at the time.
 *  The tool bar also controls a pop-up menu with the same tools as 
 *  the tool bar.
 *  
 *  @author Martin P. Robillard
 */
public class ToolBar extends BorderPane
{
	private static final double BUTTON_HEIGHT = 20;
	private static final int PADDING = 5;
	private static final int FONT_SIZE = 14;
	private static final String EXPAND = "<<";
	private static final String COLLAPSE = ">>";
	
	private ArrayList<ToggleButton> aButtons = new ArrayList<>();
	private ArrayList<ToggleButton> aButtonsEx = new ArrayList<>();
	private FlowPane aToolsLayout = new FlowPane(Orientation.VERTICAL, PADDING, PADDING);
	private FlowPane aToolsLayoutEx = new FlowPane(Orientation.VERTICAL, PADDING, PADDING);
	private BorderPane aLayout = new BorderPane();
	private BorderPane aLayoutEx = new BorderPane();
	private ArrayList<GraphElement> aTools = new ArrayList<>();
	private JPopupMenu aPopupMenu = new JPopupMenu();

	/**
     * Constructs the tool bar.
     * @param pGraph The graph associated with this tool bar.
	 */
	public ToolBar(Graph pGraph)
	{
		ToggleGroup group = new ToggleGroup();
		ToggleGroup groupEx = new ToggleGroup();
		aToolsLayout.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
		// Adjust preferred height to use all available vertical space
		aToolsLayout.setPrefHeight(Double.MAX_VALUE);
		aLayout.setCenter(aToolsLayout);
		
		aToolsLayoutEx.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
		// Adjust preferred height to use all available vertical space
		aToolsLayoutEx.setPrefHeight(Double.MAX_VALUE);
		aLayoutEx.setCenter(aToolsLayoutEx);
		
		createSelectionTool(group, groupEx);
		createNodesAndEdgesTools(pGraph, group, groupEx);
		addCopyToClipboard();
		createExpandButton();
		setCenter(aLayout);
		
	}
	
	private void createSelectionTool(ToggleGroup pGroup, ToggleGroup pGroupEx)
	{
		installTool(ImageCreator.createSelectionImage(), IconCreator.createSelectionIcon(), 
				ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("grabber.tooltip"), 
				null, true, pGroup, pGroupEx);
	}
	
	/*
	 * Adds a tool to the tool bars and menus.
	 * @param pImage The image for the tool
	 * @param pIcon The icon for the popup menu
	 * @param pToolTip the tool's tool tip
	 * @param pTool the object representing the tool
	 * @param pIsSelected true if the tool is initially selected.
	 */
	private void installTool( Image pImage, 
				Icon pIcon, 
				String pToolTip, 
				GraphElement pTool, 
				boolean pIsSelected, 
				ToggleGroup pCollapsed, 
				ToggleGroup pExpanded )
	{
		final ToggleButton button = new ToggleButton();
		button.setGraphic(new ImageView(pImage));
		button.setToggleGroup(pCollapsed);
		aButtons.add(button);
		button.setSelected(pIsSelected);
		aToolsLayout.getChildren().add(button);
		aTools.add(pTool);
		
		final ToggleButton buttonEx = new ToggleButton();
		buttonEx.setGraphic(new ImageView(pImage));
		buttonEx.setToggleGroup(pExpanded);
		aButtonsEx.add(buttonEx);
		buttonEx.setSelected(pIsSelected);
		aToolsLayoutEx.getChildren().add(createExpandedRowElement(buttonEx, pToolTip));
		
		Tooltip toolTip = new Tooltip(pToolTip);
		Tooltip.install(button, toolTip);
		Tooltip.install(buttonEx, toolTip);
      
		button.setOnAction(pEvent->
		{
			button.setSelected(true);
			buttonEx.setSelected(true);
		});
		buttonEx.setOnAction(pEvent->
		{
			button.setSelected(true);
			buttonEx.setSelected(true);
		});
		
		JMenuItem item = new JMenuItem(pToolTip, pIcon);
		item.addActionListener(pEvent -> 
		{
			Platform.runLater(()->
			{
				button.setSelected(true);
				buttonEx.setSelected(true);
			});
		});
		aPopupMenu.add(item);
	}
	
	/*
	 * Return a HBox pane with a button on the left and a label on the right
	 */
	private HBox createExpandedRowElement(ButtonBase pButton, String pToolTip)
	{
		Label buttonLabel = new Label(pToolTip);
		Font font = new Font(buttonLabel.getFont().getName(), FONT_SIZE);
		buttonLabel.setFont(font);
		HBox buttonLayout = new HBox(PADDING);
		buttonLayout.getChildren().addAll(pButton, buttonLabel);
		return buttonLayout;
	}
	
	private void createNodesAndEdgesTools(Graph pGraph, ToggleGroup pGroup, ToggleGroup pGroupEx)
	{
		ResourceBundle resources = ResourceBundle.getBundle(pGraph.getClass().getName() + "Strings");

		Node[] nodeTypes = pGraph.getNodePrototypes();
		for(int i = 0; i < nodeTypes.length; i++)
		{
			installTool(ImageCreator.createImage(nodeTypes[i]), IconCreator.createIcon(nodeTypes[i]), 
					resources.getString("node" + (i + 1) + ".tooltip"), nodeTypes[i], false, pGroup, pGroupEx);
		}
		
		Edge[] edgeTypes = pGraph.getEdgePrototypes();
		for(int i = 0; i < edgeTypes.length; i++)
		{
			installTool(ImageCreator.createImage(edgeTypes[i]), IconCreator.createIcon(edgeTypes[i]), 
					resources.getString("edge" + (i + 1) + ".tooltip"), edgeTypes[i], false, pGroup, pGroupEx);
		}
	}
	
	/**
     * Gets the node or edge prototype that is associated with
     * the currently selected button.
     * @return a Node or Edge prototype
	 */
	public GraphElement getSelectedTool()
	{
		return aTools.get(getSelectedButtonIndex());
	}
	
	/**
	 * Overrides the currently selected tool to be the grabber tool instead.
	 */
	public void setToolToBeSelect()
	{
		for( ToggleButton button : aButtons )
		{
			button.setSelected(false);
		}
		for( ToggleButton button : aButtonsEx )
		{
			button.setSelected(false);
		}
		aButtons.get(0).setSelected(true);
		aButtonsEx.get(0).setSelected(true);
	}


	private void addCopyToClipboard()
	{
		String imageLocation = getClass().getClassLoader().
				getResource(ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").
						getString("toolbar.copyToClipBoard")).toString();
		String toolTipText = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("file.copy_to_clipboard.text");
		
		final Button button = new Button();
		ImageView buttonImageView = new ImageView(imageLocation);
		final Button buttonEx = new Button();
		ImageView buttonExImageView = new ImageView(imageLocation);
		
		if( aButtonsEx.size() > 0 )
		{
			button.prefWidthProperty().bind(aButtons.get(0).widthProperty());
			button.prefHeightProperty().bind(aButtons.get(0).heightProperty());
			buttonEx.prefWidthProperty().bind(aButtons.get(0).widthProperty());
			buttonEx.prefHeightProperty().bind(aButtons.get(0).heightProperty());
		}

		button.setGraphic(buttonImageView);
		buttonEx.setGraphic(buttonExImageView);
		Tooltip toolTip = new Tooltip(toolTipText);
		Tooltip.install(button, toolTip);
		Tooltip.install(buttonEx, toolTip);

		aToolsLayout.getChildren().add(button);
		aToolsLayoutEx.getChildren().add(createExpandedRowElement(buttonEx, toolTipText));
		
		button.setOnAction(pEvent-> copyToClipboard());
		buttonEx.setOnAction(pEvent-> copyToClipboard());
	}
	
	private void copyToClipboard()
	{
		// Obtain the editor frame by going through the component graph
		Parent parent = getParent();
		while( parent.getClass() != EditorFrame.class )
		{
			parent = parent.getParent();
		}
		((EditorFrame)parent).copyToClipboard();	
	}
	
	private void createExpandButton()
	{
		final Button expandButton = new Button(EXPAND);
		final Button collapseButton = new Button(COLLAPSE);
		final String expandString = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("toolbar.expand");
		final String collapseString = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings").getString("toolbar.collapse");
		Tooltip expandToolTip = new Tooltip(expandString);
		Tooltip collapseToolTip = new Tooltip(collapseString);
		Tooltip.install(expandButton, expandToolTip);
		Tooltip.install(collapseButton, collapseToolTip);
		
		expandButton.setPrefHeight(BUTTON_HEIGHT);
		expandButton.setMaxWidth(Double.MAX_VALUE);
		collapseButton.setPrefHeight(BUTTON_HEIGHT);
		collapseButton.setMaxWidth(Double.MAX_VALUE);
		
		expandButton.setOnAction(pEvent ->
		{
			synchronizeToolSelection();
			setCenter(aLayoutEx);

		});
		collapseButton.setOnAction(pEvent -> 
		{
			synchronizeToolSelection();
			setCenter(aLayout);
		});
		aLayout.setBottom(expandButton);
		aLayoutEx.setBottom(collapseButton);
	}
	
	private void synchronizeToolSelection()
	{
		int index = getSelectedButtonIndex();
		assert index >= 0;
		aButtons.get(index).setSelected(true);
		aButtonsEx.get(index).setSelected(true);
	}

	private int getSelectedButtonIndex()
	{
		ArrayList<ToggleButton> activeButtons = aButtons;
		if( isExpanded() )
		{
			activeButtons = aButtonsEx;
		}
		for(int i = 0; i < activeButtons.size(); i++)
		{
			ToggleButton button = activeButtons.get(i);
			if(button.isSelected())
			{
				return i;
			}
		}
		return -1;
	}
	
	/*
	 * The toolbar is expanded iff the main panel contains
	 * the expanded toolbar as one of its components.
	 */
	private boolean isExpanded()
	{
		if(getCenter() == aLayoutEx) 
		{
			return true;
		}
		return false;
	}
	
	/**
	 * Show the pop-up menu corresponding to this toolbar.
	 * @param pPanel The panel associated with this menu.
	 * @param pPoint The point where to show the menu.
	 */
	public void showPopup(GraphPanel pPanel, Point pPoint) 
	{
		aPopupMenu.show(pPanel, pPoint.getX(), pPoint.getY());
	}
}