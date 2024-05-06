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

import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import org.jetuml.annotations.Singleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton object that manages the notification object positions and display states.
 */
@Singleton
public final class NotificationService
{

    private static final int NOTIFICATION_DISPLAY_SPACING = 8;
    private static final int NOTIFICATION_DISPLAY_X_MARGIN = 25;
    private static final int NOTIFICATION_DISPLAY_Y_MARGIN = 25;

    private static final NotificationService INSTANCE = new NotificationService();

    /*
        This attribute sets the parent stage of all notifications. It is set at initialization,
        in JetUML.java. It is impossible to spawn a notification if the main stage attribute
        is null.
     */
    private Stage aMainStage;
    private final List<Notification> aNotifications = new ArrayList<>();
    // Dead notifications are notifications that reached the end of their lifespan and should be
    // removed. We store them in a separate list so that we can close them all at once when
    // the window is maximized, to prevent having the JetUML taskbar window flashing. - See Issue #524
    private final List<Notification> aDeadNotifications = new ArrayList<>();

    private NotificationService() {}

    /**
     * @return The NotificationService singleton instance
     */
    public static NotificationService instance()
    {
        return INSTANCE;
    }

    /**
     * Sets the parent stage of all the notification stages.
     * Only used once in JetUML.java at initialization. The parent stage should
     * be the application stage.
     *
     * @param pStage The target parent stage of the notification objects
     */
    public void setMainStage(Stage pStage)
    {
        aMainStage = pStage;

        if(pStage != null)
        {
            // Window position and size listener for notifications
            ChangeListener<Number> stageTransformationListener = (pObservableValue, pOldValue, pNewValue) ->
                    NotificationService.instance().updateNotificationPosition();

            // When the stage is moved, update the notification positions
            pStage.xProperty().addListener(stageTransformationListener);
            pStage.yProperty().addListener(stageTransformationListener);

            // When the stage is resized, update the notification positions
            pStage.heightProperty().addListener(stageTransformationListener);
            pStage.widthProperty().addListener(stageTransformationListener);
        }
    }

    /**
     * Rearranges the notifications so that they are stacked properly and do not overlap.
     */
    public void updateNotificationPosition()
    {
        double y = aMainStage.getY() + aMainStage.getHeight() - NOTIFICATION_DISPLAY_Y_MARGIN;
        double x = aMainStage.getX() + NOTIFICATION_DISPLAY_X_MARGIN;

        ArrayList<Notification> reverseNotifications = new ArrayList<>(aNotifications);
        Collections.reverse(reverseNotifications);
        for(Notification notification : reverseNotifications)
        {
            notification.setPosition(x, y);
            y = y - notification.getHeight() - NOTIFICATION_DISPLAY_SPACING;

            if(y < aMainStage.getY())
            {
                notification.close();
                aNotifications.remove(notification);
            }
        }
    }

    /**
     * Spawns a notification (it should be instantiated first).
     *
     * @param pNotification The notification object to spawn
     */
    public void spawnNotification(Notification pNotification)
    {
        if(aMainStage == null)
        {
            return;
        }
        aDeadNotifications.forEach(Notification::close);
        aDeadNotifications.clear();

        aNotifications.add(pNotification);

        pNotification.show(() -> {
            aNotifications.remove(pNotification);
            aDeadNotifications.add(pNotification);
        });
        updateNotificationPosition();
    }

    /**
     * Spawns a new toast notification without having to pass a ToastNotification object, i.e. without having
     * to fetch the main stage.
     *
     * @param pText The text to show on the toast
     */
    public void spawnNotification(String pText, ToastNotification.Type pType)
    {
        if(aMainStage == null)
        {
            return;
        }

        ToastNotification toast = new ToastNotification(pText, pType, aMainStage);
        spawnNotification(toast);
    }
}
