package ca.mcgill.cs.jetuml.gui.tips;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

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
	private static final int WINDOW_WIDTH = 650;
	private static final int WINDOW_HEIGHT = 400;
	private static final int MENU_CHECKBOX_SPACING = 200;
	private static final int MENU_PADDING = 15;
	private static final int MENU_NAVIGATION_BUTTONS_SPACING = 10;
	private static final int MENU_CLOSE_BUTTON_SPACING = 30;
	private static final double TITLE_FONT_SIZE = 22;
	private final Stage aStage = new Stage();
	
	private Tip aCurrentTip;

	/**
	 * Constructor for a TipDialog.
	 * 
	 * @param pOwner The stage of the window that generates the tip dialog
	 */
	public TipDialog( Stage pOwner )
	{
		prepareStage(pOwner);
		aStage.setScene(createScene());
	}
	
	private void prepareStage(Stage pOwner) 
	{
		aStage.setResizable(false);
		aStage.initModality(Modality.WINDOW_MODAL);
		aStage.initOwner(pOwner);
		aStage.setTitle(String.format("%s", RESOURCES.getString("dialog.tip.title")));
		aStage.getIcons().add(new Image(RESOURCES.getString("application.icon")));
	}
	
	private Scene createScene() 
	{
		BorderPane layout = new BorderPane();
		
		HBox tipMenu = createEmptyTipMenu();
		
		HBox tipMenuButtons = createTipMenuButtons();
		CheckBox showOnStartupCheckBox = new CheckBox("Show Tips on Sartup");
		showOnStartupCheckBox.setSelected(true);
		
		tipMenu.getChildren().addAll(showOnStartupCheckBox, tipMenuButtons);
		
		aCurrentTip = TipLoader.loadTipOfTheDay();
		getTipAsVBox(tipVBox);
		
		ScrollPane tipArea = new ScrollPane(tipVBox);
		
		layout.setCenter(tipArea);
		layout.setBottom(tipMenu);
		
		aStage.requestFocus();
		aStage.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if (pEvent.getCode() == KeyCode.ESCAPE) 
			{
				aStage.close();
			}
		});
		
		return new Scene(layout, WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	private static VBox getTipAsVBox(Tip pTip) 
	{
		VBox tipVBox = new VBox();
		List<TipElement> tipElements = pTip.getElements();
		
		// Adding title to VBox
		Node titleNode = getTipTitleAsNode(pTip);
		tipVBox.getChildren().add(titleNode);
		
		// Populating the VBox with the tip contents.
		for(TipElement tipElement : tipElements)
		{
			Node node = getTipElementAsNode(tipElement);
			tipVBox.getChildren().add(node);
		}
	}
	
	private HBox createEmptyTipMenu() 
	{
		HBox tipMenu = new HBox(MENU_CHECKBOX_SPACING);
		tipMenu.setPadding(new Insets(MENU_PADDING));
		tipMenu.setStyle("-fx-background-color: gainsboro;");
		tipMenu.setBorder(new Border(new BorderStroke(Color.DARKGRAY, 
	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		
		return tipMenu;
	}
	
	private HBox createTipMenuButtons()
	{
		Button nextTipButton = new Button("Next Tip");
		Button previousTipButton = new Button("Previous Tip");
		Button closeButton = new Button("Close");
		nextTipButton.setOnAction(e -> setUpNextTip());
		previousTipButton.setOnAction(e -> setUpPreviousTip());
		closeButton.setOnAction(e -> aStage.close());
		
		HBox navigationButtons = new HBox(MENU_NAVIGATION_BUTTONS_SPACING);
		navigationButtons.getChildren().addAll(previousTipButton, nextTipButton);
		
		
		HBox tipMenuButtons = new HBox(MENU_CLOSE_BUTTON_SPACING);
		tipMenuButtons.getChildren().addAll(navigationButtons, closeButton);
		
		return tipMenuButtons;
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
	 * @return node containing the image
	 * @pre pTipElement != null
	 * @pre pTipElement.getMedia().equals(Media.IMAGE)
	 */
	private Node getImageTipElementAsNode(TipElement pTipElement) 
	{
		assert pTipElement != null;
		assert pTipElement.getMedia().equals(Media.IMAGE);
		
		String imageName = pTipElement.getContent();
		String tipImagesDir = RESOURCES.getString("tips.images.directory");
		InputStream inputStream = TipDialog.class.getResourceAsStream(tipImagesDir + "/" + imageName);
		Image image = new Image(inputStream);
		ImageView imageNode = new ImageView(image);
		return imageNode;
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
		return textNode;
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
			return getTextTipElementAsNode(pTipElement);
		}
		else // then media.equals(Media.IMAGE) by @pre
		{
			return getImageTipElementAsNode(pTipElement);
		}
	}
	
	/**
	 * @param pTip a Tip
	 * @return Formatted Node containing the tip's title
	 * @pre pTip!= null
	 */
	private Node getTipTitleAsNode(Tip pTip)
	{
		assert pTip != null;
		
		String title = pTip.getTitle();
		Text titleNode = new Text(title);
		Font titleFont = new Font(TITLE_FONT_SIZE);
		titleNode.setFont(titleFont);
		return titleNode;
	}
	
