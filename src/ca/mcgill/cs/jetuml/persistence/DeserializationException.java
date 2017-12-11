package ca.mcgill.cs.jetuml.persistence;

/**
 * Represents a problem retrieving a graph from serialized form.
 * 
 * @author Martin P. Robillard
 */
@SuppressWarnings("serial")
public class DeserializationException extends RuntimeException
{
	/**
	 * Creates an exception with a message.
	 * 
	 * @param pMessage The message.
	 */
	public DeserializationException(String pMessage)
	{
		super(pMessage);
	}

	/**
	 * Creates an exception with a message and a wrapped exception.
	 * 
	 * @param pMessage The message.
	 * @param pException The wrapped exception.
	 */
	public DeserializationException(String pMessage, Throwable pException)
	{
		super(pMessage, pException);
	}
}
