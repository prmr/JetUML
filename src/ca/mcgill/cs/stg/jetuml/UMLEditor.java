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

package ca.mcgill.cs.stg.jetuml;

import java.util.ResourceBundle;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import ca.mcgill.cs.stg.jetuml.diagrams.ClassDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.ObjectDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.SequenceDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.StateDiagramGraph;
import ca.mcgill.cs.stg.jetuml.diagrams.UseCaseDiagramGraph;
import ca.mcgill.cs.stg.jetuml.framework.EditorFrame;

/**
 * A program for editing UML diagrams.
 */
public final class UMLEditor
{
	private static final int JAVA_MAJOR_VERSION = 7;
	private static final int JAVA_MINOR_VERSION = 0;
	
	private UMLEditor() {}
	
	/**
	 * @param pArgs Each argument is a file to open upon launch.
	 * Can be empty.
	 */
	public static void main(String[] pArgs)
	{
		checkVersion();
		try
		{
			System.setProperty("apple.laf.useScreenMenuBar", "true");
		}
		catch (SecurityException ex)
		{
			// well, we tried...
		}
		final String[] arguments = pArgs;
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				setLookAndFeel();
				EditorFrame frame = new EditorFrame(UMLEditor.class);
				frame.addGraphType("class_diagram", ClassDiagramGraph.class);
				frame.addGraphType("sequence_diagram", SequenceDiagramGraph.class);
				frame.addGraphType("state_diagram", StateDiagramGraph.class);
			    frame.addGraphType("object_diagram", ObjectDiagramGraph.class);
			    frame.addGraphType("usecase_diagram", UseCaseDiagramGraph.class);
				frame.setVisible(true);
				frame.readArgs(arguments);
				frame.addWelcomeTab();
				frame.setIcon();
			}
		});
   }
	
	private static void setLookAndFeel()
	{
		try
		{
			for(LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) 
			{
				if("Nimbus".equals(info.getName())) 
				{
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} 
		catch(UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) 
		{
		    // Nothing: We revert to the default LAF
		}
	}
	
	/**
	 *  Checks if the current VM has at least the given
	 *  version, and exits the program with an error dialog if not.
	 */
	private static void checkVersion()
	{
		String version = obtainJavaVersion();
		if( version == null || !isOKJVMVersion(version))
		{
			ResourceBundle resources = ResourceBundle.getBundle("ca.mcgill.cs.stg.jetuml.framework.EditorStrings");
			String minor = "";
			int minorVersion = JAVA_MINOR_VERSION;
			if( minorVersion > 0 )
			{
				minor = "." + JAVA_MINOR_VERSION;
			}
			JOptionPane.showMessageDialog(null, resources.getString("error.version") + 
					"1." + JAVA_MAJOR_VERSION + minor);
			System.exit(1);
		}
	}
	
	static String obtainJavaVersion()
	{
		String version = System.getProperty("java.version");
		if( version == null )
		{
			version = System.getProperty("java.runtime.version");
		}
		return version;
	}
	
	/**
	 * @return True is the JVM version is higher than the 
	 * versions specified as constants.
	 */
	static boolean isOKJVMVersion(String pVersion)
	{
		assert pVersion != null;
		String[] components = pVersion.split("\\.|_");
		boolean lReturn = true;
		
		try
		{
			int systemMajor = Integer.parseInt(String.valueOf(components[1]));
			int systemMinor = Integer.parseInt(String.valueOf(components[2]));
			if( systemMajor > JAVA_MAJOR_VERSION )
			{
				lReturn = true;
			}
			else if( systemMajor < JAVA_MAJOR_VERSION )
			{
				lReturn = false;
			}
			else // major Equals
			{
				if( systemMinor > JAVA_MINOR_VERSION )
				{
					lReturn = true;
				}
				else if( systemMinor < JAVA_MINOR_VERSION )
				{
					lReturn = false;
				}
				else // minor equals
				{
					lReturn = true;
				}
			}
        }
		catch( NumberFormatException e)
		{
			lReturn = false;
		}
		return lReturn;
    }
}