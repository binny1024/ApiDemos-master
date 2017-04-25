/*
 * Copyright (C) 2007 The Android Open Source Project
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

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.android.apis.R;


/**
 * Example of how to use a custom title {@link android.view.Window#FEATURE_CUSTOM_TITLE}.
 * <h3>CustomTitle</h3>

<p>This demonstrates how a custom title can be used.</p>

<h4>Demo</h4>
App/Title/Custom Title
 
<h4>Source files</h4>
 * <table class="LinkTable">
 *         <tr>
 *             <td >src/com.example.android.apis/app/CustomTitle.java</td>
 *             <td >The Custom Title implementation</td>
 *         </tr>
 *         <tr>
 *             <td >/res/any/layout/custom_title.xml</td>
 *             <td >Defines contents of the screen</td>
 *         </tr>
 * </table> 
 */
public class CustomTitle extends Activity {
    /**
     * Called when the activity is starting. First we call through to our super's implementation
     * of onCreate. Then we request the window feature FEATURE_CUSTOM_TITLE, set our content view
     * to our layout file R.layout.custom_title, retrieve the current Window for the activity and
     * use it to set the value of the Window's FEATURE_CUSTOM_TITLE to the layout file we want it
     * to use: R.layout.custom_title_1. Next we locate the input and output View's of our layout:
     *
     * TextView leftText (R.id.left_text) the left TextView of our custom title
     * TextView rightText (R.id.right_text) the right TextView of our custom title
     * EditText leftTextEdit (R.id.left_text_edit) the EditText for entering a new left TextView
     *     for our custom title
     * EditText rightTextEdit (R.id.right_text_edit) the EditText for entering a new right TextView
     *     for our custom title
     * Button leftButton (R.id.left_text_button) the Button used to set "leftText" to the contents
     *     of "leftTextEdit"
     * Button rightButton (R.id.right_text_button) the Button used to set "rightText" to the contents
     *     of "rightTextEdit"
     *
     * Finally we set the onClickListener of "leftButton" to set the text of "leftText" to the
     * contents of "leftTextEdit" when pressed, and "rightButton" to set the text of "rightText"
     * to the contents of "rightTextEdit" when pressed.
     *
     * @param savedInstanceState always null since we not override onSaveInstanceState
     */
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.custom_title);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title_1);
        
        final TextView leftText = (TextView) findViewById(R.id.left_text);
        final TextView rightText = (TextView) findViewById(R.id.right_text);
        final EditText leftTextEdit = (EditText) findViewById(R.id.left_text_edit);
        final EditText rightTextEdit = (EditText) findViewById(R.id.right_text_edit);
        Button leftButton = (Button) findViewById(R.id.left_text_button);
        Button rightButton = (Button) findViewById(R.id.right_text_button);
        
        leftButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                leftText.setText(leftTextEdit.getText());
            }
        });
        rightButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                rightText.setText(rightTextEdit.getText());
            }
        });
    }
}
