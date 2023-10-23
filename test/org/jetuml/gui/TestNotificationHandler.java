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

import javafx.application.Platform;
import javafx.stage.Stage;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedList;

public class TestNotificationHandler {

    private static Field aListField;

    @BeforeAll
    public static void setup()
    {
        Platform.startup(() -> {});
        try {
            aListField = NotificationHandler.class.getDeclaredField("aNotificationList");
            aListField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    public static void resetModifiers() {
        try
        {
            aListField.setAccessible(false);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testSpawnNotifications()
    {
        Platform.runLater(() -> {
            Stage stage;
            stage = new Stage();
            NotificationHandler.instance().setMainStage(stage);


            NotificationHandler handler = NotificationHandler.instance();

            handler.spawn("This is a test error notification.", ToastNotification.Type.ERROR);
            handler.spawn("This is a test info notification.", ToastNotification.Type.INFO);
            handler.spawn("This is a test warning notification.", ToastNotification.Type.WARNING);
            handler.spawn("This is a test success notification.", ToastNotification.Type.SUCCESS);

            LinkedList<Notification> notificationList;
            try
            {
                notificationList = (LinkedList<Notification>) aListField.get(handler);
                assertEquals(4, notificationList.size());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testSpawnNotificationsWhenMainStageNotSet()
    {
        Platform.runLater(() -> {

            NotificationHandler handler = NotificationHandler.instance();

            handler.spawn("This is a test error notification.", ToastNotification.Type.ERROR);
            handler.spawn("This is a test info notification.", ToastNotification.Type.INFO);

            LinkedList<Notification> notificationList;
            try
            {
                notificationList = (LinkedList<Notification>) aListField.get(handler);
                assertEquals(0, notificationList.size());
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        });
    }

}
