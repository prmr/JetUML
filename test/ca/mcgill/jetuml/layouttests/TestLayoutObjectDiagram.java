package ca.mcgill.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import ca.mcgill.cs.jetuml.diagram.Edge;
import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.edges.ObjectReferenceEdge;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.ObjectNodeViewer;

/**
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutObjectDiagram extends AbstractTestDiagramLayout
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.object.jet");

	/**
	 * We add two pixels to the length of an edge to account for the stroke width and/or the arrow head.
	 */
	private static final int BUFFER = 2; 
	
	TestLayoutObjectDiagram() throws IOException
	{
		super(PATH);
	}
	
	/**
	 * Tests that nodes are in the position that corresponds to their position value 
	 * in the file. 
	 */
	@ParameterizedTest
	@CsvSource({":Type1, 240, 130",
				"object2:, 540, 150",
				":Type3, 610, 300",
				"A note, 280, 330"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/**
	 * Tests that the unnamed node is in the position that corresponds to its position value in the file. 
	 */
	@Test
	void testUnnamedNodePosition()
	{
		verifyPosition(nodeByName(""), 440, 290);
	}
	
	/**
	 * Tests that all object nodes that are supposed to have the default dimension
	 * actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"object2:", ":Type3"})
	void testObjectNodesDefaultDimension(String pNodeName)
	{
		verifyObjectNodeDefaultDimensions(nodeByName(pNodeName));
	}
	
	/**
	 * Tests that the object nodes that are supposed to be expanded, actually are. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {":Type1", ""})
	void testObjectNodeExpandedVertically(String pNodeName)
	{
		try
		{
			final int DEFAULT_HEIGHT = getStaticIntFieldValue(ObjectNodeViewer.class, "DEFAULT_HEIGHT");
			Rectangle bounds = NodeViewerRegistry.getBounds(nodeByName(pNodeName));
			assertTrue(bounds.getHeight() > DEFAULT_HEIGHT);
		} 
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	/**
	 * Tests that the collaboration edge connects to its node boundaries. 
	 */
	@Test
	void testCollaborationEdge()
	{
		Rectangle boundsUnnamedNode = NodeViewerRegistry.getBounds(nodeByName(""));
		Rectangle boundsNodeObject2 = NodeViewerRegistry.getBounds(nodeByName("object2:"));
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edgeByMiddleLabel("e1"));
		assertWithTolerance(boundsNodeObject2.getMaxY(), BUFFER, edgeBounds.getY());
		assertWithTolerance(boundsUnnamedNode.getY(), BUFFER, edgeBounds.getMaxY());
	}
	
	/**
	 * Tests that the note edge connects to the note node boundary and falls within
	 *  the target node.
	 */
	@Test
	void testNoteEdgeBetweenNoteNodeAndType1Node()
	{
		Node type1Node = nodeByName(":Type1");
		Node noteNode = nodeByName("A note");
		Rectangle boundsType1Node = NodeViewerRegistry.getBounds(type1Node);
		Rectangle boundsNoteNode = NodeViewerRegistry.getBounds(noteNode);
		Edge noteEdge = edgesByType(NoteEdge.class).stream()
				.filter(edge -> boundsType1Node.contains(edge.getStart().position()) ||
						boundsType1Node.contains(edge.getEnd().position()))
				.toList().get(0);
		Rectangle boundsNoteEdge = EdgeViewerRegistry.getBounds(noteEdge);
		assertWithTolerance(boundsNoteNode.getY(), BUFFER, boundsNoteEdge.getMaxY());
		assertTrue(boundsType1Node.contains(noteEdge.getStart().position()));
	}
	
	/**
	 * Tests that the note edge connects to the note node boundary and falls within
	 *  the target node.
	 */
	@Test
	void testNoteEdgeBetweenNoteNodeAndUnnamedNode()
	{
		Node unnamedNode = nodeByName("");
		Node noteNode = nodeByName("A note");
		Rectangle boundsUnnamedNode = NodeViewerRegistry.getBounds(unnamedNode);
		Rectangle boundsNoteNode = NodeViewerRegistry.getBounds(noteNode);
		Edge noteEdge = edgesByType(NoteEdge.class).stream()
				.filter(edge -> boundsUnnamedNode.contains(edge.getStart().position()) ||
						boundsUnnamedNode.contains(edge.getEnd().position()))
				.toList().get(0);
		Rectangle boundsNoteEdge = EdgeViewerRegistry.getBounds(noteEdge);
		assertWithTolerance(boundsNoteNode.getMaxX(), BUFFER, boundsNoteEdge.getX());
		assertTrue(boundsUnnamedNode.contains(noteEdge.getEnd().position()));
	}
	
	/**
	 * Tests that the (self) reference edge connects to the node ":Type1" boundary and falls within
	 *  the node ":Type1".
	 */
	@Test
	void testSelfReferenceEdge()
	{
		Node type1Node = nodeByName(":Type1");
		Edge referenceEdge = edgesByType(ObjectReferenceEdge.class).stream()
				.filter(edge -> edge.getEnd().equals(type1Node))
				.toList().get(0);
		Rectangle boundsType1Node = NodeViewerRegistry.getBounds(type1Node);
		assertWithTolerance(boundsType1Node.getX(), BUFFER, referenceEdge.getEnd().position().getX());
		assertTrue(boundsType1Node.contains(referenceEdge.getStart().position()));
	}
	
	/**
	 * Tests that the reference edge connects to the unnamed node boundary and falls within
	 *  the node ":Type1".
	 */
	@Test
	void testReferenceEdgeBetweenType1NodeAndUnnamedNode()
	{
		Node type1Node = nodeByName(":Type1");
		Node unnamedNode = nodeByName("");
		Edge referenceEdge = edgesByType(ObjectReferenceEdge.class).stream()
				.filter(edge -> edge.getEnd().equals(unnamedNode)).toList().get(0);
		Rectangle boundsType1Node = NodeViewerRegistry.getBounds(type1Node);
		Rectangle boundsUnnamedNode = NodeViewerRegistry.getBounds(unnamedNode);
		Rectangle boundsReferenceEdge = EdgeViewerRegistry.getBounds(referenceEdge);
		assertWithTolerance(boundsUnnamedNode.getX(), BUFFER, boundsReferenceEdge.getMaxX());
		assertTrue(boundsType1Node.contains(referenceEdge.getStart().position()));
	}
	
	/**
	 * Tests that the reference edge connects to the node ":Type3" boundary and falls within
	 *  the unnamed node.
	 */
	@Test
	void testReferenceEdgeBetweenUnnamedNodeAndType3Node()
	{
		Node unnamedNode = nodeByName("");
		Node type3Node = nodeByName(":Type3");
		Edge referenceEdge = edgesByType(ObjectReferenceEdge.class).stream()
				.filter(edge -> edge.getEnd().equals(type3Node)).toList().get(0);
		Rectangle boundsUnnamedNode = NodeViewerRegistry.getBounds(unnamedNode);
		Rectangle boundsType3Node = NodeViewerRegistry.getBounds(type3Node);
		Rectangle boundsReferenceEdge = EdgeViewerRegistry.getBounds(referenceEdge);
		assertWithTolerance(boundsType3Node.getX(), BUFFER, boundsReferenceEdge.getMaxX());
		assertTrue(boundsUnnamedNode.contains(referenceEdge.getStart().position()));
	}
}
