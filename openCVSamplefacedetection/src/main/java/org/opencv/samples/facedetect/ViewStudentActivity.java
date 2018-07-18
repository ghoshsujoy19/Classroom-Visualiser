/*
<header>
Module Name          : ViewStudentActivity
Date of Creation     : 10-04-2018
Author               : Sujoy Ghosh

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public void onCreate(Bundle)
                       public void onClick(View)

</header>
*/

package org.opencv.samples.facedetect;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.view.Gravity;
import android.graphics.Color;
import android.widget.Toast;

public class ViewStudentActivity extends AppCompatActivity{
    private DBHelper myDb;                                                   // instance of database containing student record.
    EditText input_id;                                                       // controlling course id textfield.
    Button viewList;                                                         // controlling view list button.
    String courseID;                                                         // stores the course id in it

    @Override                                                                // this functions overrides the function defined in the parent class "AppCompatActivity".
    protected void onCreate(Bundle savedInstanceState) {                     // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                                  // creates the activity.
        setContentView(R.layout.activity_view_student);                      // sets the content UI to be displayed on screen.
        myDb = new DBHelper(this);                                           // creating instance of the students' record database.
        input_id = findViewById(R.id.courseID);                              // locating the course id textbox and creating instance of it.
        viewList = findViewById(R.id.viewlist);                              // locating the viewList button and creating instance of it.
        final TableLayout table_records = findViewById(R.id.table_records);  // locating the table and creating instance of it.
        final TableRow rowHeader = new TableRow(this);                       // declaring new row header for the table

        //setting background color for header row
        rowHeader.setBackgroundColor(Color.parseColor("#c0c0c0"));           // set the background colour for table header

        // defining table row parameters
        rowHeader.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

        // Header row text
        String[] headerText={"Roll Number","Name","Course Code","Last Appear","Box Colour"};

        for(String c:headerText) {                                           //This sets parameters and text for five columns of table for header row
            TextView tv = new TextView(this);                                // declare variable to store the column values
            // setting height an length parameters using wrap_content params
            tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);                                   // alignment of text wrt cell
            tv.setTextSize(18);                                              // setting text size for header row
            tv.setPadding(5, 5, 5, 5);                                       // padding of cell
            tv.setText(c);                                                   // insert the text to be displyed in column
            rowHeader.addView(tv);                                           // add the column into row
        }
        viewList.setOnClickListener(                                         // new process starts if the button is clicked.
                new View.OnClickListener() {                                 // this creates the constructor for onClick().
                    @Override                                                // this functions overrides the function defined in the parent class "AppCompatActivity".
                    public void onClick(View v) {                            // this function defines the tasks to be done when button is clicked.
                        // if the button lsbel is view student, then function as table viewer
                        if(viewList.getText().toString().equalsIgnoreCase("View Students List")) {
                            courseID = input_id.getText().toString();        // fetch the course id from textfield
                            Cursor res = myDb.getCourseData(courseID);       // fetch the list of all studnets with given course id from the database
                            if (courseID.isEmpty()) {
                                Toast.makeText(ViewStudentActivity.this, "Field is empty", Toast.LENGTH_LONG).show();
                            } else if (res.getCount() == 0) {                  // if number of student corresponding to course id is 0, then display suitable message
                                // show message
                                Toast.makeText(ViewStudentActivity.this, "No student record from selected course", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                table_records.addView(rowHeader);                // add a row into table
                                while (res.moveToNext()) {                       // while all records are yet to be visited
                                    // declare variable to store new table row
                                    TableRow row = new TableRow(ViewStudentActivity.this);
                                    // row parameters
                                    row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));
                                    // setting row text
                                    String[] colText = {res.getString(0), res.getString(1), res.getString(3), res.getString(4), res.getString(5)};

                                    for (String text : colText) {                //This sets parameters and text for the columns of table for header row
                                        // declare variable to store the column values
                                        TextView tv = new TextView(ViewStudentActivity.this);
                                        // this stores the column value
                                        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));//setting height an length parameters usin wrap_content params
                                        tv.setGravity(Gravity.CENTER);           // alignment of text wrt cell
                                        tv.setTextSize(16);                      // setting text size for header row
                                        tv.setPadding(5, 5, 5, 5);               // padding of cell
                                        tv.setText(text);                        // insert the text to be displyed in column
                                        row.addView(tv);                         // add the column into row
                                    }
                                    table_records.addView(row);                  // add the row into table
                                    viewList.setText("Clear");                   // make button label as clear
                                }
                            }
                        } else {
                            table_records.removeAllViews();                  // remove all table rows
                            viewList.setText("View Students List");          // change the text of button to "view student list" to enable view other courses
                            input_id.setText("");                            // clear the textfield
                        }
                    }
                }
        );

    }
}
