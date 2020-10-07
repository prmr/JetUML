package ca.mcgill.cs.jetuml.gui;

import static ca.mcgill.cs.jetuml.application.ApplicationResources.RESOURCES;

import java.util.List;

import ca.mcgill.cs.jetuml.gui.TipLoader.Tip;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
	private final Stage aStage = new Stage();

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
		
		VBox tipVBox = new VBox();
		
		Tip tipOfTheDay = TipLoader.getTipOfTheDay();
		
		List<TipElement> tipElements = tipOfTheDay.getElements();
		for(TipElement tipElement : tipElements)
		{
			Node node = tipElement.getAsNode();
			tipVBox.getChildren().add(node);
		}
		
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
	
}

