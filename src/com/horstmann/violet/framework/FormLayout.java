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

import java.awt.*;

/**
   A layout manager that lays out components along a central axis
*/
class FormLayout implements LayoutManager
{  
   public Dimension preferredLayoutSize(Container parent)
   {  
      Component[] components = parent.getComponents();
      left = 0;
      right = 0;
      height = 0;
      for (int i = 0; i < components.length; i += 2)
      {
         Component cleft = components[i];
         Component cright = components[i + 1];

         Dimension dleft = cleft.getPreferredSize();
         Dimension dright = cright.getPreferredSize();
         left = Math.max(left, dleft.width);
         right = Math.max(right, dright.width);
         height = height + Math.max(dleft.height,
            dright.height);
      }      
      return new Dimension(left + GAP + right, height);
   }
      
   public Dimension minimumLayoutSize(Container parent)
   {  
      return preferredLayoutSize(parent);
   }

   public void layoutContainer(Container parent)
   {  
      preferredLayoutSize(parent); // sets left, right

      Component[] components = parent.getComponents();

      Insets insets = parent.getInsets();
      int xcenter = insets.left + left;
      int y = insets.top;

      for (int i = 0; i < components.length; i += 2)
      {
         Component cleft = components[i];
         Component cright = components[i + 1];

         Dimension dleft = cleft.getPreferredSize();
         Dimension dright = cright.getPreferredSize();

         int height = Math.max(dleft.height,
            dright.height);

         cleft.setBounds(xcenter - dleft.width, y + (height -
            dleft.height) / 2, dleft.width, dleft.height);

         cright.setBounds(xcenter + GAP, y + (height 
            - dright.height) / 2, dright.width, dright.height);
         y += height;
      }
   }

   public void addLayoutComponent(String name,
      Component comp)
   {}

   public void removeLayoutComponent(Component comp)
   {}

   private int left;
   private int right;
   private int height;
   private static final int GAP = 6;
}
