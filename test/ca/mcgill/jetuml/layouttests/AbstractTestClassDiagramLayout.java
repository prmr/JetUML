package ca.mcgill.jetuml.layouttests;

import java.io.IOException;
import java.nio.file.Path;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.viewers.nodes.TypeNodeViewer;

/**
 * Superclass for classes that test the layout of a class diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestClassDiagramLayout extends AbstractTestDiagramLayout
{
	AbstractTestClassDiagramLayout(Path pDiagramPath) throws IOException
	{
		super(pDiagramPath);
	}
	
	protected static void verifyClassNodeDefaultDimensions(Node pNode)
	{
		final int DEFAULT_WIDTH = getStaticIntFieldValue(TypeNodeViewer.class, "DEFAULT_WIDTH");
		final int DEFAULT_HEIGHT = getStaticIntFieldValue(TypeNodeViewer.class, "DEFAULT_HEIGHT");
		verifyDefaultDimensions(pNode, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}
}
