package ca.mcgill.jetuml.layouttests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;

import ca.mcgill.cs.jetuml.diagram.Node;
import ca.mcgill.cs.jetuml.geom.Rectangle;
import ca.mcgill.cs.jetuml.viewers.nodes.CallNodeViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.ImplicitParameterNodeViewer;
import ca.mcgill.cs.jetuml.viewers.nodes.NodeViewerRegistry;

/**
 * Superclass for classes that test the layout of a sequence diagram.
 * Declares convenience methods to test diagram elements. 
 */
public abstract class AbstractTestSequenceDiagramLayout extends AbstractTestDiagramLayout 
{
	AbstractTestSequenceDiagramLayout(Path pDiagramPath) throws IOException 
	{
		super(pDiagramPath);
	}
	
	protected static void verifyImplicitParameterNodeTopRectangleDefaultHeight(Node pNode)
	{
		final int TOP_HEIGHT = getStaticIntFieldValue(ImplicitParameterNodeViewer.class, "TOP_HEIGHT");
		ImplicitParameterNodeViewer instanceOfImplicitParameterNodeViewer = getInstanceOfImplicitParameterNodeViewer(pNode);
		Rectangle implicitParameterTopRectangle = instanceOfImplicitParameterNodeViewer.getTopRectangle(pNode);
		assertEquals(TOP_HEIGHT, implicitParameterTopRectangle.getHeight());
	}
	
	protected static void verifyCallNodeDefaultWidth(Node pNode)
	{
		final int WIDTH = getStaticIntFieldValue(CallNodeViewer.class, "WIDTH");
		Rectangle nodeBounds = NodeViewerRegistry.getBounds(pNode);
		assertEquals(WIDTH, nodeBounds.getWidth());
	}
	
	private static ImplicitParameterNodeViewer getInstanceOfImplicitParameterNodeViewer(Node pImplicitParameterNode)
	{
		try
		{
			Field fieldInstanceOfNodeViewerRegistry = NodeViewerRegistry.class.getDeclaredField("INSTANCE");
			fieldInstanceOfNodeViewerRegistry.setAccessible(true);
			NodeViewerRegistry instanceOfNodeViewerRegistry = (NodeViewerRegistry) fieldInstanceOfNodeViewerRegistry.get(null);
			
			Method method = NodeViewerRegistry.class.getDeclaredMethod("viewerFor", Node.class);
			method.setAccessible(true);
			ImplicitParameterNodeViewer instanceOfImplicitParameterNodeViewer =
					(ImplicitParameterNodeViewer)method.invoke(instanceOfNodeViewerRegistry, pImplicitParameterNode);
			return instanceOfImplicitParameterNodeViewer;
		}
		catch (ReflectiveOperationException e)
		{
			assert false;
			fail();
			return null;
		}
	}
}
