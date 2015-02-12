/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2015 Cay S. Horstmann and the contributors of the 
 * JetUML project.
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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.ExceptionListener;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.PropertyVetoException;
import java.beans.Statement;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.filechooser.FileFilter;

import ca.mcgill.cs.stg.jetuml.UMLEditor;
import ca.mcgill.cs.stg.jetuml.graph.AbstractNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

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
	
	private MenuFactory aAppFactory;
	private ResourceBundle aAppResources;
	private ResourceBundle aVersionResources;
	private ResourceBundle aEditorResources;
	private JDesktopPane aDesktop;
	private JMenu aNewMenu;
	
	private RecentFilesQueue aRecentFiles = new RecentFilesQueue();
	private JMenu aRecentFilesMenu;

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

		aDesktop = new JDesktopPane();
		setContentPane(aDesktop);

     	setJMenuBar(new JMenuBar());
     	
		createFileMenu(factory);
		createEditMenu(factory);
		createViewMenu(factory);
    	createWindowMenu(factory);
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

     	editMenu.add(pFactory.createMenuItem("edit.properties", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
            {
               final GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
               if(frame == null)
               {
            	   return;
               }
               GraphPanel panel = frame.getGraphPanel();
               panel.editSelected();
            }
         }));

     	editMenu.add(pFactory.createMenuItem("edit.delete", new ActionListener()
     	{
            public void actionPerformed(ActionEvent pEvent)
            {
               GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
               if(frame == null)
               {
            	   return;
               }
               GraphPanel panel = frame.getGraphPanel();
               panel.removeSelected();
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
               GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
               if(frame == null)
               {
            	   return;
               }
               GraphPanel panel = frame.getGraphPanel();
               panel.changeZoom(-1);
            }
         }));

     	viewMenu.add(pFactory.createMenuItem("view.zoom_in", new ActionListener()
     	{
            public void actionPerformed(ActionEvent pEvent)
            {
               GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
               if(frame == null)
               {
            	   return;
               }
               GraphPanel panel = frame.getGraphPanel();
               panel.changeZoom(1);
            }
         }));
      
     	viewMenu.add(pFactory.createMenuItem("view.grow_drawing_area", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
     		{
     			GraphFrame frame = (GraphFrame) aDesktop.getSelectedFrame();
     			if(frame == null)
				{
					return;
				}
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
     			GraphFrame frame = (GraphFrame) aDesktop.getSelectedFrame();
     			if(frame == null)
				{
					return;
				}
                Graph g = frame.getGraph();
                g.setMinBounds(null); 
                frame.getGraphPanel().revalidate();
                frame.repaint();
     		}
     	}));

     	final JCheckBoxMenuItem hideGridItem  = (JCheckBoxMenuItem) pFactory.createCheckBoxMenuItem("view.hide_grid", new ActionListener()
     	{
            public void actionPerformed(ActionEvent pEvent)
            {
               GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
               if(frame == null)
               {
            	   return;
               }
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
     			GraphFrame frame = (GraphFrame) aDesktop.getSelectedFrame();
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
	
	private void createWindowMenu(MenuFactory pFactory)
	{
		JMenuBar menuBar = getJMenuBar();
		JMenu windowMenu = pFactory.createMenu("window");
		menuBar.add(windowMenu);

		windowMenu.add(pFactory.createMenuItem("window.next", new ActionListener()
		{
			public void actionPerformed(ActionEvent pEvent)
			{
				JInternalFrame[] frames = aDesktop.getAllFrames();
				for (int i = 0; i < frames.length; i++)
				{
					if(frames[i] == aDesktop.getSelectedFrame())
					{
						i++; 
						if(i == frames.length)
						{
							i = 0;
						}
						try
						{
							frames[i].toFront();
							frames[i].setSelected(true); 
						}
						catch (PropertyVetoException exception)
						{}
						return;
					}
				}
			}
		}));

	      windowMenu.add(pFactory.createMenuItem("window.previous", new ActionListener()
	      {
	    	  public void actionPerformed(ActionEvent pEvent)
	    	  {
	    		  JInternalFrame[] frames = aDesktop.getAllFrames();
	    		  for(int i = 0; i < frames.length; i++)
	    		  {
	    			  if(frames[i] == aDesktop.getSelectedFrame())
	                  {
	                     if (i == 0)
	                     {
							i = frames.length;
	                     }	
	                     i--; 
	                     try
	                     {
	                        frames[i].toFront();
	                        frames[i].setSelected(true); 
	                     }
	                     catch (PropertyVetoException exception)
	                     {}
	                     return;
	                  }
	    		  }
	    	  }
	      }));

	      windowMenu.add(pFactory.createMenuItem("window.maximize", new ActionListener()
	      {
	    	  public void actionPerformed(ActionEvent pEvent)
	    	  {
	    		  GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
	    		  if(frame == null)
	    		  {
	    			  return;
	    		  }
	              try
	              {
	            	  frame.setMaximum(true);
	              }
	              catch(PropertyVetoException exception)
	              {}
	    	  }
	      }));

	      windowMenu.add(pFactory.createMenuItem("window.restore", new ActionListener()
	      {
	    	  public void actionPerformed(ActionEvent pEvent)
	    	  {
	    		  GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
	    		  if(frame == null)
	    		  {
	    			  return;
	    		  }
	    		  try
	              {
	    			  frame.setMaximum(false);
	              }
	    		  catch (PropertyVetoException exception) 
	    		  {}
	    	  }
	      }));

	      windowMenu.add(pFactory.createMenuItem("window.close", new ActionListener()
	      {
	    	  public void actionPerformed(ActionEvent pEvent)
	          {
	    		  GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
	              if(frame == null)
	              {
	            	  return;
	              }
	              try
	              {
	            	  frame.setClosed(true);
	              }
	              catch(PropertyVetoException exception)
	              {}
	          }
	      }));
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
					JTextArea text = new JTextArea(10, 40);
					String line;
					while ((line = reader.readLine()) != null)
					{
						text.append(line);
						text.append("\n");
					}   
					text.setCaretPosition(0);
					text.setEditable(false);
					JOptionPane.showInternalMessageDialog(aDesktop, new JScrollPane(text), 
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
                  GraphFrame frame = new GraphFrame((Graph) pGraphClass.newInstance());
                  addInternalFrame(frame);
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
	   setTitle();
   	}
   
   /*
    * Opens a file with the given name, or switches to the frame if it is already open.
    * @param pName the file name
    */
	private void open(String pName)
	{	
		JInternalFrame[] frames = aDesktop.getAllFrames();
		for(int i = 0; i < frames.length; i++)
		{
			if(frames[i] instanceof GraphFrame)
			{
				GraphFrame frame = (GraphFrame)frames[i];
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
			Graph graph = read(new FileInputStream(pName));
			GraphFrame frame = new GraphFrame(graph);
			addInternalFrame(frame);
			frame.setFile(new File(pName).getAbsoluteFile());    
			addRecentFile(new File(pName).getPath());
			setTitle();
		}
		catch(IOException exception)
		{
			JOptionPane.showInternalMessageDialog(aDesktop, exception.getMessage(), 
    			  aEditorResources.getString("file.open.text"), JOptionPane.ERROR_MESSAGE);
		}      
	}   

   	/*
     * Creates an internal frame on the desktop.
     * @param c the component to display in the internal frame
     * @param t the title of the internal frame.
    */
   private void addInternalFrame(final JInternalFrame pInternalFrame)
   {  
	   pInternalFrame.setResizable(true);
	   pInternalFrame.setClosable(true);
	   pInternalFrame.setMaximizable(true);
	   pInternalFrame.setIconifiable(true);
	   int frameCount = aDesktop.getAllFrames().length;      
	   aDesktop.add(pInternalFrame);
	   // position frame
	   int emptySpace = FRAME_GAP * Math.max(ESTIMATED_FRAMES, frameCount);
	   int width = Math.max(aDesktop.getWidth() / 2, aDesktop.getWidth() - emptySpace);            
	   int height = Math.max(aDesktop.getHeight() / 2, aDesktop.getHeight() - emptySpace);

	   pInternalFrame.reshape(frameCount * FRAME_GAP, frameCount * FRAME_GAP, width, height);
	   pInternalFrame.show();

	   pInternalFrame.addInternalFrameListener(new InternalFrameAdapter()
       {
		   public void internalFrameActivated(InternalFrameEvent pEvent)
		   {
               setTitle();
		   }
            
		   public void internalFrameDeactivated(InternalFrameEvent pEvent)
		   {
               setTitle();
		   }
       });

	   // select the frame--might be vetoed
	   try
	   {  
		   pInternalFrame.setSelected(true);
	   }
	   catch(PropertyVetoException e)
	   {}
   	}

   	private void setTitle()
   	{
   		String appName = aAppResources.getString("app.name");
   		if(aDesktop.getSelectedFrame() == null || !(aDesktop.getSelectedFrame() instanceof GraphFrame ))
   		{
   			setTitle(appName);
   		}
   		else
   		{
   			GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
   			File file = frame.getFileName();
   			if( file == null )
   			{
   				setTitle(appName);
   			}
   			else
   			{
   				setTitle(appName + " - " + file.getAbsolutePath());
   			}
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
   			item.setMnemonic((char)'0'+i);
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
   	 * Copies the current image to the clipboard.
   	 */
   	public void copyToClipboard()
   	{
   		GraphFrame frame = (GraphFrame) aDesktop.getSelectedFrame();
   		if( frame == null )
   		{
   			return;
   		}
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
   		JOptionPane.showInternalMessageDialog(aDesktop, aEditorResources.getString("dialog.to_clipboard.message"), 
   				aEditorResources.getString("dialog.to_clipboard.title"), JOptionPane.INFORMATION_MESSAGE);
   	}

   	/**
   	 * Save a file. Called by reflection. 
   	 */
   	public void save()
   	{
   		GraphFrame frame = (GraphFrame) aDesktop.getSelectedFrame();
   		if(frame == null)
   		{
   			return;
   		}
   		File file = frame.getFileName(); 
   		if(file == null) 
   		{	
   			saveAs(); 
   			return; 
   		}
   		try
   		{
   			saveFile(frame.getGraph(), new FileOutputStream(file));
   			frame.getGraphPanel().setModified(false);
   		}        
   		catch(Exception exception)
   		{
   			JOptionPane.showInternalMessageDialog(aDesktop, exception);
   		}        
   	}
   
   	/**
     * Saves the current graph as a new file.
   	 */
   	public void saveAs()
   	{
   		GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
   		if(frame == null) 
   		{
   			return;
   		}
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
   					saveFile(graph, out);
   				}
   				finally
   				{
   					out.close();
   				}
   				addRecentFile(result.getAbsolutePath());
   				frame.setFile(result);
   				setTitle();
   				frame.getGraphPanel().setModified(false);
   			}
   		}
   		catch(IOException exception)
   		{
   			JOptionPane.showInternalMessageDialog(aDesktop, exception);
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
   		GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
   		if(frame == null) 
   		{
   			return;
   		}
   		File file = chooseFileToExportTo();
   		if( file == null )
   		{
   			return;
   		}
   		try( OutputStream out = new FileOutputStream(file))
   		{
   			String format = "png";
   			String fileName = file.getPath();
   			if(fileName != null)
   			{
   				format = fileName.substring(fileName.lastIndexOf(".") + 1);
			}
   			if(!ImageIO.getImageWritersByFormatName(format).hasNext())
   			{
   				MessageFormat formatter = new MessageFormat(aEditorResources.getString("error.unsupported_image"));
   				JOptionPane.showInternalMessageDialog(aDesktop, formatter.format(new Object[] { format }));
   				return;
   			}
   			ImageIO.write(getImage(frame.getGraph()), format, out);
   		}
   		catch(IOException exception)
   		{
   			JOptionPane.showInternalMessageDialog(aDesktop, exception);
   		}      
   	}

   	/*
   	 * Can return null if no file is selected.
   	 */
   	private File chooseFileToExportTo()
   	{
   		GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
   		assert frame != null;
   		File file = null;
	   	JFileChooser fileChooser = new JFileChooser();
	   	FileFilter imageFilter = new FileFilter()
		{
			@Override
			public String getDescription()
			{
				return aEditorResources.getString("files.image.name");
			}
			
			@Override
			public boolean accept(File pFile)
			{
				return !pFile.isDirectory() && (pFile.getName().endsWith(".png") ||
						pFile.getName().endsWith(".jpg") || pFile.getName().endsWith(".jpeg"));
			}
		};
	   	fileChooser.setFileFilter(imageFilter);
	   	
		fileChooser.setCurrentDirectory(new File("."));
		if(frame.getFileName() != null)
		{
			File f = new File(replaceExtension(frame.getFileName().getAbsolutePath(), aAppResources.getString("files.extension"), ""));                  
			fileChooser.setSelectedFile(f);
		}
		else    			
		{
			fileChooser.setSelectedFile(new File(""));
		}
		int response = fileChooser.showSaveDialog(this);
		if(response == JFileChooser.APPROVE_OPTION)
		{
			File f = fileChooser.getSelectedFile();	
			if( !imageFilter.accept(f))
			{
				f = new File(f.getPath() + ".png");
			}

			if(!f.exists()) 
			{
				file = f;
			}
			else
			{
				ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
				int result = JOptionPane.showConfirmDialog(this, editorResources.getString("dialog.overwrite"), null, JOptionPane.YES_NO_OPTION);
				if(result == JOptionPane.YES_OPTION) 
				{
					file = f;
				}	     
			}
		}
		return file;
   	}
   
   /**
    * Reads a graph file.
    *  @param pIn the input stream to read
    *  @return the graph that is read in
    *  @throws IOException if the graph cannot be read.
    */
   	public static Graph read(InputStream pIn) throws IOException
   	{
   		XMLDecoder reader = new XMLDecoder(pIn);
   		Graph graph = (Graph) reader.readObject();
   		pIn.close();
   		return graph;
   	}

   	/**
     * Saves the current graph in a file. We use long-term
     * bean persistence to save the program data. 
     * @param out the stream for saving
     */
   	private static void saveFile(Graph graph, OutputStream out)
   {
      XMLEncoder encoder = new XMLEncoder(out);
         
      encoder.setExceptionListener(new 
         ExceptionListener() 
         {
            public void exceptionThrown(Exception ex) 
            {
               ex.printStackTrace();
            }
         });
      /*
      The following does not work due to bug #4741757
        
      encoder.setPersistenceDelegate(
         Point2D.Double.class,
         new DefaultPersistenceDelegate(
            new String[]{ "x", "y" }) );
      */
      encoder.setPersistenceDelegate(Point2D.Double.class, new
            DefaultPersistenceDelegate()
            {
               protected void initialize(Class type, 
                  Object oldInstance, Object newInstance, 
                  Encoder out) 
               {
                  super.initialize(type, oldInstance, 
                     newInstance, out);
                  Point2D p = (Point2D)oldInstance;
                  out.writeStatement(
                        new Statement(oldInstance,
                           "setLocation", new Object[]{ new Double(p.getX()), new Double(p.getY()) }) );
               }
            });
      
      encoder.setPersistenceDelegate(BentStyle.class,
         staticFieldDelegate);
      encoder.setPersistenceDelegate(LineStyle.class,
         staticFieldDelegate);
      encoder.setPersistenceDelegate(ArrowHead.class,
         staticFieldDelegate);
      
      Graph.setPersistenceDelegate(encoder);
      AbstractNode.setPersistenceDelegate(encoder);
      
      encoder.writeObject(graph);
      encoder.close();
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
   		JOptionPane.showInternalMessageDialog(aDesktop, formatter.format(new Object[] { 
               aAppResources.getString("app.name"),
               aVersionResources.getString("version.number"),
               aVersionResources.getString("version.date"),
               aAppResources.getString("app.copyright"),
               aEditorResources.getString("dialog.about.license")}),
               new MessageFormat(aEditorResources.getString("dialog.about.title")).format(new Object[] { 
                       aAppResources.getString("app.name")}),
               JOptionPane.INFORMATION_MESSAGE,
               new ImageIcon(getClass().getResource(aAppResources.getString("app.icon"))));  
   	}

   	/**
     * Exits the program if no graphs have been modified
     * or if the user agrees to abandon modified graphs.
   	 */
   	public void exit()
   	{
   		int modcount = 0;
   		JInternalFrame[] frames = aDesktop.getAllFrames();
   		for(int i = 0; i < frames.length; i++)
   		{
   			if(frames[i] instanceof GraphFrame)
   			{
   				GraphFrame frame = (GraphFrame)frames[i];
   				if(frame.getGraphPanel().isModified()) 
   				{
   					modcount++;
   				}
   			}	
   		}
   		if(modcount > 0)
   		{
   			// ask user if it is ok to close
   			int result = JOptionPane.showInternalConfirmDialog(aDesktop, MessageFormat.format(aEditorResources.getString("dialog.exit.ok"),
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

   	private static PersistenceDelegate staticFieldDelegate = new DefaultPersistenceDelegate()
    {
            protected Expression instantiate(Object oldInstance, Encoder out)
            {
               try
               {
                  Class cl = oldInstance.getClass();
                  Field[] fields = cl.getFields();
                  for (int i = 0; i < fields.length; i++)
                  {
                     if (Modifier.isStatic(
                            fields[i].getModifiers()) &&
                        fields[i].get(null) == oldInstance)
                     {
                        return new Expression(fields[i], 
                           "get",
                           new Object[] { null });
                     }
                  }
               }
               catch (IllegalAccessException ex) 
               {
                  ex.printStackTrace();
               }
               return null;
            }
            
            protected boolean mutatesTo(Object oldInstance, Object newInstance)
            {
               return oldInstance == newInstance;
            }
         };

   
   // workaround for bug #4646747 in J2SE SDK 1.4.0
   private static java.util.HashMap beanInfos;
   static
   {
      beanInfos = new java.util.HashMap();
      Class[] cls = new Class[]
         {
            Point2D.Double.class,
            BentStyle.class,
            ArrowHead.class,
            LineStyle.class,
            Graph.class,
            AbstractNode.class,
         };
      for (int i = 0; i < cls.length; i++)
      {
         try
         {
            beanInfos.put(cls[i], 
               java.beans.Introspector.getBeanInfo(cls[i]));
         }         
         catch (java.beans.IntrospectionException ex)
         {
         }
      }
   }
}
