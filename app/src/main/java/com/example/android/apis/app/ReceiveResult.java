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

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
import com.example.android.apis.R;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Shows how an activity can send data to its launching activity when done.
 * <p>This can be used, for example, to implement a dialog allowing the user to
pick an e-mail address or image -- the picking activity sends the selected
data back to the originating activity when done.</p>

<p>The example here is composed of two activities: ReceiveResult launches
the picking activity and receives its results; SendResult allows the user
to pick something and sends the selection back to its caller.  Implementing
this functionality involves the
{@link android.app.Activity#setResult setResult()} method for sending a
result and
{@link android.app.Activity#onActivityResult onActivityResult()} to
receive it.</p>

<h4>Demo</h4>
App/Activity/Receive Result
 
<h4>Source files</h4>
<table class="LinkTable">
        <tr>
            <td class="LinkColumn">src/com.example.android.apis/app/ReceiveResult.java</td>
            <td class="DescrColumn">Launches pick activity and receives its result</td>
        </tr>
        <tr>
            <td class="LinkColumn">src/com.example.android.apis/app/SendResult.java</td>
            <td class="DescrColumn">Allows user to pick an option and sends it back to its caller</td>
        </tr>
        <tr>
            <td class="LinkColumn">/res/any/layout/receive_result.xml</td>
            <td class="DescrColumn">Defines contents of the ReceiveResult screen</td>
        </tr>
        <tr>
            <td class="LinkColumn">/res/any/layout/send_result.xml</td>
            <td class="DescrColumn">Defines contents of the SendResult screen</td>
        </tr>
</table>

 */
public class ReceiveResult extends Activity {

    private TextView mResults; // TextView in our layout for displaying results

    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView setContentView()} to
     * describe what is to be displayed in the screen.
     *
     * First we call through to our super's implementation of onCreate. Then we set our content
     * view to our layout file R.layout.receive_result. Then we set our field TextView mResults
     * to the TextView in our layout for displaying results returned by the Activity SendResult
     * (R.id.results). We set the text in mResults to the current contents of the TextView using
     * the TextView.BufferType TextView.BufferType.EDITABLE so that the text buffer can be extended
     * as we add more text. Finally we locate the Button R.id.get and set the OnClickListener for
     * it to the OnClickListener mGetListener.
     *
     * @param savedInstanceState always null since onSaveInstanceState is not overridden
     */
    @Override
	protected void onCreate(Bundle savedInstanceState) {
        // Be sure to call the super class.
        super.onCreate(savedInstanceState);

        // See assets/res/any/layout/hello_world.xml for this
        // view layout definition, which is being set here as
        // the content of our screen.
        setContentView(R.layout.receive_result);

        // Retrieve the TextView widget that will display results.
        mResults = (TextView)findViewById(R.id.results);

        // This allows us to later extend the text buffer.
        mResults.setText(mResults.getText(), TextView.BufferType.EDITABLE);

        // Watch for button clicks.
        Button getButton = (Button)findViewById(R.id.get);
        getButton.setOnClickListener(mGetListener);
    }

    /**
     * This method is called when the sending activity has finished, with the
     * result it supplied.
     *
     * First we check if the requestCode matches the one used to launch the sending activity (GET_CODE)
     * and if not we do nothing. If it does match we create Editable text by casting the CharSequence
     * returned from mResults.getText() to Editable (we can do this since we set the text with the
     * option TextView.BufferType.EDITABLE). Then if the resultCode from the sending activity is
     * RESULT_CANCELED we append the string (cancelled) to Editable text, otherwise we append to
     * text the value of resultCode and the value of the action of the intent returned sandwiched
     * between other text. This results in the update of the text displayed in the TextView mResults.
     *
     * The result is something like this:
     *
     *     (okay -1) Corky!
     *     (okay -1) Violet!
     *
     * @param requestCode The original request code as given to
     *                    startActivity().
     * @param resultCode From sending activity as per setResult().
     * @param data From sending activity as per setResult().
     */
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // You can use the requestCode to select between multiple child
        // activities you may have started.  Here there is only one thing
        // we launch.
        if (requestCode == GET_CODE) {

            // We will be adding to our text.
            Editable text = (Editable)mResults.getText();

            // This is a standard resultCode that is sent back if the
            // activity doesn't supply an explicit result.  It will also
            // be returned if the activity failed to launch.
            if (resultCode == RESULT_CANCELED) {
                text.append("(cancelled)");

            // Our protocol with the sending activity is that it will send
            // text in 'data' as its result.
            } else {
                text.append("(okay ");
                text.append(Integer.toString(resultCode));
                text.append(") ");
                if (data != null) {
                    text.append(data.getAction());
                }
            }

            text.append("\n");
        }
    }

    // Definition of the one requestCode we use for receiving results.
    static final private int GET_CODE = 0;

    /**
     * Set as the OnClickListener for the Button "GET RESULT" is lauches the Activity SendResult
     * using startActivityForResult and the result intent will be handled in the callback
     * OnActivityResult
     */
    private OnClickListener mGetListener = new OnClickListener() {
        /**
         * Called when the Button "GET RESULT" (R.id.get) is clicked. First we create an Intent to
         * start the Activity SendResult, then we use that Intent to launch the Activity using
         * startActivityForResult with the requestCode GET_CODE. SendResult will call setResult
         * with an Intent containing the result of that Activity which we will receive in the
         * callback onActivityResult.
         *
         * @param v The View of the Button "GET RESULT" (R.id.get)
         */
        public void onClick(View v) {
            // Start the activity whose result we want to retrieve.  The
            // result will come back with request code GET_CODE.
            Intent intent = new Intent(ReceiveResult.this, SendResult.class);
            startActivityForResult(intent, GET_CODE);
        }
    };
}

