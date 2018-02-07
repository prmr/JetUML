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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import ca.mcgill.cs.jetuml.UMLEditor;
import ca.mcgill.cs.jetuml.application.FileExtensions;
import ca.mcgill.cs.jetuml.application.RecentFilesQueue;
import ca.mcgill.cs.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.graph.Graph;
import ca.mcgill.cs.jetuml.persistence.DeserializationException;
import ca.mcgill.cs.jetuml.persistence.PersistenceService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

/**
 * This panel contains panes that show graphs.
 * 
 * @author Cay S. Horstmann - Original code
 * @author Martin P. Robillard - Refactorings, file handling, menu management.
 * @author Kaylee I. Kutschera - Migration to JavaFX
 */
@SuppressWarnings("serial")
public class EditorFrame extends JPanel
{
	private static final int FRAME_GAP = 20;
	private static final int ESTIMATED_FRAMES = 5;
	private static final int MAX_RECENT_FILES = 8;
	private static final int MARGIN_IMAGE = 2; // Number of pixels to leave around the graph when exporting it as an
												// image
	private static final int HELP_MENU_TEXT_WIDTH = 32; // Number of columns for the text area of the Help Menu.
	private static final int HELP_MENU_TEXT_HEIGHT = 10; // Number of rows for the text area of the Help Menu.
	private static final int HELP_MENU_SPACING = 10; // Number of pixels between text area and button of the Help Menu.
	private static final int HELP_MENU_PADDING = 10; // Number of pixels padding the nodes in the Help Menu.
	
	private Stage aMainStage;
	private MenuFactory aAppFactory;
	private ResourceBundle aAppResources;
	private ResourceBundle aVersionResources;
	private ResourceBundle aEditorResources;
	private JTabbedPane aTabbedPane;
	private ArrayList<JInternalFrame> aTabs = new ArrayList<>();
	private JMenu aNewMenu;
	private JMenuBar aMenuBar = new JMenuBar();
	
	private RecentFilesQueue aRecentFiles = new RecentFilesQueue();
	private JMenu aRecentFilesMenu;

	private WelcomeTab aWelcomeTab;

	// Menus or menu items that must be disabled if there is no current diagram.
	private final List<JMenuItem> aDiagramRelevantMenus = new ArrayList<>();

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

		aTabbedPane = new JTabbedPane();
		aTabbedPane.addChangeListener(new ChangeListener() 
		{
			@Override
			public void stateChanged(ChangeEvent pEvent) 
			{
				boolean noGraphFrame = noCurrentGraphFrame();
				for (JMenuItem menuItem : aDiagramRelevantMenus) 
				{
					menuItem.setEnabled(!noGraphFrame);
				}
			}
		});

		this.setLayout(new BorderLayout());
		this.add(aMenuBar, BorderLayout.NORTH);
		this.add(aTabbedPane, BorderLayout.CENTER);

		createFileMenu(factory);
		createEditMenu(factory);
		createViewMenu(factory);
		createHelpMenu(factory);
	}

	private void createFileMenu(MenuFactory pFactory) 
	{
		JMenu fileMenu = pFactory.createMenu("file");
		fileMenu.setVisible(true);
		aMenuBar.add(fileMenu);

		aNewMenu = pFactory.createMenu("file.new");
		fileMenu.add(aNewMenu);

		JMenuItem fileOpenItem = pFactory.createMenuItem("file.open", this, "openFile");
		fileMenu.add(fileOpenItem);

		aRecentFilesMenu = pFactory.createMenu("file.recent");
		buildRecentFilesMenu();
		fileMenu.add(aRecentFilesMenu);

		JMenuItem closeFileItem = pFactory.createMenuItem("file.close", this, "close");
		fileMenu.add(closeFileItem);
		aDiagramRelevantMenus.add(closeFileItem);
		closeFileItem.setEnabled(!noCurrentGraphFrame());

		JMenuItem fileSaveItem = pFactory.createMenuItem("file.save", this, "save");
		fileMenu.add(fileSaveItem);
		aDiagramRelevantMenus.add(fileSaveItem);
		fileSaveItem.setEnabled(!noCurrentGraphFrame());

		JMenuItem fileSaveAsItem = pFactory.createMenuItem("file.save_as", this, "saveAs");
		fileMenu.add(fileSaveAsItem);
		aDiagramRelevantMenus.add(fileSaveAsItem);
		fileSaveAsItem.setEnabled(!noCurrentGraphFrame());

		JMenuItem fileExportItem = pFactory.createMenuItem("file.export_image", this, "exportImage");
		fileMenu.add(fileExportItem);
		aDiagramRelevantMenus.add(fileExportItem);
		fileExportItem.setEnabled(!noCurrentGraphFrame());

		JMenuItem fileCopyToClipboard = pFactory.createMenuItem("file.copy_to_clipboard", this, "copyToClipboard");
		fileMenu.add(fileCopyToClipboard);
		aDiagramRelevantMenus.add(fileCopyToClipboard);
		fileCopyToClipboard.setEnabled(!noCurrentGraphFrame());

		fileMenu.addSeparator();

		JMenuItem fileExitItem = pFactory.createMenuItem("file.exit", this, "exit");
		fileMenu.add(fileExitItem);
	}

	private void createEditMenu(MenuFactory pFactory) 
	{
		JMenu editMenu = pFactory.createMenu("edit");
		editMenu.setVisible(true);
		aMenuBar.add(editMenu);

		aDiagramRelevantMenus.add(editMenu);
		editMenu.setEnabled(!noCurrentGraphFrame());

		editMenu.add(pFactory.createMenuItem("edit.undo", new ActionListener() 
		{
			public void actionPerformed(ActionEvent pEvent) 
			{
				if (noCurrentGraphFrame()) 
     	
				{
					return;
				}
				((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().undo();
			}
		}));

		editMenu.add(pFactory.createMenuItem("edit.redo", new ActionListener() 
		{
			public void actionPerformed(ActionEvent pEvent) 
			{
				if (noCurrentGraphFrame()) 
				{
					return;
				}
				((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().redo();
			}
		}));

		editMenu.add(pFactory.createMenuItem("edit.selectall", new ActionListener() 
		{
			public void actionPerformed(ActionEvent pEvent) 
			{
				if (noCurrentGraphFrame()) 
				{
					return;
				}
				((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().selectAll();
			}
		}));

		editMenu.add(pFactory.createMenuItem("edit.properties", new ActionListener() 
		{
			public void actionPerformed(ActionEvent pEvent) 
			{
				if (noCurrentGraphFrame()) 
				{
					return;
				}
				((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().editSelected();
			}
		}));

		editMenu.add(pFactory.createMenuItem("edit.cut", this, "cut"));
		editMenu.add(pFactory.createMenuItem("edit.paste", this, "paste"));
		editMenu.add(pFactory.createMenuItem("edit.copy", this, "copy"));

		editMenu.add(pFactory.createMenuItem("edit.delete", new ActionListener() 
		{
			public void actionPerformed(ActionEvent pEvent) 
			{
				if (noCurrentGraphFrame()) 
				{
					return;
				}
				((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().removeSelected();
			}
		}));
	}

	private void createViewMenu(MenuFactory pFactory) 
	{
		JMenu viewMenu = pFactory.createMenu("view");
		viewMenu.setVisible(true);
		aMenuBar.add(viewMenu);
		aDiagramRelevantMenus.add(viewMenu);
		viewMenu.setEnabled(!noCurrentGraphFrame());
		

		viewMenu.add(pFactory.createMenuItem("view.zoom_out", new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				if( noCurrentGraphFrame() )
				{
					return;
				}
				((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().zoomOut();
			}
		}));
		viewMenu.add(pFactory.createMenuItem("view.zoom_in", new ActionListener()
		{
		    public void actionPerformed(ActionEvent pEvent)
		    {
		    	if( noCurrentGraphFrame() )
		    	{
		    		return;
		    	}
		    	((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().zoomIn();
		    }
		}));
	
		final JCheckBoxMenuItem hideGridItem  = (JCheckBoxMenuItem) pFactory.createCheckBoxMenuItem("view.hide_grid", new ActionListener()
		{
		    public void actionPerformed(ActionEvent pEvent)
		    {
		    	if( noCurrentGraphFrame() )
		    	{
		    		return;
		    	}
		    	GraphFrame frame = (GraphFrame)aTabbedPane.getSelectedComponent();
		    	GraphPanel panel = frame.getGraphPanel();
		    	JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) pEvent.getSource();               
		    	panel.setHideGrid(menuItem.isSelected());
		    }
		});
		viewMenu.add(hideGridItem);
	
		viewMenu.addMenuListener(new MenuListener()
		{
			public void menuSelected(MenuEvent pEvent)
			{
		 		if(aTabbedPane.getSelectedComponent() instanceof WelcomeTab)
		 		{
		 			return;
		 		}	
					GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
						if (frame == null) 
						{
							return;
						}
						GraphPanel panel = frame.getGraphPanel();
						hideGridItem.setSelected(panel.getHideGrid());
			}
	
			public void menuDeselected(MenuEvent pEvent) {}
	
			public void menuCanceled(MenuEvent pEvent) {}
		});
	}

	private void createHelpMenu(MenuFactory pFactory) 
	{
		JMenu helpMenu = pFactory.createMenu("help");
		helpMenu.setVisible(true);
		aMenuBar.add(helpMenu);

		helpMenu.add(pFactory.createMenuItem("help.about", this, "showAboutDialog"));
		helpMenu.add(pFactory.createMenuItem("help.license", pActionEvent -> 
		{
			Platform.runLater(() -> 
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
					button.setOnAction(pEvent -> window.close());
					button.addEventHandler(KeyEvent.KEY_PRESSED, pEvent -> 
					{
						if (pEvent.getCode() == KeyCode.ENTER) 
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
					window.showAndWait();
				} 
				catch(IOException exception){}
			});
		}));
	}

	/**
	 * Adds a graph type to the File->New menu.
	 * 
	 * @param pResourceName
	 *            the name of the menu item resource
	 * @param pGraphClass
	 *            the class object for the graph
	 */
	public void addGraphType(String pResourceName, final Class<?> pGraphClass) 
	{
		aNewMenu.add(aAppFactory.createMenuItem(pResourceName, new ActionListener() 
		{
			public void actionPerformed(ActionEvent pEvent) 
			{
				try 
				{
					GraphFrame frame = new GraphFrame((Graph) pGraphClass.newInstance(), aTabbedPane);
					addTab(frame);
				}
				catch (Exception exception) 
				{
					exception.printStackTrace();
				}
			}
		}));
	}

	/**
	 * Reads the command line arguments.
	 * 
	 * @param pArgs
	 *            the command line arguments
	 */
	public void readArgs(String[] pArgs) 
	{
		if (pArgs.length != 0) 
		{
			for (String argument : pArgs) 
			{
				open(argument);
			}
		}
		/* @JoelChev may be needed later */
		// setTitle();
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
			if (aTabbedPane.getComponentAt(i) instanceof GraphFrame) 
			{
				GraphFrame frame = (GraphFrame) aTabbedPane.getComponentAt(i);
				if (frame.getFileName() != null	&& frame.getFileName().getAbsoluteFile().equals(new File(pName).getAbsoluteFile())) 
				{
					try 
					{
						frame.toFront();
						frame.setSelected(true);
						addRecentFile(new File(pName).getPath());
					}
					catch (PropertyVetoException exception) {}
					return;
				}
			}
		}
		try 
		{
			Graph graph = PersistenceService.read(new File(pName));
			GraphFrame frame = new GraphFrame(graph, aTabbedPane);
			frame.setFile(new File(pName).getAbsoluteFile());
			addRecentFile(new File(pName).getPath());
			addTab(frame);
		} 
		catch (IOException | DeserializationException exception) 
		{
			Platform.runLater(() ->
			{
				Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.open_file"), ButtonType.OK);
				alert.initOwner(aMainStage);
				alert.showAndWait();
			});
		}
	}

	/*
	 * Adds an InternalFrame to the list of Tabs.
	 * 
	 * @param c the component to display in the internal frame
	 * 
	 * @param t the title of the internal frame.
	 */
	private void addTab(final JInternalFrame pInternalFrame) 
	{
		int frameCount = aTabbedPane.getComponentCount();
		BasicInternalFrameUI ui = (BasicInternalFrameUI) pInternalFrame.getUI();
		Container north = ui.getNorthPane();
		north.remove(0);
		north.validate();
		north.repaint();
		aTabbedPane.add(setTitle(pInternalFrame), pInternalFrame);
		int i = aTabs.size();
		aTabbedPane.setTabComponentAt(i, new ButtonTabComponent(this, pInternalFrame, aTabbedPane));
		aTabs.add(pInternalFrame);
		// position frame
		int emptySpace = FRAME_GAP * Math.max(ESTIMATED_FRAMES, frameCount);
		int width = Math.max(aTabbedPane.getWidth() / 2, aTabbedPane.getWidth() - emptySpace);
		int height = Math.max(aTabbedPane.getHeight() / 2, aTabbedPane.getHeight() - emptySpace);

		pInternalFrame.reshape(frameCount * FRAME_GAP, frameCount * FRAME_GAP, width, height);
		pInternalFrame.show();
		int last = aTabs.size();
		aTabbedPane.setSelectedIndex(last - 1);
		if (aTabbedPane.getComponentAt(0) instanceof WelcomeTab) 
		{
			removeWelcomeTab();
		}

	}

	/**
	 * @param pInternalFrame
	 *            The current frame to give a Title in its tab.
	 * @return The title of a given tab.
	 */
	private String setTitle(JInternalFrame pInternalFrame) 
	{
		String appName = aAppResources.getString("app.name");
		String diagramName;

		if (pInternalFrame == null || !(pInternalFrame instanceof GraphFrame)) 
		{
			return appName;
		} 
		else 
		{
			GraphFrame frame = (GraphFrame) pInternalFrame;
			File file = frame.getFileName();
			if (file == null) 
			{
				Graph graphType = frame.getGraph();
				if (graphType instanceof ClassDiagramGraph) 
				{
					diagramName = "Class Diagram";
				} 
				else if (graphType instanceof ObjectDiagramGraph) 
				{
					diagramName = "Object Diagram";
				} 
				else if (graphType instanceof UseCaseDiagramGraph) 
				{
					diagramName = "Use Case Diagram";
				} 
				else if (graphType instanceof StateDiagramGraph) 
				{
					diagramName = "State Diagram";
				} 
				else 
				{
					diagramName = "Sequence Diagram";
				}
				return diagramName;
			} 
			else 
			{
				return file.getName();
			}
		}
	}

	/**
	 * This adds a WelcomeTab to the tabs. This is only done if all other tabs have
	 * been previously closed.
	 */
	public void addWelcomeTab() 
	{
		aWelcomeTab = new WelcomeTab(aNewMenu, aRecentFilesMenu);
		aTabbedPane.add("Welcome", aWelcomeTab);
		aTabs.add(aWelcomeTab);
	}

	/**
	 * This method removes the WelcomeTab after a file has been opened or a diagram
	 * starts being created.
	 */
	public void removeWelcomeTab() 
	{
		if (aWelcomeTab != null) 
		{
			aTabbedPane.remove(0);
			aTabs.remove(0);
		}
	}

	/**
	 * @param pInternalFrame
	 *            The JInternalFrame to remove. Calling this method will remove a
	 *            given JInternalFrame.
	 */
	public void removeTab(final JInternalFrame pInternalFrame) 
	{
		if (!aTabs.contains(pInternalFrame)) 
		{
			return;
		}
		JTabbedPane tp = aTabbedPane;
		int pos = aTabs.indexOf(pInternalFrame);
		tp.remove(pos);
		aTabs.remove(pInternalFrame);
		if (aTabs.size() == 0) 
		{
			aWelcomeTab = new WelcomeTab(aNewMenu, aRecentFilesMenu);
			aTabbedPane.add("Welcome", aWelcomeTab);
			aTabs.add(aWelcomeTab);
		}
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
	 * Rebuilds the "recent files" menu. Only works if the number of recent files is
	 * less than 8. Otherwise, additional logic will need to be added to 0-index the
	 * mnemonics for files 1-9
	 */
	private void buildRecentFilesMenu() 
	{
		assert aRecentFiles.size() <= MAX_RECENT_FILES;
		aRecentFilesMenu.removeAll();
		aRecentFilesMenu.setEnabled(aRecentFiles.size() > 0);
		int i = 1;
		for (File file : aRecentFiles) 
		{
			String name = i + " " + file.getName();
			final String fileName = file.getAbsolutePath();
			JMenuItem item = new JMenuItem(name);
			item.setMnemonic('0' + i);
			aRecentFilesMenu.add(item);
			item.addActionListener(new ActionListener() 
			{
				public void actionPerformed(ActionEvent pEvent) 
				{
					open(fileName);
				}
			});
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
		Platform.runLater(() -> 
		{
			File selectedFile = fileChooser.showOpenDialog(aMainStage);
			SwingUtilities.invokeLater(() -> open(selectedFile.getAbsolutePath()));
		});
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
		GraphPanel panel = ((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel();
		panel.cut();
		panel.repaint();
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
		((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().copy();
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
		GraphPanel panel = ((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel();
		panel.paste();
		panel.repaint();
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
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
		final BufferedImage image = getImage(frame.getGraph());
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new Transferable() 
		{
			@Override
			public boolean isDataFlavorSupported(DataFlavor pFlavor) 
			{
				return DataFlavor.imageFlavor.equals(pFlavor);
			}

			@Override
			public DataFlavor[] getTransferDataFlavors() 
			{
				return new DataFlavor[] { DataFlavor.imageFlavor };
			}

			@Override
			public Object getTransferData(DataFlavor pFlavor) throws UnsupportedFlavorException, IOException 
			{
				if (DataFlavor.imageFlavor.equals(pFlavor)) 
				{
					return image;
				}
				else 
				{
					throw new UnsupportedFlavorException(pFlavor);
				}
			}
		}, null);
		Platform.runLater(() -> 
		{
			Alert alert = new Alert(AlertType.INFORMATION, aEditorResources.getString("dialog.to_clipboard.message"), ButtonType.OK);
			alert.initOwner(aMainStage);
			alert.setHeaderText(aEditorResources.getString("dialog.to_clipboard.title"));
			alert.showAndWait();
		});
	}

	private boolean noCurrentGraphFrame() 
	{
		return aTabbedPane.getSelectedComponent() == null || !(aTabbedPane.getSelectedComponent() instanceof GraphFrame);
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
		JInternalFrame curFrame = (JInternalFrame) aTabbedPane.getSelectedComponent();
		if (curFrame != null) 
		{
			GraphFrame openFrame = (GraphFrame) curFrame;
			// we only want to check attempts to close a frame
			if (openFrame.getGraphPanel().isModified()) 
			{
				// ask user if it is ok to close
				Platform.runLater(() -> 
				{
					Alert alert = new Alert(AlertType.CONFIRMATION, aEditorResources.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
					alert.initOwner(aMainStage);
					alert.setTitle(aEditorResources.getString("dialog.close.title"));
					alert.setHeaderText(aEditorResources.getString("dialog.close.title"));
					alert.showAndWait();

					if (alert.getResult() == ButtonType.YES) 
					{
						SwingUtilities.invokeLater(() -> removeTab(curFrame));
					}
				});
				return;
			} 
			else 
			{
				removeTab(curFrame);
			}
		}
	}

	/**
	 * If a user confirms that they want to close their modified graph, this method
	 * will remove it from the current list of tabs.
	 * 
	 * @param pJInternalFrame
	 *            The current JInternalFrame that one wishes to close.
	 */
	public void close(JInternalFrame pJInternalFrame) 
	{
		JInternalFrame curFrame = pJInternalFrame;
		if (curFrame != null) 
		{
			GraphFrame openFrame = (GraphFrame) curFrame;
			// we only want to check attempts to close a frame
			if (openFrame.getGraphPanel().isModified()) 
			{
				if (openFrame.getGraphPanel().isModified()) 
				{
					// ask user if it is ok to close
					Platform.runLater(() -> 
					{
						Alert alert = new Alert(AlertType.CONFIRMATION, aEditorResources.getString("dialog.close.ok"), ButtonType.YES, ButtonType.NO);
						alert.initOwner(aMainStage);
						alert.setTitle(aEditorResources.getString("dialog.close.title"));
						alert.setHeaderText(aEditorResources.getString("dialog.close.title"));
						alert.showAndWait();

						if (alert.getResult() == ButtonType.YES) 
						{
							SwingUtilities.invokeLater(() -> removeTab(curFrame));
						}
					});
				}
				return;
			}
			removeTab(curFrame);
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
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
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
		catch (Exception exception) 
		{
				Platform.runLater(() -> 
				{
					Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.save_file"), ButtonType.OK);
					alert.initOwner(aMainStage);
					alert.showAndWait();
				});
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
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
		Graph graph = frame.getGraph();

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

		Platform.runLater(() -> 
		{
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
					aTabbedPane.setTitleAt(aTabbedPane.getSelectedIndex(), frame.getFileName().getName());
					frame.getGraphPanel().setModified(false);
				}
			} 
			catch (IOException exception) 
			{
				Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.save_file"), ButtonType.OK);
				alert.initOwner(aMainStage);
				alert.showAndWait();
			}
		});
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
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();

		FileChooser fileChooser = getImageFileChooser();
		Platform.runLater(() -> 
		{
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

			try (OutputStream out = new FileOutputStream(file)) 
			{
				ImageIO.write(getImage(frame.getGraph()), format, out);
			} 
			catch (IOException exception) 
			{
				Alert alert = new Alert(AlertType.ERROR, aEditorResources.getString("error.save_file"), ButtonType.OK);
				alert.initOwner(aMainStage);
				alert.showAndWait();
			}
		});
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
		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
		assert frame != null;

		// Initialize the file chooser widget
		FileChooser fileChooser = new FileChooser();
		for (String format : getAllSupportedImageWriterFormats()) 
		{
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
	private static BufferedImage getImage(Graph pGraph) 
	{
		Rectangle bounds = pGraph.getBounds();
		BufferedImage image = new BufferedImage((int) (bounds.getWidth() + MARGIN_IMAGE * 2),
				(int) (bounds.getHeight() + MARGIN_IMAGE * 2), BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = (Graphics2D) image.getGraphics();
		g2.translate(-bounds.getX(), -bounds.getY());
		g2.setColor(Color.WHITE);
		g2.fill(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth() + MARGIN_IMAGE * 2,
				bounds.getHeight() + MARGIN_IMAGE * 2));
		g2.translate(MARGIN_IMAGE, MARGIN_IMAGE);
		g2.setColor(Color.BLACK);
		g2.setBackground(Color.WHITE);
		pGraph.draw(g2);
		return image;
	}

	/**
	 * Displays the About dialog box.
	 */
	public void showAboutDialog() 
	{
		Platform.runLater(() -> 
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
		});
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
			Platform.runLater(() -> 
			{
				Alert alert = new Alert(AlertType.CONFIRMATION, 
						MessageFormat.format(aEditorResources.getString("dialog.exit.ok"), new Object[] { new Integer(finalModCount) }),
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
			});
		}
		else 
		{
			Preferences.userNodeForPackage(UMLEditor.class).put("recent", aRecentFiles.serialize());
			System.exit(0);
		}
	}		
}
