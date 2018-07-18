package org.opencv.samples.facedetect;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {
    /*Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the view from activity_main.xml
        setContentView(R.layout.activity_menu);

        // Locate the button in activity_main.xml
        button = (Button) findViewById(R.id.MyButton);

        // Capture button clicks
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                // Start NewActivity.class
                Intent myIntent = new Intent(MenuActivity.this,
                        CameraActivity.class);
                startActivity(myIntent);
            }
        });
    }*/

    DBHelper myDb;
    Button btnAdd;
    Button btnviewAll;
    Button btnDelete;
    Button btnviewUpdate;
    Button btndispImg;
    Button btncamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        System.loadLibrary("opencv_java3");
        myDb = new DBHelper(this);

        btnAdd = (Button)findViewById(R.id.button_add);
        btnviewAll = (Button)findViewById(R.id.button_viewAll);
        btnviewUpdate = (Button)findViewById(R.id.button_edit);
        btnDelete = (Button)findViewById(R.id.button_delete);
        btndispImg = (Button)findViewById(R.id.button_dispImg);
        btncamera = (Button)findViewById(R.id.button_camera);
        AddData();
        viewAll();
        UpdateData();
        DeleteData();
        DispImg();
        Camera();
    }
    public void DeleteData() {
        btnDelete.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this,
                                DeleteStudentActivity.class);
                        startActivity(myIntent);
                    }
                }
        );
    }
    public void UpdateData() {
        btnviewUpdate.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this,
                                EditStudentActivity.class);
                        startActivity(myIntent);
                    }
                }
        );
    }
    public void AddData() {
        btnAdd.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this,
                                AddStudentActivity.class);
                        startActivity(myIntent);
                    }
                }
        );
    }
    public void DispImg() {
        btndispImg.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this,
                                DisplayImageActivity.class);
                        startActivity(myIntent);
                    }
                }
        );
    }
    public void Camera() {
        btncamera.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MenuActivity.this,
                               CameraSession.class);
                        startActivity(myIntent);
                    }
                }
        );
    }

    public void viewAll() {
        btnviewAll.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = myDb.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            showMessage("Error","Nothing found");
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Id :"+ res.getString(0)+"\n");
                            buffer.append("Name :"+ res.getString(1)+"\n");
                            buffer.append("Surname :"+ res.getString(2)+"\n");
                            buffer.append("Course :"+ res.getString(3)+"\n\n");
                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

}
