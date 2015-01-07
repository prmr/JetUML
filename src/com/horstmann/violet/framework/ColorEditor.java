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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.Icon;
import javax.swing.JComboBox;

/**
   A property editor for the MultiLineString type.
*/
public class ColorEditor extends PropertyEditorSupport
{
   public ColorEditor()
   {
      combo = new JComboBox(colors);
      combo.addItemListener(new 
            ItemListener()
            {
               public void itemStateChanged(ItemEvent event)
               {
                  Color value = ((ColorIcon) combo.getSelectedItem()).getColor();
                  setValue(value);
               }
            });
   }
   public boolean supportsCustomEditor()
   {
      return true;
   }

   public Component getCustomEditor()
   {
      final ColorIcon value = new ColorIcon((Color) getValue());
      int i = Arrays.binarySearch(colors, value, new ColorComparator());
      if (i < 0) i = -i - 1;
      combo.setSelectedIndex(i);
      return combo;
   }
   
   static class ColorIcon implements Icon
   {
      public ColorIcon(Color color) { this.color = color; }
      public Color getColor() { return color; }
      public int getIconWidth() { return WIDTH; }
      public int getIconHeight() { return HEIGHT; }
      public void paintIcon(Component c, Graphics g, int x, int y)
      {
         Rectangle r = new Rectangle(x, y, WIDTH - 1, HEIGHT - 1);
         Graphics2D g2 = (Graphics2D) g;
         Color oldColor = g2.getColor();         
         g2.setColor(color);
         g2.fill(r);
         g2.setColor(Color.BLACK);
         g2.draw(r);
         g2.setColor(oldColor);
      }
      private Color color;
      private static final int WIDTH = 40;
      private static final int HEIGHT = 15;
   }
   
   private JComboBox combo;
   
   private static int[] colorValues = 
   {
         // standard web colors
         0xFFFFFF,
         0xFFFF00,
         0xE6E6FA,
         0xADFF2F,
         0x7FFF00,
         0x00FF7F,
         0xFFFAFA,
         0xFFD700,
         0xFFC0CB,
         0x9ACD32,
         0x7CFC00,
         0x00FA9A,
         0xF8F8FF,
         0xFFA500,
         0xFFB6C1,
         0x808000,
         0x32CD32,
         0x00FF00,
         0xF5F5F5,
         0xFF8C00,
         0xFF69B4,
         0x8FBC8F,
         0x228B22,
         0x008000,
         0xFFF5EE,
         0xF4A460,
         0xD8BFD8,
         0x90EE90,
         0x6B8E23,
         0x006400,
         0xFFFAF0,
         0xE9967A,
         0xDDA0DD,
         0x98FB98,
         0x556B2F,
         0x008B8B,
         0xFDF5E6,
         0xFFA07A,
         0xEE82EE,
         0xF0FFF0,
         0x2E8B57,
         0x008080,
         0xFAF0E6,
         0xD2691E,
         0xFF00FF,
         0xF5FFFA,
         0x3CB371,
         0x00CED1,
         0xF5F5DC,
         0xFF4500,
         0xFF1493,
         0xF0FFFF,
         0x66CDAA,
         0x00FFFF,
         0xFFF8DC,
         0xB22222,
         0xC71585,
         0xE0FFFF,
         0x20B2AA,
         0x00BFFF,
         0xFFEFD5,
         0x8B0000,
         0xCD5C5C,
         0xAFEEEE,
         0x48D1CC,
         0x6495ED,
         0xFAEBD7,
         0x800000,
         0xDB7093,
         0xB0C4DE,
         0x40E0D0,
         0x1E90FF,
         0xFFEBCD,
         0xA52A2A,
         0xF08080,
         0xFF00FF,
         0x7FFFD4,
         0x4169E1,
         0xFFE4C4,
         0xA0522D,
         0xFA8072,
         0xDA70D6,
         0xB0E0E6,
         0x6A5ACD,
         0xFFDEAD,
         0x8B4513,
         0xFF7F50,
         0x9370DB,
         0xADD8E6,
         0x7B68EE,
         0xFFE4B5,
         0xB8860B,
         0xFF6347,
         0xBA55D3,
         0x87CEEB,
         0x0000FF,
         0xF5DEB3,
         0xDAA520,
         0xDC143C,
         0x9932CC,
         0x87CEFA,
         0x0000CD,
         0xFFDAB9,
         0xCD853F,
         0x8A2BE2,
         0x4682B4,
         0x483D8B,
         0xEEE8AA,
         0xBDB76B,
         0xFFE4E1,
         0x9400D3,
         0x5F9EA0,
         0x00008B,
         0xFAFAD2,
         0xBC8F8F,
         0xFFF0F5,
         0x8B008B,
         0x778899,
         0x000080,
         0xFFFACD,
         0xDEB887,
         0xDCDCDC,
         0x800080,
         0x708090,
         0x191970,
         0xFFFFE0,
         0xD2B48C,
         0xD3D3D3,
         0xA9A9A9,
         0x696969,
         0x4B0082,
         0xFFFFF0,
         0xF0E68C,
         0xC0C0C0,
         0x808080,
         0x2F4F4F,
         0x000000,
         // note color
         0xE6E699
   };
   
   static ColorIcon[] colors;
   
   static
   {
      colors = new ColorIcon[colorValues.length];
      for (int i = 0; i < colorValues.length; i++)
         colors[i] = new ColorIcon(new Color(colorValues[i]));
      
      Arrays.sort(colors, new ColorComparator());
   }
   
   static class ColorComparator implements Comparator
   {
      public int compare(Object obj1, Object obj2)
      {
         Color c1 = ((ColorIcon) obj1).getColor();
         Color c2 = ((ColorIcon) obj2).getColor();
         Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), hsb);
         float hue1 = hsb[0];
         float sat1 = hsb[1];
         float bri1 = hsb[2];
         Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), hsb);
         float hue2 = hsb[0];
         float sat2 = hsb[1];
         float bri2 = hsb[2];
         if (hue1 < hue2) return 1;
         if (hue1 > hue2) return -1;
         if (sat1 < sat2) return 1;
         if (sat1 > sat2) return -1;
         if (bri1 < bri2) return 1;
         if (bri1 > bri2) return -1;
         return 0;
      }
      private static float[] hsb = new float[3];
   }
}
