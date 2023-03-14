package org.jetuml.diagram.validator.constraints;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.NoteNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.geom.Point;
import org.junit.jupiter.api.Test;

public class TestEdgeSemanticConstraints 
{

  private Diagram aDiagram = new Diagram(DiagramType.CLASS);
  private ClassNode aNode1 = new ClassNode();
  private ClassNode aNode2 = new ClassNode();
  private PointNode aPointNode = new PointNode();
  private DependencyEdge aEdge1 = new DependencyEdge();
  private DependencyEdge aEdge2 = new DependencyEdge();

  private NoteEdge aNoteEdge = new NoteEdge();
  private NoteNode aNote = new NoteNode();

  private void createDiagram()
  {
    aNode2.moveTo(new Point(0,100));
    aNote.moveTo(new Point(100,100));
    aDiagram.addRootNode(aNode1);
    aDiagram.addRootNode(aNode2);
    aDiagram.addRootNode(aNote);
    aPointNode.moveTo(new Point(200,200));
    aDiagram.addRootNode(aPointNode);
  }

  @Test
  void testPointNodeToNoteEdge()
  {
    createDiagram();
    aNoteEdge.connect(aNote, aPointNode);
    assertTrue(EdgeSemanticConstraints.pointNode().satisfied(aNoteEdge,aNote, aPointNode, aDiagram));
    aEdge1.connect(aNote, aPointNode);
    assertFalse(EdgeSemanticConstraints.pointNode().satisfied(aEdge1,aNote, aPointNode, aDiagram));
    aEdge2.connect(aNode1,aNode2);
    assertTrue(EdgeSemanticConstraints.pointNode().satisfied(aEdge2, aNode1, aNode2, aDiagram));
  }



  @Test
  void testNoteEdgeNotNoteEdge()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.noteEdge().satisfied(aEdge1, aNode1, aNode2,aDiagram));
  }

  @Test
  void testNoteEdgeNodeNotePoint()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.noteEdge().satisfied(aNoteEdge, aNote, aPointNode,aDiagram));
  }

  @Test
  void testNoteEdgeNodeNoteNotPoint()
  {
    createDiagram();
    assertFalse(EdgeSemanticConstraints.noteEdge().satisfied(aNoteEdge, aNote, aNode1,aDiagram));
  }

  @Test
  void testNoteEdgeNodeNoteNotePoint()
  {
    createDiagram();
    assertFalse(EdgeSemanticConstraints.noteEdge().satisfied(aNoteEdge, aNode1, aPointNode,aDiagram));
  }

  @Test
  void testNoteEdgeNodeAnyNode()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.noteEdge().satisfied(aNoteEdge, aNode1, aNote,aDiagram));
  }

  @Test
  void testNoteNodeAnyAny()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.noteNode().satisfied(aEdge1, aNode1, aNode2,aDiagram));
  }

  @Test
  void testNoteNodeNoteAny()
  {
    createDiagram();
    assertFalse(EdgeSemanticConstraints.noteNode().satisfied(aEdge1, aNote, aNode2,aDiagram));
    assertTrue(EdgeSemanticConstraints.noteNode().satisfied(aNoteEdge, aNote, aNode2,aDiagram));
  }

  @Test
  void testNoteNodeAnyNote()
  {
    createDiagram();
    assertFalse(EdgeSemanticConstraints.noteNode().satisfied(aEdge1, aNode1, aNote, aDiagram));
    assertTrue(EdgeSemanticConstraints.noteNode().satisfied(aNoteEdge, aNode1, aNote,aDiagram));
  }

  @Test
  void testNoteNodeNoteNote()
  {
    createDiagram();
    assertFalse(EdgeSemanticConstraints.noteNode().satisfied(aEdge1, aNote, aNote,aDiagram));
    assertTrue(EdgeSemanticConstraints.noteNode().satisfied(aNoteEdge, aNote, aNote,aDiagram));
  }

  @Test
  void testMaxEdgesOne()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(aEdge1, aNode1, aNode2,aDiagram));
    aEdge1.connect(aNode1, aNode2);
    aDiagram.addEdge(aEdge1);
    assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(new DependencyEdge(), aNode1, aNode2,aDiagram));
  }

  @Test
  void testMaxEdgesTwo()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.maxEdges(2).satisfied(aEdge1, aNode1, aNode2,aDiagram));
    aEdge1.connect(aNode1, aNode2);
    aDiagram.addEdge(aEdge1);
    assertTrue(EdgeSemanticConstraints.maxEdges(2).satisfied(new DependencyEdge(), aNode1, aNode2,aDiagram));
    DependencyEdge edge = aEdge1;
    edge.connect(aNode1, aNode2);
    aDiagram.addEdge(edge);
    assertTrue(EdgeSemanticConstraints.maxEdges(2).satisfied(new DependencyEdge(), aNode1, aNode2,aDiagram));
  }

  @Test
  void testMaxEdgesNodesMatchNoMatch()
  {
    createDiagram();
    aEdge1.connect(aNode1, aNode2);
    aDiagram.addEdge(aEdge1);
    ClassNode node3 = new ClassNode();
    assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(new DependencyEdge(), aNode1, node3,aDiagram));
  }

  @Test
  void testMaxEdgesNodesNoMatchMatch()
  {
    createDiagram();
    aEdge1.connect(aNode1, aNode2);
    aDiagram.addEdge(aEdge1);
    ClassNode node3 = new ClassNode();
    assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(aEdge1, node3, aNode2,aDiagram));
  }

  @Test
  void testMaxEdgesNodesNoMatchNoMatch()
  {
    createDiagram();
    aEdge1.connect(aNode1, aNode2);
    aDiagram.addEdge(aEdge1);
    ClassNode node3 = new ClassNode();
    assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(aEdge1, node3, new ClassNode(),aDiagram));
  }

  @Test
  void testMaxEdgesNodesDifferentEdgeType()
  {
    createDiagram();
    aEdge1.connect(aNode1, aNode2);
    aDiagram.addEdge(aEdge1);
    assertTrue(EdgeSemanticConstraints.maxEdges(1).satisfied(new NoteEdge(), aNode1, aNode2,aDiagram ));
  }

  @Test
  void testNodeSelfEdgeTrue()
  {
    createDiagram();
    assertTrue(EdgeSemanticConstraints.noSelfEdge().satisfied(aEdge1, aNode1, aNode2,aDiagram));
  }

  @Test
  void testNodeSelfEdgeFalse()
  {
    createDiagram();
    assertFalse(EdgeSemanticConstraints.noSelfEdge().satisfied(aEdge1, aNode1, aNode1,aDiagram));
  }
}
