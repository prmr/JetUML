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

import com.horstmann.violet.framework.Edge;
import com.horstmann.violet.framework.Graph;
import com.horstmann.violet.framework.MultiLineString;
import com.horstmann.violet.framework.Node;

/**
   An UML-style object diagram that shows object references.
*/
public class ObjectDiagramGraph extends Graph
{
   public Node[] getNodePrototypes()
   {
      return NODE_PROTOTYPES;
   }

   public Edge[] getEdgePrototypes()
   {
      return EDGE_PROTOTYPES;
   }

   private static final Node[] NODE_PROTOTYPES = new Node[3];

   private static final Edge[] EDGE_PROTOTYPES = new Edge[3];

   static
   {
      NODE_PROTOTYPES[0] = new ObjectNode();
      FieldNode f = new FieldNode();
      MultiLineString fn = new MultiLineString();
      fn.setText("name");
      f.setName(fn);
      MultiLineString fv = new MultiLineString();
      fv.setText("value");
      f.setValue(fv);
      NODE_PROTOTYPES[1] = f;
      NODE_PROTOTYPES[2] = new NoteNode();
      EDGE_PROTOTYPES[0] = new ObjectReferenceEdge();
      ClassRelationshipEdge association = new ClassRelationshipEdge();
      association.setBentStyle(BentStyle.STRAIGHT);
      EDGE_PROTOTYPES[1] = association;
      EDGE_PROTOTYPES[2] = new NoteEdge();
   }
}





