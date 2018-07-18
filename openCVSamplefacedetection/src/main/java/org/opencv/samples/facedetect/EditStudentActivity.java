/*
<header>
Module Name          : EditStudentActivity
Date of Creation     : 10-04-2018
Author               : Akul Agrawal

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public void onCreate(Bundle)
                       public void updateImage()
                       public void updateData()
                       public void onClick(View)

</header>
*/

package org.opencv.samples.facedetect;

// importing package for android
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EditStudentActivity extends AppCompatActivity {

    private DBHelper myDb;                        // instance of database containing student record.
    private EditText editName;                    // controlling name to be entered by user.
    private EditText editSurname;                 // controlling surname to be entered by user.
    private EditText editCourse;                  // controlling course-id to be entered by user.
    private EditText editTextId;                  // controlling roll number to be entered by user.
    private Button btnviewUpdate;                 // controlling the update button.
    private DBImgHelper imDb;                     // instance of database containing images.
    private Button button;                        // controlling reset-image option button.

    @Override                                                         // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onCreate(Bundle savedInstanceState) {                 // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                           // creates the activity.
        setContentView(R.layout.activity_edit_student);               // sets the content UI to be displayed on screen.
        myDb = new DBHelper(this);                                    // creating instance of the students' record database.
        imDb = new DBImgHelper(this);                                 // creating instance of the students' image database.

        editName = (EditText)findViewById(R.id.editText_name);        // locating the name textbox and creating instance of it.
        editSurname = (EditText)findViewById(R.id.editText_surname);  // locating the surname textbox and creating instance of it.
        editCourse = (EditText)findViewById(R.id.editText_Course);    // locating the course id textbox and creating instance of it.
        editTextId = (EditText)findViewById(R.id.editText_id);        // locating the roll number textbox and creating instance of it.
        btnviewUpdate= (Button)findViewById(R.id.button_update);      // locating the update button and creating instance of it.

        button = (Button) findViewById(R.id.ImgButton);               // locating the update image button and creating instance of it.

        updateImage();                                                // method for updating image is called when the corresponding button is clicked.
        updateData();                                                 // method for updating record is called when the corresponding button is clicked.
    }

    public void updateImage() {                                       // this method is responsible for updating images of the corresponding roll number.
        button.setOnClickListener(                                    // new process starts if the button is clicked.
                new View.OnClickListener() {                          // this creates the constructor for onClick().
                    @Override                                         // this functions overrides the function defined in the parent class "AppCompatActivity".
                    public void onClick(View v) {                     // this function defines the tasks to be done when button is clicked.
                        boolean isInteger = false;                    // declaring boolean variable for checking if entered roll number is integer and initialising it.
                        String courseID = editCourse.getText().toString();
                        String name = editName.getText().toString();
                        String surname = editSurname.getText().toString();
                        String rollID = editTextId.getText().toString();
                        int roll_number=0;                            // declaring integer for storing the value of entered roll number.
                        try {                                         // error handling for entered roll number.
                            // try to parse/convert the entered roll number to integer if possible.
                            roll_number = Integer.parseInt(editTextId.getText().toString());
                            isInteger = true;                         // if parsing is possible, then assign true to this variable.
                        } catch ( NumberFormatException e ) {         // if the entered string cannot be converted to integer, then handle this case separately.
                            isInteger = false;                        // if parsing is not possible, then assign false to this variable.
                        }

                        if (courseID.isEmpty() || surname.isEmpty() || name.isEmpty() || rollID.isEmpty()) {
                            Toast.makeText(EditStudentActivity.this, "Field(s) is empty", Toast.LENGTH_LONG).show();
                        } else if(isInteger) {
                            if ( myDb.ifStudentExists(roll_number, courseID) ) {    // if the entered value is integer.
                                Integer deletedImg = imDb.deleteImg(roll_number);   // try to delete the images of corresponding roll number.
                                if (deletedImg > 0) {                               // if the roll number exists.
                                    // display the message that images have been deleted.
                                    Toast.makeText(EditStudentActivity.this, "Images Deleted", Toast.LENGTH_LONG).show();
                                    // this defines the next activity(add images) to start if images have been deleetd.
                                    Intent intent = new Intent(EditStudentActivity.this, AddImages.class);
                                    // stores the roll number to be used in next activity.
                                    intent.putExtra("id", editTextId.getText().toString());
                                    startActivity(intent);                          // starts the next activity/opens the next screen view.
                                    finish();                                       // finishes this activity, cleaning all data and memory.
                                } else{                                             // if nothing is deleted, display roll number is not present.
                                    Toast.makeText(EditStudentActivity.this, "Roll ID not present in database", Toast.LENGTH_LONG).show();
                                }
                            } else {                                                // display roll number is not present.
                                Toast.makeText(EditStudentActivity.this, "Roll ID with given course not present", Toast.LENGTH_LONG).show();
                            }
                        } else {                                                     // if the entered value is not integer, display the message showing the same.
                            Toast.makeText(EditStudentActivity.this,"Entered Roll ID is not integer",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    public void updateData() {                                                  // this method is responsible for updating images of the corresponding roll number.
        btnviewUpdate.setOnClickListener(                                       // new process starts if the update record button is clicked.
                new View.OnClickListener() {                                    // this creates the constructor for onClick().
                    @Override                                                   // this functions overrides the function defined in the parent class "AppCompatActivity".
                    public void onClick(View v) {                               // this function defines the tasks to be done when button is clicked.
                        boolean isInteger = false;                              // declaring boolean variable for checking if entered roll number is integer and initialising it.
                        String courseID = editCourse.getText().toString();
                        String name = editName.getText().toString();
                        String surname = editSurname.getText().toString();
                        String rollID = editTextId.getText().toString();
                        int roll_number=0;                                      // declaring integer for storing the value of entered roll number.
                        try {                                                   // error handling for entered roll number.
                            // try to parse/convert the entered roll number to integer if possible.
                            roll_number = Integer.parseInt(editTextId.getText().toString());
                            isInteger = true;                                   // if parsing is possible, then assign true to this variable.
                        } catch ( NumberFormatException e ) {                   // if the entered string cannot be converted to integer, then handle this case separately.
                            isInteger = false;                                  // if parsing is not possible, then assign false to this variable.
                        }

                        if (courseID.isEmpty() || surname.isEmpty() || name.isEmpty() || rollID.isEmpty()) {
                            Toast.makeText(EditStudentActivity.this, "Field(s) is empty", Toast.LENGTH_LONG).show();
                        } else if(isInteger) {                                  // if the entered value is integer.
                            // try to update the record of corresponding roll number.
                            boolean isUpdate = myDb.updateData(roll_number,
                                    editName.getText().toString(),
                                    editSurname.getText().toString(), editCourse.getText().toString());
                            if (isUpdate == true) {                             // if update is successful
                                // display the message that record has been deleted.
                                Toast.makeText(EditStudentActivity.this, "Data Updated", Toast.LENGTH_LONG).show();
                            } else {                                            // if not successful.
                                // display the message that roll number is not present.
                                Toast.makeText(EditStudentActivity.this, "Data not Updated", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            // if the entered value is not integer, display the message showing the same.
                            Toast.makeText(EditStudentActivity.this,"Roll Number is not integer",Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

}
