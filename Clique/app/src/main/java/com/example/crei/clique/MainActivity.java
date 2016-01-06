package com.example.crei.clique;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.app.ActionBar;
import android.content.Intent;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
@SuppressWarnings("ResourceAsColor")

public class MainActivity extends Activity implements BlankFragment.OnFragmentInteractionListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener { // GET BACK ARROW GONE ON MAIN SCREEN?
    PopupWindow pw;
    EditText edit;
    String name = "Anonymous";
    boolean privateOption = false;
    String organize = "";
    int selected = 0;
    int initialColor = -11568135;  // 0xff000fff;
    int drawColor = -11568135;
    int editTextRowCount = 0;
    BlankFragment frag;
    FragmentTransaction ft;
    boolean drawHappened = false;
    boolean drawModeActive = false;

    View roomView;
    View createView;
    String createRoomName;
    boolean locked;
    String password;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    LocationListener lol;
    String mLatitudeText;
    String mLongitudeText;
    //Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        createLocationRequest();
        Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
        TextView groups = (TextView) findViewById(R.id.textView);
        groups.setTypeface(type);
        TextView groups7 = (TextView) findViewById(R.id.textView7);
        groups7.setTypeface(type);
        TextView groups3 = (TextView) findViewById(R.id.textView3);
        groups3.setTypeface(type);
        Button groups4 = (Button) findViewById(R.id.createButton);
        groups4.setTypeface(type);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        Button colorbutton = (Button) findViewById(R.id.colourButton);
        colorbutton.setTypeface(type);
        GradientDrawable buttonBack = new GradientDrawable();
        buttonBack.setStroke(5, Color.BLACK);                        // Black border, can change thickness
        //noinspection ResourceType,ResourceType,ResourceType,ResourceType
        buttonBack.setColor(initialColor);
        buttonBack.setCornerRadius(5);                      // How round corners are
        colorbutton.setBackground(buttonBack);
        final EditText sname = (EditText) findViewById(R.id.screenName);
        sname.setTypeface(type);
        sname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) { // Might need this not here
                View newView;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    name = String.valueOf(sname.getText());
                    newView = findViewById(R.id.hello);
                    ((TextView) newView).setText("Your Screen Name: " + name);
                    //sname.setCompoundDrawables(null, null, ResourcesCompat.getDrawable(getResources(), R.drawable.greencheck, null), null);
                }
                return false;
            }
        });
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
            mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        System.out.println("Failed");
    }

    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("Suspended");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        //Locationlistener, or pendingintent
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, new com.google.android.gms.location.LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mLastLocation = location;
                updateUI();
            }
        });
    }

//    @Override
//    public void onProviderEnabled(String i) {
//
//    }
//
//    @Override
//    public void onProviderDisabled(String i) {
//
//    }
//
//    @Override
//    public void onStatusChanged(String lol, int i, Bundle meh) {
//
//    }

    private void updateUI() {
        mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
        mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
    }

    public boolean settingOnClick(MenuItem v) {   // this format for menus
        View view;
        switch(v.getItemId()) {
            case R.id.action_settings:   // Options. Choose what levels of connection to display. Size and display?
                LayoutInflater inflaterOption = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflaterOption.inflate(R.layout.options, null);
                Switch toggle = (Switch) view.findViewById(R.id.switch1);
                toggle.setChecked(privateOption);
                setContentView(view);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            privateOption = true;
                        } else {
                            privateOption = false;
                        }
                    }
                });
                Spinner spin = (Spinner) view.findViewById((R.id.spinner));
                spin.setSelection(selected);
                spin.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                        if (position == 0) {
                            selected = position;
                            organize = "Alphabetical";
                        } else if (position == 1) {
                            selected = position;
                            organize = "Most Populated";
                        } else {
                            selected = position;
                            organize = "Connection";
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parentView) {
                        // your code here
                    }
                });
                break;
            case R.id.action_settings2:   // Help message I guess
                LayoutInflater inflaterHelp = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflaterHelp.inflate(R.layout.help, null);
                setContentView(view);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                break;
            case R.id.action_settings3:   // About section. Version, copyrights and how I coded?
                LayoutInflater inflaterAbout = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                view = inflaterAbout.inflate(R.layout.about, null);
                setContentView(view);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }
        return true;
    }

    public void createClick(View v) {
        switch(v.getId()) {
            case R.id.createButton:    // NEW LAYOUT TO CREATE PUB OR PRIVATE ROOM THEN GO INTO IT
                EditText sname = (EditText) findViewById(R.id.screenName);
                name = String.valueOf(sname.getText());
                LayoutInflater inflaterCreate = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                createView = inflaterCreate.inflate(R.layout.roomcreate, null);
                Switch toggle = (Switch) createView.findViewById(R.id.privateSwitch);
                setContentView(createView);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        EditText passwordEdit = (EditText) createView.findViewById(R.id.passwordText);
                        ImageView pic = (ImageView) createView.findViewById(R.id.imageView);
                        if (isChecked) {
                            locked = true;
                            passwordEdit.setEnabled(true);
                            pic.setVisibility(View.GONE);
                        } else {
                            locked = false;
                            passwordEdit.setText("");
                            passwordEdit.setHint("Password");
                            passwordEdit.setEnabled(false);
                            pic.setVisibility(View.VISIBLE);
                        }
                    }
                });
                break;
            case R.id.createNow:     // Actually make the room. Watch out for createView errors
                EditText nameOfRoom = (EditText) createView.findViewById(R.id.roomName);
                createRoomName = String.valueOf(nameOfRoom.getText());
                EditText passwordRoom = (EditText) createView.findViewById(R.id.passwordText);
                password = String.valueOf(passwordRoom.getText());
                if(createRoomName.equals("")) {
                    createRoomName = "Chat room";
                }
                if(password.equals("")) {
                    locked = false;
                }
                // use createRoomName, password, and locked to create room
                // go to room, if password = blank make public and if room name blank just call it room
                System.out.println(createRoomName + " " + password + " " + locked);
                Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
                LayoutInflater inflaterRoom = (LayoutInflater)getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                roomView = inflaterRoom.inflate(R.layout.chatroom, null);
                final EditText et = (EditText) roomView.findViewById(R.id.drawSpot);
                et.setTypeface(type);
                if(name.equals("")) {
                    name = "Anonymous";
                }
                TextView username = (TextView) roomView.findViewById(R.id.personalizedName);
                username.setText(name);
                //noinspection ResourceType
                username.setTextColor(initialColor);
                username.setTypeface(type);
                Button cbutton = (Button) roomView.findViewById(R.id.button3);
                cbutton.setTypeface(type);
                setContentView(roomView);
                et.addTextChangedListener(new TextWatcher() {
                    private String text;
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        text = charSequence.toString();
                    }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }
                    @Override
                    public void afterTextChanged(Editable editable) {
                        int lineCount = et.getLineCount();
                        if (lineCount > 7) {
                            et.setText(text);
                        }
                    }
                });

                ImageButton eraseButton = (ImageButton) roomView.findViewById(R.id.eraseButton);
                GradientDrawable buttonBack = new GradientDrawable();
                buttonBack.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack.setColor(Color.GRAY);
                buttonBack.setCornerRadius(5);                      // How round corners are
                eraseButton.setBackground(buttonBack);

                Button textButton = (Button) roomView.findViewById(R.id.textButton);
                textButton.setTypeface(type);
                GradientDrawable buttonBack1 = new GradientDrawable();
                buttonBack1.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack1.setColor(Color.YELLOW);
                buttonBack1.setCornerRadius(5);                      // How round corners are
                textButton.setBackground(buttonBack1);

                ImageButton drawButton = (ImageButton) roomView.findViewById(R.id.drawButton);
                GradientDrawable buttonBack2 = new GradientDrawable();
                buttonBack2.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack2.setColor(Color.GRAY);
                buttonBack2.setCornerRadius(5);                      // How round corners are
                drawButton.setBackground(buttonBack2);

                ImageButton sendButton = (ImageButton) roomView.findViewById(R.id.sendButton);
                GradientDrawable buttonBack3 = new GradientDrawable();
                buttonBack3.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack3.setColor(Color.GRAY);
                buttonBack3.setCornerRadius(5);                      // How round corners are
                sendButton.setBackground(buttonBack3);
                break;
        }
    }

    public void colourChange(View v) {
        switch(v.getId()) {
            case R.id.colourButton:    // Show them more colours and save selected one for screen name
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, initialColor, new OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        initialColor = color;
                        Button colorbutton = (Button) findViewById(R.id.colourButton);
                        GradientDrawable buttonBack = new GradientDrawable();
                        buttonBack.setStroke(5, Color.BLACK);
                        buttonBack.setColor(initialColor);
                        buttonBack.setCornerRadius(5);
                        colorbutton.setBackground(buttonBack);
                        System.out.println(color);
                    }
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
                break;
        }
    }

    public void modeSelect(View v) {
        ImageButton eraseButton = (ImageButton) roomView.findViewById(R.id.eraseButton);
        Button textButton = (Button) roomView.findViewById(R.id.textButton);
        ImageButton drawButton = (ImageButton) roomView.findViewById(R.id.drawButton);
        switch (v.getId()) {
            case R.id.textButton:
                GradientDrawable buttonBack = new GradientDrawable();
                buttonBack.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack.setColor(Color.GRAY);
                buttonBack.setCornerRadius(5);                      // How round corners are
                eraseButton.setBackground(buttonBack);

                GradientDrawable buttonBack1 = new GradientDrawable();
                buttonBack1.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack1.setColor(Color.YELLOW);
                buttonBack1.setCornerRadius(5);                      // How round corners are
                textButton.setBackground(buttonBack1);

                GradientDrawable buttonBack2 = new GradientDrawable();
                buttonBack2.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack2.setColor(Color.GRAY);
                buttonBack2.setCornerRadius(5);                      // How round corners are
                drawButton.setBackground(buttonBack2);
                if(drawModeActive == true) {
                    frag.setEnabler(false);
                }
                drawModeActive = false;
                break;
            case R.id.drawButton:
                GradientDrawable buttonBack01 = new GradientDrawable();
                buttonBack01.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack01.setColor(Color.GRAY);
                buttonBack01.setCornerRadius(5);                      // How round corners are
                eraseButton.setBackground(buttonBack01);

                GradientDrawable buttonBack11 = new GradientDrawable();
                buttonBack11.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack11.setColor(Color.GRAY);
                buttonBack11.setCornerRadius(5);                      // How round corners are
                textButton.setBackground(buttonBack11);

                GradientDrawable buttonBack21 = new GradientDrawable();
                buttonBack21.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack21.setColor(Color.YELLOW);
                buttonBack21.setCornerRadius(5);                      // How round corners are
                drawButton.setBackground(buttonBack21);

                if(drawHappened == false) {
                    frag = new BlankFragment();
                    FragmentManager fm = getFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.viewThing, frag);
                    ft.commit();
                    drawHappened = true;
                }
                frag.setTheColor(getDrawColor());
                drawModeActive = true;
                frag.setEnabler(true);
                break;
            case R.id.eraseButton:
                GradientDrawable buttonBack02 = new GradientDrawable();
                buttonBack02.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack02.setColor(Color.YELLOW);
                buttonBack02.setCornerRadius(5);                      // How round corners are
                eraseButton.setBackground(buttonBack02);

                GradientDrawable buttonBack12 = new GradientDrawable();
                buttonBack12.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack12.setColor(Color.GRAY);
                buttonBack12.setCornerRadius(5);                      // How round corners are
                textButton.setBackground(buttonBack12);

                GradientDrawable buttonBack22 = new GradientDrawable();
                buttonBack22.setStroke(5, Color.BLACK);                        // Black border, can change thickness
                buttonBack22.setColor(Color.GRAY);
                buttonBack22.setCornerRadius(5);                      // How round corners are
                drawButton.setBackground(buttonBack22);
//                if(drawModeActive == true) {
//                    frag.setEnabler(false);
//                }
                if(drawHappened) {      // Maybe change colour and size of erase brush
                    frag.setTheColor(Color.WHITE);
                    drawModeActive = true;
                }
                break;
        }
    }

    public void drawColour(View v) {
        switch(v.getId()) {
            case R.id.button3:    // Show them more colours and save selected one for screen name
                AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, drawColor, new OnAmbilWarnaListener() {
                    @Override
                    public void onOk(AmbilWarnaDialog dialog, int color) {
                        drawColor = color;
                        Button colorbutton = (Button) roomView.findViewById(R.id.button3);
                        GradientDrawable buttonBack = new GradientDrawable();
                        buttonBack.setStroke(5, Color.BLACK);
                        buttonBack.setColor(color);
                        buttonBack.setCornerRadius(5);
                        colorbutton.setBackground(buttonBack);
                        if(drawHappened) {
                            frag.setTheColor(color);
                        }
                    }
                    @Override
                    public void onCancel(AmbilWarnaDialog dialog) {
                        // cancel was selected by the user
                    }
                });
                dialog.show();
                break;
        }
    }

    public void sendMessage(View v) {
        switch (v.getId()) {
            case R.id.sendButton:
                if(drawHappened == true) {
                    frag = new BlankFragment();
                    FragmentManager fm = getFragmentManager();
                    ft = fm.beginTransaction();
                    ft.replace(R.id.viewThing, frag);
                    ft.commit();
                    if(drawModeActive == false) {
                        drawHappened = false;
                    }
                    frag.setTheColor(getDrawColor());
                }
                EditText messageBox = (EditText) roomView.findViewById(R.id.drawSpot);

                // Take picture of message they want to send
                LinearLayout test = (LinearLayout) findViewById(R.id.viewThing);
                roomView.layout(0, 0, roomView.getWidth(), roomView.getHeight());
                roomView.setDrawingCacheEnabled(true);
                roomView.buildDrawingCache();
                int[] hi = new int[2];
                test.getLocationOnScreen(hi);
                int x = hi[0];
                int y = hi[1];
                System.out.println("x: " + x + " y: " + y);
                System.out.println("Height room " + roomView.getHeight());
                System.out.println("Width room " + roomView.getWidth());
                System.out.println("Height test " + test.getHeight());
                System.out.println("Width test " + test.getWidth());
                Bitmap bitmap = Bitmap.createBitmap(roomView.getWidth(), roomView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                Paint paint = new Paint();
                int height = bitmap.getHeight();
                canvas.drawBitmap(bitmap, 0, height, paint);
                roomView.draw(canvas);
                int statusHeight = getStatusBarHeight();
                // CHECK THE Y FORMULA
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap, x, y-(statusHeight*3), test.getWidth(), test.getHeight());
                bitmap.recycle();
                String filePath = "";
                if (bitmap != null) {
                    try {
                        filePath = Environment.getExternalStorageDirectory().toString();
                        OutputStream out = null;
                        File file = new File(filePath, "/webviewScreenShot.png");
                        out = new FileOutputStream(file);
                        bitmap2.compress(Bitmap.CompressFormat.PNG, 50, out);
                        out.flush();
                        out.close();
                        bitmap.recycle();
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("damn");
                    }
                }
                messageBox.setText("");

                // Show message on screen
                ScrollView scrollView = (ScrollView) findViewById(R.id.scrollViewChat);
                LinearLayout messageScreen = (LinearLayout) findViewById(R.id.messageSpot);
                InputStream is;
                try {
                    Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Lato-Regular.ttf");
                    TextView sentName = new TextView(this);
                    //noinspection ResourceType,ResourceType
                    sentName.setTextColor(initialColor);
                    sentName.setTypeface(type);
                    android.text.format.DateFormat df = new android.text.format.DateFormat();
                    sentName.setText(name + " @ " + df.format("hh:mm", new java.util.Date()));
                    messageScreen.addView(sentName);
                    is = new FileInputStream(filePath + "/webviewScreenShot.png");
                    Drawable icon = new BitmapDrawable(is);
                    ImageView imgView = new ImageView(this);
                    imgView.setImageDrawable(icon);
                    messageScreen.addView(imgView);
                    messageScreen.measure(messageScreen.getWidth(), messageScreen.getHeight());
                    messageScreen.layout(0, 0, messageScreen.getWidth(), messageScreen.getHeight());
                    //put line for separation?
                } catch(FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("RIP");
                }
                scrollView.fullScroll(View.FOCUS_DOWN);      // scrollView.smoothScrollTo(0,0);

                // Delete picture
                File file = new File(filePath + "/webviewScreenShot.png");
                boolean deleted = file.delete();
        }
    }

    public void popOnClick(View v) {  // THIS DOES NOTHING
        View newView;
        switch(v.getId()) {
            case R.id.submitBtn:    // Save text field values (and check if correct?)
                name=edit.getText().toString();
                newView = findViewById(R.id.hello);
                ((TextView) newView).setText("Your Screen Name: " + name);
                pw.dismiss();
                break;
            case R.id.cancelBtn:
                pw.dismiss();
                break;
        }
    }

    public int getDrawColor() {
        return drawColor;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if(resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        if (id==android.R.id.activity_main) {
//            finish();
//        }
//
//        return super.onOptionsItemSelected(item);

        //Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
        //startActivityForResult(myIntent, 0);
        setContentView(R.layout.activity_main);
        final EditText sname = (EditText) findViewById(R.id.screenName);
        sname.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) { // Might need this not here
                View newView;
                if (i == EditorInfo.IME_ACTION_DONE) {
                    name = String.valueOf(sname.getText());
                    newView = findViewById(R.id.hello);
                    ((TextView) newView).setText("Your Screen Name: " + name);
                }
                return false;
            }
        });
        sname.setText(name);                                           // Name in edittext
        Button colorbutton = (Button) findViewById(R.id.colourButton);
        GradientDrawable buttonBack = new GradientDrawable();
        buttonBack.setStroke(5, Color.BLACK);                        // Black border, can change thickness
        //noinspection ResourceType,ResourceType
        buttonBack.setColor(initialColor);                    // Set colour to what they chose
        buttonBack.setCornerRadius(5);                      // How round corners are
        colorbutton.setBackground(buttonBack);
        getActionBar().setDisplayHomeAsUpEnabled(false);
        return true;
    }
}
