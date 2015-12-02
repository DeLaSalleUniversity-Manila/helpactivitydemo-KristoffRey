package com.example.kristoffrey.reyeshelp2;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;


/*this is from Sir Melvin Kong Cabatuan's github, used for education purposes,
I tried to understand what each function does to the activity
 */
public class SettingsActivity extends SettingsExtension{
    SharedPreferences appSettings;
    static final int DATE_ID = 0;
    static final int PASSWORD_ID = 1;
    static final int CAMERA_REQUEST = 1;
    static final int GALLERY_REQUEST = 2;

    @Override   //when the activity is created
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        appSettings = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        initSettings();
    }

    private void initSettings() {
        initAvatar();
        initNameInp();
        initEmailInp();
        initPasswordInp();
        initDOBInp();
        //initGenderSpinner();
    }

    private void initAvatar(){
        ImageButton avatarButton = (ImageButton) findViewById(R.id.imageAvatar);

        if (appSettings.contains(PREFS_AVATAR)) {
            String avatarIdentifier = appSettings.getString(PREFS_AVATAR, "android.resource://drawable/avatar");        //identifies avatar
            Uri imageUri = Uri.parse(avatarIdentifier);
            avatarButton.setImageURI(imageUri);
        } else {
            avatarButton.setImageResource(R.drawable.avatar);
        }

        avatarButton.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                String strAvatarPrompt = "Choose a picture to use as your avatar!";
                Intent pickPhoto = new Intent(Intent.ACTION_PICK);
                pickPhoto.setType("image/*");
                startActivityForResult(
                        Intent.createChooser(pickPhoto, strAvatarPrompt),
                        GALLERY_REQUEST);
                return true;
            }
        });
    }

    private void initNameInp(){
        EditText nameText = (EditText) findViewById(R.id.editName);

        if (appSettings.contains(PREFS_NICKNAME)) {
            nameText.setText(appSettings.getString(PREFS_NICKNAME, "Guest"));
        } else {
            nameText.setText("Guest");
        }
    }

    private void initEmailInp(){
        EditText emailText = (EditText) findViewById(R.id.editEmail);
        if (appSettings.contains(PREFS_EMAIL)) {
            emailText.setText(appSettings.getString(PREFS_EMAIL, ""));
        } else {
            emailText.setText("firstName_lastName@gmail.com");
        }
    }

    private void initPasswordInp(){     //has another function that listens for "set password"
        TextView passwordInfo = (TextView) findViewById(R.id.textPassword);
        if (appSettings.contains(PREFS_PASSWORD)) {
            passwordInfo.setText(R.string.pwd_clear);
        } else {
            passwordInfo.setText(R.string.pwd_null);
        }

    }

    private void initDOBInp(){          //has another function that listens for input date
        TextView dobInfo = (TextView) findViewById(R.id.textDOB);
        if (appSettings.contains(PREFS_DOB)) {
            dobInfo.setText(DateFormat.format("MMMM dd, yyyy",
                    appSettings.getLong(PREFS_DOB, 0)));
        } else {
            dobInfo.setText(R.string.dob_null);
        }

    }

    private void initGenderSpinner(){
        // This will take the string array "genders" and populate the spinner with these values
        final Spinner spinner = (Spinner) findViewById(R.id.spinnerGender);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.genders, R.layout.activity_settings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (appSettings.contains(PREFS_GENDER)) {
            spinner.setSelection(appSettings.getInt(PREFS_GENDER,0));
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {    //can identify which item is selected
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                SharedPreferences.Editor editor = appSettings.edit();
                editor.putInt(PREFS_GENDER, selectedItemPosition);
                editor.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        EditText nameInp = (EditText)findViewById(R.id.editName);
        EditText emailInp = (EditText)findViewById(R.id.editEmail);
        String savedName = nameInp.getText().toString();
        String savedEmail = emailInp.getText().toString();

        SharedPreferences.Editor saveIt = appSettings.edit();
        saveIt.putString(PREFS_NICKNAME, savedName);
        saveIt.putString(PREFS_EMAIL, savedEmail);
        saveIt.commit();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case CAMERA_REQUEST:    //get avatar by taking a picture

                if (resultCode == Activity.RESULT_CANCELED) {   //the user canceled action
                }

                else if (resultCode == Activity.RESULT_OK) {    //will take a picture
                    Bitmap cameraPic = (Bitmap) data.getExtras().get("data");
                    if (cameraPic != null) {
                        try {
                            saveAvatar(cameraPic);
                            Toast toast = Toast.makeText(this, "Take Pic Success!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        catch (Exception e) {
                            Toast toast = Toast.makeText(this, "Take Pic Failed!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                break;

            case GALLERY_REQUEST:   //get avatar from database in the device

                if (resultCode == Activity.RESULT_CANCELED) {   //the user canceled action
                }

                else if (resultCode == Activity.RESULT_OK) {    //user picks an image
                    Uri photoUri = data.getData();
                    if (photoUri != null) {
                        try {
                            int maxLength = 150; //to scale image
                            Bitmap galleryPic = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), photoUri);
                            Bitmap scaledGalleryPic = createScaledBitmapKeepingAspectRatio(
                                    galleryPic, maxLength);
                            saveAvatar(scaledGalleryPic);
                            Toast toast = Toast.makeText(this, "Gallery Success!", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        catch (Exception e) {
                            Toast toast = Toast.makeText(this, "Gallery Failed!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }
                break;
        }
    }

    public void onLaunchCamera(View v) {
        String strAvatarPrompt = "Take your picture to store as your avatar!";
        Intent pictureIntent = new Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(
                Intent.createChooser(pictureIntent, strAvatarPrompt),
                CAMERA_REQUEST);
    }

    /**
     * Scale a Bitmap, keeping its aspect ratio
     *
     * @param bitmap  Bitmap to scale
     * @param maxSide Maximum length of either side
     * @return a new, scaled Bitmap
     */

    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide) {
        int orgHeight = bitmap.getHeight();
        int orgWidth = bitmap.getWidth();

        int scaledWidth = (orgWidth >= orgHeight) ? maxSide : (int) (maxSide * ((float) orgWidth / (float) orgHeight));
        int scaledHeight = (orgHeight >= orgWidth) ? maxSide : (int) (maxSide * ((float) orgHeight / (float) orgWidth));

        Bitmap scaledGalleryPic = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        return scaledGalleryPic;
    }

    private void saveAvatar(Bitmap avatar) {
        String avatarJPGName = "avatar.jpg";
        try {
            avatar.compress(Bitmap.CompressFormat.JPEG, 100,
                    openFileOutput(avatarJPGName, MODE_PRIVATE));
        }
        catch (Exception e) {
        }

        Uri imageUriToSaveCameraImageTo = Uri.fromFile(new File(
                SettingsActivity.this.getFilesDir(), avatarJPGName));

        SharedPreferences.Editor saveIt = appSettings.edit();
        saveIt.putString(PREFS_AVATAR, imageUriToSaveCameraImageTo.getPath());
        saveIt.commit();

        // Update the settings screen
        ImageButton avatarButton = (ImageButton) findViewById(R.id.imageAvatar);
        String strAvatarUri = appSettings.getString(PREFS_AVATAR, "android.resource://drawable/avatar");
        //"android.resource://com.cabatuan.settingsactivity/drawable/avatar"
        Uri imageUri = Uri.parse(strAvatarUri);
        avatarButton.setImageURI(null); // Workaround for refreshing an
        // ImageButton, which tries to cache the
        // previous image Uri. Passing null
        // effectively resets it.
        avatarButton.setImageURI(imageUri);
    }

    public void onSetPasswordButtonClick(View view) {
        showDialog(PASSWORD_ID);
    }

    public void onPickDateButtonClick(View view) {
       showDialog(DATE_ID);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_ID:
                final TextView dob = (TextView) findViewById(R.id.textDOB);
                Calendar now = Calendar.getInstance();

                DatePickerDialog dateDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                Time dateOfBirth = new Time();
                                dateOfBirth.set(dayOfMonth, monthOfYear, year);
                                long dtDob = dateOfBirth.toMillis(true);
                                dob.setText(DateFormat.format("MMMM dd, yyyy",
                                        dtDob));

                                SharedPreferences.Editor editor = appSettings.edit();
                                editor.putLong(PREFS_DOB, dtDob);
                                editor.commit();
                            }
                        }, now.get(Calendar.YEAR), now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH));
                return dateDialog;
            case PASSWORD_ID:
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View layout = inflater.inflate(R.layout.activity_settings,
                        (ViewGroup) findViewById(R.id.actsettings));
                final EditText p1 = (EditText) layout
                        .findViewById(R.id.editpwd1);
                final EditText p2 = (EditText) layout
                        .findViewById(R.id.editpwd2);
                final TextView error = (TextView) layout
                        .findViewById(R.id.textError);
                p2.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void afterTextChanged(Editable s) {
                        String strPass1 = p1.getText().toString();
                        String strPass2 = p2.getText().toString();
                        if (strPass1.equals(strPass2)) {
                            error.setText(R.string.pwdEq);
                        }
                        else {
                            error.setText(R.string.pwdNotEq);
                        }
                    }

                    // ... other required overrides need not be implemented
                    @Override
                    public void beforeTextChanged(CharSequence s, int start,
                                                  int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start,
                                              int before, int count) {
                    }
                });
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(layout);
                // Now configure the AlertDialog
                builder.setTitle(R.string.pwd_button_pressed);
                builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                                // We forcefully dismiss and remove the Dialog, so
                                // it cannot be used again (no cached info)
                                SettingsActivity.this
                                        .removeDialog(PASSWORD_ID);
                            }
                        });
                builder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                TextView passwordInfo = (TextView) findViewById(R.id.textPasswordInfo);
                                String strPassword1 = p1.getText().toString();
                                String strPassword2 = p2.getText().toString();
                                if (strPassword1.equals(strPassword2)) {
                                    SharedPreferences.Editor editor = appSettings.edit();
                                    editor.putString(PREFS_PASSWORD,
                                            strPassword1);
                                    editor.commit();
                                    passwordInfo.setText(R.string.pwd_clear);
                                }
                                else {
                                    // Log.d(DEBUG_TAG,
                                    //       "Passwords do not match. Not saving. Keeping old password (if set).");
                                }
                                // We forcefully dismiss and remove the Dialog, so
                                // it cannot be used again
                                SettingsActivity.this.removeDialog(PASSWORD_ID);
                            }
                        });
                // Create the AlertDialog and return it
                AlertDialog passwordDialog = builder.create();
                return passwordDialog;
        }
        return null;
    }

    @Override
    protected void onPrepareDialog(int id, Dialog dialog) {
        super.onPrepareDialog(id, dialog);
        switch (id) {
            case DATE_ID:
                // Handle any DatePickerDialog initialization here
                DatePickerDialog dateDialog = (DatePickerDialog) dialog;
                int iDay,
                        iMonth,
                        iYear;
                // Check for date of birth preference
                if (appSettings.contains(PREFS_DOB)) {
                    // Retrieve Birth date setting from preferences
                    long msBirthDate = appSettings.getLong(PREFS_DOB,
                            0);
                    Time dateOfBirth = new Time();
                    dateOfBirth.set(msBirthDate);

                    iDay = dateOfBirth.monthDay;
                    iMonth = dateOfBirth.month;
                    iYear = dateOfBirth.year;
                } else {
                    Calendar cal = Calendar.getInstance();
                    // Today's date fields
                    iDay = cal.get(Calendar.DAY_OF_MONTH);
                    iMonth = cal.get(Calendar.MONTH);
                    iYear = cal.get(Calendar.YEAR);
                }
                // Set the date in the DatePicker to the date of birth OR to the
                // current date
                dateDialog.updateDate(iYear, iMonth, iDay);
                return;
            case PASSWORD_ID:
                // Handle any Password Dialog initialization here
                // Since we don't want to show old password dialogs, just set new
                // ones, we need not do anything here
                // Because we are not "reusing" password dialogs once they have
                // finished, but removing them from
                // the Activity Dialog pool explicitly with removeDialog() and
                // recreating them as needed.
                return;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
        startActivity(new Intent(SettingsActivity.this,
                SettingsActivity.class));
    }
        /*
        Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.

        if (id == R.id.action_play) {
            startActivity(new Intent(SettingsActivity.this,
                    MainActivity.class));
        }

        if (id == R.id.action_help) {
            startActivity(new Intent(SettingsActivity.this,
                    HelpActivity.class));
        }
        */
        return super.onOptionsItemSelected(item);
    }


}
