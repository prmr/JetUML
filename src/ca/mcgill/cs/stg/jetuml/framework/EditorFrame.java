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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

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

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.stg.jetuml.graph.AbstractNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * This desktop frame contains panes that show graphs.
 */
@SuppressWarnings("serial")
public class EditorFrame extends JFrame
{
	private static final int FRAME_GAP = 20;
	private static final int ESTIMATED_FRAMES = 5;
	private static final int DEFAULT_MAX_RECENT_FILES = 5;
	private static final double GROW_SCALE_FACTOR = Math.sqrt(2);
	
	private ResourceFactory aAppFactory;
	private ResourceBundle aAppResources;
	private ResourceBundle aVersionResources;
	private ResourceBundle aEditorResources;
	private JDesktopPane aDesktop;
	private FileService aFileService;
	private PreferencesService aPreferences;
	private JMenu aNewMenu;
	private String aDefaultExtension;
	private ArrayList<String> aRecentFiles;
	private JMenu aRecentFilesMenu;
	private int aMaxRecentFiles = DEFAULT_MAX_RECENT_FILES;

	private ExtensionFilter aVioletFilter;
	private ExtensionFilter aStateFilter;
	private ExtensionFilter aSequenceFilter;
	private ExtensionFilter aObjectFilter;
	private ExtensionFilter aUsecaseFilter;
	private ExtensionFilter aClassFilter;
	private ExtensionFilter[] aOptionalFilters;

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
		aAppFactory = new ResourceFactory(aAppResources);
		aVersionResources = ResourceBundle.getBundle(appClassName + "Version");
		aEditorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");      
		ResourceFactory factory = new ResourceFactory(aEditorResources);
		aPreferences = PreferencesService.getInstance();
      
		aRecentFiles = new ArrayList<>(); 
		File lastDir = new File(".");
		String recent = aPreferences.get("recent", "").trim();
		if(recent.length() > 0)
		{
			aRecentFiles.addAll(Arrays.asList(recent.split("[|]")));         
			lastDir = new File((String) aRecentFiles.get(0)).getParentFile();
		}
		aFileService = FileService.getInstance();      
		aFileService.setInitialDirectory(lastDir);
      
		setTitle(aAppResources.getString("app.name"));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
  
		int screenWidth = (int)screenSize.getWidth();
		int screenHeight = (int)screenSize.getHeight();

		setBounds(screenWidth / 16, screenHeight / 16, screenWidth * 7 / 8, screenHeight * 7 / 8);

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

		aDefaultExtension = aAppResources.getString("files.extension");

		aVioletFilter = new ExtensionFilter(aAppResources.getString("files.name"), new String[] { aDefaultExtension });
		aStateFilter = new ExtensionFilter(aAppResources.getString("state.name"), aAppResources.getString("state.extension"));
		aObjectFilter = new ExtensionFilter(aAppResources.getString("object.name"), aAppResources.getString("object.extension"));
		aClassFilter = new ExtensionFilter(aAppResources.getString("class.name"), aAppResources.getString("class.extension"));
		aUsecaseFilter = new ExtensionFilter(aAppResources.getString("usecase.name"), aAppResources.getString("usecase.extension"));
		aSequenceFilter = new ExtensionFilter(aAppResources.getString("sequence.name"), aAppResources.getString("sequence.extension"));
      
     	aOptionalFilters = new ExtensionFilter[]{aClassFilter, aObjectFilter, aStateFilter, aSequenceFilter, aUsecaseFilter};

     	JMenuBar menuBar = new JMenuBar();
     	setJMenuBar(menuBar);
     	JMenu fileMenu = factory.createMenu("file");
     	menuBar.add(fileMenu);

     	aNewMenu = factory.createMenu("file.new");
     	fileMenu.add(aNewMenu);

     	JMenuItem fileOpenItem = factory.createMenuItem("file.open", this, "openFile"); 
     	fileMenu.add(fileOpenItem);      

     	aRecentFilesMenu = factory.createMenu("file.recent");
     	buildRecentFilesMenu();
     	fileMenu.add(aRecentFilesMenu);
      
     	JMenuItem fileSaveItem = factory.createMenuItem("file.save", this, "save"); 
     	fileMenu.add(fileSaveItem);
     	JMenuItem fileSaveAsItem = factory.createMenuItem("file.save_as", this, "saveAs");
     	fileMenu.add(fileSaveAsItem);

     	JMenuItem fileExportItem = factory.createMenuItem("file.export_image", this, "exportImage"); 
     	fileMenu.add(fileExportItem);

     	JMenuItem fileExitItem = factory.createMenuItem("file.exit", this, "exit");
     	fileMenu.add(fileExitItem);

     	if(aFileService == null)
     	{
     		fileOpenItem.setEnabled(false);
     		fileSaveAsItem.setEnabled(false);
     		fileExportItem.setEnabled(false);
     		fileExitItem.setEnabled(false);
     	}

     	if(aFileService == null) 
     	{
     		aRecentFilesMenu.setEnabled(false);
     		fileSaveItem.setEnabled(false);
     	}
                  
     	JMenu editMenu = factory.createMenu("edit");
     	menuBar.add(editMenu);

     	editMenu.add(factory.createMenuItem("edit.properties", new ActionListener()
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

     	editMenu.add(factory.createMenuItem("edit.delete", new ActionListener()
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

     	editMenu.add(factory.createMenuItem("edit.select_next", new ActionListener()
     	{
            public void actionPerformed(ActionEvent pEvent)
            {
               GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
               if(frame == null)
               {
            	   return;
               }
               GraphPanel panel = frame.getGraphPanel();
               panel.selectNext(1);
            }
     	}));

     	editMenu.add(factory.createMenuItem("edit.select_previous", new ActionListener()
     	{
            public void actionPerformed(ActionEvent pEvent)
            {
            	GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
            	if(frame == null)
            	{
            		return;
            	}
            	GraphPanel panel = frame.getGraphPanel();
            	panel.selectNext(-1);
            }
     	}));

     	JMenu viewMenu = factory.createMenu("view");
     	menuBar.add(viewMenu);

     	viewMenu.add(factory.createMenuItem("view.zoom_out", new ActionListener()
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

     	viewMenu.add(factory.createMenuItem("view.zoom_in", new ActionListener()
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
      
     	viewMenu.add(factory.createMenuItem("view.grow_drawing_area", new ActionListener()
     	{
     		public void actionPerformed(ActionEvent pEvent)
     		{
     			GraphFrame frame = (GraphFrame) aDesktop.getSelectedFrame();
     			if(frame == null)
				{
					return;
				}
     			Graph g = frame.getGraph();
     			Rectangle2D bounds = g.getBounds((Graphics2D) frame.getGraphics());
     			bounds.add(frame.getGraphPanel().getBounds());
     			g.setMinBounds(new Rectangle2D.Double(0, 0, GROW_SCALE_FACTOR * bounds.getWidth(), GROW_SCALE_FACTOR * bounds.getHeight()));
                frame.getGraphPanel().revalidate();
                frame.repaint();
     		}
     	}));
      
     	viewMenu.add(factory.createMenuItem("view.clip_drawing_area", new ActionListener()
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

     	final JCheckBoxMenuItem hideGridItem  = (JCheckBoxMenuItem) factory.createCheckBoxMenuItem("view.hide_grid", new ActionListener()
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
      
      JMenu windowMenu = factory.createMenu("window");
      menuBar.add(windowMenu);

      windowMenu.add(factory.createMenuItem("window.next", new ActionListener()
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

      windowMenu.add(factory.createMenuItem("window.previous", new ActionListener()
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

      windowMenu.add(factory.createMenuItem("window.maximize", new ActionListener()
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

      windowMenu.add(factory.createMenuItem("window.restore", new ActionListener()
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

      windowMenu.add(factory.createMenuItem("window.close", new ActionListener()
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

      JMenu helpMenu = factory.createMenu("help");
      menuBar.add(helpMenu);

      helpMenu.add(factory.createMenuItem("help.about", this, "showAboutDialog"));

      helpMenu.add(factory.createMenuItem("help.license", new ActionListener()
      {
    	  public void actionPerformed(ActionEvent pEvent)
    	  {
    		  try
    		  {
    			  BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("license.txt")));
                  JTextArea text = new JTextArea(10, 50);
                  String line;
                  while ((line = reader.readLine()) != null)
                  {
                	  text.append(line);
                	  text.append("\n");
                  }   
                  text.setCaretPosition(0);
                  text.setEditable(false);
                  JOptionPane.showInternalMessageDialog(aDesktop, new JScrollPane(text), null, JOptionPane.INFORMATION_MESSAGE);
              }
              catch(IOException exception) 
    		  {}
    	  }
      }));
	}
	
	private File getLastDirectory()
	{
		File lastDir = new File(".");
		String recent = aPreferences.get("recent", "").trim();
		if(recent.length() > 0)
		{
			aRecentFiles.addAll(Arrays.asList(recent.split("[|]")));         
			lastDir = new File((String) aRecentFiles.get(0)).getParentFile();
		}
		return lastDir;
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
      for (int i = 0; i < frames.length; i++)
      {
         if (frames[i] instanceof GraphFrame)
         {
            GraphFrame frame = (GraphFrame)frames[i];
            if (frame.getFileName() != null && frame.getFileName().equals(pName)) 
            {
               try
               {
                  frame.toFront();
                  frame.setSelected(true); 
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
         frame.setFileName(pName);              
      }
      catch(IOException exception)
      {
         JOptionPane.showInternalMessageDialog(aDesktop, exception);
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
   			String fileName = frame.getFileName();
   			if( fileName == null )
   			{
   				setTitle(appName);
   			}
   			else
   			{
   				setTitle(appName + " - " + fileName);
   			}
   		}
   }
   
   	/*
   	 * Adds a file name to the "recent files" list and rebuilds the "recent files" menu. 
   	 * @param pNewFile the file name to add
   	 */
   	private void addRecentFile(final String pNewFile)
   	{
   		aRecentFiles.remove(pNewFile);
   		if(pNewFile == null || pNewFile.equals("")) 
   		{
   			return;
   		}
   		aRecentFiles.add(0, pNewFile);
   		buildRecentFilesMenu();
   	}
   
   	/*
   	 * Rebuilds the "recent files" menu.
   	 */
   	private void buildRecentFilesMenu()
   	{
   		aRecentFilesMenu.removeAll();
   		for(int i = 0; i < aRecentFiles.size(); i++)
   		{
         final String file = (String) aRecentFiles.get(i); 
         String name = new File(file).getName();
         if(i < 10)
         {
			name = i + " " + name;
         } 
         else if(i == 10) 
         {
			name = "0 " + name;
         }         
         JMenuItem item = new JMenuItem(name);
         if (i < 10)
         {
			item.setMnemonic((char)('0' + i));
         }
         else if(i == 10) 
         {
			item.setMnemonic('0');
         }
         aRecentFilesMenu.add(item);
         item.addActionListener(new ActionListener()
         {
        	 public void actionPerformed(ActionEvent pEvent)
        	 {
        		 open(file);
        	 }
         });
      }      
   }

   	/**
     * Asks the user to open a graph file.
   	 */
   	public void openFile()
   	{  
   		try
   		{
   			JFileChooser fileChooser = new JFileChooser(getLastDirectory());
   			fileChooser.setFileFilter(aVioletFilter);
   			for(ExtensionFilter filter: aOptionalFilters)
			{
				fileChooser.addChoosableFileFilter(filter);
			}
   			InputStream in = null;
   			if(fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) 
   			{
   				in = new FileInputStream(fileChooser.getSelectedFile());
   			}
   			if(in != null)
   			{      
   				Graph graph = read(in);
   				GraphFrame frame = new GraphFrame(graph);
   				addInternalFrame(frame);
   				frame.setFileName(fileChooser.getSelectedFile().getPath());
   				addRecentFile(fileChooser.getSelectedFile().getPath());
   				setTitle();
   			}               
   		}
   		catch(IOException exception)     
   		{
   			JOptionPane.showInternalMessageDialog(aDesktop, exception);
   		}
   	}

   	/**
   	 * Open a file from an URL--used by applet.
   	 * @param pURL the URL 
   	 * @throws IOException If the graph can't be read.
   	 */
   	public void openURL(URL pURL) throws IOException
   	{
   		InputStream in = pURL.openStream();
   		if(in != null)
   		{      
   			Graph graph = read(in);
   			GraphFrame frame = new GraphFrame(graph);
   			addInternalFrame(frame);
   			try
   			{
   				frame.setMaximum(true);
   			}
   			catch(PropertyVetoException ex) 
   			{}
   		}	               
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
   		String fileName = frame.getFileName(); 
   		if(fileName == null) 
   		{	saveAs(); 
   			return; 
   		}
   		try
   		{
   			saveFile(frame.getGraph(), new FileOutputStream(fileName));
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
   			File result;
   			ExtensionFilter filter;
   			if(graph instanceof UseCaseDiagramGraph)
   			{
   				filter = aUsecaseFilter;
   			}
   			else if(graph instanceof ClassDiagramGraph)
   			{
   				filter = aClassFilter;
   			}	
   			else if(graph instanceof ObjectDiagramGraph)
   			{
   				filter = aObjectFilter;
   			}
   			else if(graph instanceof SequenceDiagramGraph)
   			{
   				filter = aSequenceFilter;
   			}
   			else
   			{
   				filter = aStateFilter;
   			} 
   			result = activateSaveDialog(filter, null);
   			
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
   				frame.setFileName(result.getPath());
   				setTitle();
   				frame.getGraphPanel().setModified(false);
   			}
   		}
   		catch(IOException exception)
   		{
   			JOptionPane.showInternalMessageDialog(aDesktop, exception);
   		}
   	}

   	/*
   	 * Can return null if the operation is cancelled.
   	 */
   	private File activateSaveDialog(ExtensionFilter pFilter, String pRemoveExtension) throws FileNotFoundException
	{
   		assert pFilter.getExtensions().length == 0;
   		
   		GraphFrame frame = (GraphFrame)aDesktop.getSelectedFrame();
   		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(pFilter);
		fileChooser.setCurrentDirectory(new File("."));
		
		if(frame.getFileName() != null)
		{
			File f = new File(editExtension(frame.getFileName(), pRemoveExtension, pFilter.getExtensions()[0]));                  
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
			if(pFilter.getExtensions()[0] != null && f.getName().indexOf(".") < 0)
			{
				f = new File(f.getPath() + pFilter.getExtensions()[0]);
			}

			if(!f.exists()) 
			{
				return f;
			}
        
			ResourceBundle editorResources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
			int result = JOptionPane.showConfirmDialog(this, editorResources.getString("dialog.overwrite"), null, JOptionPane.YES_NO_OPTION);
			if(result == JOptionPane.YES_OPTION) 
			{
				return f;
			}                       
		}
		return null;
	}

   	/**
   	 * Edits the file path so that it ends in the desired extension.
   	 * @param pOriginal the file to use as a starting point
     * @param pToBeRemoved the extension that is to be removed before adding the desired extension. Use
     * null if nothing needs to be removed. 
   	 * @param pDesired the desired extension (e.g. ".png"), or a | separated list of extensions
   	 * @return original if it already has the desired extension, or a new file with the edited file path
   	 */
	public static String editExtension(String pOriginal, String pToBeRemoved, String pDesired)
	{
		if (pOriginal == null) 
		{
			return null;
		}
		int n = pDesired.indexOf('|');
		if(n >= 0) 
		{
			pDesired = pDesired.substring(0, n);
		}
		String path = pOriginal;
		if(!path.toLowerCase().endsWith(pDesired.toLowerCase()))
		{   		
			if(pToBeRemoved != null && path.toLowerCase().endsWith(pToBeRemoved.toLowerCase())) 
			{
				path = path.substring(0, path.length() - pToBeRemoved.length());
			}
			path = path + pDesired;
		}
		return path;      
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

   		try
   		{
   			String imageExtensions = aEditorResources.getString("files.image.extension");
   			ExtensionFilter imageFilter = new ExtensionFilter(aEditorResources.getString("files.image.name"), 
   					aEditorResources.getString("files.image.extension"));
   			File file = activateSaveDialog(imageFilter, aAppResources.getString("files.extension"));
   			
   			if(file != null)
   			{
   				OutputStream out = new FileOutputStream(file);
   				String format;
   				String fileName = file.getPath();
   				if(fileName == null)
   				{
   					int n = imageExtensions.indexOf("|");
   					if(n < 0)
   					{
   						n = imageExtensions.length();
   					}
   					format = imageExtensions.substring(1, n);
   				} 
   				else 
   				{
					format = fileName.substring(fileName.lastIndexOf(".") + 1);
				}
   				if(!ImageIO.getImageWritersByFormatName(format).hasNext())
   				{
   					MessageFormat formatter = new MessageFormat(aEditorResources.getString("error.unsupported_image"));
   					JOptionPane.showInternalMessageDialog(aDesktop, formatter.format(new Object[] { format }));
   					out.close();
   					return;
   				}
         
   				Graph graph = frame.getGraph();
   				try
   				{
   					saveImage(graph, out, format);
   				}
   				finally
   				{
   					out.close();
   				}
   			}
   		}
   		catch(Exception exception)
   		{
   			JOptionPane.showInternalMessageDialog(aDesktop, exception);
   		}      
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

   	/**
     * Exports a current graph to an image file.
     * @param pGraph the graph
     * @param pOut the output stream
     * @param pFormat the image file format
     * @throws IOException if the image cannot be exported.
   	 */
   	public static void saveImage(Graph pGraph, OutputStream pOut, String pFormat) throws IOException
   	{
   		BufferedImage dummy = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
   		// need a dummy image to get a Graphics to measure the size
   		Rectangle2D bounds = pGraph.getBounds((Graphics2D) dummy.getGraphics());
   		BufferedImage image = new BufferedImage((int)bounds.getWidth() + 1, (int)bounds.getHeight() + 1, BufferedImage.TYPE_INT_RGB);
   		Graphics2D g2 = (Graphics2D)image.getGraphics();
   		g2.translate(-bounds.getX(), -bounds.getY());
   		g2.setColor(Color.WHITE);
   		g2.fill(new Rectangle2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth() + 1, bounds.getHeight() + 1));
   		g2.setColor(Color.BLACK);
   		g2.setBackground(Color.WHITE);
   		pGraph.draw(g2, null);
   		ImageIO.write(image, pFormat, pOut);
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
               null, JOptionPane.INFORMATION_MESSAGE,
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
   		savePreferences();
   		System.exit(0);
   	}
   
   	/**
   	 * Saves the user preferences before exiting.
   	 */
   	public void savePreferences()
   	{
   		String recent = "";     
   		for(int i = 0; i < Math.min(aRecentFiles.size(), aMaxRecentFiles); i++)
   		{
   			if(recent.length() > 0) 
   			{
   				recent += "|";
   			}
   			recent += aRecentFiles.get(i);
   		}      
   		aPreferences.put("recent", recent);   
   	}

   private static PersistenceDelegate staticFieldDelegate 
      = new 
         DefaultPersistenceDelegate()
         {
            protected Expression instantiate(Object 
               oldInstance, Encoder out)
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
            
            protected boolean mutatesTo(
               Object oldInstance, Object newInstance)
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
