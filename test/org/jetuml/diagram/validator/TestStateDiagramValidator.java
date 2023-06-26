/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
 * along with this program.  If not, see http://www.gnu.org/licenses.
 *******************************************************************************/
package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.nodes.FinalStateNode;
import org.jetuml.diagram.nodes.InitialStateNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestStateDiagramValidator
{

  private Diagram aDiagram;

  private StateDiagramValidator aStateDiagramValidator;
  private InitialStateNode aInitialStateNode;
  private FinalStateNode aFinalStateNode;
  private NoteNode aNoteNode;

  @BeforeEach
  public void setUp()
  {
    aDiagram = new Diagram(DiagramType.STATE);
    aNoteNode = new NoteNode();
    aInitialStateNode = new InitialStateNode();
    aFinalStateNode = new FinalStateNode();
    aStateDiagramValidator = new StateDiagramValidator(aDiagram);
  }

  @Test
  public void testValidNodeHierarchyAndValidElementName()
  {
    aDiagram.addRootNode(aInitialStateNode);
    aDiagram.addRootNode(aFinalStateNode);
    aDiagram.addRootNode(aNoteNode);
    assertTrue(aStateDiagramValidator.isValid());
  }

  @Test
  public void testValidElementName_False()
  {
    ObjectNode aObjectNode = new ObjectNode();
    aDiagram.addRootNode(aObjectNode);
    assertFalse(aStateDiagramValidator.isValid());
  }

}
