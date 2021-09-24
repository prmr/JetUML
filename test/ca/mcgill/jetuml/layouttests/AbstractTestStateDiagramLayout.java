package ca.mcgill.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.nio.file.Path;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.CircularStateNodeViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;
import ca.mcgill.cs.jetuml.viewers.nodes.StateNodeViewer;

/**
 * Superclass for classes that test the layout of a state diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestStateDiagramLayout extends AbstractTestDiagramLayout 
{
	AbstractTestStateDiagramLayout(Path pDiagramPath) throws IOException 
	{
		super(pDiagramPath);
	}
	
	protected static void verifyStateNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(StateNodeViewer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(StateNodeViewer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
	
	protected static void verifyCircularStateNodeDefaultDimensions(Node pNode)
	{
		final int DIAMETER = getStaticIntFieldValue(CircularStateNodeViewer.class, "DIAMETER");
		Rectangle bounds = NodeViewerRegistry.getBounds(pNode);
		assertEquals(DIAMETER, bounds.getWidth());
		assertEquals(DIAMETER, bounds.getHeight());
	}
}
