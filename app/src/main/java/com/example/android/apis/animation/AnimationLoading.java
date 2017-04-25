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
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.graphics.Color;
import com.example.android.apis.R;

import java.util.ArrayList;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Loads animations from Xml files: R.animator.object_animator (animates ball[0] "y"
 * from 0 to 200, and reverses), R.animator.animator (animates ball[1] alpha from 1 to
 * 0 and reverses), R.animator.animator_set (an animator set which animates ball[2]'s
 * "x" from 0 to 200, and "y" from 0 to 400), R.animator.color_animator (an animator
 * which animates ball[3]'s color from #0f0 to #00ffff), R.animator.object_animator_pvh
 * (an animator which animates ball[4]'s "x" from 0 to 400, and "y" from 0 to 200 using
 * propertyValuesHolder's), R.animator.object_animator_pvh_kf (uses propertyValuesHolder
 * to hold keyframe specs for x and y and uses the default linear interpolator on balls[5]),
 * R.animator.value_animator_pvh_kf (uses propertyValuesHolder to hold keyframe specs
 * for a value which balls[6] uses in an AnimatorUpdateListener for an alpha animation),
 * and R.animator.object_animator_pvh_kf_interpolated (the animation used for balls[7] has an
 * accelerate interpolator applied on each keyframe interval instead of the default used on
 * balls[5], As these two animations use the exact same path, the effect of the per-keyframe
 * interpolator has been made obvious.)
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class AnimationLoading extends Activity {

    @SuppressWarnings("unused")
    private static final int DURATION = 1500;

    /** Called when the activity is first created. */
    /**
     * Set the content view to R.layout.animation_loading, finds the LinearLayout container
     * which we will use for our View (R.id.container), creates an instance of MyAnimationView
     * animView and .addView's it to container. Finds the RUN Button (R.id.startButton) and sets
     * its OnClickListener to start the animation of animView.
     *
     * @param savedInstanceState always null since onSaveInstanceState is not overridden
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_loading);
        LinearLayout container = (LinearLayout) findViewById(R.id.container);
        final MyAnimationView animView = new MyAnimationView(this);
        container.addView(animView);

        Button starter = (Button) findViewById(R.id.startButton);
        starter.setOnClickListener(new View.OnClickListener() {
            /**
             * Start the MyAnimationView animView animation running
             *
             * @param v Button View which was clicked
             */
            @Override
            public void onClick(View v) {
                animView.startAnimation();
            }
        });
    }

    /**
     * This is the custom View which contains our animation demonstration
     */
    public class MyAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

        private static final float BALL_SIZE = 100f;

        public final ArrayList<ShapeHolder> balls = new ArrayList<>();
        Animator animation = null;

        /**
         * Creates the eight balls which are in our View. (Two of the balls (5 and 7) start in the
         * same place)
         *
         * @param context The Context the view is running in, through which it can access the
         *                current theme, resources, etc.
         */
        public MyAnimationView(Context context) {
            super(context);
            addBall(50, 50);
            addBall(200, 50);
            addBall(350, 50);
            addBall(500, 50, Color.GREEN);
            addBall(650, 50);
            addBall(800, 50);
            addBall(950, 50);
            addBall(800, 50, Color.YELLOW);
        }

        /**
         * Loads, creates and configures the Animator animation used for the 8 balls. If this is
         * the first time it is called (animation == null) it creates animators for the balls as
         * follows:
         *
         * balls[0] (50,50) Uses an ObjectAnimator anim created by loading the animation from
         *          the file R.animator.object_animator which animates "y" from the starting point
         *          to 200 with a repeat count of 1 and a repeatMode of "reverse", uses "this" as
         *          the UpdateListener which causes our classes override of onAnimationUpdate to
         *          be called which invalidates the View and sets the "y" value of the ShapeHolder
         *          holding balls[0] to the current value of the animation.
         * balls[1] (200,50) Uses a ValueAnimator fader which it creates by loading the file
         *          R.animator.animator which animates a value from 1 to 0 with a repeat count
         *          of 1 and a repeatMode of "reverse", and sets the UpdateListener to an
         *          AnimatorUpdateListener which sets the alpha of the ShapeHolder holding
         *          balls[1] to the current value of the animation (relying on the call to
         *          invalidate() for balls[0] to trigger a re-draw of the View.)
         * balls[2] (350,50) Uses an AnimatorSet seq which it creates by loading the file
         *          R.animator.animator_set which creates two objectAnimator's to animate the "x"
         *          value from the current value to 200, and the "y" value from the current value
         *          to 400 with a repeat count of 1 and a repeatMode of "reverse"
         * balls[3] (500,50) Color.GREEN Uses an ObjectAnimator colorizer which it creates by
         *          loading the file R.animator.color_animator which animates the value "color"
         *          of the ShapeHolder holding balls[3] from "#0f0" to "#00ffff" with a repeat
         *          count of 1 and a repeatMode of "reverse"
         * balls[4] (650,50) Use an ObjectAnimator animPvh which it loads from the file
         *          R.animator.object_animator_pvh which animates "x" from 0 to 400, and "y"
         *          from 0 to 200 using propertyValuesHolder's
         * balls[5] (800,50) Uses an ObjectAnimator animPvhKf which it creates by loading the file
         *          R.animator.object_animator_pvh_kf which uses propertyValuesHolder to hold
         *          keyframe specs for x and y and uses the default linear interpolator
         * balls[6] (950,50) Uses a ValueAnimator faderKf which it loads from the file
         *          R.animator.value_animator_pvh_kf which uses propertyValuesHolder to hold
         *          keyframe specs for a value, it then sets the UpdateListener to an
         *          AnimatorUpdateListener which sets the alpha of the ShapeHolder holding balls[6]
         *          to the current animated value.
         * balls[7] (800,50) Color.YELLOW Uses an ObjectAnimator animPvhKfInterpolated which
         *          it loads from R.animator.object_animator_pvh_kf_interpolated which uses
         *          propertyValuesHolder's to hold keyframe specs for "x" and "y" and has an
         *          accelerate interpolator applied on each keyframe interval. In comparison,
         *          the animation defined in R.anim.object_animator_pvh_kf for balls[5] uses
         *          the default linear interpolator throughout the animation. As these two
         *          animations use the exact same path, the effect of the per-keyframe interpolator
         *          has been made obvious.
         *
         * It then creates the AnimatorSet animation configures it to playTogether the
         * 8 Animator's created for the 8 balls.
         */
        private void createAnimation() {
            Context appContext = AnimationLoading.this;

            if (animation == null) {
                ObjectAnimator anim = (ObjectAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.object_animator);
                anim.addUpdateListener(this);
                anim.setTarget(balls.get(0));

                ValueAnimator fader = (ValueAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.animator);
                fader.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        balls.get(1).setAlpha((Float) animation.getAnimatedValue());
                    }
                });

                AnimatorSet seq =
                        (AnimatorSet) AnimatorInflater.loadAnimator(appContext,
                        R.animator.animator_set);
                seq.setTarget(balls.get(2));

                ObjectAnimator colorizer = (ObjectAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.color_animator);
                colorizer.setTarget(balls.get(3));

                ObjectAnimator animPvh = (ObjectAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.object_animator_pvh);
                animPvh.setTarget(balls.get(4));


                ObjectAnimator animPvhKf = (ObjectAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.object_animator_pvh_kf);
                animPvhKf.setTarget(balls.get(5));

                ValueAnimator faderKf = (ValueAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.value_animator_pvh_kf);
                faderKf.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        balls.get(6).setAlpha((Float) animation.getAnimatedValue());
                    }
                });

                // This animation has an accelerate interpolator applied on each
                // keyframe interval. In comparison, the animation defined in
                // R.anim.object_animator_pvh_kf uses the default linear interpolator
                // throughout the animation. As these two animations use the
                // exact same path, the effect of the per-keyframe interpolator
                // has been made obvious.
                ObjectAnimator animPvhKfInterpolated = (ObjectAnimator) AnimatorInflater.
                        loadAnimator(appContext, R.animator.object_animator_pvh_kf_interpolated);
                animPvhKfInterpolated.setTarget(balls.get(7));

                animation = new AnimatorSet();
                ((AnimatorSet) animation).playTogether(anim, fader, seq, colorizer, animPvh,
                        animPvhKf, faderKf, animPvhKfInterpolated);

            }
        }

        /**
         * Calls createAnimation() to create the AnimatorSet animation (if it does not exist yet)
         * then calls animation.start() to start the animation running. Called from the
         * onClickListener for the "RUN" button.
         */
        public void startAnimation() {
            createAnimation();
            animation.start();
        }

        /**
         * Creates a ball in a ShapeHolder. Creates an OvalShape circle, resize's it to be a 100x100
         * circle, creates a ShapeDrawable drawable from "circle", creates a ShapeHolder shapeHolder
         * containing "drawable", and sets the "x" and "y" coordinates of "shapeHolder" to the (x,y)
         * arguments and returns "shapeHolder" to the caller.
         *
         * @param x x coordinate for ball
         * @param y y coordinate for ball
         * @return ShapeHolder containing ball at (x, y)
         */
        private ShapeHolder createBall(float x, float y) {
            OvalShape circle = new OvalShape();
            circle.resize(BALL_SIZE, BALL_SIZE);
            ShapeDrawable drawable = new ShapeDrawable(circle);
            ShapeHolder shapeHolder = new ShapeHolder(drawable);
            shapeHolder.setX(x);
            shapeHolder.setY(y);
            return shapeHolder;
        }

        /**
         * Adds a new ball containing ShapeHolder to ArrayList<ShapeHolder> balls at location (x,y),
         * and with Color color. It does this by creating a ball containing ShapeHolder shapeHolder
         * at (x,y) by calling createBall(x, y), sets the color of "shapeHolder" to the "color"
         * argument and the .add()'ing "shapeHolder" to "balls"
         *
         * @param x x coordinate for ball
         * @param y y coordinate for ball
         * @param color color of ball
         */
        private void addBall(float x, float y, int color) {
            ShapeHolder shapeHolder = createBall(x, y);
            shapeHolder.setColor(color);
            balls.add(shapeHolder);
        }

        /**
         * Adds a new ball containing ShapeHolder to ArrayList<ShapeHolder> balls at location (x,y),
         * with a random color. It does this by creating a ball containing ShapeHolder shapeHolder
         * at (x,y) by calling createBall(x, y). The Paint instance "paint" is fetched from
         * "shapeHolder" and Random colors and a RadialGradient created and are used to set the
         * Shader used by the ShapeHolder's Paint instance, and the ShapeHolder is then add()'ed
         * to the balls List.
         *
         * @param x x coordinate for ball
         * @param y y coordinate for ball
         */
        private void addBall(float x, float y) {
            ShapeHolder shapeHolder = createBall(x, y);
            int red = (int)(100 + Math.random() * 155);
            int green = (int)(100 + Math.random() * 155);
            int blue = (int)(100 + Math.random() * 155);
            int color = 0xff000000 | red << 16 | green << 8 | blue;
            Paint paint = shapeHolder.getShape().getPaint();
            int darkColor = 0xff000000 | red/4 << 16 | green/4 << 8 | blue/4;
            RadialGradient gradient = new RadialGradient(37.5f, 12.5f,
                    50f, color, darkColor, Shader.TileMode.CLAMP);
            paint.setShader(gradient);
            balls.add(shapeHolder);
        }

        /**
         * Called when the View needs to draw itself. For each of the ShapeHolder ball's in the
         * ArrayList<ShapeHolder> balls the canvas has a translation to the current (x,y) ball
         * location pre-concatenated to it (the x,y coordinates are fetched from the ShapeHolder),
         * the Shape contained in the ShapeHolder is then fetched and instructed to draw itself.
         * The canvas is then restored to its previous state by pre-concatenating a translation
         * that is the inverse of the previous one that moved the canvas to the ball's (x,y)
         * location.
         *
         * @param canvas the canvas on which the background will be drawn
         */
        @Override
        protected void onDraw(Canvas canvas) {
            for (ShapeHolder ball : balls) {
                canvas.translate(ball.getX(), ball.getY());
                ball.getShape().draw(canvas);
                canvas.translate(-ball.getX(), -ball.getY());
            }
        }

        /**
         * This callback is called to notify us of the occurrence of another frame of an animation,
         * and is called by the animation used for balls[0] because of the anim.addUpdateListener(this)
         * included in the creation of the animation. First we invalidate() the View so that onDraw()
         * will be called at some point in the future, then we fetch the ShapeHolder holding balls[0]
         * and set the y coordinate to the current value specified by the ValueAnimator animation.
         *
         * @param animation animation which has moved to a new frame
         */
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            invalidate();
            ShapeHolder ball = balls.get(0);
            ball.setY((Float)animation.getAnimatedValue());
        }
    }
}