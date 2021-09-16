package ca.mcgill.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import ca.mcgill.cs.jetuml.diagram.PropertyName;
import ca.mcgill.cs.jetuml.diagram.edges.AggregationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.GeneralizationEdge;
import ca.mcgill.cs.jetuml.diagram.edges.NoteEdge;
import ca.mcgill.cs.jetuml.diagram.nodes.PackageNode;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.edges.EdgeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.AbstractPackageNodeViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.TypeNodeViewer;

/*
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutClassDiagram1 extends AbstractTestDiagramLayout
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.class.jet");
	
	// We add two pixels to the length of an edge to account for the stroke width and/or 
	// the arrow head.
	private static final int BUFFER = 2; 

	public TestLayoutClassDiagram1() throws IOException
	{
		super(PATH);
	}
	 
	/*
	 * Tests that nodes are in the position that corresponds to their position value 
	 * in the file. We don't test Node7 because its position is calculated from its children
	 */
	@ParameterizedTest
	@CsvSource({"Node1, 200, 10",
				"Node2, 30, 130",
				"Node3, 200, 130",
				"Node4, 370, 130",
				"Node5, 200, 280",
				"Node6, 440, 290"})
	void testNamedNodePosition(String pNodeName, int pExpectedX, int pExpectedY)
	{
		verifyPosition(nodeByName(pNodeName), pExpectedX, pExpectedY);
	}
	
	/*
	 * Tests that all class nodes that are supposed to have the default dimension
	 * actually do. 
	 */
	@ParameterizedTest
	@ValueSource(strings = {"Node1", "Node2", "Node3", "Node4"})
	void testClassNodesDefaultDimension(String pNodeName)
	{
		verifyClassNodeDefaultDimensions(nodeByName(pNodeName));
	}
	
	/*
	 * Tests that the note node has the default dimensions. 
	 */
	@Test
	void testNoteNodeDefaultDimension()
	{
		verifyNoteNodeDefaultDimensions(nodeByName("Node6"));
	}
	
	/*
	 * Tests that Node5 is longer that the default height 
	 */
	@Test
	void testNode5IsExpanded()
	{
		try
		{
			final int DEFAULT_HEIGHT = getStaticIntFieldValue(TypeNodeViewer.class, "DEFAULT_HEIGHT");
			Rectangle bounds = NodeViewerRegistry.getBounds(nodeByName("Node5"));
			assertTrue(bounds.getHeight() > DEFAULT_HEIGHT);
		} 
		catch(ReflectiveOperationException e)
		{
			fail();
		}
	}
	
	/*
	 * Tests that the bounds of the package node are outside of the bounds
	 * of its child Node2
	 */
	@Test
	void testPackageNodeContainment()
	{
		try 
		{
			final int packageNodePadding = getStaticIntFieldValue(AbstractPackageNodeViewer.class, "PADDING");
			Rectangle boundsNode2 = NodeViewerRegistry.getBounds(nodeByName("Node2"));
			Rectangle boundsNode7 = NodeViewerRegistry.getBounds(nodesByType(PackageNode.class).get(0));
			assertEquals(boundsNode2.getX() - packageNodePadding, boundsNode7.getX());
			assertEquals(boundsNode2.getMaxX() + packageNodePadding, boundsNode7.getMaxX());
			assertEquals(boundsNode2.getMaxY() + packageNodePadding, boundsNode7.getMaxY());
			assertTrue(boundsNode7.getY() < boundsNode2.getY());
		} 
		catch (ReflectiveOperationException e) 
		{
			fail();
		}
	}
	
	/*
	 * Tests that the dependency edge connects to its node boundaries. 
	 */
	@Test
	void testDependencyEdge()
	{
		Rectangle boundsNode2 = NodeViewerRegistry.getBounds(nodeByName("Node2"));
		Rectangle boundsNode3 = NodeViewerRegistry.getBounds(nodeByName("Node3"));
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edgeByMiddleLabel("e1"));
		assertWithTolerance(boundsNode2.getMaxX(), BUFFER, edgeBounds.getX());
		assertWithTolerance(boundsNode3.getX(), BUFFER, edgeBounds.getMaxX());
	}
	
	/*
	 * Tests that the implementation edge connects to its node boundaries. 
	 */
	@Test
	void testImplementationEdge()
	{
		Rectangle boundsNode1 = NodeViewerRegistry.getBounds(nodeByName("Node1"));
		Rectangle boundsNode3 = NodeViewerRegistry.getBounds(nodeByName("Node3"));
		GeneralizationEdge edge = (GeneralizationEdge) edgesByType(GeneralizationEdge.class).stream()
				.filter(e -> e.properties().get(PropertyName.GENERALIZATION_TYPE).get() == GeneralizationEdge.Type.Implementation)
				.findFirst()
				.get();
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edge);
		assertWithTolerance(boundsNode1.getMaxY(), BUFFER, edgeBounds.getY());
		assertWithTolerance(boundsNode3.getY(), BUFFER, edgeBounds.getMaxY());
	}
	
	/*
	 * Tests that the inheritance edge connects to its node boundaries. 
	 */
	@Test
	void testInheritanceEdge()
	{
		Rectangle boundsNode3 = NodeViewerRegistry.getBounds(nodeByName("Node3"));
		Rectangle boundsNode5 = NodeViewerRegistry.getBounds(nodeByName("Node5"));
		GeneralizationEdge edge = (GeneralizationEdge) edgesByType(GeneralizationEdge.class).stream()
				.filter(e -> e.properties().get(PropertyName.GENERALIZATION_TYPE).get() == GeneralizationEdge.Type.Inheritance)
				.findFirst()
				.get();
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edge);
		assertWithTolerance(boundsNode3.getMaxY(), BUFFER, edgeBounds.getY());
		assertWithTolerance(boundsNode5.getY(), BUFFER, edgeBounds.getMaxY());
	}
	
	/*
	 * Tests that the aggregation edge connects to its node boundaries. 
	 */
	@Test
	void testAggregationEdge()
	{
		Rectangle boundsNode3 = NodeViewerRegistry.getBounds(nodeByName("Node3"));
		Rectangle boundsNode4 = NodeViewerRegistry.getBounds(nodeByName("Node4"));
		AggregationEdge edge = (AggregationEdge) edgesByType(AggregationEdge.class).stream()
				.filter(e -> e.properties().get(PropertyName.AGGREGATION_TYPE).get() == AggregationEdge.Type.Aggregation)
				.findFirst()
				.get();
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edge);
		assertWithTolerance(boundsNode3.getMaxX(), BUFFER, edgeBounds.getX());
		assertWithTolerance(boundsNode4.getX(), BUFFER, edgeBounds.getMaxX());
	}
	
	/*
	 * Tests that the composition edge connects to its node boundaries. 
	 */
	@Test
	void testCompositionEdge()
	{
		Rectangle boundsNode5 = NodeViewerRegistry.getBounds(nodeByName("Node5"));
		Rectangle boundsNode4 = NodeViewerRegistry.getBounds(nodeByName("Node4"));
		AggregationEdge edge = (AggregationEdge) edgesByType(AggregationEdge.class).stream()
				.filter(e -> e.properties().get(PropertyName.AGGREGATION_TYPE).get() == AggregationEdge.Type.Composition)
				.findFirst()
				.get();
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edge);
		assertWithTolerance(boundsNode5.getMaxX(), BUFFER, edgeBounds.getX());
		assertWithTolerance(boundsNode4.getX(), BUFFER, edgeBounds.getMaxX());
	}
	
	/*
	 * Tests that the note edge connects to the note node boundary and falls within
	 *  the target node.
	 */
	@Test
	void testNoteEdge()
	{
		Rectangle boundsNode6 = NodeViewerRegistry.getBounds(nodeByName("Node6"));
		Rectangle boundsNode4 = NodeViewerRegistry.getBounds(nodeByName("Node4"));
		NoteEdge edge = (NoteEdge) edgesByType(NoteEdge.class).stream()
				.findFirst()
				.get();
		Rectangle edgeBounds = EdgeViewerRegistry.getBounds(edge);
		assertWithTolerance(boundsNode6.getY(), BUFFER, edgeBounds.getMaxY());
		assertTrue(boundsNode4.contains(edge.getEnd().position()));
	}
}
