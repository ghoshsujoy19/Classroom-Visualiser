/*
<header>
Module Name          : SignupActivity
Date of Creation     : 10-04-2018
Author               : Mitansh Jain

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public void onCreate(Bundle)
                       public void onClick(View)
                       public void signup()
                       public void onSignupSuccess()
                       public void onSignupFailed()
                       public void onBackPressed()
                       public void SQLiteDataBaseBuild()
                       public void SQLiteTableBuild()
                       public void EmptyEditTextAfterDataInsert()
                       public void CheckingEmailAlreadyExistsOrNot()
                       public void CheckFinalResult()
                       public boolean validate()

</header>
*/

package org.opencv.samples.facedetect;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @BindView(R.id.input_name) EditText _nameText;                              // binding the textbox with name _nameText
    @BindView(R.id.input_email) EditText _emailText;                            // binding the textbox with name _emailText
    @BindView(R.id.input_password) EditText _passwordText;                      // binding the textbox with name _passwordText
    @BindView(R.id.input_reEnterPassword) EditText _reEnterPasswordText;        // binding the textbox with name _reEnterPasswordText
    @BindView(R.id.btn_signup) Button _signupButton;                            // binding the button with name _signupButton
    @BindView(R.id.link_login) TextView _loginLink;                             // binding the label with name _loginLink

    private Boolean EditTextEmptyHolder;                                        // stores when to empty the textfields
    private SQLiteDatabase sqLiteDatabaseObj;                                   // sqlite database transaction handler
    private SQLiteHelper sqLiteHelper;                                          // saves the instance of our database
    private Cursor cursor;                                                      // stores the results
    private String F_Result = "Not_Found";                                      // stores if signup to be done or not
    private String name, email, password, reEnterPassword;                      // stores the signup credentials


    @Override                                                                   // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onCreate(Bundle savedInstanceState) {                           // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                                     // creates the activity.
        setContentView(R.layout.activity_signup);                               // sets the content UI to be displayed on screen.
        ButterKnife.bind(this);                                                 // handle all ui elements.
        sqLiteHelper = new SQLiteHelper(this);                                  // creates instance of users' database for accessing.
        _signupButton.setOnClickListener(new View.OnClickListener() {           // this creates the constructor for onClick().
            @Override                                                           // this functions overrides the function defined in the parent class "AppCompatActivity".
            public void onClick(View v) {                                       // this function defines the tasks to be done when button is clicked.
                signup();                                                       // run the signup method on clicking signup button
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {              // this creates the constructor for onClick().
            @Override                                                           // this functions overrides the function defined in the parent class "AppCompatActivity".
            public void onClick(View v) {                                       // this function defines the tasks to be done when button is clicked.
                // Finish the registration screen and return to the login activity
                Intent intent = new Intent(getApplicationContext(),LoginActivity1.class);
                startActivity(intent);                                          // start the login activity.
                finish();                                                       // finish this activity
                // defines the animation of moving from one activity to another.
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void signup() {                                                      // this method handles signup of new user.
        EditTextEmptyHolder = false;                                            // initialise it to false
        if (!validate()) {                                                      // if the entered form is not valid
            onSignupFailed();                                                   // display the signup failed message and return.
            return;
        }
        EditTextEmptyHolder = true;                                             // set it to true to empty the textfields agter signup
        _signupButton.setEnabled(false);                                        // if the form is valid, then disable the signup button.
        // start the progressbar
        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);                                  // keep it running till the process completes
        progressDialog.setMessage("Creating Account...");                       // set the message to be displayed
        progressDialog.show();                                                  // display the progress bar

        name = _nameText.getText().toString();                                  // fetch the name as entered by user
        email = _emailText.getText().toString();                                // fetch the email as entered by user
        password = _passwordText.getText().toString();                          // fetch the password as entered by user
        reEnterPassword = _reEnterPasswordText.getText().toString();            // fetch the confirm-password as entered by user

        SQLiteDataBaseBuild();                                                  // Creating SQLite database if doesn't exist
        SQLiteTableBuild();                                                     // Creating SQLite table if doesn't exist.
        EmptyEditTextAfterDataInsert();                                         // Empty EditText After done inserting process.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {                                         // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success.
                        CheckingEmailAlreadyExistsOrNot();                      // Method to check Email is already exists or not.
                        progressDialog.dismiss();                               // closes the progress bar.
                    }
                }, 3000);
    }


    public void onSignupSuccess() {                                                 // this method handles tasks to be performed on successful signup
        _signupButton.setEnabled(true);                                             // enables the signup button
        setResult(RESULT_OK, null);                                                 // set result for previous activity as complete
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);     // define the next activity to start
        startActivity(intent);                                                      // start the new activity
        finish();                                                                   // finish this actiity
    }

    public void onSignupFailed() {                                                  // this method handles tasks to be performed on failed signup
        Toast.makeText(getBaseContext(), "signup failed", Toast.LENGTH_LONG).show();// display the message of failed signup
        _signupButton.setEnabled(true);                                             // enable the signup button
    }

    @Override                                                             // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onBackPressed() {
        new AlertDialog.Builder(this)                                     // launches new alert dialog box
                .setIcon(android.R.drawable.ic_dialog_alert)              // sets the icon for dialog box
                .setTitle("Close App")                                    // sets the title for the dailog box
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

    // SQLite database build method.
    public void SQLiteDataBaseBuild(){
        // creates the database if it doesn't exist already
        sqLiteDatabaseObj = openOrCreateDatabase(SQLiteHelper.DATABASE_NAME, Context.MODE_PRIVATE, null);
    }

    // SQLite table build method.
    public void SQLiteTableBuild() {
        // creates the table if it doesn't exist
        sqLiteDatabaseObj.execSQL("CREATE TABLE IF NOT EXISTS " + SQLiteHelper.TABLE_NAME + "(" + SQLiteHelper.Table_Column_ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + SQLiteHelper.Table_Column_1_Name + " VARCHAR, " + SQLiteHelper.Table_Column_2_Email +
                " VARCHAR, " + SQLiteHelper.Table_Column_3_Password + " VARCHAR);");
    }


    // Empty edittext after done inserting process method.
    public void EmptyEditTextAfterDataInsert(){
        _nameText.getText().clear();                                                // clear the name textbox
        _emailText.getText().clear();                                               // clear the email textbox
        _passwordText.getText().clear();                                            // clear the password textbox
        _reEnterPasswordText.getText().clear();                                     // clear the confirm-password textbox
    }

    public void CheckingEmailAlreadyExistsOrNot(){                                  // this method is responsible for checking if Email already exists or not.

        boolean ifEmailExists = sqLiteHelper.ifEmailExists(email);                  // Adding search email query to cursor.
        if(ifEmailExists){                                                          // If Email is already exists then Result variable value set as Email Found.
            F_Result = "Email Found";
        }

        CheckFinalResult();                                                         // Calling method to check final result and insert data into SQLite database.

    }

    public void CheckFinalResult(){                                                 // this method is responsible for check final result.

        if(F_Result.equalsIgnoreCase("Email Found")) {                              // Checking whether email is already exists or not.
            // If email is exists then disply the corresponding message.
            Toast.makeText(SignupActivity.this,"Email Already Exists",Toast.LENGTH_LONG).show();
            onSignupFailed();                                                       // call signupFiled().
        } else {                                                                    // if the corresponding details doesn't exist in database.
            boolean val = sqLiteHelper.insertData(name, email, password);           // user registration details will entered to SQLite database.
            if(val) {                                                               // if successfully inserted.
                onSignupSuccess();                                                  // call signupSuccess().
            } else {                                                                // if insertion not successful, then call signupFailed()
                onSignupFailed();
            }
        }
        F_Result = "Not_Found" ;
    }



    public boolean validate() {                                                        // this method is responsible for form data validation
        boolean valid = true;                                                          // initialise the valid variable, assuming the form is valid, set to true.
        String name = _nameText.getText().toString();                                  // fetch the name from the textbox
        String email = _emailText.getText().toString();                                // fetch the email from the textbox
        String password = _passwordText.getText().toString();                          // fetch the password from the textbox
        String reEnterPassword = _reEnterPasswordText.getText().toString();            // fetch the confirm-password from the textbox

        if (name.isEmpty() || name.length() < 3) {                                     // checks whether name is empty or length is less than 3 letters.
            _nameText.setError("at least 3 characters");                               // set the error message to be displayed
            valid = false;                                                             // set valid to false
        } else {
            _nameText.setError(null);                                                  // if the entered is correct as per guidelines, then no error is displayed
        }
        // checks whether it follows proper email syntax and is not empty
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");                        // if it doesnt follow, then display the error
            valid = false;                                                             // set valid to false
        } else {
            _emailText.setError(null);                                                 // if it follows proper guidelines, then don't display any error
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {   // checks whether password is not empty and has length between 4 to 10 characters
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;                                                             // if it doesnt follow, then display the error, and make valid equal to false
        } else {
            _passwordText.setError(null);                                              // if it follows proper guidelines, then don't display any error
        }
        // checks whether password is not empty and has length between 4 to 10 characters
        // and equals the password entered
        if (reEnterPassword.isEmpty() || reEnterPassword.length() < 4 || reEnterPassword.length() > 10 || !(reEnterPassword.equals(password))) {
            _reEnterPasswordText.setError("Password Do not match");
            valid = false;                                                             // if it doesnt follow, then display the error, and make valid equal to false
        } else {
            _reEnterPasswordText.setError(null);                                       // if it follows proper guidelines, then don't display any error
        }

        return valid;                                                                  // return the value denoting validity of form
    }
}