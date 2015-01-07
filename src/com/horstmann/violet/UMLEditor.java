/*
 Violet - A program for editing UML diagrams.

 Copyright (C) 2002 Cay S. Horstmann (http://horstmann.com)

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.horstmann.violet;

import java.io.IOException;
import java.net.URL;

import javax.swing.JApplet;

import com.horstmann.violet.framework.EditorFrame;
import com.horstmann.violet.framework.VersionChecker;


/**
 * A program for editing UML diagrams.
 */
public class UMLEditor extends JApplet
{
   public static void main(String[] args)
   {
      VersionChecker checker = new VersionChecker();
      checker.check(JAVA_VERSION);
      try
      {
         System.setProperty("apple.laf.useScreenMenuBar", "true");
      }
      catch (SecurityException ex)
      {
         // well, we tried...
      }

      EditorFrame frame = makeFrame();
      frame.setVisible(true);
      frame.readArgs(args);
   }
   
   public void init()
   {
      EditorFrame frame = makeFrame();
      setContentPane(frame.getContentPane());
      setJMenuBar(frame.getJMenuBar());
      
      String url = getParameter("diagram");
      if (url != null)
         try
         {
            frame.openURL(new URL(getDocumentBase(), url));
         }
         catch (IOException ex)
         {
            ex.printStackTrace();
         }
   }

   public static EditorFrame makeFrame()
   {
      EditorFrame frame = new EditorFrame(UMLEditor.class);
      frame.addGraphType("class_diagram", ClassDiagramGraph.class);
      frame.addGraphType("sequence_diagram", SequenceDiagramGraph.class);
      frame.addGraphType("state_diagram", StateDiagramGraph.class);
      frame.addGraphType("object_diagram", ObjectDiagramGraph.class);
      frame.addGraphType("usecase_diagram", UseCaseDiagramGraph.class);
      return frame;
   }
   
   private static final String JAVA_VERSION = "1.4";
}