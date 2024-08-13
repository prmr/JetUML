/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020, 2021 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.gui.tips;

import static org.jetuml.application.ApplicationResources.RESOURCES;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.jetuml.application.UserPreferences;
import org.jetuml.application.UserPreferences.IntegerPreference;
import org.jetuml.gui.tips.TipLoader.Tip;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * A window that presents the tip of the day (by calling show()).
 */
public class TipDialog 
{
	private static final int WINDOW_PREF_WIDTH = 650;
	private static final int WINDOW_PREF_HEIGHT = 450;
	private static final int WINDOW_MIN_WIDTH = 650;
	private static final int WINDOW_MIN_HEIGHT = 450;
	private static final int PADDING = 15;
	private static final int MENU_NAVIGATION_BUTTONS_SPACING = 10;
	private static final int MENU_CLOSE_BUTTON_SPACING = 30;
	private static final int TIP_ELEMENTS_SPACING = 10;
	private static final double TITLE_FONT_SIZE = 23;
	private static final double TEXT_FONT_SIZE = 13.5;
	private static final double TEXT_LINE_SPACING = 2;
	private static final double TEXT_WIDTH = 570;
	private static final Insets IMAGE_PADDING = new Insets(25, 0, 25, 0);
	private static final double DEFAULT_NODE_SPACING = 10;
	
	private Stage aStage;
	private Tip aCurrentTip;
	private ViewedTips aViewedTips;
	private final ScrollPane aTipDisplay;
	private final CheckBox aShowTipsOnStartupCheckBox;

	/**
	 * Constructor for a TipDialog.
	 * 
	 * @param pDialogStage The stage of the window that generates the tip dialog.
	 *        pOwner can be null to get a TipDialog with no owner.
	 */
	public TipDialog(Stage pDialogStage)
	{
		aStage = pDialogStage;
		aTipDisplay = new ScrollPane();
		aViewedTips = new ViewedTips(getUserPrefNextTipId());
		aShowTipsOnStartupCheckBox = new CheckBox(RESOURCES.getString("dialog.tips.checkbox.text"));
		aShowTipsOnStartupCheckBox.setSelected(UserPreferences.instance().getBoolean(UserPreferences.BooleanPreference.showTips));
		aShowTipsOnStartupCheckBox.setOnAction(e -> UserPreferences.instance().setBoolean(UserPreferences.BooleanPreference.showTips, 
				aShowTipsOnStartupCheckBox.isSelected()));
	}
	
	/**
	 * Shows the tip dialog and blocks the remainder of the UI
	 * until the tip dialog is closed.
	 */
	public void show() 
	{
		prepareStage();
		aStage.showAndWait();
	}
	
	private void prepareStage() 
	{
		aStage.setTitle(RESOURCES.getString("dialog.tips.title"));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
		aStage.getScene().setRoot(createRoot());
		aTipDisplay.requestFocus();
	}
	
	private Pane createRoot() 
	{
		BorderPane layout = new BorderPane();

		//Removing the focus highlight on the ScrollPane
		aTipDisplay.setStyle("-fx-background-color: -fx-outer-border, -fx-inner-border, -fx-body-color;" + 
							 "-fx-background-insets: 0, 1, 2;");
		
		aTipDisplay.setFitToWidth(true);
		layout.setCenter(aTipDisplay);
		
		HBox tipMenu = createTipMenu();
		layout.setBottom(tipMenu);
		
		Tip tip = TipLoader.loadTip(getUserPrefNextTipId());
		setUserPrefNextTip(aViewedTips.getNewNextTipOfTheDayId());
		setupNewTip(tip);
		
		layout.setMinWidth(WINDOW_MIN_WIDTH);
		layout.setMaxWidth(WINDOW_PREF_WIDTH);
		layout.setMinHeight(WINDOW_MIN_HEIGHT);
		layout.setMaxHeight(WINDOW_PREF_HEIGHT);
		
		return layout;
	}

	private HBox createTipMenu()
	{
		HBox tipMenu = createEmptyTipMenu();
		HBox tipMenuButtons = createTipMenuButtons();
		HBox emptyBox = new HBox();
		HBox.setHgrow(emptyBox, Priority.ALWAYS);
		
		tipMenu.getChildren().addAll(aShowTipsOnStartupCheckBox, emptyBox, tipMenuButtons);
		
		return tipMenu;
	}
	
	private static HBox createEmptyTipMenu() 
	{
		HBox tipMenu = new HBox();
		tipMenu.setPadding(new Insets(PADDING));
		tipMenu.getStyleClass().add("tip-menu");
		return tipMenu;
	}
	
	private HBox createTipMenuButtons()
	{
		Button nextTipButton = new Button(RESOURCES.getString("dialog.tips.button.next.text"));

		Button previousTipButton = new Button(RESOURCES.getString("dialog.tips.button.previous.text"));
		
		Button closeButton = new Button(RESOURCES.getString("dialog.tips.button.close.text"));
		
		nextTipButton.setOnAction(e -> 
		{ 
			int nextTipId = aViewedTips.getNextTipId();
			Tip nextTip = TipLoader.loadTip(nextTipId);
			setUserPrefNextTip(aViewedTips.getNewNextTipOfTheDayId());
			setupNewTip(nextTip);
		});
		
		previousTipButton.setOnAction(e -> 
		{ 
			int previousTipId = aViewedTips.getPreviousTipId();
			Tip previousTip = TipLoader.loadTip(previousTipId);
			setupNewTip(previousTip);
		});
	
		closeButton.setOnAction(e -> 
		{
			aStage.close();
		});
		
		HBox navigationButtons = new HBox(MENU_NAVIGATION_BUTTONS_SPACING);
		navigationButtons.getChildren().addAll(previousTipButton, nextTipButton);
		
		HBox tipMenuButtons = new HBox(MENU_CLOSE_BUTTON_SPACING);
		tipMenuButtons.getChildren().addAll(navigationButtons, closeButton);
		
		return tipMenuButtons;
	}
	
	/**
	 * @pre pTip != null
	 */
	private void setupNewTip(Tip pTip)
	{
		assert pTip != null;
		
		this.aCurrentTip = pTip;
		VBox tipVBox = getTipAsVBox(aCurrentTip);
		tipVBox.setFillWidth(true);
		aTipDisplay.setContent(tipVBox);
	}
	
	/**
	 * @pre pTip != null
	 */
	private static VBox getTipAsVBox(Tip pTip) 
	{
		assert pTip != null;
		
		VBox tipVBox = new VBox();
		tipVBox.setSpacing(TIP_ELEMENTS_SPACING);
		tipVBox.setPadding(new Insets(PADDING));
		
		List<TipElement> tipElements = pTip.getElements();
		
		Node titleNode = getTipTitleAsLabel(pTip);
		tipVBox.getChildren().add(titleNode);
		
		for(TipElement tipElement : tipElements)
		{
			Node node = getTipElementAsNode(tipElement, tipVBox);
			tipVBox.getChildren().add(node);
		}
		
		return tipVBox;
	}
	
	/**
	 * @param pTip a Tip
	 * @return Formatted Node containing the tip's title
	 * @pre pTip!= null
	 */
	private static Label getTipTitleAsLabel(Tip pTip)
	{
		assert pTip != null;
		
		String title = pTip.getTitle();
		Label titleNode = new Label(title);
		Font titleFont = new Font(TITLE_FONT_SIZE);
		titleNode.setFont(titleFont);
		return titleNode;
	}
	
	/**
	 * Returns a Node that presents the content of a TipElement. Remark that the preconditions
	 * will change if new media are used in TipElements
	 * 
	 * @param pTipElement the TipElement to get as a Node
	 * @param pParent the VBox that will eventually contain the tipElement. This parameter is
	 * 		  necessary to ensure that dimensions and properties of the TipElement Nodes such 
	 * 		  as text wrapping width are appropriate.
	 * @return node containing the tip element content
	 * @pre pTipElement != null;
	 * @pre pTipElement.getMedia().equals(Media.TEXT) || pTipElement.getMedia().equals(Media.IMAGE)
	 */
	private static Node getTipElementAsNode(TipElement pTipElement, VBox pParent)
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.TEXT) || pTipElement.getMedia().equals(Media.IMAGE);
		
		Media media = pTipElement.getMedia();
		if(media.equals(Media.TEXT))
		{
			return getTextTipElementAsLabel(pTipElement);
		}
		else // media.equals(Media.IMAGE) by @pre
		{
			HBox imageContainer = new HBox(getImageTipElementAsImageView(pTipElement));
			imageContainer.setAlignment(Pos.CENTER);
			VBox.setMargin(imageContainer, IMAGE_PADDING);
			return imageContainer;
		}
	}
	
	/**
	 * @return Text Node presenting the content of the text TipElement
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.TEXT);
	 */
	private static Label getTextTipElementAsLabel(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.TEXT);
		
		String text = pTipElement.getContent();
		Label textNode = new Label(text);
		textNode.setWrapText(true);
		textNode.setPrefWidth(TEXT_WIDTH);
		
		Font textFont = new Font(TEXT_FONT_SIZE);
		textNode.setFont(textFont);
		textNode.setLineSpacing(TEXT_LINE_SPACING);
		
		return textNode;
	}
	
	/**
	 * @return ImageView of the Image whose name is stored in the image TipElement
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.IMAGE)
	 */
	private static ImageView getImageTipElementAsImageView(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.IMAGE);
		
		String imageName = pTipElement.getContent();
		String tipImagesDir = RESOURCES.getString("tips.images.directory");
		try(InputStream inputStream = TipDialog.class.getResourceAsStream(tipImagesDir + "/" + imageName))
		{
			Image image = new Image(inputStream);
			ImageView imageNode = new ImageView(image);
			if(imageNode.getImage().getWidth() > WINDOW_PREF_WIDTH - 2 * PADDING - 4 * DEFAULT_NODE_SPACING)
			{
				imageNode.setPreserveRatio(true);
				imageNode.setFitWidth(WINDOW_PREF_WIDTH - 2 * PADDING - 4 * DEFAULT_NODE_SPACING);
				// two times the padding because of the VBox padding, and a bit extra to make up for
				// other default spacing added between nodes
			}
			return imageNode;
		}
		catch( IOException e )
		{
			// The unit tests check that all tip images can be loaded properly.
			assert false;
			return null;
		}
	}
	
	private static int getUserPrefNextTipId()
	{
		return UserPreferences.instance().getInteger(IntegerPreference.nextTipId);
	}
	
	private static void setUserPrefNextTip(int pId)
	{
		UserPreferences.instance().setInteger(IntegerPreference.nextTipId, pId);
	}
}	
