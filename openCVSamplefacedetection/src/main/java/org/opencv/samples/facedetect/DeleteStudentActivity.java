/*
<header>
Module Name          : DeleteStudentActivity
Date of Creation     : 10-04-2018
Author               : Akul Agrawal

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : protected void onCreate(Bundle)
                       public void deleteStudData()
                       public void onClick(View)
</header>
*/

package org.opencv.samples.facedetect;

// importing package for android
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DeleteStudentActivity extends AppCompatActivity {

    private DBHelper myDb;                                         // instance of database containing student record.
    private DBImgHelper imDb;                                      // instance of database containing student images.
    private EditText editTextId;                                   // controlling course-id to be entered by user.
    private Button btnDelete;                                      // controlling delete button.

    @Override                                                      // this functions overrides the function defined in the parent class "AppCompatActivity".
    protected void onCreate(Bundle savedInstanceState) {           // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                        // creates the activity.
        setContentView(R.layout.activity_delete_student);          // sets the content UI to be displayed on screen.
        myDb = new DBHelper(this);                                 // creating instance of the students' record database.
        imDb = new DBImgHelper(this);                              // creating instance of the students' image database.

        editTextId = (EditText)findViewById(R.id.editText_id);     // locating the course id textbox and creating instance of it.
        btnDelete= (Button)findViewById(R.id.button_delete);       // locating the delete button and creating instance of it.
        deleteStudData();                                          // method for deleting image is called when the corresponding button is clicked.
    }

    public void deleteStudData() {                                 // this method is responsible for deleting record and images of the corresponding roll number.
        btnDelete.setOnClickListener(                              // new process starts if the button is clicked.
                new View.OnClickListener() {                       // this creates the constructor for onClick().
                    @Override                                      // this functions overrides the function defined in the parent class "AppCompatActivity".
                    public void onClick(View v) {                  // this function defines the tasks to be done when button is clicked.
                        boolean isInteger = false;                 // declaring boolean variable for checking if entered roll number is integer and initialising it.
                        String rollID = editTextId.getText().toString();   // fetch roll id from text-field
                        int roll_number=0;                         // declaring integer for storing the value of entered roll number.
                        try {                                      // error handling for entered roll number.
                            // try to parse/convert the entered roll number to integer if possible.
                            roll_number = Integer.parseInt(editTextId.getText().toString());
                            if (roll_number > 0) {
                                isInteger = true;                  // assign true to the variable if it is positive
                            }
                        } catch ( NumberFormatException e ) {      // if the entered string cannot be converted to integer, then handle this case separately.
                            isInteger = false;                     // if parsing is not possible, then assign false to this variable.
                        }
                        // if any field is empty, display the same
                        if (rollID.isEmpty()) {
                            Toast.makeText(DeleteStudentActivity.this, "Field(s) is empty", Toast.LENGTH_LONG).show();
                        } else if(isInteger) {                                     // if the entered value is integer.
                            Integer deletedRows = myDb.deleteData(roll_number);    // try to delete the record of corresponding roll number.
                            Integer deletedImg = imDb.deleteImg(roll_number);      // try to delete the images of corresponding roll number.
                            if (deletedRows > 0 && deletedImg > 0){                // if the roll number exists.
                                // display the message that images have been deleted.
                                Toast.makeText(DeleteStudentActivity.this, "Data Deleted", Toast.LENGTH_LONG).show();
                            } else{                                                // if not successful.
                                // display the message that roll number is not present.
                                Toast.makeText(DeleteStudentActivity.this, "Roll ID not present in database", Toast.LENGTH_LONG).show();
                            }
                        } else{                                                    // if the entered value is not integer, display the message showing the same.
                            Toast.makeText(DeleteStudentActivity.this,"Entered Roll ID is not integer",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

}
