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
import org.jetuml.diagram.nodes.ActorNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.ObjectNode;
import org.jetuml.diagram.nodes.UseCaseNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestUseCaseDiagramValidator
{
  private Diagram aDiagram;
  private ActorNode aActorNode;
  private UseCaseNode aUseCaseNode;
  private NoteNode aNoteNode;
  private UseCaseDiagramValidator aUseCaseDiagramValidator;

  @BeforeEach
  public void setUp()
  {
    aDiagram = new Diagram(DiagramType.USECASE);
    aActorNode = new ActorNode();
    aUseCaseNode = new UseCaseNode();
    aNoteNode = new NoteNode();
    aUseCaseDiagramValidator = new UseCaseDiagramValidator(aDiagram);
  }

  @Test
  public void testValidNodeHierarchyAndValidElementName()
  {
    aDiagram.addRootNode(aActorNode);
    aDiagram.addRootNode(aUseCaseNode);
    aDiagram.addRootNode(aNoteNode);
    assertTrue(aUseCaseDiagramValidator.isValid());
  }

  @Test
  public void testValidElementName_False()
  {
    ObjectNode aObjectNode = new ObjectNode();
    aDiagram.addRootNode(aObjectNode);
    assertFalse(aUseCaseDiagramValidator.isValid());
  }

}
