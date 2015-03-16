package ca.mcgill.cs.stg.jetuml.framework;

import java.beans.DefaultPersistenceDelegate;
import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import ca.mcgill.cs.stg.jetuml.graph.AbstractNode;
import ca.mcgill.cs.stg.jetuml.graph.Graph;

/**
 * Services for saving and loading Graph objects (i.e., UML diagrams).
 * We use long-term bean persistence to save the graph data. 
 * 
 * @author Martin P. Robillard
 */
public final class PersistenceService
{
	private static PersistenceDelegate staticFieldDelegate = new DefaultPersistenceDelegate()
	{
		@Override
		protected Expression instantiate(Object pOldInstance, Encoder pOut)
		{
			try
			{
				Class<?> cl = pOldInstance.getClass();
				Field[] fields = cl.getFields();
				for(int i = 0; i < fields.length; i++)
				{
					if(Modifier.isStatic(fields[i].getModifiers()) && fields[i].get(null) == pOldInstance)
					{
						return new Expression(fields[i], "get", new Object[] { null });
					}
				}
			}
			catch(IllegalAccessException ex) 
			{
				ex.printStackTrace();
			}
			return null;
		}
            
		@Override
		protected boolean mutatesTo(Object pOldInstance, Object pNewInstance)
		{
			return pOldInstance == pNewInstance;
		}
	};
         
	private PersistenceService() {}
	
	/**
	 * Reads a graph file from pIn then close pIn.
	 * @param pIn the input stream to read. Cannot be null.
	 * @return the graph that is read in
	 * @throws IOException if the graph cannot be read.
	 */
	public static Graph read(InputStream pIn) throws IOException
	{
		assert pIn != null;
		try( XMLDecoder reader = new XMLDecoder(pIn) )
		{
			Graph graph = (Graph) reader.readObject();
			return graph;
		}
		finally
		{
			pIn.close();
		}
	}
	
	/**
     * Saves the current graph in a file. 
     * 
     * @param pGraph The graph to save
     * @param pOut the stream for saving
     */
	public static void saveFile(Graph pGraph, OutputStream pOut)
	{
		XMLEncoder encoder = new XMLEncoder(pOut);
		encoder.setPersistenceDelegate(BentStyle.class, staticFieldDelegate);
		encoder.setPersistenceDelegate(LineStyle.class, staticFieldDelegate);
		encoder.setPersistenceDelegate(ArrowHead.class, staticFieldDelegate);
      
		Graph.setPersistenceDelegate(encoder);
		AbstractNode.setPersistenceDelegate(encoder);
      
		encoder.writeObject(pGraph);
		encoder.close();
	}
}
