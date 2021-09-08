package ca.mcgill.jetuml.layouttests;

import java.io.IOException;
import java.nio.file.Path;

/*
 * This class tests that the layout of a manually-created diagram file corresponds to expectations.
 */
public class TestLayoutObjectDiagram extends AbstractTestDiagramLayout
{
	private static final Path PATH = Path.of("testdata", "testPersistenceService.object.jet");

	TestLayoutObjectDiagram() throws IOException
	{
		super(PATH);
	}
}
