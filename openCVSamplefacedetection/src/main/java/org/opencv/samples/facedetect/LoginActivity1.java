/*
<header>
Module Name          : LoginActivity
Date of Creation     : 10-04-2018
Author               : Mitansh Jain

Modification History : 11-04-2018 :- added validate()

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public void onCreate(Bundle)
                       public void login()
                       protected void onActivityResult(int, int, Intent)
                       public void onBackPressed()
                       public void onLoginSuccess()
                       public void onLoginFailed()
                       public boolean validate()

</header>
*/

package org.opencv.samples.facedetect;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity1 extends AppCompatActivity {

    private static final String TAG = "LoginActivity";      // for debugging purpose
    private static final int REQUEST_SIGNUP = 0;            // stores the values corresponding to whether user asks for signup or not
    SQLiteDatabase sqLiteDatabaseObj;                       // used for database handling
    SQLiteHelper sqLiteHelper;                              // variable for accessing the users' database.
    Cursor cursor;                                          // stores the values returned by database.
    private String TempPassword;                            // used for storing the password as provided by database

    @BindView(R.id.input_email) EditText _emailText;        // binding the textbox with name _emailText
    @BindView(R.id.input_password) EditText _passwordText;  // binding the textbox with name _passwordText
    @BindView(R.id.btn_login) Button _loginButton;          // binding the button with name _loginButton
    @BindView(R.id.link_signup) TextView _signupLink;       // binding the label with name _signupLink

    @Override                                                             // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onCreate(Bundle savedInstanceState) {                     // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                               // creates the activity.
        setContentView(R.layout.activity_login);                          // sets the content UI to be displayed on screen.
        int PERMIT_ALL = 1;                                               // give permissions to all required permissions.
        // contains all permissions
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMIT_ALL);// asks for granting permissions if not done already.
        ButterKnife.bind(this);                                           // handle all ui elements.
        sqLiteHelper = new SQLiteHelper(this);                           // creates instance of users' database for accessing.
        _loginButton.setOnClickListener(new View.OnClickListener() {      // this creates the constructor for onClick().
            @Override                                                     // this functions overrides the function defined in the parent class "AppCompatActivity".
            public void onClick(View v) {                                 // this function defines the tasks to be done when button is clicked.
                login();                                                  // run the login method on clicking login button
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {       // this creates the constructor for onClick().
            @Override                                                     // this functions overrides the function defined in the parent class "AppCompatActivity".
            public void onClick(View v) {                                 // this function defines the tasks to be done when button is clicked.
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);           // start the signup activity.
                finish();                                                 // finish the present activity, clearing memory usage and decreasing ram usage
                // defines the animation of moving from one activity to another.
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {                                                 // this method handles login of the user
        // Log.d(TAG, "Login");                                              //

        if (!validate()) {                                                // if all fields are not valid entries, then display login failed message
            onLoginFailed();                                              // display the login failed message.
            return;
        }

        _loginButton.setEnabled(false);                                   // if the form is valid, then disable the login button.
        // start the progressbar
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity1.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);                            // keep it running till the process completes
        progressDialog.setMessage("Authenticating...");                   // set the message to be displayed
        progressDialog.show();                                            // display the progress bar

        String email = _emailText.getText().toString();                   // obtain the email as enterd by user
        final String password = _passwordText.getText().toString();       // fetch the password as entered by user

        Cursor result = sqLiteHelper.getPassword(email);                  // get the password corresponding to given email id.
        if(result.getCount() == 0) {                                      // if there are no records with given email id
            new android.os.Handler().postDelayed(                         // this defines what to be performed under this circumstance
                    new Runnable() {                                      // define what to run
                        public void run() {
                            onLoginFailed();                              // display that login has failed.
                            progressDialog.dismiss();                     // close the progress bar
                        }
                    }, 3000);
            return;
        }
        result.moveToFirst();                                             // else move to first row
        TempPassword = result.getString(3);                               // extract the password from it.

        new android.os.Handler().postDelayed(                             // this defines what to be performed under this circumstance
                new Runnable() {                                          // defines what to run on login success/failed
                    public void run() {
                        if(TempPassword.equals(password)) {               // if extracted password equals entered passowrd, call onLoginSuccess()
                            onLoginSuccess();
                        } else {                                          // if not, then call onLoginFailed()
                            onLoginFailed();
                        }
                        progressDialog.dismiss();                         // close the progress dialog
                    }
                }, 3000);
    }


    @Override                                                             // this functions overrides the function defined in the parent class "AppCompatActivity".
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {                              // if signup is successful then finish the login activity and progress to main_activity
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    @Override                                                             // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onBackPressed() {
        new AlertDialog.Builder(this)                                     // sets the icon for dialog box
                .setIcon(android.R.drawable.ic_dialog_alert)              // sets the title for the dailog box
                .setTitle("Close App")
                .setMessage("Are you sure you want to exit the app?")     // defines tasks to be performed when user clicks yes.
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true);                             // prevent going back to main menu
                    }

                })
                .setNegativeButton("No", null)                            // if the user selects no, then do nothing
                .show();
    }

    public void onLoginSuccess() {                                        // this method handles tasks to be performed on successful login
        _loginButton.setEnabled(true);                                    // enable the login button
        // define the next activity to start
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);                                            // start the new activity
        finish();                                                         // finish this activity
    }

    public void onLoginFailed() {                                         // this method handles tasks to be performed on failed login
        // display the message of failed login
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);                                    // enable the login button
    }

    public boolean validate() {                                           // this method handles the form validation
        boolean valid = true;                                             // initialise the valid variable, assuming the form is valid, set to true.

        String email = _emailText.getText().toString();                   // fetch the email from the textbox
        String password = _passwordText.getText().toString();             // fetch the password from the textbox
                                                                          // checks whether it follows proper email syntax and is not empty
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");           // if it doesn't follow, then display the error
            valid = false;                                                // set valid to false
        } else {                                                          // if it follows proper guidelines, then don't display any error
            _emailText.setError(null);
        }
        // checks whether password is not empty and has length between 4 to 10 characters
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;                                                // if it doesn't follow, then display the error, and make valid equal to false
        } else {
            _passwordText.setError(null);                                 // if it follows proper guidelines, then don't display any error
        }

        return valid;                                                     // return the value denoting validity of form
    }
}
