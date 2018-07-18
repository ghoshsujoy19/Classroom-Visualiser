/*
<header>
Module Name          : FaceRecognitionActivity
Date of Creation     : 15-04-2018
Author               : Sujoy Ghosh

Modification History : added LBPHFaceRecogniser() instead of FisherFaceRecogniser()

Synopsis             : This module is responsible for adding new student records.

Global Variables     : None

Functions            : public FdActivity()
                       public void onManagerConnected(int)
                       public void onCreate(Bundle)
                       public void onPause()
                       public void onResume()
                       public void onDestroy()
                       public void onCameraViewStarted(int, int)
                       public void onCameraViewStopped()
                       private Scalar getBoxColor(int)
                       private long getTimeDiff(String, String)
                       public void training()
                       public void onBackPressed()
                       public Mat onCameraFrame(CvCameraViewFrame)
                       public boolean onCreateOptionsMenu(Menu)
                       private void setDetectorType(int)
                       private void setMinFaceSize(float)
                       public boolean onOptionsItemSelected(MenuItem)
</header>
*/

package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.face.FaceRecognizer;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;
import org.opencv.face.LBPHFaceRecognizer;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

public class FdActivity extends Activity implements CvCameraViewListener2 {

    private static final String    TAG                 = "OCVSample::Activity";      // for debugging purposes
    public static final int        JAVA_DETECTOR       = 0;                          // for specifying it is java detector
    public static final int        NATIVE_DETECTOR     = 1;                          // for specifying it is native/c++ based detector

    private MenuItem               mItemFace50;                                      // specifies menuitem 50%
    private MenuItem               mItemFace40;                                      // specifies menuitem 40%
    private MenuItem               mItemFace30;                                      // specifies menuitem 30%
    private MenuItem               mItemFace20;                                      // specifies menuitem 20%
    private MenuItem               mItemType;                                        // specifies detector type
    private Mat                    mRgba;                                            // stores rgba form of camera view
    private Mat                    mGray;                                            // stores gray form of camera view
    private File                   mCascadeFile;                                     // used for loading cascade file
    private CascadeClassifier      mJavaDetector;                                    // specifies java detector
    private DetectionBasedTracker  mNativeDetector;                                  // specifies c++ detector

    private int                    mDetectorType       = JAVA_DETECTOR;              // initialise detector type
    private String[]               mDetectorName;                                    // stores possible detectors

    private float                  mRelativeFaceSize   = 0.2f;                       // initialise relative face size
    private int                    mAbsoluteFaceSize   = 0;                          // specifies absolute face size

    private CameraBridgeViewBase   mOpenCvCameraView;                                // for connecting opencv with camera
    String id;                                       // store the roll id
    ArrayList<Mat> sourceImages;                     // stores the images
    List<Integer> personIndexList;                   // stores indices of person
    List<Integer> namesIntList;                      // stores roll number of person
    HashMap<Integer, Integer> boundaryBoxColor;      // stores box color of person
    HashMap<Integer, String> personName;             // stores name of person
    HashMap<Integer, String> lastModifiedTime;       // stores last modified time of person
    DBHelper myDb;                                   // instance of student database
    DBImgHelper imDb;                                // instance of student image database
    boolean isTrainComplete = false;                 // specifies if training is complete

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {    // this calls the opencv manager to load opencv modules
        @Override                                                                   // this overrides the method declared in parent class of opencv
        public void onManagerConnected(int status) {                                // this method handles the connection of camera with opencv
            switch (status) {                                                       // based on status of connection with opencv manager, it chooses different paths
                case LoaderCallbackInterface.SUCCESS:                               // if the conection is successful
                {
                    // Log.i(TAG, "OpenCV loaded successfully");
                    System.loadLibrary("detection_based_tracker");                  // load the native c++ based tracker

                    try {
                        // load cascade file from application resources
                        InputStream is = getResources().openRawResource(R.raw.haarcascade_frontalface_default);
                        File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);  // create a directory named "cascade"
                        // create a new file for storing haar_cascade classifier
                        mCascadeFile = new File(cascadeDir, "haarcascade_frontalface_default.xml");
                        FileOutputStream os = new FileOutputStream(mCascadeFile);   // for writing into the file

                        byte[] buffer = new byte[4096];                             // reading bytes from original haarcascade
                        int bytesRead = 0;                                          // number of bytes read from file
                        while ((bytesRead = is.read(buffer)) != -1) {               // if the file has not become empty yet
                            os.write(buffer, 0, bytesRead);                         // write the contents into new file
                        }
                        is.close();                                                 // close the main file
                        os.close();                                                 // close the new file
                        // load the cascade classifier file for face detection
                        mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
                        if (mJavaDetector.empty()) {                                // if there is no such file
                            // Log.e(TAG, "Failed to load cascade classifier");
                            mJavaDetector = null;                                   // make this variable null
                        }
                        // load the c++ based detection
                        mNativeDetector = new DetectionBasedTracker(mCascadeFile.getAbsolutePath(), 0);
                        cascadeDir.delete();                                        // delete the cascade directory

                    } catch (IOException e) {                                         // if there is any sort of input/output error
                        e.printStackTrace();                                        // print the error onto error stack
                        // Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
                    }

                    mOpenCvCameraView.enableView();                                 // start the opencv camera
                }
                break;
                default: {                                                          // for default cases
                    super.onManagerConnected(status);                               // call the parent onManagerConnected() method.
                }
                break;
            }
        }
    };

    FaceRecognizer faceRecognizer;                                                  // face recogniser that stores the training set
    public FdActivity() {                                                           // constructor for the class
        mDetectorName = new String[2];                                              // declare different types of detector
        mDetectorName[JAVA_DETECTOR] = "Java";                                      // first is java detector
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";                       // second is the c++ based detector

    }

    @Override                                                                  // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onCreate(Bundle savedInstanceState) {                          // this method is called when the activity is first created
        // Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);                                    // creates the activity.
        id = getIntent().getExtras().getString("id");                          // extract course id from previous activity
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);  // set screen on always during this activity
        setContentView(R.layout.face_detect_surface_view);                     // set the activity layout
        // set the camera view
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);         // set the camera on
        mOpenCvCameraView.setCvCameraViewListener(this);                       // now start the camera recording
    }

    @Override                                          // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onPause() {                            // this method handles the tasks to be done on pausing
        super.onPause();                               // run the super class pause function
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();           // stop the camera
        }
    }

    @Override                                          // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onResume() {                           // this method handles the tasks to be done on running of activity
        super.onResume();                              // resume the activity
        if (!OpenCVLoader.initDebug()) {               // load the opencv loader inside the project
            // Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            // load the opencv java handler
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_4_0, this, mLoaderCallback);
        } else {
            // load the opencv manager installed on the android phone
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {                          // this method handles the taks to be done when it exits
        super.onDestroy();                             // destroy the activity
        mOpenCvCameraView.disableView();               // stop the camera
    }

    public void onCameraViewStarted(int width, int height) { // this function specifies the tasks to be done when camera starts
        mGray = new Mat();                                   // create empty mat
        mRgba = new Mat();                                   // create empty mat
    }

    public void onCameraViewStopped() {                      // this function specifies the tasks to be done when camera stops
        mGray.release();                                     // clear the memory used
        mRgba.release();                                     // clear the memory used
    }

    private Scalar getBoxColor(int boxColor) {               // this method return the colour value corresponding to box color value
        if(boxColor < 5) {                                   // if less than 5, return red
            return new Scalar(255, 0, 0);
        } else if(boxColor < 8) {                            // if between 5 and 7, return blue
            return new Scalar(0, 0, 255);
        } else {                                             // return green
            return new Scalar(0, 255, 0);
        }
    }

    private String getTime() {                               // for getting time
        // extract time in format as specified
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Date date = new Date();                              // initialise a date variable
        String currentTime = dateFormat.format(date);        // extract the time
//        Log.d(TAG, nd);
        return currentTime;                                  // return the time
    }

    private long getTimeDiff(String globalTime, String playerTime) { // for getting the time difference
        // extract time in format as specified
        DateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {                                                        // need to insert into try-catch since time difference maynot be always defined
            Date date1 = df.parse(globalTime);                       // convert string to time
            Date date2 = df.parse(playerTime);                       // convert string to time
            return date1.getTime()-date2.getTime();                  // return their difference
        } catch (ParseException e) {                                 // if there is any exception
            // Log.d(TAG, "error in timediff");
            return 0;                                                // return 0
        }
    }

    public void training() {                                         // this method handles the training of face recogniser
        myDb = new DBHelper(this);                                   // instance of the student record database
        imDb = new DBImgHelper(this);                                // instance of the student image database

        Cursor result = myDb.getCourseData(id);                      // get the students belonging to particular database
        Mat imgs = new Mat();                                        // instantiate the images variable
        if(result.getCount() == 0) {                                 // if there is no student in course
            finish();                                                // finish the activity
        } else {
            sourceImages = new ArrayList<>();                        // instantiate the image source variable
            personIndexList = new ArrayList<>();                     // instantiate the person index list variable
            namesIntList = new ArrayList<>();                        // instantiate the names int list variable
            boundaryBoxColor = new HashMap<>();                      // instantiate the box color list variable
            personName = new HashMap<>();                            // instantiate the person name variable
            lastModifiedTime = new HashMap<>();                      // instantiate the modified time list variable

            String name, modifiedTime;
            int id, boxColor;
            result.moveToFirst();                                    // move the cursor to top
            do{                                                      // iterate over all person in the given course id
                name = result.getString(1);                          // extract name
                id = result.getInt(0);                               // extract roll number
                modifiedTime = result.getString(4);                  // extract modified time
                boxColor = result.getInt(5);                         // extract box color
                ArrayList<Bitmap> allImages = imDb.getImg(id);       // extract all images
                personIndexList.add(id);                             // put roll number to list
                personName.put(id, name);                            // put roll-name to hashmap
                boundaryBoxColor.put(id, boxColor);                  // put roll-boxcolor to hashmap
                lastModifiedTime.put(id, modifiedTime);              // put roll-modified time to hashmap

                for (Bitmap img : allImages) {                       // iterate over all images
                    Mat mat = new Mat();                             // store the images
                    // convert into mat
                    Bitmap copyimg = img.copy(Bitmap.Config.RGB_565, true);
                    Utils.bitmapToMat(copyimg, mat);                 // convert bitmap into mat
                    // convert rgba to gray
                    Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
                    imgs = mat;                                      // store the image
                    MatOfRect faces = new MatOfRect();               // insert into mat of rectangles
                    // extract the face from the image
                    mJavaDetector.detectMultiScale(mat, faces, 1.1, 2, 2,
                            new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());

                    Rect[] facesArray = faces.toArray();             // used for getting boundaries
                    if(facesArray.length > 0) {                      // if there is any face detected in photo
                        // create empty rectangle
                        Rect roi = new Rect(facesArray[0].tl(), facesArray[0].br());
                        Mat cropped = new Mat(mat, roi);             // crop the face from image
                        // add it to person id
                        namesIntList.add(personIndexList.indexOf(id));
                        sourceImages.add(cropped);                   // add image to source file
                    }
                }
            }while (result.moveToNext());
            faceRecognizer = LBPHFaceRecognizer.create();            // instantiate the lbph recogniser
            MatOfInt matOfInt = new MatOfInt();                      // create empty mat of int
            matOfInt.fromList(namesIntList);                         // convert list to mat of int
            faceRecognizer.train(sourceImages, matOfInt);            // train the face recogniser
            isTrainComplete = true;                                  // make isTrainComplete to true
        }
    }

    @Override                                      // this functions overrides the function defined in the parent class "AppCompatActivity".
    public void onBackPressed() {                  // this method handles the updation of database when back is pressed
        for(Integer rollNumber : personIndexList) {// iterating over all persons
            // update the modified time and box color
            boolean isDone = myDb.updateModifiedTime(rollNumber.longValue(), boundaryBoxColor.get(rollNumber), lastModifiedTime.get(rollNumber));
            if(!isDone) {                          // if updation is not done
                // show "not updated" onto screen
                Toast.makeText(getApplicationContext(), "Not Updated", Toast.LENGTH_LONG).show();
            }
        }
        finish();                                  // finish the activity
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {                   // this method handles the recognition part
        mRgba = inputFrame.rgba();											   // extract the rgba form of input frame
        mGray = inputFrame.gray();                                             // extract the gray form of input frame
        if(!isTrainComplete) {                                                 // if the training is not complete, then train the dataset
            training();
        }
        String globalTime = getTime();                                         // get the time of running this frame
        if (mAbsoluteFaceSize == 0) {                                          // if absolute face size set to 0
            int height = mGray.rows();                                         // get the height of frame
            if (Math.round(height * mRelativeFaceSize) > 0) {                  // if new face size is not 0, then make it new face size
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);                 // set the face size for native detector
        }

        MatOfRect faces = new MatOfRect();                                     // creating empty mat of rect

        if (mJavaDetector != null) {                                           // if there is java detector
            mJavaDetector.detectMultiScale(mGray, faces, 1.1, 2, 2,
                    new Size(mAbsoluteFaceSize, mAbsoluteFaceSize), new Size());// set the detector with face size
        } else {                                                               // if not
            finish();                                                          // then finish the activity
        }
        int x=0,y=0;
        Rect[] facesArray = faces.toArray();                                   // stores the corner of detected faces
        for (int i = 0; i < facesArray.length; i++) {                          // iterating over all faces detected
            x = facesArray[i].x;
            y = facesArray[i].y;
            Rect roi = new Rect(facesArray[i].tl(), facesArray[i].br());       // create empty rectangle
            Mat cropped = new Mat(mGray, roi);                                 // crop the face
            int[] label = new int[1];                                          // used for storing label of identified person
            double[] conf = new double[1];                                     // confidence value
            try {                                                              // predict the face
                faceRecognizer.predict(cropped, label, conf);
            } catch (Exception e) {
                Log.d(TAG, "Nothing");
            }
            Random randomVal = new Random();                                   // used for generating random values
            // criteria for unknown face
            if(label[0] < 0 || conf[0] > 150.0) {                              // surround white rectangle with "unknown" lable
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(255,255,255), 2);
                Imgproc.putText(mRgba,"Unknown "+conf[0],new Point(x,y),Core.FONT_HERSHEY_PLAIN, 1.0, new Scalar(255,255,255));
            } else {
                int roll = personIndexList.get(label[0]);                      // get the roll number of the student
                String playerTime = lastModifiedTime.get(roll);                // get time of checking
                int boxColor = boundaryBoxColor.get(roll);                     // get the boxcolor
                if(getTimeDiff(globalTime, playerTime) > 60000) {              // if the time differnce is greater than 1 minute, change the boxcolor and modification time
                    int newValue = randomVal.nextInt(9) + 1;
                    boxColor = newValue;
                    lastModifiedTime.put(roll, globalTime);
                    boundaryBoxColor.put(roll, newValue);
                }                                                              // put rectangle of corresponding color with label
                Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), getBoxColor(boxColor), 2);
                Imgproc.putText(mRgba,personName.get(roll)+" "+conf[0],new Point(x,y),Core.FONT_HERSHEY_PLAIN, 1.0, getBoxColor(boxColor));
            }
        }
        return mRgba;                                                          // return the final camera frame
    }

    @Override                                                  // this functions overrides the function defined in the parent class "Menu".
    public boolean onCreateOptionsMenu(Menu menu) {            // this method handles the creation of menu for face size
        // Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Face size 50%");               // add the menu item with size 50%
        mItemFace40 = menu.add("Face size 40%");               // add the menu item with size 40%
        mItemFace30 = menu.add("Face size 30%");               // add the menu item with size 30%
        mItemFace20 = menu.add("Face size 20%");               // add the menu item with size 20%
        mItemType   = menu.add(mDetectorName[mDetectorType]);  // add the menu item for detector type
        return true;                                           // return true when complete
    }

    @Override                                                  // this functions overrides the function defined in the parent class "Menu".
    public boolean onOptionsItemSelected(MenuItem item) {      // this method handles the work to be done when a particular option is selected

        if (item == mItemFace50) {                             // if face size 50% is seleted, set face size to 50%
            setMinFaceSize(0.5f);
        } else if (item == mItemFace40) {                      // if face size 40% is seleted, set face size to 40%
            setMinFaceSize(0.4f);
        } else if (item == mItemFace30) {                      // if face size 30% is seleted, set face size to 30%
            setMinFaceSize(0.3f);
        } else if (item == mItemFace20) {                      // if face size 20% is seleted, set face size to 20%
            setMinFaceSize(0.2f);
        } else if (item == mItemType) {                        // if detection type is selected then set the detector as selected
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);     // set the new menu option title
            setDetectorType(tmpDetectorType);                  // set detector type
        }
        return true;
    }

    private void setMinFaceSize(float faceSize) {              // this method is used to set face size for detection
        mRelativeFaceSize = faceSize;                          // set relative face size
        mAbsoluteFaceSize = 0;                                 // set absolute face size to 0
    }

    private void setDetectorType(int type) {                   // this method handles native detector start/stop
        if (mDetectorType != type) {                           // if type is not set, then set it
            mDetectorType = type;                              // set the detector type to as given detector type

            if (type == NATIVE_DETECTOR) {                     // if type is native detector
                mNativeDetector.start();                       // start the native detector
            } else {
                mNativeDetector.stop();                        // if not of native detector, then stop it.
            }
        }
    }
}
