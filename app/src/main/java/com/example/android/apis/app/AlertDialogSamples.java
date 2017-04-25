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

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.database.Cursor;
import android.provider.ContactsContract;

import com.example.android.apis.R;

/**
 * Example of how to use an {@link android.app.AlertDialog}.
 * <h3>AlertDialogSamples</h3>
 * <p/>
 * <p>This demonstrates the different ways the AlertDialog can be used.</p>
 * <p/>
 * <h4>Demo</h4>
 * App/Dialog/Alert Dialog
 * <p/>
 * <h4>Source files</h4>
 * <table class="LinkTable">
 * <tr>
 * <td >src/com.example.android.apis/app/AlertDialogSamples.java</td>
 * <td >The Alert Dialog Samples implementation</td>
 * </tr>
 * <tr>
 * <td >/res/any/layout/alert_dialog.xml</td>
 * <td >Defines contents of the screen</td>
 * </tr>
 * </table>
 */
@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class AlertDialogSamples extends Activity {
    private static final String TAG = "AlertDialogSamples"; // TAG used for logging

    private static final int DIALOG_YES_NO_MESSAGE = 1; // OK Cancel dialog with a message
    private static final int DIALOG_YES_NO_LONG_MESSAGE = 2; // OK Cancel dialog with a long message
    private static final int DIALOG_LIST = 3; // List dialog
    private static final int DIALOG_PROGRESS = 4; // Progress bar dialog
    private static final int DIALOG_SINGLE_CHOICE = 5; // Single choice list
    private static final int DIALOG_MULTIPLE_CHOICE = 6; // Repeat alarm
    private static final int DIALOG_TEXT_ENTRY = 7; // Text Entry dialog
    private static final int DIALOG_MULTIPLE_CHOICE_CURSOR = 8; // Send Call to VoiceMail
    private static final int DIALOG_YES_NO_ULTRA_LONG_MESSAGE = 9; // OK Cancel dialog with ultra long message
    private static final int DIALOG_YES_NO_OLD_SCHOOL_MESSAGE = 10; // OK Cancel dialog with traditional theme
    private static final int DIALOG_YES_NO_HOLO_LIGHT_MESSAGE = 11; // OK Cancel dialog with Holo Light theme
    private static final int DIALOG_YES_NO_DEFAULT_LIGHT_MESSAGE = 12; // OK Cancel dialog with DeviceDefault Light theme
    private static final int DIALOG_YES_NO_DEFAULT_DARK_MESSAGE = 13; // OK Cancel dialog with DeviceDefault theme
    private static final int DIALOG_PROGRESS_SPINNER = 14; // Progress spinner dialog

    private static final int MAX_PROGRESS = 100; // Value used for ProgressDialog.setMax call

    @SuppressWarnings("FieldCanBeLocal")
    private ProgressDialog mProgressSpinnerDialog; // ProgressDialog used for DIALOG_PROGRESS_SPINNER
    private ProgressDialog mProgressDialog; // ProgressDialog used for DIALOG_PROGRESS
    private int mProgress; // Count used by mProgressHandler for the two ProgressDialog's
    static private Handler mProgressHandler; // Handler which "moves" the ProgressDialog progress setting
    ViewGroup mRoot;

    /**
     * Callback for creating dialogs that are managed (saved and restored) for you by the activity.
     * We switch based on the id passed us which is the int parameter that the onClickListener for
     * the Button clicked in our layout gave when calling showDialog. In each case statement an
     * AlertDialog.Builder is used to build a Dialog which it returns to the caller. If the int id
     * is not among the ones defined above (DIALOG_*) we return null. See each case statement for
     * comments about what is done.
     *
     * @param id The id of the dialog.
     * @return The dialog. If you return null, the dialog will not be created.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_YES_NO_MESSAGE: // OK Cancel dialog with a message
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_two_buttons_title)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*OK*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Cancel*/}
                        })
                        .create();
            case DIALOG_YES_NO_LONG_MESSAGE: // OK Cancel dialog with a long message (Note .setMessage and .setNeutralButton)
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_two_buttons_msg)
                        .setMessage(R.string.alert_dialog_two_buttons2_msg)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*OK*/}
                        })
                        .setNeutralButton(R.string.alert_dialog_something, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Something*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Cancel*/}
                        })
                        .create();
            case DIALOG_YES_NO_ULTRA_LONG_MESSAGE: // OK Cancel dialog with ultra long message
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_two_buttons_msg)
                        .setMessage(R.string.alert_dialog_two_buttons2ultra_msg)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*OK*/}
                        })
                        .setNeutralButton(R.string.alert_dialog_something, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Something*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Cancel*/}
                        })
                        .create();
            case DIALOG_LIST: // List dialog
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.select_dialog)
                        .setItems(R.array.select_dialog_items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            /* User clicked so do some stuff */
                                String[] items = getResources().getStringArray(R.array.select_dialog_items);
                                new AlertDialog.Builder(AlertDialogSamples.this)
                                        .setMessage("You selected: " + which + " , " + items[which])
                                        .show();
                            }
                        })
                        .create();
            case DIALOG_PROGRESS: // Progress bar dialog
                mProgressDialog = new ProgressDialog(AlertDialogSamples.this);
                mProgressDialog.setTitle(R.string.select_dialog);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setMax(MAX_PROGRESS);
                mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                        getText(R.string.alert_dialog_hide), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Yes*/}
                        });
                mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                        getText(R.string.alert_dialog_cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*No*/}
                        });
                return mProgressDialog;
            case DIALOG_PROGRESS_SPINNER: // Progress spinner dialog
                mProgressSpinnerDialog = new ProgressDialog(AlertDialogSamples.this);
                mProgressSpinnerDialog.setTitle(R.string.select_dialog);
                mProgressSpinnerDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                return mProgressSpinnerDialog;
            case DIALOG_SINGLE_CHOICE: // Single choice list
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_single_choice)
                        .setSingleChoiceItems(R.array.select_dialog_items2, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*radio button clicked*/}
                        })
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Yes*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*No*/}
                        })
                        .create();
            case DIALOG_MULTIPLE_CHOICE: // Repeat alarm
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_multi_choice)
                        .setMultiChoiceItems(R.array.select_dialog_items3,
                                new boolean[]{false, true, false, true, false, false, false},
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {/*check box clicked*/}
                                })
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton) {/*Yes*/}
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton) {/*No*/}
                                })
                        .create();
            case DIALOG_MULTIPLE_CHOICE_CURSOR: // Send Call to VoiceMail
                String[] projection = new String[]{
                        ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,
                        ContactsContract.Contacts.SEND_TO_VOICEMAIL
                };
                Cursor cursor = managedQuery(ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_multi_choice_cursor)
                        .setMultiChoiceItems(cursor,
                                ContactsContract.Contacts.SEND_TO_VOICEMAIL,
                                ContactsContract.Contacts.DISPLAY_NAME,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int whichButton, boolean isChecked) {
                                        Toast.makeText(AlertDialogSamples.this,
                                                "Readonly Demo Only - Data will not be updated",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                })
                        .create();
            case DIALOG_TEXT_ENTRY: // Text Entry dialog
                // This example shows how to add a custom layout to an AlertDialog
                LayoutInflater factory = LayoutInflater.from(this);
                final View textEntryView = factory.inflate(R.layout.alert_dialog_text_entry, mRoot, false);
                return new AlertDialog.Builder(AlertDialogSamples.this)
                        .setTitle(R.string.alert_dialog_text_entry)
                        .setView(textEntryView)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*OK*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*cancel*/}
                        })
                        .create();
            case DIALOG_YES_NO_OLD_SCHOOL_MESSAGE: // OK Cancel dialog with traditional theme
                return new AlertDialog.Builder(AlertDialogSamples.this, AlertDialog.THEME_TRADITIONAL)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.alert_dialog_two_buttons_title)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*OK*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Cancel*/}
                        })
                        .create();
            case DIALOG_YES_NO_HOLO_LIGHT_MESSAGE: // OK Cancel dialog with Holo Light theme
                return new AlertDialog.Builder(AlertDialogSamples.this, AlertDialog.THEME_HOLO_LIGHT)
                        .setIconAttribute(android.R.attr.alertDialogIcon)
                        .setTitle(R.string.alert_dialog_two_buttons_title)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*Ok*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*cancel*/}
                        })
                        .create();
            case DIALOG_YES_NO_DEFAULT_LIGHT_MESSAGE: // OK Cancel dialog with DeviceDefault Light theme
                return new AlertDialog.Builder(AlertDialogSamples.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                        .setTitle(R.string.alert_dialog_two_buttons_title)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*ok*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*cancel*/}
                        })
                        .create();
            case DIALOG_YES_NO_DEFAULT_DARK_MESSAGE: // OK Cancel dialog with DeviceDefault theme
                return new AlertDialog.Builder(AlertDialogSamples.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle(R.string.alert_dialog_two_buttons_title)
                        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*ok*/}
                        })
                        .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {/*cancel*/}
                        })
                        .create();
        }
        return null;
    }

    /**
     * Initialization of the Activity after it is first created.  Must at least
     * call {@link android.app.Activity#setContentView(int)} to
     * describe what is to be displayed in the screen.
     */
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.alert_dialog);
        mRoot = (ViewGroup) findViewById(android.R.id.content); // In order to have a rootView for inflate
                
        /* Display a text message with yes/no buttons and handle each message as well as the cancel action */
        Button twoButtonsTitle = (Button) findViewById(R.id.two_buttons); // OK Cancel dialog with DeviceDefault theme
        twoButtonsTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_MESSAGE);
            }
        });
        
        /* Display a long text message with yes/no buttons and handle each message as well as the cancel action */
        Button twoButtons2Title = (Button) findViewById(R.id.two_buttons2); // OK Cancel dialog with a long message
        twoButtons2Title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_LONG_MESSAGE);
            }
        });
        
        
        /* Display an ultra long text message with yes/no buttons and handle each message as well as the cancel action */
        Button twoButtons2UltraTitle = (Button) findViewById(R.id.two_buttons2ultra); // OK Cancel dialog with ultra long message
        twoButtons2UltraTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_ULTRA_LONG_MESSAGE);
            }
        });


        /* Display a list of items */
        Button selectButton = (Button) findViewById(R.id.select_button); // List dialog
        selectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_LIST);
            }
        });
        
        /* Display a custom progress bar */
        Button progressButton = (Button) findViewById(R.id.progress_button); // Progress bar dialog
        progressButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_PROGRESS);
                mProgress = 0;
                mProgressDialog.setProgress(0);
                mProgressHandler.sendEmptyMessage(0);
            }
        });

        /* Display a custom progress bar */
        Button progressSpinnerButton = (Button) findViewById(R.id.progress_spinner_button); // Progress spinner dialog
        progressSpinnerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_PROGRESS_SPINNER);
            }
        });
        
        /* Display a radio button group */
        Button radioButton = (Button) findViewById(R.id.radio_button); // Single choice list
        radioButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_SINGLE_CHOICE);
            }
        });
        
        /* Display a list of checkboxes */
        Button checkBox = (Button) findViewById(R.id.checkbox_button); // Repeat alarm
        checkBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_MULTIPLE_CHOICE);
            }
        });
        
        /* Display a list of checkboxes, backed by a cursor */
        Button checkBox2 = (Button) findViewById(R.id.checkbox_button2); // Send Call to VoiceMail
        checkBox2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_MULTIPLE_CHOICE_CURSOR);
            }
        });

        /* Display a text entry dialog */
        Button textEntry = (Button) findViewById(R.id.text_entry_button); // Text Entry dialog
        textEntry.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TEXT_ENTRY);
            }
        });
        
        /* Two points, in the traditional theme */
        Button twoButtonsOldSchoolTitle = (Button) findViewById(R.id.two_buttons_old_school); // OK Cancel dialog with traditional theme
        twoButtonsOldSchoolTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_OLD_SCHOOL_MESSAGE);
            }
        });
        
        /* Two points, in the light holographic theme */
        Button twoButtonsHoloLightTitle = (Button) findViewById(R.id.two_buttons_holo_light); // OK Cancel dialog with Holo Light theme
        twoButtonsHoloLightTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_HOLO_LIGHT_MESSAGE);
            }
        });

        /* Two points, in the light default theme */
        Button twoButtonsDefaultLightTitle = (Button) findViewById(R.id.two_buttons_default_light); // OK Cancel dialog with DeviceDefault Light theme
        twoButtonsDefaultLightTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_DEFAULT_LIGHT_MESSAGE);
            }
        });

        /* Two points, in the dark default theme */
        Button twoButtonsDefaultDarkTitle = (Button) findViewById(R.id.two_buttons_default_dark); // OK Cancel dialog with DeviceDefault theme
        twoButtonsDefaultDarkTitle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_YES_NO_DEFAULT_DARK_MESSAGE);
            }
        });

        /**
         * Handler for background Thread which advances the Progress Bar in the Progress bar dialog.
         */
        mProgressHandler = new Handler() {
            /**
             * Subclasses must implement this to receive messages. We advance the counter whenever
             * we are called, whether by sendEmptyMessage(0) when the ProgressBar is started, or
             * by sendEmptyMessageDelayed(0, 100) which we call after incrementing the ProgressBar
             * to schedule the next increment for 100 milliseconds later
             *
             * @param msg Message sent us by sendEmptyMessage and sendEmptyMessageDelayed, the int
             *            value given them is Bundle'd under the key "what".
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (mProgress >= MAX_PROGRESS) {
                    mProgressDialog.dismiss();
                } else {
                    mProgress++;
                    mProgressDialog.incrementProgressBy(1);
                    mProgressHandler.sendEmptyMessageDelayed(0, 100);
                }
            }
        };
    }

    /**
     * Activities cannot draw during the period that their windows are animating in. In order
     * to know when it is safe to begin drawing they can override this method which will be
     * called when the entering animation has completed. Called after the Activity has finished
     * onCreate and the content view has been rendered, but not called for the Dialog's.
     */
    @Override
    public void onEnterAnimationComplete() {
        Log.i(TAG, "onEnterAnimationComplete");
    }
}
