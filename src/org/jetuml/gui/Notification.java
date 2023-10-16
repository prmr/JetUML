package org.jetuml.gui;

/**
 * An object that can be displayed by the NotificationHandler.
 */
public interface Notification
{

    /**
     * Show the Notification object to the desired coordinates.
     *
     * @param pX The target X position
     * @param pY The target Y position
     */
    void show(double pX, double pY, NotificationHandler.CleanUpCallback pCleanUpCallback);

    /**
     * Move the Notification object to the desired X position.
     *
     * @param pX The target X position
     */
    void setX(double pX);

    /**
     * Move the Notification object to the desired Y position.
     *
     * @param pY The target Y position
     */
    void setY(double pY);

    /**
     * @return The height of the Notification object.
     */
    double getHeight();

}
