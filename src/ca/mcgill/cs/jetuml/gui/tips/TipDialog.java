package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.gui.tips.TipLoader.Tip;
import java.io.InputStream;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * A window that presents the tip of the day.
 */
public class TipDialog 
{
	private static final int WINDOW_MIN_WIDTH = 650;
	private static final int WINDOW_MIN_HEIGHT = 400;
	private static final int SCENE_WIDTH = 635;
	private static final int SCENE_HEIGHT = 385;
	private static final int MENU_CHECKBOX_SPACING = 200;
	private static final int PADDING = 15;
	private static final int MENU_NAVIGATION_BUTTONS_SPACING = 10;
	private static final int MENU_CLOSE_BUTTON_SPACING = 30;
	private static final int TIP_ELEMENTS_SPACING = 10;
	private static final double TITLE_FONT_SIZE = 22;
	private static final String NEXT_BUTTON_STYLE = "next-tip-button";
	private static final String BUTTON_STYLE = "tip-menu-button";
	private static final String WINDOW_TITLE = "Tip of the Day";
	
	private final ScrollPane aTipDisplay = new ScrollPane();
	private final Stage aStage;
	private Tip aCurrentTip;
	private ViewedTips aViewedTips;


	/**
	 * Constructor for a TipDialog.
	 * 
	 * @param pOwner The stage of the window that generates the tip dialog.
	 *        pOwner can be null to get a TipDialog with no owner.
	 */
	public TipDialog(Stage pOwner)
	{
		
		int nextTipOfTheDayId = getUserPrefNextTipId();
		
		//Handling the case where the number of tips changed and the 
		//user preference for the tip id is no longer a valid id
		if (nextTipOfTheDayId > NUM_TIPS)
		{
			nextTipOfTheDayId = 1;
			setUserPrefNextTip(1);
		}
		
		aViewedTips = new ViewedTips(getUserPrefNextTipId());
		aStage = new Stage();
		aTipDisplay.setMinWidth(SCENE_WIDTH);
		prepareStage(pOwner);
	}
	
	/**
	 * Shows the dialog and blocks the remainder of the UI
	 * until it is closed.
	 */
	public void show() 
	{
        aStage.showAndWait();
    }
	
	/**
	 * @pre pOwner != null
	 */
	private void prepareStage(Stage pOwner) 
	{
		assert pOwner != null;
		
		aStage.setResizable(true);
		aStage.setMinWidth(0);
		aStage.setMinHeight(WINDOW_MIN_HEIGHT);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(WINDOW_TITLE);
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
		aStage.setScene(createScene());
		aStage.getScene().getStylesheets().add(getClass().getResource("TipDialog.css").toExternalForm());
	}
	
	private Scene createScene() 
	{
		BorderPane layout = new BorderPane();
		
		HBox tipMenu = createTipMenu();
		
		aCurrentTip = TipLoader.loadTip(getUserPrefNextTipId());
		setUserPrefNextTip(aViewedTips.getNewNextTipOfTheDayId());
		VBox tipVBox = getTipAsVBox(aCurrentTip);
		
		aTipDisplay.setContent(tipVBox);
		
		layout.setCenter(aTipDisplay);
		layout.setBottom(tipMenu);
		
		aStage.requestFocus();
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if (pEvent.getCode() == KeyCode.ESCAPE) 
			{
				aStage.close();
			}
		});
		
		Scene tipDialogScene = new Scene(layout, SCENE_WIDTH, SCENE_HEIGHT);	
		
		return tipDialogScene;
	}

	private HBox createTipMenu()
	{
		HBox tipMenu = createEmptyTipMenu();
		
		CheckBox showOnStartupCheckBox = new CheckBox("Show Tips on Sartup");
		showOnStartupCheckBox.setSelected(true);
		HBox tipMenuButtons = createTipMenuButtons(showOnStartupCheckBox);
		
		tipMenu.getChildren().addAll(showOnStartupCheckBox, tipMenuButtons);
		
		return tipMenu;
	}
	
	private static HBox createEmptyTipMenu() 
	{
		HBox tipMenu = new HBox(MENU_CHECKBOX_SPACING);
		tipMenu.setPadding(new Insets(PADDING));
		tipMenu.setStyle("-fx-background-color: gainsboro;");
		tipMenu.setBorder(new Border(new BorderStroke(Color.DARKGRAY, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		return tipMenu;
	}
	
	private HBox createTipMenuButtons(CheckBox pShowOnStartupCheckBox)
	{
		Button nextTipButton = new Button("Next Tip");
		nextTipButton.getStyleClass().add(NEXT_BUTTON_STYLE);
		
		Button previousTipButton = new Button("Previous Tip");
		previousTipButton.getStyleClass().add(BUTTON_STYLE);
		
		Button closeButton = new Button("Close");
		closeButton.getStyleClass().add(BUTTON_STYLE);
		
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
			boolean shouldShowOnStartup = pShowOnStartupCheckBox.isSelected();
			if(!shouldShowOnStartup)
			{
				UserPreferences.instance().setBoolean(UserPreferences.BooleanPreference.showTips, false);
			}
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
		aTipDisplay.setContent(tipVBox);
	}
	
	/**
	 * @pre pTip != null
	 */
	private VBox getTipAsVBox(Tip pTip) 
	{
		assert pTip != null;
		
		VBox tipVBox = new VBox();
		tipVBox.setSpacing(TIP_ELEMENTS_SPACING);
		
		tipVBox.setPadding(new Insets(PADDING));
		List<TipElement> tipElements = pTip.getElements();
		
		Node titleNode = getTipTitleAsNode(pTip);
		tipVBox.getChildren().add(titleNode);
		
		for(TipElement tipElement : tipElements)
		{
			Node node = this.getTipElementAsNode(tipElement);
			tipVBox.getChildren().add(node);
		}
		
		return tipVBox;
	}
	
	/**
	 * @param pTip a Tip
	 * @return Formatted Node containing the tip's title
	 * @pre pTip!= null
	 */
	private static Node getTipTitleAsNode(Tip pTip)
	{
		assert pTip != null;
		
		String title = pTip.getTitle();
		Text titleNode = new Text(title);
		Font titleFont = new Font(TITLE_FONT_SIZE);
		titleNode.setFont(titleFont);
		return titleNode;
	}
	
	/**
	 * @return node containing the tip element content
	 * @pre pTipElement != null;
	 * @pre pTipElement.getMedia().equals(Media.TEXT) || pTipElement.getMedia().equals(Media.IMAGE)
	 */
	private Node getTipElementAsNode(TipElement pTipElement)
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.TEXT) || pTipElement.getMedia().equals(Media.IMAGE);
		
		Media media = pTipElement.getMedia();
		if(media.equals(Media.TEXT))
		{
			return this.getTextTipElementAsNode(pTipElement);
		}
		else // media.equals(Media.IMAGE) by @pre
		{
			return getImageTipElementAsNode(pTipElement);
		}
	}
	
	/**
	 * @return node containing the text
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.TEXT);
	 */
	private Node getTextTipElementAsNode(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.TEXT);
		
		String text = pTipElement.getContent();
		Text textNode = new Text(text);
		textNode.wrappingWidthProperty().bind(aTipDisplay.widthProperty().subtract(2 * PADDING));
		
		return textNode;
	}
	
	/**
	 * @return node containing the image
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.IMAGE)
	 */
	private static Node getImageTipElementAsNode(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.IMAGE);
		
		String imageName = pTipElement.getContent();
		String tipImagesDir = RESOURCES.getString("tips.images.directory");
		InputStream inputStream = TipDialog.class.getResourceAsStream(tipImagesDir + "/" + imageName);
		Image image = new Image(inputStream);
		ImageView imageNode = new ImageView(image);
		if(imageNode.getImage().getWidth() >= SCENE_WIDTH - 2 * PADDING)
		{
			imageNode.setPreserveRatio(true);
			imageNode.setFitWidth(SCENE_WIDTH - 2 * PADDING); 
		}
		
		return imageNode;
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
