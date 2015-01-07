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

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;

/**
   This class is a superclass for enumerated types that
   can be serialized. Subclass like this:
   <pre>
   public class MyEnumeration extends SerializableEnumeration
   {
      private MyEnumeration() {}
      public static final MyEnumeration FOO = new MyEnumeration();
      public static final MyEnumeration BAR = new MyEnumeration();
      . . .
   }
   </pre>
   This defines instances <code>MyEnumeration.FOO</code> and
   <code>MyEnumeration.BAR</code> that are guaranteed to
   be preserved through serialization and deserialization.
   Conveniently, the toString method yields the name (such
   as <code>&quot;FOO&quot;<code>).
*/
public class SerializableEnumeration implements Serializable
{
   protected Object writeReplace() throws ObjectStreamException
   {
      if (name == null) toString();
      if (name == null)
         throw new ObjectStreamException("No matching field") {};
      return this;
   }

   public String toString()
   {
      if (name != null) return getClass().getName() + "." + name;
      try
      {
         Field[] fields = getClass().getFields();
         for (int i = 0; i < fields.length; i++)
         {
            if (fields[i].get(this) == this)
            {
               name = fields[i].getName();
               return toString();
            }
         }
      }
      catch (IllegalAccessException ex) {}
      return super.toString();
   }

   protected Object readResolve() throws ObjectStreamException
   {
      try
      {
         return getClass().getField(name).get(null);
      }
      catch (IllegalAccessException ex) {}
      catch (NoSuchFieldException ex) {}
      throw new ObjectStreamException("No matching field") {};
   }

   private String name;
}
