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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.prefs.Preferences;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.FileExtensions;
import ca.mcgill.cs.jetuml.application.RecentFilesQueue;
import ca.mcgill.cs.jetuml.application.UserPreferences;
import ca.mcgill.cs.jetuml.application.UserPreferences.BooleanPreference;
import ca.mcgill.cs.jetuml.application.UserPreferences.IntegerPreference;
import ca.mcgill.cs.jetuml.diagram.Diagram;
import ca.mcgill.cs.jetuml.diagram.DiagramType;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.persistence.DeserializationException;
import ca.mcgill.cs.jetuml.persistence.PersistenceService;
import ca.mcgill.cs.jetuml.views.ImageCreator;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * The main frame that contains panes that contain diagrams.
 */
public class EditorFrame extends BorderPane
{
	private static final String KEY_LAST_EXPORT_DIR = "lastExportDir";
	private static final String KEY_LAST_SAVEAS_DIR = "lastSaveAsDir";
	private static final String KEY_LAST_IMAGE_FORMAT = "lastImageFormat";
	
	private static final String[] IMAGE_FORMATS = validFormats("png", "jpg", "gif", "bmp");
	
	private Stage aMainStage;
	private RecentFilesQueue aRecentFiles = new RecentFilesQueue();
	private Menu aRecentFilesMenu;
	private WelcomeTab aWelcomeTab;
	
	/**
	 * Constructs a blank frame with a desktop pane but no diagram window.
	 * 
	 * @param pMainStage The main stage used by the UMLEditor
	 */
	public EditorFrame(Stage pMainStage) 
	{
		aMainStage = pMainStage;
		aRecentFiles.deserialize(Preferences.userNodeForPackage(UMLEditor.class).get("recent", "").trim());

		MenuBar menuBar = new MenuBar();
		setTop(menuBar);
		
		TabPane tabPane = new TabPane();
		tabPane.getSelectionModel().selectedItemProperty().addListener((pValue, pOld, pNew) -> setMenuVisibility());
		setCenter( tabPane );
	
		List<NewDiagramHandler> newDiagramHandlers = createNewDiagramHandlers();
		createFileMenu(menuBar, newDiagramHandlers);
		createEditMenu(menuBar);
		createViewMenu(menuBar);
		createHelpMenu(menuBar);
		setMenuVisibility();
		
		aWelcomeTab = new WelcomeTab(newDiagramHandlers);
		showWelcomeTabIfNecessary();
	}
	
	/* Returns the subset of pDesiredFormats for which a registered image writer 
	 * claims to recognized the format */
	private static String[] validFormats(String... pDesiredFormats)
	{
		List<String> recognizedWriters = Arrays.asList(ImageIO.getWriterFormatNames());
		List<String> validFormats = new ArrayList<>();
		for( String format : pDesiredFormats )
		{
			if( recognizedWriters.contains(format))
			{
				validFormats.add(format);
			}
		}
		return validFormats.toArray(new String[validFormats.size()]);
	}
	
	/*
	 * Traverses all menu items up to the second level (top level
	 * menus and their immediate sub-menus), that have "true" in their user data,
	 * indicating that they should only be enabled if there is a diagram 
	 * present. Then, sets their visibility to the boolean value that
	 * indicates whether there is a diagram present.
	 * 
	 * This method assumes that any sub-menu beyond the second level (sub-menus of
	 * top menus) will NOT be diagram-specific.
	 */
	private void setMenuVisibility()
	{
			((MenuBar)getTop()).getMenus().stream() // All top level menus
				.flatMap(menu -> Stream.concat(Stream.of(menu), menu.getItems().stream())) // All menus and immediate sub-menus
				.filter( item -> Boolean.TRUE.equals(item.getUserData())) // Retain only diagram-relevant menu items
				.forEach( item -> item.setDisable(isWelcomeTabShowing()));
	}
	
	// Returns the new menu
	private void createFileMenu(MenuBar pMenuBar, List<NewDiagramHandler> pNewDiagramHandlers) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		
		// Special menu items whose creation can't be inlined in the factory call.
		Menu newMenu = factory.createMenu("file.new", false);
		for( NewDiagramHandler handler : pNewDiagramHandlers )
		{
			newMenu.getItems().add(factory.createMenuItem(handler.getDiagramType().getName(), false, handler));
		}
		
		aRecentFilesMenu = factory.createMenu("file.recent", false);
		buildRecentFilesMenu();
		
		// Standard factory invocation
		pMenuBar.getMenus().add(factory.createMenu("file", false, 
				newMenu,
				factory.createMenuItem("file.open", false, pEvent -> openFile()),
				aRecentFilesMenu,
				factory.createMenuItem("file.close", true, pEvent -> close()),
				factory.createMenuItem("file.save", true, pEvent -> save()),
				factory.createMenuItem("file.save_as", true, pEvent -> saveAs()),
				factory.createMenuItem("file.export_image", true, pEvent -> exportImage()),
				factory.createMenuItem("file.copy_to_clipboard", true, pEvent -> copyToClipboard()),
				new SeparatorMenuItem(),
				factory.createMenuItem("file.exit", false, pEvent -> exit())));
	}
	
	private void createEditMenu(MenuBar pMenuBar) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		pMenuBar.getMenus().add(factory.createMenu("edit", true, 
				factory.createMenuItem("edit.undo", true, pEvent -> getSelectedDiagramTab().undo()),
				factory.createMenuItem("edit.redo", true, pEvent -> getSelectedDiagramTab().redo()),
				factory.createMenuItem("edit.selectall", true, pEvent -> getSelectedDiagramTab().selectAll()),
				factory.createMenuItem("edit.properties", true, pEvent -> getSelectedDiagramTab().editSelected()),
				factory.createMenuItem("edit.cut", true, pEvent -> getSelectedDiagramTab().cut()),
				factory.createMenuItem("edit.paste", true, pEvent -> getSelectedDiagramTab().paste()),
				factory.createMenuItem("edit.copy", true, pEvent -> getSelectedDiagramTab().copy()),
				factory.createMenuItem("edit.delete", true, pEvent -> getSelectedDiagramTab().removeSelected() )));
	}
	
	private void createViewMenu(MenuBar pMenuBar) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		pMenuBar.getMenus().add(factory.createMenu("view", false, 
				
				factory.createCheckMenuItem("view.show_grid", false, 
				UserPreferences.instance().getBoolean(BooleanPreference.showGrid), 
				pEvent -> UserPreferences.instance().setBoolean(BooleanPreference.showGrid, ((CheckMenuItem) pEvent.getSource()).isSelected())),
			
				factory.createCheckMenuItem("view.show_hints", false, 
				UserPreferences.instance().getBoolean(BooleanPreference.showToolHints),
				pEvent -> UserPreferences.instance().setBoolean(BooleanPreference.showToolHints, 
						((CheckMenuItem) pEvent.getSource()).isSelected())),
		
				factory.createMenuItem("view.diagram_size", false, Event -> new DiagramSizeDialog(aMainStage).show())));
	}
	
	private void createHelpMenu(MenuBar pMenuBar) 
	{
		MenuFactory factory = new MenuFactory(RESOURCES);
		pMenuBar.getMenus().add(factory.createMenu("help", false,
				factory.createMenuItem("help.about", false, pEvent -> new AboutDialog(aMainStage).show())));
	}
	
	/*
	 * Opens a file with the given name, or switches to the frame if it is already
	 * open.
	 * 
	 * @param pName the file name
	 */
	private void open(String pName) 
	{
		for( Tab tab : tabs() )
		{
			if(tab instanceof DiagramTab)
			{
				if(((DiagramTab) tab).getFile() != null	&& 
						((DiagramTab) tab).getFile().getAbsoluteFile().equals(new File(pName).getAbsoluteFile())) 
				{
					tabPane().getSelectionModel().select(tab);
					addRecentFile(new File(pName).getPath());
					return;
				}
			}
		}
		
		try 
		{
			Diagram diagram2 = PersistenceService.read(new File(pName));
			
			Rectangle bounds = DiagramType.newViewInstanceFor(diagram2).getBounds();
			int viewWidth = UserPreferences.instance().getInteger(IntegerPreference.diagramWidth);
			int viewHeight = UserPreferences.instance().getInteger(IntegerPreference.diagramHeight);
			if( bounds.getMaxX() > viewWidth || bounds.getMaxY() > viewHeight )
			{
				showDiagramViewTooSmallAlert(bounds, viewWidth, viewHeight);
				return;
			}
			
			DiagramTab frame2 = new DiagramTab(diagram2);
			frame2.setFile(new File(pName).getAbsoluteFile());
			addRecentFile(new File(pName).getPath());
			insertGraphFrameIntoTabbedPane(frame2);
		}
		catch (IOException | DeserializationException exception2) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.open_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}
	
	private void showDiagramViewTooSmallAlert(Rectangle pBounds, int pWidth, int pHeight)
	{
		String content = RESOURCES.getString("dialog.open.size_error_content");
		content = content.replace("#1", Integer.toString(pBounds.getMaxX()));
		content = content.replace("#2", Integer.toString(pBounds.getMaxY()));
		content = content.replace("#3", Integer.toString(pWidth));
		content = content.replace("#4", Integer.toString(pHeight));
		Alert alert = new Alert(AlertType.ERROR, content, ButtonType.OK);
		alert.setTitle(RESOURCES.getString("alert.error.title"));
		alert.setHeaderText(RESOURCES.getString("dialog.open.size_error_header"));
		alert.initOwner(aMainStage);
		alert.showAndWait();
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
	
	private List<NewDiagramHandler> createNewDiagramHandlers()
	{
		List<NewDiagramHandler> result = new ArrayList<>();
		for( DiagramType diagramType : DiagramType.values() )
		{
			result.add(new NewDiagramHandler(diagramType, pEvent ->
			{
				insertGraphFrameIntoTabbedPane(new DiagramTab(diagramType.newInstance()));
			}));
		}
		return Collections.unmodifiableList(result);
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

	private void openFile() 
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
	 * Copies the current image to the clipboard.
	 */
	public void copyToClipboard() 
	{
		DiagramTab frame = getSelectedDiagramTab();
		final Image image = ImageCreator.createImage(frame.getDiagram());
		final Clipboard clipboard = Clipboard.getSystemClipboard();
	    final ClipboardContent content = new ClipboardContent();
	    content.putImage(image);
	    clipboard.setContent(content);
		Alert alert = new Alert(AlertType.INFORMATION, RESOURCES.getString("dialog.to_clipboard.message"), ButtonType.OK);
		alert.initOwner(aMainStage);
		alert.setHeaderText(RESOURCES.getString("dialog.to_clipboard.title"));
		alert.showAndWait();
	}

	/* @pre there is a selected diagram tab, not just the welcome tab */
	private DiagramTab getSelectedDiagramTab()
	{
		Tab tab = ((TabPane) getCenter()).getSelectionModel().getSelectedItem();
		assert tab instanceof DiagramTab; // implies a null check.
		return (DiagramTab) tab;
	}

	private void close() 
	{
		DiagramTab openFrame = getSelectedDiagramTab();
		// we only want to check attempts to close a frame
		if (openFrame.isModified()) 
		{
			// ask user if it is ok to close
			Alert alert = new Alert(AlertType.CONFIRMATION, RESOURCES.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
			alert.initOwner(aMainStage);
			alert.setTitle(RESOURCES.getString("dialog.close.title"));
			alert.setHeaderText(RESOURCES.getString("dialog.close.title"));
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) 
			{
				removeGraphFrameFromTabbedPane(openFrame);
			}
			return;
		} 
		else 
		{
			removeGraphFrameFromTabbedPane(openFrame);
		}
	}

	/**
	 * If a user confirms that they want to close their modified graph, this method
	 * will remove it from the current list of tabs.
	 * 
	 * @param pDiagramTab The current Tab that one wishes to close.
	 */
	public void close(DiagramTab pDiagramTab) 
	{
		if(pDiagramTab.isModified()) 
		{
			Alert alert = new Alert(AlertType.CONFIRMATION, RESOURCES.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
			alert.initOwner(aMainStage);
			alert.setTitle(RESOURCES.getString("dialog.close.title"));
			alert.setHeaderText(RESOURCES.getString("dialog.close.title"));
			alert.showAndWait();

			if (alert.getResult() == ButtonType.YES) 
			{
				removeGraphFrameFromTabbedPane(pDiagramTab);
			}
		}
		else
		{
			removeGraphFrameFromTabbedPane(pDiagramTab);
		}
	}

	private void save() 
	{
		DiagramTab frame = getSelectedDiagramTab();
		File file = frame.getFile();
		if(file == null) 
		{
			saveAs();
			return;
		}
		try 
		{
			PersistenceService.save(frame.getDiagram(), file);
			frame.setModified(false);
		} 
		catch(IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}

	private void saveAs() 
	{
		DiagramTab frame = (DiagramTab) getSelectedDiagramTab();
		Diagram diagram = frame.getDiagram();

		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(FileExtensions.getAll());
		fileChooser.setSelectedExtensionFilter(FileExtensions.get(diagram.getDescription()));

		if (frame.getFile() != null) 
		{
			fileChooser.setInitialDirectory(frame.getFile().getParentFile());
			fileChooser.setInitialFileName(frame.getFile().getName());
		} 
		else 
		{
			fileChooser.setInitialDirectory(getLastDir(KEY_LAST_SAVEAS_DIR));
			fileChooser.setInitialFileName("");
		}

		try 
		{
			File result = fileChooser.showSaveDialog(aMainStage);
			if(fileChooser.getSelectedExtensionFilter() != FileExtensions.get(diagram.getDescription()))
			{
				result = new File(result.getPath() + diagram.getFileExtension() + RESOURCES.getString("application.file.extension"));
			}
			if(result != null) 
			{
				PersistenceService.save(diagram, result);
				addRecentFile(result.getAbsolutePath());
				frame.setFile(result);
				frame.setText(frame.getFile().getName());
				frame.setModified(false);
				File dir = result.getParentFile();
				if( dir != null )
				{
					setLastDir(KEY_LAST_SAVEAS_DIR, dir);
				}
			}
		} 
		catch (IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.save_file"), ButtonType.OK);
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

	private File getLastDir(String pKey)
	{
		String dir = Preferences.userNodeForPackage(UMLEditor.class).get(pKey, ".");
		File result = new File(dir);
		if( !(result.exists() && result.isDirectory()))
		{
			result = new File(".");
		}
		return result;
	}
	
	private void setLastDir(String pKey, File pLastExportDir)
	{
		Preferences.userNodeForPackage(UMLEditor.class).put(pKey, pLastExportDir.getAbsolutePath().toString());
	}
	
	/**
	 * Exports the current graph to an image file.
	 */
	private void exportImage() 
	{
		FileChooser fileChooser = getImageFileChooser(getLastDir(KEY_LAST_EXPORT_DIR), 
				Preferences.userNodeForPackage(UMLEditor.class).get(KEY_LAST_IMAGE_FORMAT, "png"));
		File file = fileChooser.showSaveDialog(aMainStage);
		if(file == null) 
		{
			return;
		}

		String fileName = file.getPath();
		String format = fileName.substring(fileName.lastIndexOf(".") + 1);
		Preferences.userNodeForPackage(UMLEditor.class).put(KEY_LAST_IMAGE_FORMAT, format);
				
		File dir = file.getParentFile();
		if( dir != null )
		{
			setLastDir(KEY_LAST_EXPORT_DIR, dir);
		}
		DiagramTab frame = getSelectedDiagramTab();
		try (OutputStream out = new FileOutputStream(file)) 
		{
			BufferedImage image = getBufferedImage(frame.getDiagram()); 
			if(format.equals("jpg"))	// to correct the display of JPEG/JPG images (removes red hue)
			{
				BufferedImage imageRGB = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.OPAQUE);
				Graphics2D graphics = imageRGB.createGraphics();
				graphics.drawImage(image, 0,  0, null);
				ImageIO.write(imageRGB, format, out);
				graphics.dispose();
			}
			else if(format.equals("bmp"))	// to correct the BufferedImage type
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
		catch(IOException exception) 
		{
			Alert alert = new Alert(AlertType.ERROR, RESOURCES.getString("error.save_file"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.showAndWait();
		}
	}
	
	private FileChooser getImageFileChooser(File pInitialDirectory, String pInitialFormat) 
	{
		assert pInitialDirectory.exists() && pInitialDirectory.isDirectory();
		DiagramTab frame = getSelectedDiagramTab();

		FileChooser fileChooser = new FileChooser();
		for(String format : IMAGE_FORMATS ) 
		{
			ExtensionFilter filter = new ExtensionFilter(format.toUpperCase() + " " + RESOURCES.getString("files.image.name"), "*." +format);
			fileChooser.getExtensionFilters().add(filter);
			if( format.equals(pInitialFormat ))
			{
				fileChooser.setSelectedExtensionFilter(filter);
			}
		}
		fileChooser.setInitialDirectory(pInitialDirectory);

		// If the file was previously saved, use that to suggest a file name root.
		if(frame.getFile() != null) 
		{
			File file = new File(replaceExtension(frame.getFile().getAbsolutePath(), RESOURCES.getString("application.file.extension"), ""));
			fileChooser.setInitialDirectory(file.getParentFile());
			fileChooser.setInitialFileName(file.getName());
		}
		return fileChooser;
	}

	/*
	 * Return the image corresponding to the graph.
	 * 
	 * @param pDiagram The graph to convert to an image.
	 * 
	 * @return bufferedImage. To convert it into an image, use the syntax :
	 * Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
	 */
	private static BufferedImage getBufferedImage(Diagram pDiagram) 
	{
		return SwingFXUtils.fromFXImage(ImageCreator.createImage(pDiagram), null);
	}
	
	private int getNumberOfDirtyDiagrams()
	{
		return (int) tabs().stream()
			.filter( tab -> tab instanceof DiagramTab ) 
			.filter( frame -> ((DiagramTab) frame).isModified())
			.count();
	}

	/**
	 * Exits the program if no graphs have been modified or if the user agrees to
	 * abandon modified graphs.
	 */
	public void exit() 
	{
		final int modcount = getNumberOfDirtyDiagrams();
		if (modcount > 0) 
		{
			Alert alert = new Alert(AlertType.CONFIRMATION, 
					MessageFormat.format(RESOURCES.getString("dialog.exit.ok"), new Object[] { Integer.valueOf(modcount) }),
					ButtonType.YES, 
					ButtonType.NO);
			alert.initOwner(aMainStage);
			alert.setTitle(RESOURCES.getString("dialog.exit.title"));
			alert.setHeaderText(RESOURCES.getString("dialog.exit.title"));
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
	
	private List<Tab> tabs()
	{
		return ((TabPane) getCenter()).getTabs();
	}
	
	private TabPane tabPane()
	{
		return (TabPane) getCenter();
	}
	
	private boolean isWelcomeTabShowing()
	{
		return aWelcomeTab != null && 
				tabs().size() == 1 && 
				tabs().get(0) instanceof WelcomeTab;
	}
	
	/* Insert a graph frame into the tabbedpane */ 
	private void insertGraphFrameIntoTabbedPane(DiagramTab pGraphFrame) 
	{
		if( isWelcomeTabShowing() )
		{
			tabs().remove(0);
		}
		tabs().add(pGraphFrame);
		tabPane().getSelectionModel().selectLast();
	}
	
	/*
	 * Shows the welcome tab if there are no other tabs.
	 */
	private void showWelcomeTabIfNecessary() 
	{
		if( tabs().size() == 0)
		{
			aWelcomeTab.loadRecentFileLinks(getOpenFileHandlers());
			tabs().add(aWelcomeTab);
		}
	}
	
	/*
	 * Removes the graph frame from the tabbed pane
	 */
	private void removeGraphFrameFromTabbedPane(DiagramTab pTab) 
	{
		pTab.close();
		tabs().remove(pTab);
		showWelcomeTabIfNecessary();
	}
}
