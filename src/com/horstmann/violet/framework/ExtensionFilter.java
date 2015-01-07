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

package com.horstmann.violet.framework;

import java.io.File;
import java.util.StringTokenizer;
import javax.swing.filechooser.FileFilter;

/**
   A file filter that accepts all files with a given set
   of extensions.
*/
public class ExtensionFilter 
   extends FileFilter
{
   /**
      Constructs an extension file filter.
      @param description the description (e.g. "Woozle files")
      @param extensions the accepted extensions (e.g.
      new String[] { ".woozle", ".wzl" })
   */
   public ExtensionFilter(String description, 
      String[] extensions)
   {
      this.description = description; 
      this.extensions = extensions;
   }

   /**
      Constructs an extension file filter.
      @param description the description (e.g. "Woozle files")
      @param extensions the accepted extensions, separated
      by | (e.g.".woozle|.wzl" })
   */
   public ExtensionFilter(String description, 
      String extensions)
   {
      this.description = description; 
      StringTokenizer tokenizer = new StringTokenizer(
         extensions, "|");
      this.extensions = new String[tokenizer.countTokens()];
      for (int i = 0; i < this.extensions.length; i++)
         this.extensions[i] = tokenizer.nextToken();
   }
   
   public boolean accept(File f)
   {  
      if (f.isDirectory()) return true;
      String fname = f.getName().toLowerCase();
      for (int i = 0; i < extensions.length; i++)
         if (fname.endsWith(extensions[i].toLowerCase())) 
            return true;
      return false;
   }
   
   public String getDescription()
   { 
      return description; 
   }
   
   public String[] getExtensions()
   {
      return extensions;
   }

   private String description;
   private String[] extensions;
}
