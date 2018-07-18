/*
<header>
Module Name          : MainActivity
Date of Creation     : 10-04-2018
Author               : Akul Agrawal

Modification History : 11-04-2018 :- added error handling for roll number if it is not an integer.

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : protected void onCreate(Bundle)
                       public void onClick(View)
                       public void onBackPressed()
                       public boolean onCreateOptionsMenu(Menu)
                       public boolean onOptionsItemSelected(MenuItem)
                       public boolean onNavigationItemSelected(MenuItem)

</header>
*/
package org.opencv.samples.facedetect;

//
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private EditText editTextId;                                     // controlling course id text-field.
    private Button btnConfirm;                                       // controlling confirm button.
    private DBHelper myDb;                                           // instance of database containing student record.

    @Override                                                        // this functions overrides the function defined in the parent class "AppCompatActivity".
    protected void onCreate(Bundle savedInstanceState) {             // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                          // creates the activity.
        setContentView(R.layout.activity_main);                      // sets the content UI to be displayed on screen.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);      // locates the toolbar for controlling and operations
        setSupportActionBar(toolbar);
        myDb = new DBHelper(this);                                   // creating instance of the students' record database.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);           // locates the navigation drawer for controlling
        // for toggling between opening and closing of navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);                                                // the toggle state is adder as option into drawer
        toggle.syncState();                                                              // syncing it with drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);    // call the drawer
        navigationView.setNavigationItemSelectedListener(this);                          // attach drawer with this main activity
        editTextId = (EditText)findViewById(R.id.courseid);                              // locates the textfield for fetching purpose
        btnConfirm= (Button)findViewById(R.id.sessionstart);                             // locates the button for controlling purpose

        btnConfirm.setOnClickListener(                                                   // new process starts if the button is clicked.
                new View.OnClickListener() {                                             // this creates the constructor for onClick().
                    @Override                                                            // this functions overrides the function defined in the parent class "AppCompatActivity".
                    public void onClick(View v) {                                        // this function defines the tasks to be done when button is clicked.
                        String courseID = editTextId.getText().toString();               // extracts the course id from the textfield
                        Cursor result = myDb.getCourseData(courseID);                    //
                        if (courseID.isEmpty()) {
                            Toast.makeText(MainActivity.this, "Field(s) is empty", Toast.LENGTH_LONG).show();
                        } else if(result.getCount() > 0) {                               // check whether if ther are students with given course id
                            // if there is, define new activity to start next
                            Intent intent = new Intent(MainActivity.this, FdActivity.class);
                            intent.putExtra("id", courseID);                             // put the course id as parameter to it
                            startActivity(intent);                                       // start the next activity
                        } else {
                            // if there are no students with given course id, then print the message
                            Toast.makeText(getApplicationContext(), "No such courseID exists", Toast.LENGTH_LONG).show();
                        }
                    }
                }
        );
    }

    @Override                                                                            // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onBackPressed() {                                                        // this method handles the process when user press back button
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);           // locates the navigation drawer for controlling
        if (drawer.isDrawerOpen(GravityCompat.START)) {                                  // if drawer is open
            drawer.closeDrawer(GravityCompat.START);                                     // then close the drawer
        }
        else {
            new AlertDialog.Builder(this)                                                // else launch new alert dialog box
                    .setIcon(android.R.drawable.ic_dialog_alert)                         // sets the icon for dialog box
                    .setTitle("Log-out")                                                 // sets the title for dialog box
                    .setMessage("Are you sure you want to logout?")                      // sets the message for dialog box
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()      // defines tasks to be performed when user clicks yes.
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {         // specifies the task to be performed when yes is clicked
                            Intent intent = new Intent(MainActivity.this, LoginActivity1.class);
                            startActivity(intent);                                       // define the next activity and go to next activity.
                            finish();                                                    // finish this activity
                        }
                    })
                    .setNegativeButton("No", null)                                       // if the user selects no, then do nothing
                    .show();

        }
    }

    @Override                                                      // this functions overrides the function defined in the parent class "Menu".
    public boolean onCreateOptionsMenu(Menu menu) {                // this handles creation of menu
        getMenuInflater().inflate(R.menu.main, menu);              // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override                                                      // this functions overrides the function defined in the parent class "Menu".
    public boolean onOptionsItemSelected(MenuItem item) {          // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();                                 // get the id of the seleted item
        if (id == R.id.action_settings) {                          // noinspection SimplifiableIfStatement
            return true;
        }
        return super.onOptionsItemSelected(item);                  // returns the onOptionsItemSelected() method of parent class
    }

    @SuppressWarnings("StatementWithEmptyBody")                            // suppresses warining specially for low level api andoid versions
    @Override                                                              // this functions overrides the function defined in the parent class "Menu".
    public boolean onNavigationItemSelected(MenuItem item) {               // this method handles navigation view item-clicks here.
        int id = item.getItemId();                                         // get the id of clicked option

        if (id == R.id.nav_add_student) {                                  // if id matches with id of add student option
            Intent intent = new Intent(getApplicationContext(),AddStudentActivity.class);
            startActivity(intent);                                         // define the next activity and go to next activity.
        } else if (id == R.id.nav_edit_student) {                          // if id matches with id of edit student option
            Intent intent = new Intent(getApplicationContext(),EditStudentActivity.class);
            startActivity(intent);                                         // define the next activity and go to next activity.
        } else if (id == R.id.nav_view_student) {                          // if id matches with id of view student option
            Intent intent = new Intent(getApplicationContext(),ViewStudentActivity.class);
            startActivity(intent);                                         // define the next activity and go to next activity.
        } else if (id == R.id.nav_delete_student) {                        // if id matches with id of delete student option
            Intent intent = new Intent(getApplicationContext(),DeleteStudentActivity.class);
            startActivity(intent);                                         // define the next activity and go to next activity.
        } else if (id == R.id.nav_logout) {                                // if id matches with id of logout option
            new AlertDialog.Builder(this)                                             // launches new alert dialog box
                    .setIcon(android.R.drawable.ic_dialog_alert)                      // sets the icon for dialog box
                    .setTitle("Log-out")                                              // sets the title for the dailog box
                    .setMessage("Are you sure you want to logout?")                   // sets the message to be displayed
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()   // defines tasks to be performed when user clicks yes.
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {      // specifies the task to be performed when yes is clicked
                            Intent intent = new Intent(MainActivity.this, LoginActivity1.class);
                            startActivity(intent);                                    // define the next activity and go to next activity.
                            finish();                                                 // finish this activity
                        }

                    })
                    .setNegativeButton("No", null)                                    // if the user selects no, then do nothing
                    .show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);        // find the drawer for controlling
        drawer.closeDrawer(GravityCompat.START);                                      // this closes the drawer
        return true;                                                                  // return true denoting operation is complete
    }
}
