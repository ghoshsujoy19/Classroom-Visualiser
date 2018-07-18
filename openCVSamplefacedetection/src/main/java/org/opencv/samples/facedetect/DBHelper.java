/*
<header>
Module Name          : DBHelper
Date of Creation     : 10-04-2018
Author               : Akul Agrawal

Modification History : 11-04-2018 :- getCourseData() added

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public DBHelper(Context)
                       public void onCreate(SQLiteDatabase)
                       public void onUpgrade(SQLiteDatabase, int, int)
                       public boolean insertData(long, String, String, String)
                       public Cursor getAllData()
                       public Cursor getCourseData(String)
                       public boolean updateData(long, String, String, String)
                       public boolean updateModifiedTime(long, int, String)
                       public Boolean ifStudentExists(long, String)
                       public Integer deleteData(long)

</header>
*/

package org.opencv.samples.facedetect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class DBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Student.db"; // stores name of the database.
    public static final String TABLE_NAME = "student_table"; // stores name of the table
    public static final String COL_1 = "ID";                 // stores name of first column
    public static final String COL_2 = "NAME";               // stores name of second column
    public static final String COL_3 = "SURNAME";            // stores name of third column
    public static final String COL_4 = "COURSE";             // stores name of fourth column
    public static final String COL_5 = "LASTAPPEAR";         // stores name of fifth column
    public static final String COL_6 = "BOXCOLOR";           // stores name of sixth column

    public DBHelper(Context context) {                       // constructor of database.
        super(context, DATABASE_NAME, null, 1);              // calls the database with name DATABASE_NAME.
    }

    @Override                                                // this functions overrides the function defined in the parent class "SqliteDatabase".
    public void onCreate(SQLiteDatabase db) {                // this method creates a table with first column as roll number, second column as name
        // third column as surname, fourth column as course id, fifth column as last-appearance of student,
        // and sixth column as box-value
        db.execSQL("create table " + TABLE_NAME +" (ID INTEGER not null, NAME TEXT, SURNAME TEXT, COURSE TEXT, LASTAPPEAR TEXT, BOXCOLOR INTEGER)");
    }

    @Override                                                // this functions overrides the function defined in the parent class "SqliteDatabase".
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //this method handles the upgradation of database.
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);      // this deletes the previously existing table,
        onCreate(db);                                        // and creates a new table.
    }

    public boolean insertData(long id,String name,String surname,String course) { // this method handles nsertion of new data into database.
        SQLiteDatabase db = this.getWritableDatabase();                           // Open the database for reading.
        ContentValues contentValues = new ContentValues();                        // declare variable to store the new row to be inserted into database.
        contentValues.put(COL_1, id);                                             // put roll number into it
        contentValues.put(COL_2, name);                                           // put name into it
        contentValues.put(COL_3, surname);                                        // put surname into it
        contentValues.put(COL_4, course);                                         // put course id into it
        // declare a varibale specifying the format of time to be used
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();                                                   // declare variable for storing time
        String modifiedTime = dateFormat.format(date);                            // fetch the system time
        contentValues.put(COL_5, modifiedTime);                                   // put time into it
        Random boxColor = new Random();                                           // declaring varibale for giving random integer between 1 and 10.
        contentValues.put(COL_6, (boxColor.nextInt(9)+1));                        // the random number between 1 and 10 is put into it
        long result = db.insert(TABLE_NAME,null ,contentValues);                  // the row is inserted into database
        if(result == -1) {                                                        // if insertion is incomplete, return false.
            return false;
        } else {                                                                  // else return true, denoting insertion is complete.
            return true;
        }
    }

    public Cursor getAllData() {                                                  // this method returns all data from table
        SQLiteDatabase db = this.getWritableDatabase();                           // Open the database for reading.
        Cursor res = db.rawQuery("select * from "+TABLE_NAME,null);               // executes the comand to fetch all data from table
        return res;                                                               // return the cursor containing all data
    }

    public Cursor getCourseData(String CourseID) {                       // this method returns all data where course id is same as input parameter
        SQLiteDatabase db = this.getWritableDatabase();                  // Open the database for reading.
        // executes the comand to fetch all data from table where course id is same as input parameter
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where COURSE='"+CourseID+"'",null);
        return res;                                                      // return the cursor containing the required data
    }

    public Boolean ifStudentExists(long id, String courseID) {           // this method handles the presence of student in particular course
        SQLiteDatabase db = this.getWritableDatabase();                  // Open the database for reading.
        // get list of all students in input course
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where COURSE='"+courseID+"'",null);
        if(res.getCount()>0) {                                           // if the course exists in database
            res.moveToFirst();                                           // iterate over all students
            do {
                String rollID = res.getString(0);                        // extract the roll number of the student

                if (rollID.equals(String.valueOf(id))) {                 // if they are equal, return true
                    return Boolean.TRUE;
                }
            } while (res.moveToNext());
        }
        return Boolean.FALSE;                                            // if no student roll number matches, return false.
    }

    public boolean updateData(long id,String name,String surname,String course) { // handles updating of data in table
        SQLiteDatabase db = this.getWritableDatabase();                           // Open the database for reading.
        ContentValues contentValues = new ContentValues();                        // declare variable to store the new row to be inserted into database.
        contentValues.put(COL_2,name);                                            // put name into it
        contentValues.put(COL_3,surname);                                         // put surname into it
        contentValues.put(COL_4,course);                                          // put course id into it
        // declare a varibale specifying the format of time to be used
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();                                                   // declare variable for storing time
        String modifiedTime = dateFormat.format(date);                            // fetch the system time
        contentValues.put(COL_5, modifiedTime);                                   // put last-appear time into it
        Random boxColor = new Random();                                           // variable for generating random numbers
        contentValues.put(COL_6, (boxColor.nextInt(9)+1));                        // generate randowm number between 1 and 10 and put boxcolor into it
        db.update(TABLE_NAME, contentValues, "ID = " +id,null );                  // update the table for roll number as given with new values as stored
        return true;                                                              // return true denoting update completion
    }

    public boolean updateModifiedTime(long id, int boxColor, String modifiedTime){ // this method is used to modify the boxcolor and last-appear time for given roll id
        SQLiteDatabase db = this.getWritableDatabase();                            // Open the database for reading.
        ContentValues contentValues = new ContentValues();                         // declare variable to store the new row to be inserted into database.
        contentValues.put(COL_5, modifiedTime);                                    // put last-appear time into it
        contentValues.put(COL_6, boxColor);                                        // put boxcolor into it
        db.update(TABLE_NAME, contentValues, "ID = "+id,null);                     // update the table for roll number as given with new values as stored
        return true;                                                               // return true denoting update completion
    }

    public Integer deleteData (long id) {                    // this method is used to delete data corresponding to given roll number
        SQLiteDatabase db = this.getWritableDatabase();      // Open the database for reading.
        return db.delete(TABLE_NAME, "ID = "+id,null);       // return the number of rows deleted, if roll number is not present, it returns 0, else returns positive value
    }
}