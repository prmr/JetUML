package ca.mcgill.jetuml.layouttests;

import java.io.IOException;
import java.nio.file.Path;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.viewers.nodes.ObjectNodeViewer;

/**
 * Superclass for classes that test the layout of an object diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestObjectDiagramLayout extends AbstractTestDiagramLayout
{
	AbstractTestObjectDiagramLayout(Path pDiagramPath) throws IOException
	{
		super(pDiagramPath);
	}
	
	protected static void verifyObjectNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(ObjectNodeViewer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(ObjectNodeViewer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
}
