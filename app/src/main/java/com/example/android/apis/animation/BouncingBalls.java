/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis.animation;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;

import com.example.android.apis.R;

import java.util.ArrayList;

/**
 * Uses several different kinds of ObjectAnimator to animate bouncing color changing balls.
 * When onTouchEvent is called with either a MotionEvent.ACTION_DOWN or MotionEvent.ACTION_MOVE
 * a ball of random color is added at the events event.getX(), event.getY() coordinates.
 * The ball motion and geometry is animated then an animator of the balls alpha is played
 * fading it out from an alpha of 1.0 to 0.0 in 250 milliseconds The onAnimationEnd callback
 * of the fade animation is set to an AnimatorListenerAdapter which removes the ball when
 * the animation is done.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class BouncingBalls extends Activity {
    String TAG = "BouncingBalls";

    /**
     * Sets the activity content to the layout R.layout.bouncing_balls, locates the LinearLayout
     * within the layout using its id R.id.container and when adds a MyAnimationView View to it.
     *
     * @param savedInstanceState Always null since onSaveInstanceState is never called
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bouncing_balls);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        container.addView(new MyAnimationView(this));
    }

    /**
     * This class does all the work of creating and  animating the bouncing balls. The balls
     * are placed inside a .animation.ShapeHolder as they are created and an animation set is
     * used to perform animation operations on that ShapeHolder.
     */
    public class MyAnimationView extends View {

        private static final int RED = 0xffFF8080;
        private static final int BLUE = 0xff8080FF;
        @SuppressWarnings("unused")
        private static final int CYAN = 0xff80ffff;
        @SuppressWarnings("unused")
        private static final int GREEN = 0xff80ff80;

        public final ArrayList<ShapeHolder> balls = new ArrayList<>();
        AnimatorSet animation = null;

        public MyAnimationView(Context context) {
            super(context);

            // Animate background color
            // Note that setting the background color will automatically invalidate the
            // view, so that the animated color, and the bouncing balls, get redisplayed on
            // every frame of the animation.
            ValueAnimator colorAnim = ObjectAnimator.ofInt(this, "backgroundColor", RED, BLUE);
            colorAnim.setDuration(3000);
            colorAnim.setEvaluator(new ArgbEvaluator());
            colorAnim.setRepeatCount(ValueAnimator.INFINITE);
            colorAnim.setRepeatMode(ValueAnimator.REVERSE);
            colorAnim.start();
        }

        /**
         * When a touch event occurs this routine is called to handle it. It handles only
         * MotionEvent.ACTION_DOWN and MotionEvent.ACTION_MOVE events, checks for these and
         * returns false (event not handled) if it is a different type of event. Otherwise it
         * spawns a new ball in a ShapeHolder container at the .getX() and .getY() of the event
         * and sets up a complex animation set (animatorSet) to be performed on the ShapeHolder
         * which is then .start()'ed before returning 'true' to indicate the event has been
         * handled.
         *
         * @param event The motion event.
         * @return True if the event was handled, false otherwise.
         */
        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() != MotionEvent.ACTION_DOWN &&
                    event.getAction() != MotionEvent.ACTION_MOVE) {
                return false;
            }
            ShapeHolder newBall = addBall(event.getX(), event.getY());

            // Bouncing animation with squash and stretch
            float startY = newBall.getY();
            float endY = getHeight() - 50f;
            float h = (float)getHeight();
            float eventY = event.getY();
            int duration = (int)(500 * ((h - eventY)/h));
            ValueAnimator bounceAnim = ObjectAnimator.ofFloat(newBall, "y", startY, endY);
            bounceAnim.setDuration(duration);
            bounceAnim.setInterpolator(new AccelerateInterpolator());
            ValueAnimator squashAnim1 = ObjectAnimator.ofFloat(newBall, "x", newBall.getX(),
                    newBall.getX() - 25f);
            squashAnim1.setDuration(duration/4);
            squashAnim1.setRepeatCount(1);
            squashAnim1.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim1.setInterpolator(new DecelerateInterpolator());
            ValueAnimator squashAnim2 = ObjectAnimator.ofFloat(newBall, "width", newBall.getWidth(),
                    newBall.getWidth() + 50);
            squashAnim2.setDuration(duration/4);
            squashAnim2.setRepeatCount(1);
            squashAnim2.setRepeatMode(ValueAnimator.REVERSE);
            squashAnim2.setInterpolator(new DecelerateInterpolator());
            ValueAnimator stretchAnim1 = ObjectAnimator.ofFloat(newBall, "y", endY,
                    endY + 25f);
            stretchAnim1.setDuration(duration/4);
            stretchAnim1.setRepeatCount(1);
            stretchAnim1.setInterpolator(new DecelerateInterpolator());
            stretchAnim1.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator stretchAnim2 = ObjectAnimator.ofFloat(newBall, "height",
                    newBall.getHeight(), newBall.getHeight() - 25);
            stretchAnim2.setDuration(duration/4);
            stretchAnim2.setRepeatCount(1);
            stretchAnim2.setInterpolator(new DecelerateInterpolator());
            stretchAnim2.setRepeatMode(ValueAnimator.REVERSE);
            ValueAnimator bounceBackAnim = ObjectAnimator.ofFloat(newBall, "y", endY,
                    startY);
            bounceBackAnim.setDuration(duration);
            bounceBackAnim.setInterpolator(new DecelerateInterpolator());
            // Sequence the down/squash&stretch/up animations
            AnimatorSet bouncer = new AnimatorSet();
            bouncer.play(bounceAnim).before(squashAnim1);
            bouncer.play(squashAnim1).with(squashAnim2);
            bouncer.play(squashAnim1).with(stretchAnim1);
            bouncer.play(squashAnim1).with(stretchAnim2);
            bouncer.play(bounceBackAnim).after(stretchAnim2);

            // Fading animation - remove the ball when the animation is done
            ValueAnimator fadeAnim = ObjectAnimator.ofFloat(newBall, "alpha", 1f, 0f);
            fadeAnim.setDuration(250);
            fadeAnim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    //noinspection SuspiciousMethodCalls
                    balls.remove(((ObjectAnimator)animation).getTarget());

                }
            });

            // Sequence the two animations to play one after the other
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(bouncer).before(fadeAnim);

            // Start the animation
            animatorSet.start();

            return true;
        }

        /**
         * Add a ball to the list of ArrayList<ShapeHolder> balls at location (x, y).
         *
         * First create a ShapeDrawable of an OvalShape .resize()'d to 50px x 50px, create
         * a ShapeHolder containing this ShapeDrawable and configure that ShapeHolder to locate
         * it at (x, y), create a Paint with a random color, create a RadialGradient and install
         * it in the Paint, then setPaint() the ShapeHolder with this paint. When done, add the
         * new ball to the balls list, and return the ShapeHolder to the caller.
         *
         * @param x x coordinate of the new ball
         * @param y y coordinate of the new ball
         * @return a ShapeHolder containing a ball located at (x, y)
         */
        private ShapeHolder addBall(float x, float y) {
            OvalShape circle = new OvalShape();
            circle.resize(50f, 50f);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            shapeHolder.setX(x - 25f);
            shapeHolder.setY(y - 25f);
            int red = (int)(Math.random() * 255);
            int green = (int)(Math.random() * 255);
            int blue = (int)(Math.random() * 255);
            int color = 0xff000000 | red << 16 | green << 8 | blue;
            Paint paint = drawable.getPaint(); //new Paint(Paint.ANTI_ALIAS_FLAG);
            int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
            RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                    50f, color, darkColor, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
            shapeHolder.setPaint(paint);
            balls.add(shapeHolder);
            return shapeHolder;
        }

        /**
         * Does the drawing of each of of the balls every time the canvas in invalidated. It does
         * this by first saving the current matrix and clip onto a private stack using canvas.save(),
         * then it moves the canvas to the location of the current ball, calls the .draw(Canvas) of
         * the ball's shape to draw it, and then restores the canvas from the stack (repeat for each
         * ball in ArrayList<ShapeHolder> balls.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            for (int i = 0; i < balls.size(); ++i) {
                ShapeHolder shapeHolder = balls.get(i);
                canvas.save();
                canvas.translate(shapeHolder.getX(), shapeHolder.getY());
                shapeHolder.getShape().draw(canvas);
                canvas.restore();
            }
        }
    }
}