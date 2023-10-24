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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class TestNotificationHandler {

    private static Field aListField;
    private static Stage aStage;

    @BeforeAll
    public static void setup()
    {
        try {
            aListField = NotificationHandler.class.getDeclaredField("aNotificationList");
            aListField.setAccessible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Platform.startup(() -> {
            aStage = new Stage();
            NotificationHandler.instance().setMainStage(aStage);
        });

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

    @BeforeEach
    public void resetList() {
        try
        {
            LinkedList<Notification> newList = new LinkedList<>();
            aListField.set(NotificationHandler.instance(), newList);
            NotificationHandler.instance().setMainStage(aStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> semaphore.release());
        semaphore.acquire();
    }

    @Test
    public void testSpawnNotifications()
    {
        NotificationHandler handler = NotificationHandler.instance();

        Platform.runLater(() -> {
            handler.spawn("This is a test error notification.", ToastNotification.Type.ERROR);
            handler.spawn("This is a test info notification.", ToastNotification.Type.INFO);
            handler.spawn("This is a test warning notification.", ToastNotification.Type.WARNING);
            handler.spawn("This is a test success notification.", ToastNotification.Type.SUCCESS);
        });

        try {
            waitForRunLater();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LinkedList<Notification> notificationList;
        try
        {
            notificationList = (LinkedList<Notification>) aListField.get(handler);
            assertEquals(4, notificationList.size());
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSpawnNotificationsWhenMainStageNotSet()
    {
        NotificationHandler handler = NotificationHandler.instance();

        Platform.runLater(() -> {
            handler.setMainStage(null);
            handler.spawn("This is a test error notification.", ToastNotification.Type.ERROR);
            handler.spawn("This is a test info notification.", ToastNotification.Type.INFO);
        });

        try {
            waitForRunLater();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LinkedList<Notification> notificationList;
        try
        {
            notificationList = (LinkedList<Notification>) aListField.get(handler);
            assertEquals(0, notificationList.size());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testNotificationCallback()
    {

        NotificationHandler handler = NotificationHandler.instance();

        Platform.runLater(() ->
        {
            // This test notification has a 2-millisecond fade in animation, a 2-millisecond fade out animation and a lifespan of 100 ms.
            ToastNotification notification = new ToastNotification("This is a test notification", ToastNotification.Type.INFO, handler.getMainStage(), 2, 2, 100);
            handler.spawn(notification);
        });

        try {
            waitForRunLater();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LinkedList<Notification> notificationList;
        try
        {
            notificationList = (LinkedList<Notification>) aListField.get(handler);
            assertEquals(1, notificationList.size());
            Thread.sleep(400);
            assertEquals(0, notificationList.size());
        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

}
