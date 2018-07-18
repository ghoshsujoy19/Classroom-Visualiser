/*
<header>
Module Name          : AddStudentActivity
Date of Creation     : 10-04-2018
Author               : Sujoy Ghosh

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public DBImgHelper(Context)
                       public void onCreate(SQLiteDatabase)
                       public void onUpgrade(SQLiteDatabase, int, int)
                       public boolean insertImg(long, Bitmap)
                       public ArrayList<Bitmap> getImg(long)
                       public boolean updateImg(long, Bitmap)
                       public Integer deleteImg (long)

</header>
*/

package org.opencv.samples.facedetect;

//importing package for android sqlite database
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class DBImgHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Img.db";            // stores name of the database.
    public static final String TABLE_NAME = "img_table";            // stores name of the table.
    public static final String COL_1 = "ID";                        // stores name of first column.
    public static final String COL_2 = "IMG";                       // stores name of second column.

    public DBImgHelper(Context context) {                           // constructor of database.
        super(context, DATABASE_NAME, null, 1);                     // calls the database with name DATABASE_NAME.
    }

    @Override                                                       // this functions overrides the function defined in the parent class "SqliteDatabase".
    public void onCreate(SQLiteDatabase db) {                       // this method handles creating table if not present already.
        // this creates a table, storing roll-number and photo of the student as pair.
        db.execSQL("create table if not exists " + TABLE_NAME +" (ID INTEGER ,IMG BLOB NOT NULL)");
    }

    @Override                                                                   // this functions overrides the function defined in the parent class "SqliteDatabase".
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {  // this method handles the upgradation of database
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);                         // this deletes the previously existing table, 
        onCreate(db);                                                           // and creates an new table.
    }

    public boolean insertImg(long id, Bitmap bm) {                              // this method handles insertion of new image of a student, returning true if inserted.
        ByteArrayOutputStream out = new ByteArrayOutputStream();                // variable is declared to store compressed version of incoming image.
        bm.compress(Bitmap.CompressFormat.PNG, 100, out);                       // the incoming image is compressed to PNG format and stored in 'out' variable.
        byte[] buffer=out.toByteArray();                                        // Convert the compressed image into byte array for storing in database.
        SQLiteDatabase db = this.getWritableDatabase();                         // Open the database for writing.
        db.beginTransaction();                                                  // Start the transaction.
        try {
            ContentValues values = new ContentValues();                         // declare variable to store the new row to be inserted into database.
            values.put("ID", id);                                               // put roll number into it.
            values.put("IMG", buffer);                                          // put image into it.
            long result = db.insert(TABLE_NAME, null, values);                  // insert row into database.
            db.setTransactionSuccessful();                                      // insert into database successfully. 
            if(result == -1) {                                                  // if insertion is incomplete, return false.
                return false;
            } else {                                                            // else return true, denoting insertion is complete.
                return true;
            }
        } catch (SQLiteException e) {                                           // if there is exception/error in inserting, print error.
            e.printStackTrace();                                                // return false, denoting unsuccessful insertion
            return false;
        } finally {                                                             // this statement is always executed even if there was insertion failure.
            db.endTransaction();                                                // end the transaction and save the changes.
            db.close();                                                         // close the database.
        }
    }

    public ArrayList<Bitmap> getImg(long id) {                                    // this method returns all images of given roll number.
        ArrayList<Bitmap> allImages = new ArrayList<Bitmap>();                    // this varisble stores all images of given roll number.
        SQLiteDatabase db = this.getReadableDatabase();                           // Open the database for reading.
        db.beginTransaction();                                                    // Start the transaction.
        try {
            String selectQuery = "SELECT * FROM "+ TABLE_NAME+" WHERE id = " +id; // query string to fetch all images.
            Cursor cursor = db.rawQuery(selectQuery, null);                       // executing the sql query.
            if(cursor.getCount() >0) {                                            // if there are positive number of images.
                cursor.moveToFirst();                                             // move the cursor head to first row.
                while (cursor.moveToNext()) {                                     // while cursor head reaches end position.
                    Bitmap bitmap = null;                                         // this variable temporarily stores the current image.
                    byte[] blob = cursor.getBlob(cursor.getColumnIndex("IMG"));   // extract the image from database cursor.
                    bitmap= BitmapFactory.decodeByteArray(blob, 0, blob.length);  // decode the image back to its original form.
                    allImages.add(bitmap);                                        // add the image to the list of images.
                }
            }
            db.setTransactionSuccessful();                                        // denote successful transaction has occured.
        } catch (SQLiteException e) {                                             // if there is exception/error in fetching, print error.
            e.printStackTrace();
        }finally {                                                                // this statement is always executed even if there was insertion failure.
            db.endTransaction();                                                  // end the transaction and save the changes.
            db.close();                                                           // close the database.
        }

        return allImages;
    }

    public Integer deleteImg (long id) {                           // this function handles the deletion of image of given roll number.
        SQLiteDatabase db = this.getWritableDatabase();            // Open the database for writing.
        return db.delete(TABLE_NAME, "ID = " +id,null);            // return the value of delete(), if there is any image present associated with roll number,
        // then positive value is returned, else 0 is returned.(So, 0 means that roll number is not present)
    }
}