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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.FileExtensions;
import ca.mcgill.cs.jetuml.application.NamedHandler;
import ca.mcgill.cs.jetuml.application.RecentFilesQueue;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagram;
import ca.mcgill.cs.jetuml.diagrams.ObjectDiagram;
import ca.mcgill.cs.jetuml.diagrams.SequenceDiagram;
import ca.mcgill.cs.jetuml.diagrams.StateDiagram;
import ca.mcgill.cs.jetuml.diagrams.UseCaseDiagram;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Diagram;
import ca.mcgill.cs.jetuml.persistence.DeserializationException;
import ca.mcgill.cs.jetuml.persistence.PersistenceService;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * The main frame that contains panes that contain diagrams.
 */
public class EditorFrame extends BorderPane
{
	private static final int MAX_RECENT_FILES = 8;
	private static final int MARGIN_IMAGE = 2; // Number of pixels to leave around the graph when exporting it as an
												// image
	private static final int HELP_MENU_TEXT_WIDTH = 32; // Number of columns for the text area of the Help Menu.
	private static final int HELP_MENU_TEXT_HEIGHT = 10; // Number of rows for the text area of the Help Menu.
	private static final int HELP_MENU_SPACING = 10; // Number of pixels between text area and button of the Help Menu.
	private static final int HELP_MENU_PADDING = 10; // Number of pixels padding the nodes in the Help Menu.
	
	private static final Class<?>[] DIAGRAM_TYPES = {ClassDiagram.class, SequenceDiagram.class, 
			StateDiagram.class, ObjectDiagram.class, UseCaseDiagram.class};
	
	private Stage aMainStage;
	private MenuFactory aAppFactory;
	private ResourceBundle aAppResources;
	private ResourceBundle aVersionResources;
	private ResourceBundle aEditorResources;
	private TabPane aTabbedPane;
	private ArrayList<Tab> aTabs = new ArrayList<>();
	
	private Menu aNewMenu;
	private MenuBar aMenuBar = new MenuBar();
	
	private RecentFilesQueue aRecentFiles = new RecentFilesQueue();
	private Menu aRecentFilesMenu;
	
	private WelcomeTab aWelcomeTab;

	// Menus or menu items that must be disabled if there is no current diagram.
	private final List<MenuItem> aDiagramRelevantMenus = new ArrayList<>();

	/**
	 * Constructs a blank frame with a desktop pane but no graph windows.
	 * 
	 * @param pAppClass
	 *            the fully qualified app class name. It is expected that the
	 *            resources are appClassName + "Strings" and appClassName +
	 *            "Version" (the latter for version-specific resources)
	 * @param pMainStage
	 *            the main stage used by the UMLEditor
	 */
	public EditorFrame(Class<?> pAppClass, Stage pMainStage) 
	{
		aMainStage = pMainStage;
		String appClassName = pAppClass.getName();
		aAppResources = ResourceBundle.getBundle(appClassName + "Strings");
		aAppFactory = new MenuFactory(aAppResources);
		aVersionResources = ResourceBundle.getBundle(appClassName + "Version");
		aEditorResources = ResourceBundle.getBundle("ca.mcgill.cs.jetuml.gui.EditorStrings");
		MenuFactory factory = new MenuFactory(aEditorResources);

		aRecentFiles.deserialize(Preferences.userNodeForPackage(UMLEditor.class).get("recent", "").trim());

		aTabbedPane = new TabPane();
		aTabbedPane.getSelectionModel().selectedItemProperty().addListener((pValue, pOld, pNew) -> 
		{
			for (MenuItem menuItem : aDiagramRelevantMenus) 
			{
				menuItem.setDisable(noCurrentGraphFrame());
			}
		});

		setTop(aMenuBar);
		setCenter(aTabbedPane);
	
		createFileMenu(factory);
		createEditMenu(factory);
		createViewMenu(factory);
		createHelpMenu(factory);
		
		List<NamedHandler> newDiagramHandlers = getNewDiagramHandlers();
		
		for( NamedHandler handler : newDiagramHandlers )
		{
			aNewMenu.getItems().add(aAppFactory.createMenuItem(handler.getName(), handler));
		}

		List<NamedHandler> renamedHandlers = new ArrayList<>();
		for( NamedHandler handler : newDiagramHandlers )
		{
			renamedHandlers.add(handler.renamed(aAppResources.getString(handler.getName()+ ".text")));
		}
		
		aWelcomeTab = new WelcomeTab(renamedHandlers);
		showWelcomeTab();
	}
	
	/*
	 * Return the name of a diagram in the resource file. Basically converts
	 * a camel case XDiagramGraph string into a x_diagram string
	 */
	private static String getResourceName(String pClassName)
	{
		assert pClassName.length() > "Diagram".length() && pClassName.endsWith("Diagram");
		return pClassName.substring(0, pClassName.length() - "Diagram".length()).toLowerCase() + "_diagram";
	}
	
	private void createFileMenu(MenuFactory pFactory) 
	{
		Menu fileMenu = pFactory.createMenu("file");
		aMenuBar.getMenus().add(fileMenu);

		aNewMenu = pFactory.createMenu("file.new");
		fileMenu.getItems().add(aNewMenu);

		MenuItem fileOpenItem = pFactory.createMenuItem("file.open", pEvent -> openFile());
		fileMenu.getItems().add(fileOpenItem);

		aRecentFilesMenu = pFactory.createMenu("file.recent");
		buildRecentFilesMenu();
		fileMenu.getItems().add(aRecentFilesMenu);

		MenuItem closeFileItem = pFactory.createMenuItem("file.close", pEvent -> close());
		fileMenu.getItems().add(closeFileItem);
		aDiagramRelevantMenus.add(closeFileItem);
		closeFileItem.setDisable(noCurrentGraphFrame());

		MenuItem fileSaveItem = pFactory.createMenuItem("file.save", pEvent -> save());
		fileMenu.getItems().add(fileSaveItem);
		aDiagramRelevantMenus.add(fileSaveItem);
		fileSaveItem.setDisable(noCurrentGraphFrame());

		MenuItem fileSaveAsItem = pFactory.createMenuItem("file.save_as", pEvent -> saveAs());
		fileMenu.getItems().add(fileSaveAsItem);
		aDiagramRelevantMenus.add(fileSaveAsItem);
		fileSaveAsItem.setDisable(noCurrentGraphFrame());

		MenuItem fileExportItem = pFactory.createMenuItem("file.export_image", pEvent -> exportImage());
		fileMenu.getItems().add(fileExportItem);
		aDiagramRelevantMenus.add(fileExportItem);
		fileExportItem.setDisable(noCurrentGraphFrame());

		MenuItem fileCopyToClipboard = pFactory.createMenuItem("file.copy_to_clipboard", pEvent -> copyToClipboard());
		fileMenu.getItems().add(fileCopyToClipboard);
		aDiagramRelevantMenus.add(fileCopyToClipboard);
		fileCopyToClipboard.setDisable(noCurrentGraphFrame());

		fileMenu.getItems().add(new SeparatorMenuItem());

		MenuItem fileExitItem = pFactory.createMenuItem("file.exit", pEvent -> exit());
		fileMenu.getItems().add(fileExitItem);
	}
	
	private void createEditMenu(MenuFactory pFactory) 
	{
		Menu editMenu = pFactory.createMenu("edit");
		aMenuBar.getMenus().add(editMenu);
		aDiagramRelevantMenus.add(editMenu);
		editMenu.setDisable(noCurrentGraphFrame());

		editMenu.getItems().add(pFactory.createMenuItem("edit.undo", pEvent -> 
		{
			if (noCurrentGraphFrame()) 
			{
				return;
			}
			((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel().undo();
		}));

		editMenu.getItems().add(pFactory.createMenuItem("edit.redo", pEvent ->
		{	
			if (noCurrentGraphFrame()) 
			{
				return;
			}
			((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel().redo();
		}));

		editMenu.getItems().add(pFactory.createMenuItem("edit.selectall", pEvent ->
		{
			if (noCurrentGraphFrame()) 
			{
				return;
			}
			((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel().selectAll();
		}));

		editMenu.getItems().add(pFactory.createMenuItem("edit.properties", pEvent -> 
		{
			if (noCurrentGraphFrame()) 
			{
				return;
			}
			((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel().editSelected();
		}));

		editMenu.getItems().add(pFactory.createMenuItem("edit.cut", pEvent -> cut()));
		editMenu.getItems().add(pFactory.createMenuItem("edit.paste", pEvent -> paste()));
		editMenu.getItems().add(pFactory.createMenuItem("edit.copy", pEvent -> copy()));

		editMenu.getItems().add(pFactory.createMenuItem("edit.delete", pEvent ->  
		{
			if (noCurrentGraphFrame()) 
			{
				return;
			}
			((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel().removeSelected();
		}));
	}
	
	private void createViewMenu(MenuFactory pFactory) 
	{
		Menu viewMenu = pFactory.createMenu("view");
		aMenuBar.getMenus().add(viewMenu);
		aDiagramRelevantMenus.add(viewMenu);
		viewMenu.setDisable(noCurrentGraphFrame());

		final CheckMenuItem showGridItem  = (CheckMenuItem) pFactory.createCheckMenuItem("view.show_grid", pEvent ->
	    {
	    	if( noCurrentGraphFrame() )
	    	{
	    		return;
	    	}
	    	CheckMenuItem menuItem = (CheckMenuItem) pEvent.getSource();  
	    	boolean selected = menuItem.isSelected();
	    	Preferences.userNodeForPackage(UMLEditor.class).put("showGrid", Boolean.toString(selected));
	    	setShowGridForAllFrames(selected);
		});
		showGridItem.setSelected(Boolean.valueOf(Preferences.userNodeForPackage(UMLEditor.class).get("showGrid", "true")));
		viewMenu.getItems().add(showGridItem);
		
		final CheckMenuItem showToolHints  = (CheckMenuItem) pFactory.createCheckMenuItem("view.show_hints", pEvent ->
	    {
	    	if( noCurrentGraphFrame() )
	    	{
	    		return;
	    	}
	    	CheckMenuItem menuItem = (CheckMenuItem) pEvent.getSource();  
	    	boolean selected = menuItem.isSelected();
	    	Preferences.userNodeForPackage(UMLEditor.class).put("showToolHints", Boolean.toString(selected));
    		setShowToolbarButtonLabelsForAllFrames(selected);
		});
		showToolHints.setSelected(Boolean.valueOf(Preferences.userNodeForPackage(UMLEditor.class).get("showToolHints", "false")));
		viewMenu.getItems().add(showToolHints);
	}
	
	private void setShowToolbarButtonLabelsForAllFrames(boolean pShow)
	{
		for( Tab tab : aTabbedPane.getTabs() )
		{
			if( tab instanceof GraphFrame )
			{
				((GraphFrame)tab).showToolbarButtonLabels(pShow);
			}
		}
	}
	
	private void setShowGridForAllFrames(boolean pShow)
	{
		for( Tab tab : aTabbedPane.getTabs() )
		{
			if( tab instanceof GraphFrame )
			{
				((GraphFrame)tab).getGraphPanel().setShowGrid(pShow);
			}
		}
	}

	private void createHelpMenu(MenuFactory pFactory) 
	{
		Menu helpMenu = pFactory.createMenu("help");
		aMenuBar.getMenus().add(helpMenu);

		helpMenu.getItems().add(pFactory.createMenuItem("help.about", pEvent -> showAboutDialog()));
		helpMenu.getItems().add(pFactory.createMenuItem("help.license", pEvent -> 
		{
			try 
			{
				BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("license.txt")));
				TextArea text = new TextArea();
				text.setPrefColumnCount(HELP_MENU_TEXT_WIDTH);
				text.setPrefRowCount(HELP_MENU_TEXT_HEIGHT);
				String line;
				while ((line = reader.readLine()) != null) 
				{
					text.appendText(line);
					text.appendText("\n");
				}
				text.positionCaret(0);
				text.setEditable(false);

				ScrollPane scrollPane = new ScrollPane(text);
				scrollPane.setFitToHeight(true);
				scrollPane.setFitToWidth(true);

				Stage window = new Stage();
				window.setTitle(aEditorResources.getString("dialog.license.title"));
				Image appIcon = new Image(aAppResources.getString("app.icon"));
				window.getIcons().add(appIcon);
				window.initModality(Modality.APPLICATION_MODAL);

				Button button = new Button("OK");
				button.setOnAction(pButtonEvent -> window.close());
				button.addEventHandler(KeyEvent.KEY_PRESSED, pKeyEvent -> 
				{
					if (pKeyEvent.getCode() == KeyCode.ENTER) 
					{
						button.fire();
						pEvent.consume();
					}
				});

				BorderPane layout = new BorderPane();
				layout.setPadding(new Insets(HELP_MENU_PADDING));
				layout.setCenter(scrollPane);
				layout.setBottom(button);
				BorderPane.setAlignment(button, Pos.CENTER_RIGHT);
				BorderPane.setMargin(button, new Insets(HELP_MENU_PADDING, 0, 0, 0));

				Scene scene = new Scene(layout);
				button.requestFocus();
				window.setScene(scene);
				window.initOwner(getScene().getWindow());
				window.showAndWait();
			} 
			catch(IOException exception){}
		}));
	}

	/*
	 * Opens a file with the given name, or switches to the frame if it is already
	 * open.
	 * 
	 * @param pName the file name
	 */
	private void open(String pName) 
	{
		for (int i = 0; i < aTabs.size(); i++) 
		{
			if (aTabbedPane.getTabs().get(i) instanceof GraphFrame)
			{
				GraphFrame frame = (GraphFrame) aTabbedPane.getTabs().get(i);
				if (frame.getFileName() != null	&& frame.getFileName().getAbsoluteFile().equals(new File(pName).getAbsoluteFile())) 
				{
					aTabbedPane.getSelectionModel().select(frame);
					addRecentFile(new File(pName).getPath());
					return;
				}
			}
		}
		try 
		{
			Diagram graph2 = PersistenceService.read(new File(pName));
			GraphFrame frame2 = new GraphFrame(graph2, aTabbedPane);
			frame2.setFile(new File(pName).getAbsoluteFile());
			addRecentFile(new File(pName).getPath());
			addTab(frame2);
		}
		catch (IOException | DeserializationException exception2) 
		{
			Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.open_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}

	/*
	 * Adds a Tab to the list of Tabs.
	 * 
	 * @param c the component to display in the internal frame
	 * 
	 * @param t the title of the internal frame.
	 */
	private void addTab(final Tab pTab) 
	{
		aTabbedPane.getTabs().add(pTab);
		aTabs.add(pTab);
		int last = aTabs.size();
		aTabbedPane.getSelectionModel().select(last-1);
		if (aTabbedPane.getTabs().get(0) instanceof WelcomeTab) 
		{
			removeWelcomeTab();
		}
	}

	/**
	 * This adds a WelcomeTab to the tabs. This is only done if all other tabs have
	 * been previously closed.
	 */
	private void showWelcomeTab() 
	{
		if(aTabs.size() == 0) 
		{
			aWelcomeTab.loadRecentFileLinks(getOpenFileHandlers());
			aTabbedPane.getTabs().add(aWelcomeTab);
			aTabs.add(aWelcomeTab);
		}
	}
	
	private List<NamedHandler> getOpenFileHandlers()
	{
		List<NamedHandler> result = new ArrayList<>();
		for( File file : aRecentFiles )
   		{
			result.add(new NamedHandler(file.getName(), pEvent -> open(file.getAbsolutePath())));
   		}
		return Collections.unmodifiableList(result);
	}
	
	private List<NamedHandler> getNewDiagramHandlers()
	{
		List<NamedHandler> result = new ArrayList<>();
		for( Class<?> diagramType : DIAGRAM_TYPES )
		{
			result.add(new NamedHandler(getResourceName(diagramType.getSimpleName()), pEvent ->
			{
				try 
				{
					Tab frame = new GraphFrame((Diagram) diagramType.getDeclaredConstructor().newInstance(), aTabbedPane);
					addTab(frame);
				}
				catch(ReflectiveOperationException exception) 
				{
					assert false;
				}
			}));
		}
		return Collections.unmodifiableList(result);
	}

	/**
	 * This method removes the WelcomeTab after a file has been opened or a diagram
	 * starts being created.
	 */
	public void removeWelcomeTab() 
	{
		if (aWelcomeTab != null) 
		{
			aTabbedPane.getTabs().remove(0);
			aTabs.remove(0);
		}
	}

	/**
	 * Calling this method will remove a given Tab.
	 * @param pTab The Tab to remove. 
	 */
	public void removeTab(final Tab pTab) 
	{
		if (!aTabs.contains(pTab)) 
		{
			return;
		}
		int pos = aTabs.indexOf(pTab);
		aTabs.remove(pos);
		aTabbedPane.getTabs().remove(pos);
		showWelcomeTab();
	}

	/*
	 * Adds a file name to the "recent files" list and rebuilds the "recent files"
	 * menu.
	 * 
	 * @param pNewFile the file name to add
	 */
	private void addRecentFile(String pNewFile) 
	{
		aRecentFiles.add(pNewFile);
		buildRecentFilesMenu();
	}
	
   	/*
   	 * Rebuilds the "recent files" menu. Only works if the number of
   	 * recent files is less than 10. Otherwise, additional logic will need
   	 * to be added to 0-index the mnemonics for files 1-9.
   	 */
   	private void buildRecentFilesMenu()
   	{ 
   		assert aRecentFiles.size() <= MAX_RECENT_FILES;
   		aRecentFilesMenu.getItems().clear();
   		aRecentFilesMenu.setDisable(!(aRecentFiles.size() > 0));
   		int i = 1;
   		for( File file : aRecentFiles )
   		{
   			String name = "_" + i + " " + file.getName();
   			final String fileName = file.getAbsolutePath();
   			MenuItem item = new MenuItem(name);
   			aRecentFilesMenu.getItems().add(item);
   			item.setOnAction(pEvent -> open(fileName));
            i++;
   		}
   }

	/**
	 * Asks the user to open a graph file.
	 */
	public void openFile() 
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(aRecentFiles.getMostRecentDirectory());
		fileChooser.getExtensionFilters().addAll(FileExtensions.getAll());

		File selectedFile = fileChooser.showOpenDialog(aMainStage);
		if (selectedFile != null) 
		{
			open(selectedFile.getAbsolutePath());
		}
	}

	/**
	 * Cuts the current selection of the current panel and puts the content into the
	 * application-specific clipboard.
	 */
	public void cut() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		GraphPanel panel = ((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel();
		panel.cut();
		panel.paintPanel();
	}

	/**
	 * Copies the current selection of the current panel and puts the content into
	 * the application-specific clipboard.
	 */
	public void copy() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel().copy();
	}

	/**
	 * Pastes a past selection from the application-specific Clipboard into current
	 * panel. All the logic is done in the application-specific CutPasteBehavior.
	 * 
	 */
	public void paste() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		GraphPanel panel = ((GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem()).getGraphPanel();
		panel.paste();
		panel.paintPanel();
	}

	/**
	 * Copies the current image to the clipboard.
	 */
	public void copyToClipboard() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem();
		final Image image = getImage(frame.getGraphPanel());
		final Clipboard clipboard = Clipboard.getSystemClipboard();
	    final ClipboardContent content = new ClipboardContent();
	    content.putImage(image);
	    clipboard.setContent(content);
		Alert alert = new Alert(AlertType.INFORMATION, aEditorResources.getString("dialog.to_clipboard.message"), ButtonType.OK);
		alert.initOwner(aMainStage);
		alert.setHeaderText(aEditorResources.getString("dialog.to_clipboard.title"));
		alert.showAndWait();
	}

	private boolean noCurrentGraphFrame() 
	{
		return aTabbedPane.getSelectionModel().getSelectedItem() == null ||
				!(aTabbedPane.getSelectionModel().getSelectedItem() instanceof GraphFrame);
	}

	/**
	 * If a user confirms that they want to close their modified graph, this method
	 * will remove it from the current list of tabs.
	 */
	public void close() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		Tab currentFrame = (Tab) aTabbedPane.getSelectionModel().getSelectedItem();
		if (currentFrame != null)
		{
			GraphFrame openFrame = (GraphFrame) currentFrame;
			// we only want to check attempts to close a frame
			if (openFrame.getGraphPanel().isModified()) 
			{
				// ask user if it is ok to close
				Alert alert = new Alert(AlertType.CONFIRMATION, aEditorResources.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
				alert.initOwner(aMainStage);
				alert.setTitle(aEditorResources.getString("dialog.close.title"));
				alert.setHeaderText(aEditorResources.getString("dialog.close.title"));
				alert.showAndWait();

				if (alert.getResult() == ButtonType.YES) 
				{
					removeTab(currentFrame);
				}
				return;
			} 
			else 
			{
				removeTab(currentFrame);
			}
		}
	}

	/**
	 * If a user confirms that they want to close their modified graph, this method
	 * will remove it from the current list of tabs.
	 * 
	 * @param pTab The current Tab that one wishes to close.
	 */
	public void close(Tab pTab) 
	{
		Tab currentFrame = pTab;
		if (currentFrame != null)
		{
			GraphFrame openFrame = (GraphFrame) currentFrame;
			// we only want to check attempts to close a frame
			if (openFrame.getGraphPanel().isModified()) 
			{
				if (openFrame.getGraphPanel().isModified()) 
				{
					// ask user if it is ok to close
					Alert alert = new Alert(AlertType.CONFIRMATION, aEditorResources.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
					alert.initOwner(aMainStage);
					alert.setTitle(aEditorResources.getString("dialog.close.title"));
					alert.setHeaderText(aEditorResources.getString("dialog.close.title"));
					alert.showAndWait();

					if (alert.getResult() == ButtonType.YES) 
					{
						removeTab(currentFrame);
					}
				}
				return;
			}
			removeTab(currentFrame);
		}
	}

	/**
	 * Save a file. Called by reflection.
	 */
	public void save() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem();
		File file = frame.getFileName();
		if (file == null) 
		{
			saveAs();
			return;
		}
		try 
		{
			PersistenceService.save(frame.getGraph(), file);
			frame.getGraphPanel().setModified(false);
		} 
		catch (IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}

	/**
	 * Saves the current graph as a new file.
	 */
	public void saveAs() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem();
		Diagram graph = frame.getGraph();

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(FileExtensions.getAll());
		fileChooser.setSelectedExtensionFilter(FileExtensions.get(graph.getDescription()));

		if (frame.getFileName() != null) 
		{
			fileChooser.setInitialDirectory(frame.getFileName().getParentFile());
			fileChooser.setInitialFileName(frame.getFileName().getName());
		} 
		else 
		{
			fileChooser.setInitialDirectory(new File("."));
			fileChooser.setInitialFileName("");
		}

		try 
		{
			File result = fileChooser.showSaveDialog(aMainStage);
			if(fileChooser.getSelectedExtensionFilter() != FileExtensions.get(graph.getDescription()))
			{
				result = new File(result.getPath() + graph.getFileExtension() + aAppResources.getString("files.extension"));
			}
			if (result != null) 
			{
				PersistenceService.save(graph, result);
				addRecentFile(result.getAbsolutePath());
				frame.setFile(result);
				aTabbedPane.getSelectionModel().getSelectedItem().setText(frame.getFileName().getName());
				frame.getGraphPanel().setModified(false);
			}
		} 
		catch (IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}

	/**
	 * Edits the file path so that the pToBeRemoved extension, if found, is replaced
	 * with pDesired.
	 * 
	 * @param pOriginal
	 *            the file to use as a starting point
	 * @param pToBeRemoved
	 *            the extension that is to be removed before adding the desired
	 *            extension.
	 * @param pDesired
	 *            the desired extension (e.g. ".png")
	 * @return original if it already has the desired extension, or a new file with
	 *         the edited file path
	 */
	static String replaceExtension(String pOriginal, String pToBeRemoved, String pDesired) 
	{
		assert pOriginal != null && pToBeRemoved != null && pDesired != null;

		if (pOriginal.endsWith(pToBeRemoved)) 
		{
			return pOriginal.substring(0, pOriginal.length() - pToBeRemoved.length()) + pDesired;
		}
		else 
		{
			return pOriginal;
		}
	}

	/**
	 * Exports the current graph to an image file.
	 */
	public void exportImage() 
	{
		if (noCurrentGraphFrame()) 
		{
			return;
		}
		
		FileChooser fileChooser = getImageFileChooser();
		File file = fileChooser.showSaveDialog(aMainStage);
		if (file == null) 
		{
			return;
		}

		// Validate the file format
		String fileName = file.getPath();
		String format = fileName.substring(fileName.lastIndexOf(".") + 1);
		if (!ImageIO.getImageWritersByFormatName(format).hasNext())
		{
			Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.unsupported_image"),	ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.setHeaderText(aEditorResources.getString("error.unsupported_image.title"));
			alert.showAndWait();
			return;
		}
		
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem();
		try (OutputStream out = new FileOutputStream(file)) 
		{
			BufferedImage image = getBufferedImage(frame.getGraphPanel()); 
			if (format.equals("jpg") || format.equals("jpeg"))	// to correct the display of JPEG/JPG images (removes red hue)
			{
				BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE);
				Graphics2D graphics = imageRGB.createGraphics();
				graphics.drawImage(image, 0,  0, null);
				ImageIO.write(imageRGB, format, out);
				graphics.dispose();
			}
			else if (format.equals("bmp"))	// to correct the BufferedImage type
			{
				BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D graphics = imageRGB.createGraphics();
				graphics.drawImage(image, 0, 0, Color.WHITE, null);
				ImageIO.write(imageRGB, format, out);
				graphics.dispose();
			}
			else
			{
				ImageIO.write(image, format, out);
			}
		} 
		catch (IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}

	private static String[] getAllSupportedImageWriterFormats() 
	{
		String[] names = ImageIO.getWriterFormatNames();
		HashSet<String> formats = new HashSet<String>();
		for (String name : names) 
		{
			formats.add(name.toLowerCase());
		}
		String[] lReturn = formats.toArray(new String[formats.size()]);
		Arrays.sort(lReturn);
		return lReturn;
	}

	private FileChooser getImageFileChooser() 
	{
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectionModel().getSelectedItem();
		assert frame != null;

		// Initialize the file chooser widget
		FileChooser fileChooser = new FileChooser();
		for (String format : getAllSupportedImageWriterFormats()) 
		{
			if (format.equals("wbmp"))	// no supported conversion available
			{
				continue;
			}
			fileChooser.getExtensionFilters()
				.add(new ExtensionFilter(format.toUpperCase() + " " + aEditorResources.getString("files.image.name"), "*." +format));
		}
		fileChooser.setInitialDirectory(new File("."));

		// If the file was previously saved, use that to suggest a file name root.
		if (frame.getFileName() != null) 
		{
			File f = new File(replaceExtension(frame.getFileName().getAbsolutePath(), aAppResources.getString("files.extension"), ""));
			fileChooser.setInitialDirectory(f.getParentFile());
			fileChooser.setInitialFileName(f.getName());
		}
		return fileChooser;
	}

	/*
	 * Return the image corresponding to the graph.
	 * 
	 * @param pGraph The graph to convert to an image.
	 * 
	 * @return bufferedImage. To convert it into an image, use the syntax :
	 * Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
	 */
	private static BufferedImage getBufferedImage(GraphPanel pGraphPanel) 
	{
		BufferedImage image = SwingFXUtils.fromFXImage(getImage(pGraphPanel), null);
		return image;
	}
	
	/*
	 * Return the image corresponding to the graph.
	 * 
	 * @param pGraph The graph to convert to an image.
	 */
	private static Image getImage(GraphPanel pGraphPanel) 
	{
		Rectangle bounds = pGraphPanel.getGraph().getBounds();
		WritableImage image = new WritableImage((int) bounds.getWidth() + MARGIN_IMAGE * 2, (int) bounds.getHeight() + MARGIN_IMAGE * 2);
		boolean oldShowGrid = pGraphPanel.getShowGrid();
		pGraphPanel.setShowGrid(false);
		SnapshotParameters parameter = new SnapshotParameters();
		parameter.setViewport(new Rectangle2D(bounds.getX() - MARGIN_IMAGE, bounds.getY() - MARGIN_IMAGE, 
				bounds.getWidth() + MARGIN_IMAGE * 2, bounds.getHeight() + MARGIN_IMAGE * 2));
		((Node) pGraphPanel).snapshot(parameter, image);
		pGraphPanel.setShowGrid(oldShowGrid);
		return image;
	}

	/**
	 * Displays the About dialog box.
	 */
	public void showAboutDialog() 
	{
		MessageFormat formatter = new MessageFormat(aEditorResources.getString("dialog.about.version"));
		Text text = new Text(formatter.format(new Object[] { aAppResources.getString("app.name"),
				aVersionResources.getString("version.number"), aVersionResources.getString("version.date"),
				aAppResources.getString("app.copyright"), aEditorResources.getString("dialog.about.license") }));
		Image appIcon = new Image(getClass().getClassLoader().getResource(aAppResources.getString("app.icon")).toString());
		
		HBox info = new HBox(HELP_MENU_SPACING);
		info.setAlignment(Pos.CENTER);
		info.getChildren().addAll(new ImageView(appIcon), text);
		
		VBox layout = new VBox(HELP_MENU_SPACING);
		layout.setPadding(new Insets(HELP_MENU_PADDING));
		layout.setAlignment(Pos.CENTER_RIGHT);
		
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(new MessageFormat(aEditorResources.getString("dialog.about.title"))
				.format(new Object[] { aAppResources.getString("app.name") }));
		window.getIcons().add(appIcon);
		
		Button button = new Button("OK");
		button.setOnAction(pEvent -> window.close());
		button.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
		{
			if (pEvent.getCode() == KeyCode.ENTER) 
			{
				button.fire();
				pEvent.consume();
			}
		});
		
		layout.getChildren().addAll(info, button);
		Scene scene = new Scene(layout);
		window.setScene(scene);
		window.showAndWait();
	}

	/**
	 * Exits the program if no graphs have been modified or if the user agrees to
	 * abandon modified graphs.
	 */
	public void exit() 
	{
		int modcount = 0;
		for (int i = 0; i < aTabs.size(); i++) 
		{
			if (aTabs.get(i) instanceof GraphFrame) 
			{
				GraphFrame frame = (GraphFrame) aTabs.get(i);
				if (frame.getGraphPanel().isModified()) 
				{
					modcount++;
				}
			}
		}
		if (modcount > 0) 
		{
			// ask user if it is ok to close
			final int finalModCount = modcount;
			Alert alert = new Alert(AlertType.CONFIRMATION, 
					MessageFormat.format(aEditorResources.getString("dialog.exit.ok"), new Object[] { Integer.valueOf(finalModCount) }),
					ButtonType.YES, 
					ButtonType.NO);
			alert.initOwner(aMainStage);
			alert.setTitle(aEditorResources.getString("dialog.exit.title"));
			alert.setHeaderText(aEditorResources.getString("dialog.exit.title"));
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) 
			{
				Preferences.userNodeForPackage(UMLEditor.class).put("recent", aRecentFiles.serialize());
				System.exit(0);
			}
		}
		else 
		{
			Preferences.userNodeForPackage(UMLEditor.class).put("recent", aRecentFiles.serialize());
			System.exit(0);
		}
	}		
}
