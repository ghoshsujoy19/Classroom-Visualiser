/*
<header>
Module Name          : SQLiteHelper
Date of Creation     : 10-04-2018
Author               : Mitansh Jain

Modification History : 11-04-2018 :- getCourseData() added

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public SQLiteHelper(Context)
                       public void onCreate(SQLiteDatabase)
                       public void onUpgrade(SQLiteDatabase, int, int)
                       public boolean insertData(String, String, String)
                       public boolean ifEmailExists(String)
                       public Cursor getPassword(String)

</header>
*/

package org.opencv.samples.facedetect;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="UserDataBase";              // stores name of the database.
    public static final String TABLE_NAME="UserTable";                    // stores name of the table
    public static final String Table_Column_ID="id";                      // stores name of first column
    public static final String Table_Column_1_Name="name";                // stores name of second column
    public static final String Table_Column_2_Email="email";              // stores name of third column
    public static final String Table_Column_3_Password="password";        // stores name of fourth column

    public SQLiteHelper(Context context) {                                // constructor of database.
        super(context, DATABASE_NAME, null, 1);                           // calls the database with name DATABASE_NAME.
    }

    @Override                                                             // this functions overrides the function defined in the parent class "SqliteDatabase".
    public void onCreate(SQLiteDatabase database) {                       // this method creates the table if not present already.
        // the table consists of 4 columns - first is id, second is name of the user,
        // third is email of the user and fourth is password of the user.
        // this string holds the command for table creation.
        String CREATE_TABLE="CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" ("+Table_Column_ID+" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+Table_Column_1_Name+" VARCHAR, "+Table_Column_2_Email+" VARCHAR, "+Table_Column_3_Password+" VARCHAR)";
        database.execSQL(CREATE_TABLE);                                   // execute the string query.
    }

    @Override                                                                   // this functions overrides the function defined in the parent class "SqliteDatabase".
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  // this method handles the upgradation of database.
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);                         // this deletes the previously existing table,
        onCreate(db);                                                           // and creates a new table.
    }

    public boolean insertData(String name,String email,String password) {       // this method handles the insertion of new row into database.
        SQLiteDatabase db = this.getWritableDatabase();                         // Open the database for reading.
        ContentValues contentValues = new ContentValues();                      // declare variable to store the new row to be inserted into database.
        contentValues.put(Table_Column_1_Name, name);                           // put name into it.
        contentValues.put(Table_Column_2_Email, email);                         // put email into it.
        contentValues.put(Table_Column_3_Password, password);                   // put password into it.
        long result = db.insert(TABLE_NAME,null ,contentValues);                // insert row into database.
        if(result == -1) {
            return false;                                                       // if insertion is incomplete, return false.
        } else {                                                                // else return true, denoting insertion is complete.
            return true;
        }
    }

    public boolean ifEmailExists(String inputEmail) {                           // this method checks whether the entered email already exists or not.
        SQLiteDatabase db = this.getWritableDatabase();                         // Open the database for reading.
        // execute the query to check for the entered email address.
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where email='"+inputEmail+"'",null);
        int count = res.getCount();                                             // count the number of entries having email equal to given email.
        if(count == 0)                                                          // if count value is 0, so there is no email of given input, return false
        {
            return false;
        } else{                                                                 // if count is positive, then ther is email as given and return true, denoting email
            return true;                                                        // is present.
        }
    }

    public Cursor getPassword(String email) {                                   // this method handles the fetching of password
        SQLiteDatabase db = this.getWritableDatabase();                         // Open the database for reading.
        // fetch the password corresponding to given email id
        Cursor res = db.rawQuery("select * from "+TABLE_NAME+" where email='"+email+"'",null);
        return res;                                                             // return the password cursor.
    }

}
