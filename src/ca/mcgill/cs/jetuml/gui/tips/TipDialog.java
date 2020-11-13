package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;
import static ca.mcgill.cs.jetuml.gui.tips.TipLoader.NUM_TIPS;

import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.gui.tips.TipLoader.Tip;
import java.io.InputStream;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.skin.ScrollPaneSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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
	private static final String NEXT_BUTTON_STYLE = "next-tip-button";
	private static final String BUTTON_STYLE = "tip-menu-button";
	private static final String WINDOW_TITLE = "Tip of the Day";
	
	private final ScrollPane aTipDisplay;
	private Stage aStage;
	private Stage aOwner;
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
		
		aTipDisplay = new ScrollPane();
		aViewedTips = new ViewedTips(getUserPrefNextTipId());
		aOwner = pOwner;
	}
	
	/**
	 * Shows the dialog and blocks the remainder of the UI
	 * until it is closed.
	 */
	public void show() 
	{
		aStage = new Stage();
		prepareStage(aOwner);
        aStage.showAndWait();
    }
	
	/**
	 * @pre pOwner != null
	 */
	private void prepareStage(Stage pOwner) 
	{
		assert pOwner != null;
		
		aStage.setResizable(true);
		aStage.setMinWidth(WINDOW_MIN_WIDTH);
		aStage.setWidth(WINDOW_PREF_WIDTH);
		aStage.setMinHeight(WINDOW_MIN_HEIGHT);
		aStage.setHeight(WINDOW_PREF_HEIGHT);
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
		
		aTipDisplay.setFitToWidth(true);
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
		
		Scene tipDialogScene = new Scene(layout, WINDOW_PREF_WIDTH, WINDOW_PREF_HEIGHT);
		
		return tipDialogScene;
	}

	private HBox createTipMenu()
	{
		HBox tipMenu = createEmptyTipMenu();
		
		CheckBox showOnStartupCheckBox = new CheckBox("Show Tips on Sartup");
		showOnStartupCheckBox.setSelected(true);
		HBox tipMenuButtons = createTipMenuButtons(showOnStartupCheckBox);
		HBox emptyBox = new HBox();
		HBox.setHgrow(emptyBox, Priority.ALWAYS);
		
		tipMenu.getChildren().addAll(showOnStartupCheckBox, emptyBox, tipMenuButtons);
		
		return tipMenu;
	}
	
	private static HBox createEmptyTipMenu() 
	{
		HBox tipMenu = new HBox();
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
		tipVBox.setFillWidth(true);
		
		List<TipElement> tipElements = pTip.getElements();
		
		Node titleNode = getTipTitleAsTextNode(pTip);
		tipVBox.getChildren().add(titleNode);
		
		for(TipElement tipElement : tipElements)
		{
			Node node = this.getTipElementAsNode(tipElement, tipVBox);
			tipVBox.getChildren().add(node);
		}
		
		tipVBox.setAlignment(Pos.CENTER);
		
		return tipVBox;
	}
	
	/**
	 * @param pTip a Tip
	 * @return Formatted Node containing the tip's title
	 * @pre pTip!= null
	 */
	private static Text getTipTitleAsTextNode(Tip pTip)
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
	private Node getTipElementAsNode(TipElement pTipElement, VBox pParent)
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.TEXT) || pTipElement.getMedia().equals(Media.IMAGE);
		
		Media media = pTipElement.getMedia();
		if(media.equals(Media.TEXT))
		{
			return this.getTextTipElementAsTextNode(pTipElement);
		}
		else // media.equals(Media.IMAGE) by @pre
		{
			return this.getImageTipElementAsImageNode(pTipElement);
		}
	}
	
	/**
	 * @return node containing the text
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.TEXT);
	 */
	private Text getTextTipElementAsTextNode(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.TEXT);
		
		String text = pTipElement.getContent();
		Text textNode = new Text(text);
		textNode.wrappingWidthProperty().bind(aTipDisplay.widthProperty().subtract(4 * PADDING));
		// two times the padding because of the VBox padding, and a bit extra to make up for
		// other default spacing added between nodes
		
		Font titleFont = new Font(TEXT_FONT_SIZE);
		textNode.setFont(titleFont);
		textNode.setLineSpacing(TEXT_LINE_SPACING);
		
		return textNode;
	}
	
	/**
	 * @return node containing the image
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.IMAGE)
	 */
	private ImageView getImageTipElementAsImageNode(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.IMAGE);
		
		String imageName = pTipElement.getContent();
		String tipImagesDir = RESOURCES.getString("tips.images.directory");
		InputStream inputStream = TipDialog.class.getResourceAsStream(tipImagesDir + "/" + imageName);
		Image image = new Image(inputStream);
		ImageView imageNode = new ImageView(image);
		if(imageNode.getFitWidth() > aTipDisplay.getWidth()- 2 * PADDING)
		{
			imageNode.setPreserveRatio(true);
			imageNode.setFitWidth(aTipDisplay.getWidth() - 2 * PADDING);
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
