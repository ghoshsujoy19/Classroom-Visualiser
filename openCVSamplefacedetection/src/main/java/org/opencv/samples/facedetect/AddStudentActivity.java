/*
<header>
Module Name          : AddStudentActivity
Date of Creation     : 10-04-2018
Author               : Akul Agrawal

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student record.

Global Variables     : None

Functions            : void AddData()
                       void onCreate(Bundle)
</header>
*/

package org.opencv.samples.facedetect;

// importing packages for android
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddStudentActivity extends AppCompatActivity {

    private DBHelper myDb;                           // database containing students' records.
    private EditText editName;                       // textBox containing name of the student.
    private EditText editSurname;                    // textBox containing surname of the student.
    private EditText editCourse;                     // textBox containing course id of the student.
    private EditText editTextId;                     // textBox containing roll number of the student.
    private Button btnAddData;                       // controlling button.
    private String id;                               // stores the roll number of the student.

    @Override                                                                // this functions overrides the function defined in the parent class "AppCompatActivity".
    protected void onCreate(Bundle savedInstanceState) {                     // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                                  // creates the activity.
        setContentView(R.layout.activity_add_student);                       // sets the content UI to be displayed on screen.
        myDb = new DBHelper(this);                                           // creating instance of the students' database.
        editName = (EditText)findViewById(R.id.editText_name);               // creating instance of EditText object.
        editSurname = (EditText)findViewById(R.id.editText_surname);         // creating instance of EditText object.
        editCourse = (EditText)findViewById(R.id.editText_Course);           // creating instance of EditText object.
        editTextId = (EditText)findViewById(R.id.editText_id);               // creating instance of EditText object.
        btnAddData = (Button)findViewById(R.id.button_add);                  // creating instance of Button object.
        id = editTextId.getText().toString();                                // storing the roll number of the corresponding student.
        // Log.d("special", id);
        AddData();                                                           // class this method to add data.
    }

    public void AddData() {                                                  // this method handles the data inserting into database.
        btnAddData.setOnClickListener(                                       // data inserting and new process starts if the button is clicked.
                new View.OnClickListener() {                                 // this creates the constructor for onClick().
                    @Override                                                // this functions overrides the function defined in the parent class "AppCompatActivity".
                    public void onClick(View v) {                            // this function defines the tasks to be done when button is clicked.
                        boolean isInteger = false;                           // stores true if entered roll number is integer, otherwise false. initialising it here.
                        String courseID = editCourse.getText().toString();   // fetch course id from text-field
                        String name = editName.getText().toString();         // fetch name from text-field
                        String surname = editSurname.getText().toString();   // fetch surname from text-field
                        String rollID = editTextId.getText().toString();     // fetch roll id from text-field
                        int roll_number=0;                                   // initialising roll number
                        try {                                                // error handling when entered roll number is not integer
                            // takes the data entered in textBox and parses it to integer, if possible.
                            roll_number = Integer.parseInt(editTextId.getText().toString());
                            if (roll_number > 0) {
                                isInteger = true;                            // assign true to the variable if it is positive
                            }
                        } catch ( NumberFormatException e ) {                // if there is an error, with entered roll number not an integer, handle the case separately
                            isInteger = false;                               // assign false to the variable.
                        }

                        // if any field is empty, display the same
                        if (courseID.isEmpty() || surname.isEmpty() || name.isEmpty() || rollID.isEmpty()) {
                            Toast.makeText(AddStudentActivity.this, "Field(s) is empty", Toast.LENGTH_LONG).show();
                        } else if(isInteger) {                               // if the entered value is integer.
                            // insert the values into database iff the roll number is not present in same course.
                            if(myDb.ifStudentExists(roll_number, courseID) == Boolean.FALSE) {
                                boolean isInserted = myDb.insertData( roll_number,
                                        editName.getText().toString(),
                                        editSurname.getText().toString(),
                                        editCourse.getText().toString() );
                                if(isInserted) {                         // if successfully inserted into database.
                                    // display message onto screen regarding confirmation of data entry.
                                    Toast.makeText(AddStudentActivity.this, "Data Inserted", Toast.LENGTH_LONG).show();
                                    // this defines the next activity to start if data have been added.
                                    Intent intent = new Intent(AddStudentActivity.this, AddImages.class);
                                    // stores the roll number to be used in next activity.
                                    intent.putExtra("id", editTextId.getText().toString());
                                    startActivity(intent);                       // starts the next activity/opens the next screen view.
                                    finish();                                    // finishes this activity, cleaning all data and memory.
                                } else {                                         // if the roll number is already present in the database with same course id
                                    // display message onto screen informing the same.
                                    Toast.makeText(AddStudentActivity.this,"Data not Inserted",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(AddStudentActivity.this, "Roll Number is not integer", Toast.LENGTH_LONG).show();
                            }

                        } else {                                                 // if the entered roll number is not an integer.
                            // display message onto screen informing the same.
                            Toast.makeText(AddStudentActivity.this,"Roll Number is not integer",Toast.LENGTH_LONG).show();
                        }

                    }
                }
        );
    }

}
