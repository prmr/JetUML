/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2016 by the contributors of the JetUML project.
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

package ca.mcgill.cs.stg.jetuml.framework;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import ca.mcgill.cs.stg.jetuml.UMLEditor;
import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.Edge;
import ca.mcgill.cs.stg.jetuml.graph.Graph;
import ca.mcgill.cs.stg.jetuml.graph.GraphElement;
import ca.mcgill.cs.stg.jetuml.graph.Node;

/**
 * This desktop frame contains panes that show graphs.
 * @author Cay S. Horstmann - Original code
 * @author Martin P. Robillard - Refactorings, file handling.
 */
@SuppressWarnings("serial")
public class EditorFrame extends JFrame
{
	private static final int FRAME_GAP = 20;
	private static final int ESTIMATED_FRAMES = 5;
	private static final int MAX_RECENT_FILES = 8;
	private static final double GROW_SCALE_FACTOR = Math.sqrt(2);
	private static final int MARGIN_SCREEN = 8; // Fraction of the screen to leave around the sides
	private static final int MARGIN_IMAGE = 2; // Number of pixels to leave around the graph when exporting it as an image
	private static final int HELP_MENU_TEXT_WIDTH = 10; //Number of pixels to give to the width of the  text area of the Help Menu.
	private static final int HELP_MENU_TEXT_HEIGHT = 40; //Number of pixels to give to the height of the text area of the Help Menu.
	
	
	private MenuFactory aAppFactory;
	private ResourceBundle aAppResources;
	private ResourceBundle aVersionResources;
	private ResourceBundle aEditorResources;
	private JTabbedPane aTabbedPane;
	private ArrayList<JInternalFrame> aTabs = new ArrayList<>();
	private JMenu aNewMenu;
	private Clipboard aClipboard = new Clipboard();
	
	private RecentFilesQueue aRecentFiles = new RecentFilesQueue();
	private JMenu aRecentFilesMenu;
	
	private WelcomeTab aWelcomeTab;

	/**
	 * Constructs a blank frame with a desktop pane
     * but no graph windows.
     * @param pAppClass the fully qualified app class name.
     * It is expected that the resources are appClassName + "Strings"
     * and appClassName + "Version" (the latter for version-specific
     * resources)
     */
	public EditorFrame(Class<?> pAppClass)
	{  
		String appClassName = pAppClass.getName();
		aAppResources = ResourceBundle.getBundle(appClassName + "Strings");
		aAppFactory = new MenuFactory(aAppResources);
		aVersionResources = ResourceBundle.getBundle(appClassName + "Version");
		aEditorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");      
		MenuFactory factory = new MenuFactory(aEditorResources);
		
		aRecentFiles.deserialize(Preferences.userNodeForPackage(UMLEditor.class).get("recent", "").trim());
      
		setTitle(aAppResources.getString("app.name"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  
		int screenWidth = (int)screenSize.getWidth();
		int screenHeight = (int)screenSize.getHeight();

		setBounds(screenWidth / (MARGIN_SCREEN*2), screenHeight / (MARGIN_SCREEN*2), (screenWidth * (MARGIN_SCREEN-1)) / MARGIN_SCREEN, 
				(screenHeight * (MARGIN_SCREEN-1))/MARGIN_SCREEN);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter()
		{
            public void windowClosing(WindowEvent pEvent)
            {
               exit();
            }
		});

		aTabbedPane = new JTabbedPane();
		setContentPane(aTabbedPane);

     	setJMenuBar(new JMenuBar());
     	
		createFileMenu(factory);
		createEditMenu(factory);
		createViewMenu(factory);
     	createHelpMenu(factory);
	}
	
	private void createFileMenu(MenuFactory pFactory)
	{
		JMenuBar menuBar = getJMenuBar();
     	JMenu fileMenu = pFactory.createMenu("file");
     	menuBar.add(fileMenu);

     	aNewMenu = pFactory.createMenu("file.new");
     	fileMenu.add(aNewMenu);

     	JMenuItem fileOpenItem = pFactory.createMenuItem("file.open", this, "openFile"); 
     	fileMenu.add(fileOpenItem);      

     	aRecentFilesMenu = pFactory.createMenu("file.recent");
     	buildRecentFilesMenu();
     	fileMenu.add(aRecentFilesMenu);
     	
     	JMenuItem closeFileItem = pFactory.createMenuItem("file.close", this, "close");
     	fileMenu.add(closeFileItem);
      
     	JMenuItem fileSaveItem = pFactory.createMenuItem("file.save", this, "save"); 
     	fileMenu.add(fileSaveItem);
     	JMenuItem fileSaveAsItem = pFactory.createMenuItem("file.save_as", this, "saveAs");
     	fileMenu.add(fileSaveAsItem);

     	JMenuItem fileExportItem = pFactory.createMenuItem("file.export_image", this, "exportImage"); 
     	fileMenu.add(fileExportItem);
     	
     	JMenuItem fileCopyToClipboard = pFactory.createMenuItem("file.copy_to_clipboard", this, "copyToClipboard"); 
     	fileMenu.add(fileCopyToClipboard);
     	
     	fileMenu.addSeparator();

     	JMenuItem fileExitItem = pFactory.createMenuItem("file.exit", this, "exit");
     	fileMenu.add(fileExitItem);
	}
	
	private void createEditMenu(MenuFactory pFactory)
	{
		JMenuBar menuBar = getJMenuBar();
		JMenu editMenu = pFactory.createMenu("edit");
     	menuBar.add(editMenu);
     	
     	editMenu.add(pFactory.createMenuItem("edit.undo", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
     		{
     			if( noCurrentGraphFrame() )
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
     			if( noCurrentGraphFrame() )
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
     			if( noCurrentGraphFrame() )
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
     			if( noCurrentGraphFrame() )
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
            	if( noCurrentGraphFrame() )
     			{
     				return;
     			}
     			((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().removeSelected();
            }
     	}));
	}
	
	private void createViewMenu(MenuFactory pFactory)
	{
		JMenuBar menuBar = getJMenuBar();
		
		JMenu viewMenu = pFactory.createMenu("view");
     	menuBar.add(viewMenu);

     	viewMenu.add(pFactory.createMenuItem("view.zoom_out", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
     		{
     			if( noCurrentGraphFrame() )
     			{
     				return;
     			}
     			((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().changeZoom(-1);
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
            	((GraphFrame) aTabbedPane.getSelectedComponent()).getGraphPanel().changeZoom(1);
            }
     	}));
      
     	viewMenu.add(pFactory.createMenuItem("view.grow_drawing_area", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
     		{
     			if( noCurrentGraphFrame() )
            	{
            		return;
            	}
     			GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
     			Graph g = frame.getGraph();
     			Rectangle2D bounds = g.getBounds();
     			bounds.add(frame.getGraphPanel().getBounds());
     			g.setMinBounds(new Rectangle2D.Double(0, 0, GROW_SCALE_FACTOR * bounds.getWidth(), GROW_SCALE_FACTOR * bounds.getHeight()));
                frame.getGraphPanel().revalidate();
                frame.repaint();
     		}
     	}));
      
     	viewMenu.add(pFactory.createMenuItem("view.clip_drawing_area", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
     		{
     			if( noCurrentGraphFrame() )
            	{
            		return;
            	}
     			GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
                Graph g = frame.getGraph();
                g.setMinBounds(new Rectangle2D.Double()); 
                frame.getGraphPanel().revalidate();
                frame.repaint();
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
                if(frame == null)
				{
					return;
				}
                GraphPanel panel = frame.getGraphPanel();
                hideGridItem.setSelected(panel.getHideGrid());  
            }
     		public void menuDeselected(MenuEvent pEvent)
            {}
            public void menuCanceled(MenuEvent pEvent)
            {}
     	});
	}
	
	
	private void createHelpMenu(MenuFactory pFactory)
	{
		JMenuBar menuBar = getJMenuBar();
		JMenu helpMenu = pFactory.createMenu("help");
		menuBar.add(helpMenu);
		
		helpMenu.add(pFactory.createMenuItem("help.about", this, "showAboutDialog"));
		helpMenu.add(pFactory.createMenuItem("help.license", new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				try
				{
					BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("license.txt")));
					JTextArea text = new JTextArea(HELP_MENU_TEXT_WIDTH, HELP_MENU_TEXT_HEIGHT);
					String line;
					while ((line = reader.readLine()) != null)
					{
						text.append(line);
						text.append("\n");
					}   
					text.setCaretPosition(0);
					text.setEditable(false);
					JOptionPane.showInternalMessageDialog(aTabbedPane, new JScrollPane(text), 
							aEditorResources.getString("dialog.license.title"), JOptionPane.PLAIN_MESSAGE);
				}
				catch(IOException exception) 
				{}
			}
		}));
	}
	
	/**
     * Adds a graph type to the File->New menu.
     * @param pResourceName the name of the menu item resource
     * @param pGraphClass the class object for the graph
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
	 * Sets the TaskBar icon for the JetUML application.
	 */
	public void setIcon()
    {
    	try
		{
			java.net.URL url = getClass().getClassLoader().getResource(aAppResources.getString("app.icon"));
			Toolkit kit = Toolkit.getDefaultToolkit();
			Image img = kit.createImage(url);
			setIconImage(img);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
    }

   	/**
     * Reads the command line arguments.
     * @param pArgs the command line arguments
   	 */
	public void readArgs(String[] pArgs)
	{
	   if(pArgs.length != 0)
	   {	
		   for(String argument : pArgs)
		   {
			   open(argument);
		   }
	   } 
	   /*@JoelChev may be needed later*/
	   //setTitle();
   	}
   
   /*
    * Opens a file with the given name, or switches to the frame if it is already open.
    * @param pName the file name
    */
	private void open(String pName)
	{	
		for(int i = 0; i < aTabs.size(); i++)
		{
			if(aTabbedPane.getComponentAt(i) instanceof GraphFrame)
			{
				GraphFrame frame = (GraphFrame)aTabbedPane.getComponentAt(i);
				if(frame.getFileName() != null && frame.getFileName().getAbsoluteFile().equals(new File(pName).getAbsoluteFile())) 
				{
					try
					{
						frame.toFront();
						frame.setSelected(true); 
						addRecentFile(new File(pName).getPath());
					}
					catch(PropertyVetoException exception)
					{}
					return;
				}
			}
		}	      
		try
		{	              
			Graph graph = PersistenceService.read(new FileInputStream(pName));
			GraphFrame frame = new GraphFrame(graph, aTabbedPane);
			frame.setFile(new File(pName).getAbsoluteFile());    
			addRecentFile(new File(pName).getPath());
			addTab(frame);
		}
		catch(IOException exception)
		{
			JOptionPane.showMessageDialog(aTabbedPane, exception.getMessage(), 
    			  aEditorResources.getString("file.open.text"), JOptionPane.ERROR_MESSAGE);
		}      
	}   

	/*
     * Adds an InternalFrame to the list of Tabs.
     * @param c the component to display in the internal frame
     * @param t the title of the internal frame.
    */
   private void addTab(final JInternalFrame pInternalFrame)
   {  
	   int frameCount = aTabbedPane.getComponentCount();   
	   BasicInternalFrameUI ui = (BasicInternalFrameUI)pInternalFrame.getUI();
	   Container north = ui.getNorthPane();
	   north.remove(0);
	   north.validate();
	   north.repaint();
	   aTabbedPane.add(setTitle(pInternalFrame), pInternalFrame);
	   int i = aTabs.size();
	   aTabbedPane.setTabComponentAt(i,
               new ButtonTabComponent(this, pInternalFrame, aTabbedPane));
	   aTabs.add(pInternalFrame);
	   // position frame
	   int emptySpace = FRAME_GAP * Math.max(ESTIMATED_FRAMES, frameCount);
	   int width = Math.max(aTabbedPane.getWidth() / 2, aTabbedPane.getWidth() - emptySpace);            
	   int height = Math.max(aTabbedPane.getHeight() / 2, aTabbedPane.getHeight() - emptySpace);

	   pInternalFrame.reshape(frameCount * FRAME_GAP, frameCount * FRAME_GAP, width, height);
	   pInternalFrame.show(); 
	   int last = aTabs.size();
	   aTabbedPane.setSelectedIndex(last-1);
	   if(aTabbedPane.getComponentAt(0) instanceof WelcomeTab)
	   {
		   removeWelcomeTab();
	   }

   	}

   	/**
   	 * @param pInternalFrame The current frame to give a Title in its tab.
   	 * @return The title of a given tab.
   	 */
   	private String setTitle(JInternalFrame pInternalFrame)
   	{
   		String appName = aAppResources.getString("app.name");
   		String diagramName;
   		
   		if(pInternalFrame == null || !(pInternalFrame instanceof GraphFrame ))
   		{
   			return appName;
   		}
   		else
   		{
   			GraphFrame frame = (GraphFrame)pInternalFrame;
   			File file = frame.getFileName();
   			if( file == null )
   			{
   				Graph graphType = frame.getGraph();
   				if(graphType instanceof ClassDiagramGraph)
   				{
   					diagramName = "Class Diagram";
   				}
   				else if(graphType instanceof ObjectDiagramGraph)
   				{
   					diagramName = "Object Diagram";
   				}
   				else if(graphType instanceof UseCaseDiagramGraph)
   				{
   					diagramName = "Use Case Diagram";
   				}
   				else if(graphType instanceof StateDiagramGraph)
   				{
   					diagramName = "State Diagram";
   				}
   				else
   				{
   					diagramName =  "Sequence Diagram";
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
   	 * This adds a WelcomeTab to the tabs. This is only done if all other tabs have been previously closed.
   	 */
   	public void addWelcomeTab()
   	{
   		aWelcomeTab = new WelcomeTab(aNewMenu, aRecentFilesMenu);
     	aTabbedPane.add("Welcome", aWelcomeTab);
     	aTabs.add(aWelcomeTab);
   	}
   
   	
   	/**
   	 * This method removes the WelcomeTab after a file has been opened or a diagram starts being created.
   	 */
   	public void removeWelcomeTab()
   	{
   		if(aWelcomeTab !=null)
   		{
   			aTabbedPane.remove(0);
   			aTabs.remove(0);
   		}
   	}
   	
   	/**
   	 * @param pInternalFrame The JInternalFrame to remove.
   	 * Calling this metod will remove a given JInternalFrame.
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
        if(aTabs.size() == 0)
        {
        	aWelcomeTab = new WelcomeTab(aNewMenu, aRecentFilesMenu);
        	aTabbedPane.add("Welcome", aWelcomeTab);
        	aTabs.add(aWelcomeTab);
        }
    }
   	
   	/*
   	 * Adds a file name to the "recent files" list and rebuilds the "recent files" menu. 
   	 * @param pNewFile the file name to add
   	 */
   	private void addRecentFile(String pNewFile)
   	{
   		aRecentFiles.add(pNewFile);
   		buildRecentFilesMenu();
   	}
   
   	/*
   	 * Rebuilds the "recent files" menu. Only works if the number of
   	 * recent files is less than 8. Otherwise, additional logic will need
   	 * to be added to 0-index the mnemonics for files 1-9
   	 */
   	private void buildRecentFilesMenu()
   	{ 
   		assert aRecentFiles.size() <= MAX_RECENT_FILES;
   		aRecentFilesMenu.removeAll();
   		aRecentFilesMenu.setEnabled(aRecentFiles.size() > 0);
   		int i = 1;
   		for( File file : aRecentFiles )
   		{
   			String name = i + " " + file.getName();
   			final String fileName = file.getAbsolutePath();
   			JMenuItem item = new JMenuItem(name);
   			item.setMnemonic('0'+i);
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
		JFileChooser fileChooser = new JFileChooser(aRecentFiles.getMostRecentDirectory());
		fileChooser.setFileFilter(new ExtensionFilter(aAppResources.getString("files.name"), aAppResources.getString("files.extension")));
   			// TODO This Editor frame should keep a list of graph types to make this operation not hard-code them
		ExtensionFilter[] filters = new ExtensionFilter[]{
			new ExtensionFilter(aAppResources.getString("state.name"), 
					aAppResources.getString("state.extension") + aAppResources.getString("files.extension")),
   			new ExtensionFilter(aAppResources.getString("object.name"), 
   						aAppResources.getString("object.extension") + aAppResources.getString("files.extension")),
   			new ExtensionFilter(aAppResources.getString("class.name"), 
   						aAppResources.getString("class.extension") + aAppResources.getString("files.extension")),
   			new ExtensionFilter(aAppResources.getString("usecase.name"), 
   						aAppResources.getString("usecase.extension") + aAppResources.getString("files.extension")),
   			new ExtensionFilter(aAppResources.getString("sequence.name"), 
   						aAppResources.getString("sequence.extension") + aAppResources.getString("files.extension"))
   		};
   		for(ExtensionFilter filter: filters)
		{
			fileChooser.addChoosableFileFilter(filter);
		}
		if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
		{
			File file = fileChooser.getSelectedFile();
   			open(file.getAbsolutePath());
		}
   	}
   	
   	/**
   	 * Cuts the current selection of the current panel and 
   	 * puts the content into the application-specific clipboard.
   	 */
   	public void cut()
   	{
   		if( noCurrentGraphFrame() )
   		{
   			return;
   		}
   		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
   		GraphPanel panel = frame.getGraphPanel();
   		Graph curGraph = frame.getGraph();
   		if(panel.getSelectionList().size()>0)
   		{
   			SelectionList currentSelection = panel.getSelectionList();
   			aClipboard.copy(currentSelection);	
   			Iterator<GraphElement> iter = currentSelection.iterator();
   			panel.startCompoundListening();
   			while(iter.hasNext())
   			{
   				GraphElement element = iter.next();
   				if(element instanceof Edge)
   				{
   					curGraph.removeEdge((Edge)element);
   				}
   				else
   				{
   					curGraph.removeNode((Node)element);
   				}
   				iter.remove();
   			}
   			panel.endCompoundListening();
   		}	
   		panel.repaint();
   	}
   	
   	/**
   	 * Copies the current selection of the current panel and 
   	 * puts the content into the application-specific clipboard.
   	 */
   	public void copy()
   	{
   		if( noCurrentGraphFrame() )
   		{
   			return;
   		}
   		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
   		GraphPanel panel = frame.getGraphPanel();
   		if(panel.getSelectionList().size()>0)
   		{
   			SelectionList currentSelection = panel.getSelectionList();
   			aClipboard.copy(currentSelection);
   		}	
   	}
   	
   	/**
   	 * Pastes a past selection from the application-specific Clipboard into current panel. All the
   	 * logic is done in the application-specific CutPasteBehavior. 
   	 * 
   	 */
   	public void paste()
   	{
   		if( noCurrentGraphFrame() )
   		{
   			return;
   		}
   		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
   		
   		GraphPanel panel = frame.getGraphPanel();
   		try
   		{
   			//This method call handles all the paste logic on the current GraphPanel.
   			SelectionList updatedSelectionList = aClipboard.paste(panel.getGraph());
   			panel.setSelectionList(updatedSelectionList);
   			panel.repaint();
   		}
   		finally
   		{
   		}
   	}
   	
   	/**
   	 * Copies the current image to the clipboard.
   	 */
   	public void copyToClipboard()
   	{
   		if( noCurrentGraphFrame() )
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
				if(DataFlavor.imageFlavor.equals(pFlavor))
		        {
		            return image;
		        }
		        else
		        {
		            throw new UnsupportedFlavorException(pFlavor);
		        }
			}
		}, null);
   		JOptionPane.showInternalMessageDialog(aTabbedPane, aEditorResources.getString("dialog.to_clipboard.message"), 
   				aEditorResources.getString("dialog.to_clipboard.title"), JOptionPane.INFORMATION_MESSAGE);
   	}
   	
   	private boolean noCurrentGraphFrame()
   	{
   		return aTabbedPane.getSelectedComponent() == null || !(aTabbedPane.getSelectedComponent() instanceof GraphFrame);
   	}
   	
   	/**
   	 * If a user confirms that they want to close their modified graph, this method will
   	 * remove it from the current list of tabs.
   	 */
   	public void close()
   	{
   		if(noCurrentGraphFrame())
   		{
   			return;
   		}
        JInternalFrame curFrame = (JInternalFrame)aTabbedPane.getSelectedComponent();
        if(curFrame != null)
        {
        	GraphFrame openFrame = (GraphFrame) curFrame;
        	// we only want to check attempts to close a frame
			if(openFrame.getGraphPanel().isModified())
			{                   
				// ask user if it is ok to close
				if(JOptionPane.showConfirmDialog(openFrame, 
						aEditorResources.getString("dialog.close.ok"), null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
				{
					removeTab(curFrame);
				}
				return;
			}
			else
			{
				removeTab(curFrame);
			}
        }
    }
   	
   	/**
   	 * If a user confirms that they want to close their modified graph, this method will
   	 * remove it from the current list of tabs.
   	 * @param pJInternalFrame  The current JInternalFrame that one wishes to close.
   	 */
   	public void close(JInternalFrame pJInternalFrame)
   	{
        JInternalFrame curFrame = pJInternalFrame;
        if (curFrame != null)
        {
        	GraphFrame openFrame = (GraphFrame) curFrame;
        	// we only want to check attempts to close a frame
			if(openFrame.getGraphPanel().isModified())
			{              
				if(JOptionPane.showConfirmDialog(openFrame, 
						aEditorResources.getString("dialog.close.ok"), null, JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) 
				{
					removeTab(curFrame);
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
   		if( noCurrentGraphFrame() )
   		{
   			return;
   		}
   		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
   		File file = frame.getFileName(); 
   		if(file == null) 
   		{	
   			saveAs(); 
   			return; 
   		}
   		try
   		{
   			PersistenceService.saveFile(frame.getGraph(), new FileOutputStream(file));
   			frame.getGraphPanel().setModified(false);
   		}        
   		catch(Exception exception)
   		{
   			JOptionPane.showInternalMessageDialog(aTabbedPane, exception);
   		}        
   	}
   
   	/**
     * Saves the current graph as a new file.
   	 */
   	public void saveAs()
   	{
   		if( noCurrentGraphFrame() )
   		{
   			return;
   		}
   		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
   		Graph graph = frame.getGraph();    
   		try
   		{
   			File result = null;
   			
   	   		JFileChooser fileChooser = new JFileChooser();
   			fileChooser.setFileFilter(new ExtensionFilter(graph.getDescription(), 
					graph.getFileExtension() + aAppResources.getString("files.extension")));
   			fileChooser.setCurrentDirectory(new File("."));
   			
   			if(frame.getFileName() != null)
   			{           
   				fileChooser.setSelectedFile(frame.getFileName());
   			}
   			else 
   			{
   				fileChooser.setSelectedFile(new File(""));
   			}
   			int response = fileChooser.showSaveDialog(this);         
   			if(response == JFileChooser.APPROVE_OPTION)
   			{
   				File f = fileChooser.getSelectedFile();
   				if( !fileChooser.getFileFilter().accept(f))
   				{
   					f = new File(f.getPath() + graph.getFileExtension() + aAppResources.getString("files.extension"));
   				}

   				if(!f.exists()) 
   				{
   					result = f;
   				}
   				else
   				{
   	        		ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
   	        		int theresult = JOptionPane.showConfirmDialog(this, editorResources.getString("dialog.overwrite"), 
   	        				null, JOptionPane.YES_NO_OPTION);
   	        		if(theresult == JOptionPane.YES_OPTION) 
   	        		{
   	        			result = f;
   	        		}
   				}
   			}
   			
   			if(result != null)
   			{
   				OutputStream out = new FileOutputStream(result);
   				try
   				{
   					PersistenceService.saveFile(graph, out);
   				}
   				finally
   				{
   					out.close();
   				}
   				addRecentFile(result.getAbsolutePath());
   				frame.setFile(result);
   				aTabbedPane.setTitleAt(aTabbedPane.getSelectedIndex(), frame.getFileName().getName());
   				frame.getGraphPanel().setModified(false);
   			}
   		}
   		catch(IOException exception)
   		{
   			JOptionPane.showInternalMessageDialog(aTabbedPane, exception);
   		}
   	}

	/**
   	 * Edits the file path so that the pToBeRemoved extension, if found, is replaced 
   	 * with pDesired.
   	 * @param pOriginal the file to use as a starting point
     * @param pToBeRemoved the extension that is to be removed before adding the desired extension.
   	 * @param pDesired the desired extension (e.g. ".png")
   	 * @return original if it already has the desired extension, or a new file with the edited file path
   	 */
	static String replaceExtension(String pOriginal, String pToBeRemoved, String pDesired)
	{
		assert pOriginal != null && pToBeRemoved != null && pDesired != null;
	
		if(pOriginal.endsWith(pToBeRemoved))
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
   		if( noCurrentGraphFrame() )
   		{
   			return;
   		}
   		GraphFrame frame = (GraphFrame) aTabbedPane.getSelectedComponent();
   		
   		File file = chooseFileToExportTo();
   		if( file == null )
   		{
   			return;
   		}
   		
   		// Validate the file format
   		String fileName = file.getPath();
		String format  = fileName.substring(fileName.lastIndexOf(".") + 1);
		if(!ImageIO.getImageWritersByFormatName(format).hasNext())
		{
			JOptionPane.showInternalMessageDialog(aTabbedPane, aEditorResources.getString("error.unsupported_image"),
					aEditorResources.getString("error.unsupported_image.title"), JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		confirmFileOverwrite(file);
   		
   		try( OutputStream out = new FileOutputStream(file))
   		{
   			ImageIO.write(getImage(frame.getGraph()), format, out);
   		}
   		catch(IOException exception)
   		{
   			JOptionPane.showInternalMessageDialog(aTabbedPane, exception);
   		}      
   	}
   	
   	private static String[] getAllSupportedImageWriterFormats()
   	{
   		String[] names = ImageIO.getWriterFormatNames();
   		HashSet<String> formats = new HashSet<String>();
   		for( String name : names )
   		{
   			formats.add(name.toLowerCase());
   		}
   		String[] lReturn = formats.toArray(new String[formats.size()]);
   		Arrays.sort(lReturn);
   		return lReturn;
   	}
   	
   	/* Creates a file filter for pFomat, where pFormat is in all-lowercases */
   	private FileFilter createFileFilter(final String pFormat)
   	{
   		return new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return pFormat.toUpperCase() + " " + aEditorResources.getString("files.image.name");
			}
			
			@Override
			public boolean accept(File pFile)
			{
				return !pFile.isDirectory() && (pFile.getName().endsWith("." +pFormat.toLowerCase()) || 
						pFile.getName().endsWith("." +pFormat.toUpperCase()));
			}
			
			/*
			 * It is important that toString returns exactly the format string
			 * because the chooseFileToExportTo method relies on this convention. 
			 */
			@Override
			public String toString()
			{
				return pFormat;
			}
		};
   	}

   	/*
   	 * Can return null if no file is selected.
   	 */
   	private File chooseFileToExportTo()
   	{
   		GraphFrame frame = (GraphFrame)aTabbedPane.getSelectedComponent();
   		assert frame != null;
 
   		// Initialize the file chooser widget
	   	JFileChooser fileChooser = new JFileChooser();
	   	for( String format : getAllSupportedImageWriterFormats() )
	   	{
	   		fileChooser.addChoosableFileFilter(createFileFilter(format));
	   	}
		fileChooser.setCurrentDirectory(new File("."));
		
		// If the file was previously saved, use that to suggest a file name root.
		if(frame.getFileName() != null)
		{
			File f = new File(replaceExtension(frame.getFileName().getAbsolutePath(), 
					aAppResources.getString("files.extension"), ""));                  
			fileChooser.setSelectedFile(f);
		}
		
		File file = null;
		if(fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			file = fileChooser.getSelectedFile();	
			FileFilter selectedFilter = fileChooser.getFileFilter();
			
			if( !selectedFilter.accept(file) && selectedFilter != fileChooser.getAcceptAllFileFilter())
			{
				file = new File(file.getPath() + "." + 
						selectedFilter.getDescription().substring(0, selectedFilter.toString().length()).toLowerCase());
			}
		}
		return file;
   	}
   	
   	/* Checks if pFile would be overwritten and, if yes, asks for a confirmation.
   	 * If the confirmation is denied, returns null.
   	 */
   	private File confirmFileOverwrite(File pFile)
   	{
   		if(!pFile.exists()) 
		{
			return pFile;
		}
		
		ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
		int result = JOptionPane.showConfirmDialog(this, editorResources.getString("dialog.overwrite"), null, JOptionPane.YES_NO_OPTION);
		if(result == JOptionPane.YES_OPTION) 
		{
			return pFile;	     
		}
		else
		{
			return null;
		}
   	}

   
   	/*
     * Return the image corresponding to the graph.
     * 
     * @param pGraph The graph to convert to an image.
     * @return bufferedImage. To convert it into an image, use the syntax :
     *         Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
     */
    private static BufferedImage getImage(Graph pGraph)
    {
        Rectangle2D bounds = pGraph.getBounds();
        BufferedImage image = new BufferedImage((int) (bounds.getWidth() + MARGIN_IMAGE*2), 
        		(int) (bounds.getHeight() + MARGIN_IMAGE*2), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = (Graphics2D) image.getGraphics();
        g2.translate(-bounds.getX(), -bounds.getY());
        g2.setColor(Color.WHITE);
        g2.fill(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth() + MARGIN_IMAGE*2, bounds.getHeight() + MARGIN_IMAGE*2));
        g2.translate(MARGIN_IMAGE, MARGIN_IMAGE);
        g2.setColor(Color.BLACK);
        g2.setBackground(Color.WHITE);
        pGraph.draw(g2, null);
        return image;
    }
    
   	/**
     * Displays the About dialog box.
   	 */
   	public void showAboutDialog()
   	{
   		MessageFormat formatter = new MessageFormat(aEditorResources.getString("dialog.about.version"));
   		JOptionPane.showInternalMessageDialog(aTabbedPane, formatter.format(new Object[] { 
               aAppResources.getString("app.name"),
               aVersionResources.getString("version.number"),
               aVersionResources.getString("version.date"),
               aAppResources.getString("app.copyright"),
               aEditorResources.getString("dialog.about.license")}),
               new MessageFormat(aEditorResources.getString("dialog.about.title")).format(new Object[] { 
                       aAppResources.getString("app.name")}),
               JOptionPane.INFORMATION_MESSAGE,
               new ImageIcon(getClass().getClassLoader().getResource(aAppResources.getString("app.icon")))); 
   		
   	}

   	/**
     * Exits the program if no graphs have been modified
     * or if the user agrees to abandon modified graphs.
   	 */
   	public void exit()
   	{
   		int modcount = 0;
   		for(int i = 0; i < aTabs.size(); i++)
   		{
   			if(aTabs.get(i) instanceof GraphFrame)
   			{	
				GraphFrame frame = (GraphFrame) aTabs.get(i);
				if(frame.getGraphPanel().isModified()) 
				{
					modcount++;
				}
   			}	
   		}
   		if(modcount > 0)
   		{
   			// ask user if it is ok to close
   			int result = JOptionPane.showInternalConfirmDialog(aTabbedPane, MessageFormat.format(aEditorResources.getString("dialog.exit.ok"),
                     new Object[] { new Integer(modcount) }), null, JOptionPane.YES_NO_OPTION);
         
   			// if the user doesn't agree, veto the close
   			if(result != JOptionPane.YES_OPTION) 
   			{
   				return;
   			}
   		}
   		Preferences.userNodeForPackage(UMLEditor.class).put("recent", aRecentFiles.serialize());
   		System.exit(0);
   	}
}
