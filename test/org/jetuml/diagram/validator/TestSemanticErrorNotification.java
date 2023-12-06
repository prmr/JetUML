/*******************************************************************************
 * JetUML - A desktop application for fast UML diagramming.
 *
 * Copyright (C) 2020 by McGill University.
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
package org.jetuml.diagram.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.jetuml.JavaFXLoader;
import org.jetuml.diagram.Diagram;
import org.jetuml.diagram.DiagramType;
import org.jetuml.diagram.Edge;
import org.jetuml.diagram.edges.DependencyEdge;
import org.jetuml.diagram.edges.NoteEdge;
import org.jetuml.diagram.nodes.ClassNode;
import org.jetuml.diagram.nodes.PointNode;
import org.jetuml.gui.Notification;
import org.jetuml.gui.NotificationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.stage.Stage;

public class TestSemanticErrorNotification
{
    private final ClassDiagramValidator aValidator =
            new ClassDiagramValidator(new Diagram(DiagramType.CLASS));
    private static Field aListField;
    private static Stage aStage;
    private static NotificationService aNotificationService;

    private final ClassNode aClassNode = new ClassNode();
    private final PointNode aPointNode = new PointNode();
    private final NoteEdge aNoteEdge = new NoteEdge();


    private Diagram diagram()
    {
        return aValidator.diagram();
    }

    /*
     * Allows waiting for the platform instructions to be executed before continuing on.
     * It then prevents having a test finishing before the JavaFX instructions are run.
     */
    public static void waitForRunLater() throws InterruptedException
    {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(semaphore::release);
        semaphore.acquire();
    }

    @BeforeAll
    public static void setup() throws ReflectiveOperationException, InterruptedException
    {
        aListField = NotificationService.class.getDeclaredField("aNotifications");
        aListField.setAccessible(true);

        aNotificationService = NotificationService.instance();

        // Manually setting the parent stage of the NotificationService
        JavaFXLoader.load();
        Platform.runLater(() -> {
            aStage = new Stage();
            NotificationService.instance().setMainStage(aStage);
            aStage.show();
        });
        waitForRunLater();
    }

    @AfterAll
    public static void closeStage() throws InterruptedException {
        Platform.runLater(() -> aStage.close());
        waitForRunLater();
        NotificationService.instance().setMainStage(null);
    }

    @BeforeEach
    public void resetList() throws ReflectiveOperationException
    {
        ArrayList<Notification> newList = new ArrayList<>();
        aListField.set(aNotificationService, newList);
    }

    @Test
    void testPointNodeNotConnected() throws InterruptedException, ReflectiveOperationException {
        diagram().addRootNode(new PointNode());
        Platform.runLater(aValidator::isValid); // invalid structure but semantically valid
        waitForRunLater();
        @SuppressWarnings("unchecked")
		List<Notification> notificationList = (List<Notification>) aListField.get(aNotificationService);
        assertEquals(0, notificationList.size());
    }

    @Test
    void testDependencyEdgeToPointNode() throws InterruptedException, ReflectiveOperationException
    {
        diagram().addRootNode(aClassNode);
        diagram().addRootNode(aPointNode);
        Edge edge = new DependencyEdge();
        edge.connect(aClassNode, aPointNode);
        diagram().addEdge(edge);
        Platform.runLater(aValidator::isValid);
        waitForRunLater();
        @SuppressWarnings("unchecked")
		List<Notification> notificationList = (List<Notification>) aListField.get(aNotificationService);
        assertEquals(1, notificationList.size());
    }

    @Test
    void testNoteEdgeFromClassToPoint() throws InterruptedException, ReflectiveOperationException
    {
        diagram().addRootNode(aClassNode);
        diagram().addRootNode(aPointNode);
        aNoteEdge.connect(aClassNode, aPointNode);
        diagram().addEdge(aNoteEdge);
        Platform.runLater(() -> assertFalse(aValidator.isValid()));
        waitForRunLater();
        @SuppressWarnings("unchecked")
		List<Notification> notificationList = (List<Notification>) aListField.get(aNotificationService);
        assertEquals(1, notificationList.size());
    }
}
