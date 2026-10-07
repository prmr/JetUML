package org.jetuml.gui;

/**
 * An object that can report notifications to the user.
 */
public interface NotificationService {
	/**
	 * Notifies the user of an error.
	 * 
	 * @param pText The text of the notification.
	 */
	void notifyError(String pText);

	/**
	 * Notifies the user of a successful operation.
	 * 
	 * @param pText The text of the notification.
	 */
	void notifySuccess(String pText);
}