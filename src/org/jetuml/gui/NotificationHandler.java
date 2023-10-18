/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2023 by McGill University.
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
package org.jetuml.gui;

import javafx.stage.Stage;

import java.util.LinkedList;

/**
 * Singleton object that manages the notification object positions and display states.
 */
public final class NotificationHandler
{

    private static final int NOTIFICATION_STACK_SPACING = 8;
    private static final int NOTIFICATION_STACK_X_MARGIN = 18;
    private static final int NOTIFICATION_STACK_Y_MARGIN = 18;

    /**
     * A callback for Notification objects called when they reached the end of their lifetime.
     * It removes them from the handler notification list.
     */
    public final class CleanUpCallback
    {

        private CleanUpCallback()
        {}

        /**
         * Removes the specified notification object from the handler notification list.
         *
         * @param pNotification The notification object that calls this function
         */
        public void execute(Notification pNotification)
        {
            if (INSTANCE.aNotificationList.contains(pNotification))
            {
                aNotificationList.remove(pNotification);
            }
        }

    }

    private static final NotificationHandler INSTANCE = new NotificationHandler();

    private Stage aMainStage;
    private LinkedList<Notification> aNotificationList = new LinkedList<>();

    private NotificationHandler()
    {}

    /**
     * @return The NotificationHandler singleton instance
     */
    public static NotificationHandler instance()
    { return INSTANCE; }

    /**
     * Sets the parent stage of all the notification stages.
     *
     * @param pStage The target parent stage of the notification objects
     */
    public void setMainStage(Stage pStage)
    {
        this.aMainStage = pStage;
    }

    /**
     * @return The application stage
     */
    public Stage getMainStage()
    {
        return this.aMainStage;
    }

    /**
     * Rearranges the notifications so that they are stacked properly and do not overlap.
     */
    public void updatePosition()
    {

        double yBuf = this.aMainStage.getY() + this.aMainStage.getHeight() - NOTIFICATION_STACK_Y_MARGIN;
        double x = this.aMainStage.getX() + NOTIFICATION_STACK_X_MARGIN;

        for (int i = aNotificationList.size() - 1; i >= 0; i--)
        {
            Notification notification = aNotificationList.get(i);
            notification.setX(x);
            notification.setY(yBuf);
            yBuf = yBuf - notification.getHeight() - NOTIFICATION_STACK_SPACING;
        }
    }

    /**
     * Spawns a notification (it should be instantiated first).
     *
     * @param pNotification The notification object to spawn
     */
    public void spawn(Notification pNotification)
    {
        if (this.aMainStage == null)
        {
            return;
        }

        double x = this.aMainStage.getX() + NOTIFICATION_STACK_X_MARGIN;
        double y = this.aMainStage.getY() + this.aMainStage.getHeight() - NOTIFICATION_STACK_Y_MARGIN;

        aNotificationList.add(pNotification);

        pNotification.show(x, y, new CleanUpCallback());
        this.updatePosition();
    }

    /**
     * Spawns a new toast notification.
     *
     * @param pText The text to show on the toast
     */
    public void spawn(String pText, ToastNotification.Type pType)
    {

        if (this.aMainStage == null)
        {
            return;
        }

        ToastNotification toast = new ToastNotification(pText, pType, this.aMainStage);
        spawn(toast);

    }

}
