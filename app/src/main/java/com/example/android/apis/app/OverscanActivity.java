/*
 * Copyright (C) 2013 The Android Open Source Project
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

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import android.app.Activity;
import android.os.Bundle;

import com.example.android.apis.R;

/**
 * <h3>Overscan Activity</h3>
 * 
 * <p>This demonstrates the how to write an activity that extends into the
 * overscan region.</p>
 *
 * Uses @android:style/Theme.Holo.NoActionBar.Overscan set in AndroidManifest, and
 * android:fitsSystemWindows="true" in its layout/overscan_activity.xml to place a
 * surrounding "white box" in the overscan region of the display, and displays a
 * picture in a FrameLayout offset to avoid the overscan region.
 */
public class OverscanActivity extends Activity {
    /**
     * Called when the activity is starting. First we call through to our super's implementation
     * of onCreate, and then we set our content view to our layout file R.layout.overscan_activity.
     * The entire demo is created by the xml.
     *
     * @param savedInstanceState always null since onSaveInstanceState is not overridden
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.overscan_activity);
    }
}
