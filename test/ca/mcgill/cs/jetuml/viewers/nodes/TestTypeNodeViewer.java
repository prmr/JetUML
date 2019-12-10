package ca.mcgill.cs.jetuml.viewers.nodes;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import ca.mcgill.cs.jetuml.JavaFXLoader;
import ca.mcgill.cs.jetuml.diagram.nodes.InterfaceNode;
import ca.mcgill.cs.jetuml.diagram.nodes.TypeNode;
import ca.mcgill.cs.jetuml.geom.Point;
import ca.mcgill.cs.jetuml.geom.Rectangle;

public class TestTypeNodeViewer
{
	private static final TypeNodeViewer aViewer = new TypeNodeViewer();
	
	@BeforeAll
	public static void setupClass()
	{
		JavaFXLoader.load();
	}
	
	@ParameterizedTest
	@MethodSource("provideArgumentsForTestBounds")
	public void testBounds(TypeNode pNode, Rectangle pOracle)
	{
		assertEquals(pOracle, aViewer.getBounds(pNode));
	}
	
	private static Stream<Arguments> provideArgumentsForTestBounds() {
	    return Stream.of(
	      createInterfaceNode1(),
	      createInterfaceNode2(),
	      createInterfaceNode3(),
	      createInterfaceNode4(),
	      createInterfaceNode5(),
	      createInterfaceNode6()
	    );
	}
	
	// At (0,0); name is just the interface prototype, no methods
	private static Arguments createInterfaceNode1()
	{
		return Arguments.of(new InterfaceNode(), 
				new Rectangle(0,0, 100, 60)); // Default width and height
	}
	
	// At (10,20); name is just the interface prototype, no methods
	private static Arguments createInterfaceNode2()
	{
		InterfaceNode node = new InterfaceNode();
		node.moveTo(new Point(10,20));
		return Arguments.of(node, 
				new Rectangle(10, 20, 100, 60)); // Default width and height, translated
	}
	
	// At (0,0), name is a single line
	private static Arguments createInterfaceNode3()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName(node.getName() + "NAME");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	// At (0,0) name is two lines, no methods
	private static Arguments createInterfaceNode4()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName(node.getName() + "NAME");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
	
	// At (0,0) name is three lines, no methods
	private static Arguments createInterfaceNode5()
	{
		InterfaceNode node = new InterfaceNode();
		node.setName(node.getName() + "NAME1\nNAME2\nNAME3");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 71)); // Default width and additional height
	}

	// Name is just the interface prototype, one methods
	private static Arguments createInterfaceNode6()
	{
		InterfaceNode node = new InterfaceNode();
		node.setMethods("METHODS");
		return Arguments.of(node, 
				new Rectangle(0, 0, 100, 60)); // Default width and height
	}
}
