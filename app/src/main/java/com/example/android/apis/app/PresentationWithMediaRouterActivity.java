/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.example.android.apis.app;

import com.example.android.apis.R;
import com.example.android.apis.graphics.CubeRenderer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.MediaRouteActionProvider;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaRouter;
import android.media.MediaRouter.RouteInfo;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;


/**
 * <h3>Presentation Activity</h3>
 *
 * <p>
 * This demonstrates how to create an activity that shows some content
 * on a secondary display using a {@link Presentation}.
 * </p><p>
 * The activity uses the {@link MediaRouter} API to automatically detect when
 * a presentation display is available and to allow the user to control the
 * media routes using a menu item.  When a presentation display is available,
 * we stop showing content in the main activity and instead open up a
 * {@link Presentation} on the preferred presentation display.  When a presentation
 * display is removed, we revert to showing content in the main activity.
 * We also write information about displays and display-related events to
 * the Android log which you can read using <b>adb logcat</b>.
 * </p><p>
 * You can try this out using an HDMI or Wifi display or by using the
 * "Simulate secondary displays" feature in Development Settings to create a few
 * simulated secondary displays.  Each display will appear in the list along with a
 * checkbox to show a presentation on that display.
 * </p><p>
 * See also the {@link PresentationActivity} sample which
 * uses the low-level display manager to enumerate displays and to show multiple
 * simultaneous presentations on different displays.
 * </p>
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
public class PresentationWithMediaRouterActivity extends Activity {
    private final String TAG = "PresentationWMRActivity"; // TAG for logging

    private MediaRouter mMediaRouter; // MediaRouter we will use control the routing of media channels
    private DemoPresentation mPresentation; // The presentation to show on the secondary display.
    private GLSurfaceView mSurfaceView; // GLSurfaceView in our layout for our openGL demo
    private TextView mInfoTextView; // text view where we will show information about what's happening.
    private boolean mPaused; // Flag set in onPause to true, set to false in onResume so updateContents
                             // knows to call onPause or onResume for the GLSurfaceView mSurfaceView.

    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     *
     * First we call through to our super's implementation of onCreate. Then we fetch the handle to
     * the MediaRouter system-level service and save it in our field MediaRouter mMediaRouter. We
     * set our content view to our layout file R.layout.presentation_with_media_router_activity.
     * We locate the GLSurfaceView in our layout (R.id.surface_view) and save it in our field
     * GLSurfaceView mSurfaceView, then set the renderer associated with this view to a new instance
     * of CubeRenderer, which also starts the thread that will call the renderer, which in turn
     * causes the openGL rendering to start. Finally we locate the TextView R.id.info and save it
     * in TextView mInfoTextView for later use.
     *
     * @param savedInstanceState always null since onSaveInstanceState is not overridden
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        // Get the media router service.
        mMediaRouter = (MediaRouter)getSystemService(Context.MEDIA_ROUTER_SERVICE);

        // See assets/res/any/layout/presentation_with_media_router_activity.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.presentation_with_media_router_activity);

        // Set up the surface view for visual interest.
        mSurfaceView = (GLSurfaceView)findViewById(R.id.surface_view);
        mSurfaceView.setRenderer(new CubeRenderer(false));

        // Get a text view where we will show information about what's happening.
        mInfoTextView = (TextView)findViewById(R.id.info);
    }

    /**
     * Called after {@link #onRestoreInstanceState}, {@link #onRestart}, or
     * {@link #onPause}, for your activity to start interacting with the user.
     *
     * First we call through to our super's implementation of onResume, then we add the callback
     * MediaRouter.SimpleCallback mMediaRouterCallback to listen to events relating to
     * ROUTE_TYPE_LIVE_VIDEO to our MediaRouter mMediaRouter. Finally we reset the mPaused flag
     * to false and call updatePresentation to update the routing and the presentation contents.
     */
    @Override
    protected void onResume() {
        // Be sure to call the super class.
        super.onResume();

        // Listen for changes to media routes.
        mMediaRouter.addCallback(MediaRouter.ROUTE_TYPE_LIVE_VIDEO, mMediaRouterCallback);

        // Update the presentation based on the currently selected route.
        mPaused = false;
        updatePresentation();
    }

    /**
     * Called as part of the activity lifecycle when an activity is going into
     * the background, but has not (yet) been killed.  The counterpart to
     * {@link #onResume}.
     *
     * First we call through to the super's implementation of onPause, then we remove our media
     * routing callback from MediaRouter mMediaRouter, set the mPaused flag to true and call
     * updateContents to pause the rendering by calling onPause on the SurfaceView that is being
     * rendered to.
     */
    @Override
    protected void onPause() {
        // Be sure to call the super class.
        super.onPause();

        // Stop listening for changes to media routes.
        mMediaRouter.removeCallback(mMediaRouterCallback);

        // Pause rendering.
        mPaused = true;
        updateContents();
    }

    /**
     * Called when you are no longer visible to the user.  You will next
     * receive either {@link #onRestart}, {@link #onDestroy}, or nothing,
     * depending on later user activity.
     *
     * First we call through to our super's implementation of onStop. Then if we are rendering
     * DemoPresentation mPresentation to a secondary display, we dismiss mPresentation, removing
     * it from the screen and set mPresentation to null.
     */
    @Override
    protected void onStop() {
        // Be sure to call the super class.
        super.onStop();

        // Dismiss the presentation when the activity is not visible.
        if (mPresentation != null) {
            Log.i(TAG, "Dismiss presentation activity invisible.");
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    /**
     * First we call through to our super's implementation of onCreateOptionsMenu. Then we inflate
     * our menu layout into the Menu menu parameter. We locate the menu item R.id.menu_media_route
     * and save it in MenuItem mediaRouteMenuItem, get the ActionProvider for mediaRouteMenuItem
     * (android:actionProviderClass="android.app.MediaRouteActionProvider") and save it in
     * MediaRouteActionProvider mediaRouteActionProvider, then set the types of routes that will
     * be shown in the media route chooser dialog launched by this button to ROUTE_TYPE_LIVE_VIDEO
     * (Route type flag for live video). Finally we return true to show the menu.
     *
     * @param menu The options menu in which we placed our items.
     * @return true to show the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Be sure to call the super class.
        super.onCreateOptionsMenu(menu);

        // Inflate the menu and configure the media router action provider.
        getMenuInflater().inflate(R.menu.presentation_with_media_router_menu, menu);

        MenuItem mediaRouteMenuItem = menu.findItem(R.id.menu_media_route);
        MediaRouteActionProvider mediaRouteActionProvider =
                (MediaRouteActionProvider)mediaRouteMenuItem.getActionProvider();
        mediaRouteActionProvider.setRouteTypes(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

        // Return true to show the menu.
        return true;
    }

    /**
     * Update the routing for the demonstration. First we retrieve the the currently selected route
     * for ROUTE_TYPE_LIVE_VIDEO to MediaRouter.RouteInfo route. Then if the route is not null, we
     * set Display presentationDisplay to the external Display that is being used by the application,
     * otherwise we set presentationDisplay to null. If the display has changed (mPresentation is
     * not null, and the display used by mPresentation is not the same as Display presentationDisplay
     * we dismiss the old mPresentation and set mPresentation to null. Then if mPresentation is null
     * and there is an external display available in presentationDisplay we create a new
     * DemoPresentation mPresentation for presentationDisplay, set the DialogInterface.OnDismissListener
     * of mPresentation to mOnDismissListener, and wrapped in a try block intended to catch
     * WindowManager.InvalidDisplayException we instruct mPresentation to start the presentation and
     * display it on the external display. If mPresentation.show() fails to connect to the Display
     * it will throw WindowManager.InvalidDisplayException and we set mPresentation to null. Finally
     * we call updateContents which will display our rotating cubes either in the main activity or
     * on the external display and will display some text explaining what is happening.
     */
    private void updatePresentation() {
        // Get the current route and its presentation display.
        MediaRouter.RouteInfo route = mMediaRouter.getSelectedRoute(
                MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
        Display presentationDisplay = route != null ? route.getPresentationDisplay() : null;

        // Dismiss the current presentation if the display has changed.
        if (mPresentation != null && mPresentation.getDisplay() != presentationDisplay) {
            Log.i(TAG, "Dismissing presentation because the current route no longer "
                    + "has a presentation display.");
            mPresentation.dismiss();
            mPresentation = null;
        }

        // Show a new presentation if needed.
        if (mPresentation == null && presentationDisplay != null) {
            Log.i(TAG, "Showing presentation on display: " + presentationDisplay);
            mPresentation = new DemoPresentation(this, presentationDisplay);
            mPresentation.setOnDismissListener(mOnDismissListener);
            try {
                mPresentation.show();
            } catch (WindowManager.InvalidDisplayException ex) {
                Log.w(TAG, "Couldn't show presentation!  Display was removed in "
                        + "the meantime.", ex);
                mPresentation = null;
            }
        }

        // Update the contents playing in this activity.
        updateContents();
    }

    /**
     * Show either the content in the main activity or the content in the presentation along with
     * some descriptive text about what is happening. If mPresentation is not null we are meant to
     * display on an external display, so we set the text in the TextView mInfoTextView to a
     * formatted string: "Now playing on secondary display" with the name of the external display
     * added to it, we set the visibility of the GLSurfaceView mSurfaceView in the main view to
     * invisible and call mSurfaceView's onPause callback. Then if the Activity has been paused
     * (mPaused == true) we call the onPause callback of the SurfaceView being used by
     * DemoPresentation mPresentation, otherwise we call its onResume callback. On the other hand
     * if mPresentation is null we are to display on the main display so we set the text in the
     * TextView mInfoTextView to a formatted string: "Now playing on main display" with the name of
     * the default display added to it, we set the visibility of the GLSurfaceView mSurfaceView in
     * the main view to VISIBLE, and if the Activity has been paused (mPaused == true) we call the
     * onPause callback of the SurfaceView mSurfaceView, otherwise we call its onResume callback.
     */
    private void updateContents() {
        // Show either the content in the main activity or the content in the presentation
        // along with some descriptive text about what is happening.
        if (mPresentation != null) {
            mInfoTextView.setText(getResources().getString(
                    R.string.presentation_with_media_router_now_playing_remotely,
                    mPresentation.getDisplay().getName()));
            mSurfaceView.setVisibility(View.INVISIBLE);
            mSurfaceView.onPause();
            if (mPaused) {
                mPresentation.getSurfaceView().onPause();
            } else {
                mPresentation.getSurfaceView().onResume();
            }
        } else {
            mInfoTextView.setText(getResources().getString(
                    R.string.presentation_with_media_router_now_playing_locally,
                    getWindowManager().getDefaultDisplay().getName()));
            mSurfaceView.setVisibility(View.VISIBLE);
            if (mPaused) {
                mSurfaceView.onPause();
            } else {
                mSurfaceView.onResume();
            }
        }
    }

    /**
     * Interface for receiving events about media routing changes. All methods of this interface
     * will be called from the application's main thread. Added in onResume, removed in onPause.
     */
    private final MediaRouter.SimpleCallback mMediaRouterCallback =
            new MediaRouter.SimpleCallback() {

                /**
                 * Called when the supplied route becomes selected as the active route
                 * for the given route type. We simply call updatePresentation to update
                 * the routing for the presentation.
                 *
                 * @param router the MediaRouter reporting the event
                 * @param type Type flag set indicating the routes that have been selected
                 * @param info Route that has been selected for the given route types
                 */
                @Override
                public void onRouteSelected(MediaRouter router, int type, RouteInfo info) {
                    Log.d(TAG, "onRouteSelected: type=" + type + ", info=" + info);
                    updatePresentation();
                }

                /**
                 * Called when the supplied route becomes unselected as the active route
                 * for the given route type. We simply call updatePresentation to update
                 * the routing for the presentation.
                 *
                 * @param router the MediaRouter reporting the event
                 * @param type Type flag set indicating the routes that have been unselected
                 * @param info Route that has been unselected for the given route types
                 */
                @Override
                public void onRouteUnselected(MediaRouter router, int type, RouteInfo info) {
                    Log.d(TAG, "onRouteUnselected: type=" + type + ", info=" + info);
                    updatePresentation();
                }

                /**
                 * Called when a route's presentation display changes.
                 * <p>
                 * This method is called whenever the route's presentation display becomes
                 * available, is removes or has changes to some of its properties (such as its size).
                 * </p>
                 * We simply call updatePresentation to update the routing for the presentation.
                 *
                 * @param router the MediaRouter reporting the event
                 * @param info The route whose presentation display changed
                 */
                @Override
                public void onRoutePresentationDisplayChanged(MediaRouter router, RouteInfo info) {
                    Log.d(TAG, "onRoutePresentationDisplayChanged: info=" + info);
                    updatePresentation();
                }
            };

    /**
     * Listens for when presentations are dismissed. Used in updatePresentation()
     */
    private final DialogInterface.OnDismissListener mOnDismissListener =
            new DialogInterface.OnDismissListener() {
                /**
                 * This method will be invoked when the dialog is dismissed. We check to make sure
                 * the the dialog passed us is the current DemoPresentation mPresentation, and if so
                 * we set mPresentation to null and call updateContents to show the demo on the main
                 * display instead.
                 *
                 * @param dialog The dialog that was dismissed will be passed into the method.
                 */
                @Override
                public void onDismiss(DialogInterface dialog) {
                    if (dialog == mPresentation) {
                        Log.i(TAG, "Presentation was dismissed.");
                        mPresentation = null;
                        updateContents();
                    }
                }
            };

    /**
     * The presentation to show on the secondary display.
     * <p>
     * Note that this display may have different metrics from the display on which
     * the main activity is showing so we must be careful to use the presentation's
     * own {@link Context} whenever we load resources.
     * </p>
     */
    private final static class DemoPresentation extends Presentation {
        private GLSurfaceView mSurfaceView; // The GLSurfaceView in the presentation layout

        /**
         * Creates a new presentation that is attached to the specified display using the default theme.
         *
         * @param context The context of the application that is showing the presentation. The
         *                presentation will create its own context (see getContext()) based on
         *                this context and information about the associated display.
         * @param display The display to which the presentation should be attached.
         */
        @SuppressWarnings("WeakerAccess")
        public DemoPresentation(Context context, Display display) {
            super(context, display);
        }

        /**
         * Similar to {@link Activity#onCreate}, you should initialize your dialog
         * in this method, including calling {@link #setContentView}.
         *
         * We set our content view to our layout file R.layout.presentation_with_media_router_content,
         * locate the GLSurfaceView in that layout (R.id.surface_view) and save it in our field
         * GLSurfaceView mSurfaceView, then set the renderer of mSurfaceView to a new instance of
         * CubeRenderer which also starts the thread that will call the renderer, which in turn
         * causes the rendering to start.
         *
         * @param savedInstanceState always null since onSaveInstanceState is not overridden
         */
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // Be sure to call the super class.
            super.onCreate(savedInstanceState);

            // Get the resources for the context of the presentation.
            // Notice that we are getting the resources from the context of the presentation.
            //noinspection unused
            Resources r = getContext().getResources();

            // Inflate the layout.
            setContentView(R.layout.presentation_with_media_router_content);

            // Set up the surface view for visual interest.
            mSurfaceView = (GLSurfaceView)findViewById(R.id.surface_view);
            mSurfaceView.setRenderer(new CubeRenderer(false));
        }

        /**
         * Getter method for our field GLSurfaceView mSurfaceView, used in updateContents().
         *
         * @return contents of our field GLSurfaceView mSurfaceView
         */
        @SuppressWarnings("WeakerAccess")
        public GLSurfaceView getSurfaceView() {
            return mSurfaceView;
        }
    }
}

