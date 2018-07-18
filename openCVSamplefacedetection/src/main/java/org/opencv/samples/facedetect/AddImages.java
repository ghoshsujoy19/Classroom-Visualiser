/*
<header>
Module Name          : AddImagesActivity
Date of Creation     : 10-04-2018
Author               : Sujoy Ghosh

Modification History : 11-04-2018 :- added onBackPressed() function to prevent functioning of "Back" key.
                       11-04-2018 :- added 'Image Counter' to limit the number of images.

Synopsis             : This module is responsible for adding the training images for face recognition.

Global Variables     : None

Functions            : void onCreate(Bundle)
                       void onResume()
                       void addImage()
                       void onActivityResult(int, int, Intent)
                       void onBackPressed()
                       void onClick(View)
</header>
*/


package org.opencv.samples.facedetect;

// importing package for android
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddImages extends AppCompatActivity{
    private static final int CAM_REQUEST = 1313;              // used for checking whether photo has been clicked or not.
    private static final String TAG = "OCVSample::Activity";  // used for debugging purpose only.
    private static final int IMG_COUNT = 15;                  // specifies total number of photos to be clicked.
    private int tempCounter = 0;                              // used for counting the number of clicked images.
    private String id;                                        // stores the Roll-Number of the new student as String.
    private DBImgHelper imDb;                                 // database containing students' record.
    private Button button;                                    // controlling buttons.
    private TextView txtview;                                 // controlling text to be displayed.
    private ImageView imgShow;                                // controlling image to be displayed.


    @Override                                                 // this functions overrides the function defined in the parent class "AppCompatActivity".
    protected void onCreate(Bundle savedInstanceState) {      // this method is called when the activity is first created.
        super.onCreate(savedInstanceState);                   // creates the activity.
        setContentView(R.layout.layout);                      // sets the content UI to be displayed on screen.
        id = getIntent().getExtras().getString("id");         // fetches the roll number of the corresponding student for whom images are to be added.
        tempCounter = 0;                                      // initialising tempCounter.
        imDb = new DBImgHelper(this);                         // creating instance of the students' database.
        imgShow = (ImageView)findViewById(R.id.showImg);      // creating instance of ImageView object.
        button = (Button) findViewById(R.id.CaptureImg);      // creating instance of Button object.
        txtview = (TextView) findViewById(R.id.imagesLeft);   // creating instance of TextView object.
    }

    @Override                                                         // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onResume() {                                          // this method is called when the activity is running.
        super.onResume();                                             // creates the activity.
        if (tempCounter != IMG_COUNT) {                               // checks whether number of photos equal to total photo to be closed.
            addImage();                                               // if not, then call the addImage() to add new image
        } else {                                                      // else, if they are equal
            button.setText("Return to Main Menu");                    // Modify the displayed text of Button.
        }
        if (tempCounter == IMG_COUNT) {                               // if number of photos equal to total photo to be closed.
            button.setOnClickListener(                                // new process starts if the button is clicked.
                    new View.OnClickListener() {                          // this creates the constructor for onClick().
                        @Override                                         // overrides the default function defined in the parent class "AppCompatActivity".
                        public void onClick(View v) {                     // this function defines the tasks to be done when button is clicked.
                            // this defines the next activity to start if images have been added.
                            Intent nextPage = new Intent(AddImages.this,
                                    MainActivity.class);
                            startActivity(nextPage);                      // starts the next activity/opens the next screen view.
                            finish();                                     // finishes this activity, cleaning all data and memory.
                        }
                    }
            );
        }
    }

    public void addImage() {                              // this method is responsible for taking new image.
        button.setOnClickListener(                        // if Button is clicked, then it calls the camera for taking photo.
                new AddImages.btnTakePhotoClicker()       // Camera is being called using this method.
        );
    }

    @Override                                                                                   // overrides the default function defined in the parent class "AppCompatActivity".
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {             // this method is responsible for accepting images and storing them in the database.
        super.onActivityResult(requestCode, resultCode, data);                                  // this method is called when the activity is running.

        if(requestCode == CAM_REQUEST) {                                                        // if the photo has been accepted by the user.
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");                              // this fetches the photo and stores into a variable.
            boolean isInserted = imDb.insertImg(Integer.parseInt(id), bitmap);                  // inserts the image into database and checks whether it is properly inserted
            if(isInserted) {                                                                    // if it is properly inserted.
                tempCounter++;                                                                  // increment tempCounter.
                imgShow.setImageBitmap(bitmap);                                                 // image is being displayed onto screen.
                int photoLeft = IMG_COUNT - tempCounter;                                        // calculates the number of photo left to be inserted.
                txtview.setText(Integer.toString(photoLeft));                                   // displays the number of photo left to be clicked onto screen.
                Toast.makeText(AddImages.this, "Image Inserted", Toast.LENGTH_LONG).show();     // displays acceptance message.
            } else {                                                                            // if not accepted
                Toast.makeText(AddImages.this,"Image not Inserted",Toast.LENGTH_LONG).show();   // display the text "Image not inserted" onto screen.
            }
        }
    }

    class btnTakePhotoClicker implements  Button.OnClickListener {                    // this is the class defining the camera activity.
        @Override                                                                     // overrides the default function defined in the parent class "AppCompatActivity".
        public void onClick(View view) {                                              // this method is responsible for clicking photo. It is called when "Capture" button of Camera is clicked.
            Intent isPhotoAcceptable = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);   // this defines the next activity to start if image has been clicked.
            startActivityForResult(isPhotoAcceptable, CAM_REQUEST);                   // starts the next activity/opens the next screen view.
        }
    }

    @Override                                                       // overrides the default function defined in the parent class "AppCompatActivity".
    public void onBackPressed() {                                   // this method is responsible for not letting the user going back without completing the required number of images
        // this shows the text if someone presses the back button.
        Toast.makeText(AddImages.this, "You cannot go back until the process completes", Toast.LENGTH_LONG).show();
    }
}
