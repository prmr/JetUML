package org.json;

/**
 * Represents an error while processing JSON data.
 */
@SuppressWarnings("serial")
public class JSONException extends RuntimeException 
{
    /**
     * Constructs a JSONException with a message.
     *
     * @param pMessage Details about the reason for the exception.
     */
    public JSONException(String pMessage)
    {
        super(pMessage);
    }

    /**
     * Constructs a JSONException with a message and cause.
     * 
     * @param pMessage Details about the reason for the exception.
     * @param pCause The original exception.
     */
    public JSONException(String pMessage, Throwable pCause) 
    {
        super(pMessage, pCause);
    }

    /**
     * Constructs a new JSONException with the specified cause.
     * 
     * @param pCause The cause.
     */
    public JSONException(Throwable pCause) 
    {
        super(pCause.getMessage(), pCause);
    }
}
