/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2022 by McGill University.
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
package org.jetuml.rendering.nodes;

 import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;

import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.persistence.PersistenceService;
import org.jetuml.rendering.DiagramRenderer;

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
 		Diagram diagram = PersistenceService.read(Path.of("testdata", "performanceDiagram.class.jet").toFile());
 		DiagramRenderer renderer = DiagramType.newRendererInstanceFor(diagram);

 		double avgExecutionTime = 0.0;
 		for(int i = 0; i < NUMBER_OF_TRIALS+1; i++)
 		{
 			Instant start = Instant.now();
 			renderer.draw(graphicContext);
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

 		System.out.println("Test DiagramRenderer.draw(diagram) : ");
 		System.out.println("Average Duration (ms) of " + NUMBER_OF_TRIALS + " trials : " + avgExecutionTime);
 	}
 }