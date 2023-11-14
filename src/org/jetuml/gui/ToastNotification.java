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

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jetuml.application.UserPreferences;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A toast notification object (that pops up and disappears without requiring any user interaction).
 */
public final class ToastNotification implements Notification
{

    /**
     * Defines the color of the toast notification.
     */
    public enum Type
    {
        ERROR("-fx-padding: 8px; -fx-background-color: rgb(200, 70, 70); -fx-background-radius: 10"),
        SUCCESS("-fx-padding: 8px; -fx-background-color: rgb(112, 173, 70); -fx-background-radius: 10"),
        WARNING("-fx-padding: 8px; -fx-background-color: rgb(220, 150, 20); -fx-background-radius: 10"),
        INFO("-fx-padding: 8px; -fx-background-color: rgb(70, 115, 195); -fx-background-radius: 10");

        private final String aStyle;

        Type(String pStyle)
        {
            aStyle = pStyle;
        }

        /**
         * @return A string containing the CSS style of the type
         */
        public String getStyle()
        {
            return aStyle;
        }
    }

    private static final int FADE_IN_DELAY = 500;
    private static final int FADE_OUT_DELAY = 500;
    private static final int NOTIFICATION_DELAY = 5000;

    private final Stage aStage;

    /**
     * Creates a new Toast notification object using default fade in delay, fade out delay and lifespan.
     *
     * @param pMessage The message to display
     * @param pType The type of the toast notification
     * @param pOwnerStage The main window stage
     */
    public ToastNotification(String pMessage, Type pType, Stage pOwnerStage)
    {
        aStage = createStage(pMessage, pType, pOwnerStage);
    }

    private Stage createStage(String pMessage, Type pType, Stage pOwnerStage)
    {
        Stage stage = new Stage();

        stage.initOwner(pOwnerStage);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(pMessage);

        text.setFont(Font.font("System", UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize)));
        text.setFill(Color.WHITE);

        StackPane pane = new StackPane(text);

        pane.setStyle(pType.getStyle());
        pane.setOpacity(0);

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        return stage;
    }

    /**
     * Show the Notification object.
     *
     * @param pCleanUpCallback The Runnable to run when the notification should be removed from the notification list
     */
    @Override
    public void show(Runnable pCleanUpCallback)
    {
        aStage.show();

        // We define the Timeline for the fadein animation
        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey = new KeyFrame(Duration.millis(FADE_IN_DELAY), new KeyValue(aStage.getScene().getRoot().opacityProperty(), 1));
        fadeInTimeline.getKeyFrames().add(fadeInKey);

        // We define the Timeline for the fadeout animation
        Timeline fadeOutTimeline = new Timeline();
        KeyFrame fadeOutKey = new KeyFrame(Duration.millis(FADE_OUT_DELAY),
                new KeyValue(aStage.getScene().getRoot().opacityProperty(), 0));

        fadeOutTimeline.getKeyFrames().add(fadeOutKey);
        // We close the stage and execute the callback at the end of the fadeout animation
        fadeOutTimeline.setOnFinished(actionEvent1 -> {
            aStage.close();
            pCleanUpCallback.run();
        });

        Timer notificationTimer = new Timer();
        TimerTask lifespan = new TimerTask()
        {
            public void run()
            {
                fadeOutTimeline.play();
            }
        };

        // When the fadein animation ends, we start a timer that will execute the fadeout animation
        fadeInTimeline.setOnFinished(actionEvent -> notificationTimer.schedule(lifespan, NOTIFICATION_DELAY));

        // We trigger the fadein animation
        fadeInTimeline.play();
    }

    /**
     * Move the Notification object to the desired position on the screen.
     *
     * @param pX The target X position
     * @param pY The target Y position
     */
    @Override
    public void setPosition(double pX, double pY)
    {
        aStage.setX(pX);
        aStage.setY(pY - aStage.getHeight()); // We consider the bottom-left corner as the y position reference point
    }

    /**
     * @return The height of the Notification object.
     */
    @Override
    public double getHeight()
    {
        return aStage.getHeight();
    }
}
