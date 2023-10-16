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

/**
 * A toast notification object (that pops up and disappears without requiring any user interaction).
 */
public class ToastNotification implements Notification
{

    private static final int FADE_IN_DELAY = 500;
    private static final int FADE_OUT_DELAY = 500;
    private static final int NOTIFICATION_DELAY = 5000;

    private final Stage aStage;

    protected ToastNotification(String pMessage, Stage pOwnerStage)
    {
        this.aStage = generate(pMessage, pOwnerStage);
    }

    private Stage generate(String pMessage, Stage pOwnerStage)
    {

        Stage stage = new Stage();

        stage.initOwner(pOwnerStage);
        stage.setResizable(false);
        stage.initStyle(StageStyle.TRANSPARENT);

        Text text = new Text(pMessage);

        text.setFont(Font.font("System", UserPreferences.instance().getInteger(UserPreferences.IntegerPreference.fontSize)));
        text.setFill(Color.WHITE);

        StackPane pane = new StackPane(text);

        pane.setStyle("-fx-padding: 8px; -fx-background-color: rgb(200, 70, 70); -fx-background-radius: 10");
        pane.setOpacity(0);

        Scene scene = new Scene(pane);
        scene.setFill(Color.TRANSPARENT);
        stage.setScene(scene);

        return stage;

    }

    /**
     * Show the Notification object to the desired coordinates.
     *
     * @param pX The target X position
     * @param pY The target Y position
     */
    @Override
    public void show(double pX, double pY, NotificationHandler.CleanUpCallback pCleanUpCallback)
    {
        this.aStage.show();

        this.aStage.setX(pX);
        this.aStage.setY(pY - this.aStage.getHeight());

        Timeline fadeInTimeline = new Timeline();
        KeyFrame fadeInKey = new KeyFrame(Duration.millis(FADE_IN_DELAY), new KeyValue(this.aStage.getScene().getRoot().opacityProperty(), 1));

        fadeInTimeline.getKeyFrames().add(fadeInKey);
        fadeInTimeline.setOnFinished(actionEvent -> {
            new Thread(() -> {
                try
                {
                    Thread.sleep(NOTIFICATION_DELAY);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                Timeline fadeOutTimeline = new Timeline();
                KeyFrame fadeOutKey = new KeyFrame(Duration.millis(FADE_OUT_DELAY),
                        new KeyValue(this.aStage.getScene().getRoot().opacityProperty(), 0));

                fadeOutTimeline.getKeyFrames().add(fadeOutKey);
                fadeOutTimeline.setOnFinished(actionEvent1 -> {
                    aStage.close();
                    pCleanUpCallback.execute(this);
                }); // AUTO CLOSE ?

                fadeOutTimeline.play();


            }).start();
        });
        fadeInTimeline.play();
    }

    /**
     * Move the Notification object to the desired X position.
     *
     * @param pX The target X position
     */
    @Override
    public void setX(double pX)
    {
        this.aStage.setX(pX);
    }

    /**
     * Move the Notification object to the desired Y position.
     *
     * @param pY The target Y position
     */
    @Override
    public void setY(double pY)
    {
        // Since notifications are shown in the bottom-left corner, we set the
        // bottom-left corner of the notification object as the reference point for its position
        this.aStage.setY(pY - this.aStage.getHeight());
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
