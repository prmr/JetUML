package ca.mcgill.cs.jetuml.viewers.nodes;

 import java.nio.file.Path;
 import java.time.Duration;
 import java.time.Instant;

 import ca.mcgill.cs.jetuml.diagram.Diagram;
 import ca.mcgill.cs.jetuml.diagram.DiagramType;
 import ca.mcgill.cs.jetuml.persistence.PersistenceService;
 import javafx.scene.canvas.Canvas;
 import javafx.scene.canvas.GraphicsContext;

 /**
  * Tests the performance of drawing a diagram. 
  */
 public final class TestPerformance 
 {
 	private static final int NUMBER_OF_TRIALS = 10;
 	
 	private TestPerformance() {}

 	/**
 	 * Test method. 
 	 */
 	public static void main(String[] pArgs) throws Exception
 	{
 		Canvas canvas = new Canvas();
 		GraphicsContext graphicContext = canvas.getGraphicsContext2D();
 		Diagram diagram = PersistenceService.read(Path.of("testdata", "performanceDiagram.class.jet").toFile()).diagram();

 		double avgExecutionTime = 0.0;
 		for (int i = 0; i < NUMBER_OF_TRIALS+1; i++)
 		{
 			Instant start = Instant.now();
 			DiagramType.viewerFor(diagram).draw(diagram, graphicContext);
 			Instant stop = Instant.now();

 			if (i == 0)
 			{
 				continue;
 			}
 			else
 			{
 				avgExecutionTime += Duration.between(start, stop).toMillis();
 			}
 		}
 		avgExecutionTime = avgExecutionTime / NUMBER_OF_TRIALS;

 		System.out.println("Test DiagramViewer.draw(diagram) : ");
 		System.out.println("Average Duration (ms) of " + NUMBER_OF_TRIALS + " trials : " + avgExecutionTime);
 	}
 }