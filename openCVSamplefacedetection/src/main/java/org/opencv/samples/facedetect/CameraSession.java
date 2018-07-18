package org.opencv.samples.facedetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.android.Utils;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.face.EigenFaceRecognizer;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CameraSession extends AppCompatActivity{
    DBHelper myDb;
    DBImgHelper imDb;
    EditText editTextId;
    Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cam_session);
//        myDb = new DBHelper(this);
//        imDb = new DBImgHelper(this);

        editTextId = (EditText)findViewById(R.id.courseid);
        btnConfirm= (Button)findViewById(R.id.sessionstart);
        TrainImages();
    }
    public void TrainImages() {
        btnConfirm.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Cursor result = myDb.getCourseData(editTextId.getText().toString());
//                        if(result.getCount() == 0) {
//                            // show message
////                            showMessage("Error","Nothing found");
//                            return;
//                        }
//                        ArrayList<Mat> sourceImages = new ArrayList<>();
//                        List<String> namesIndexList = new ArrayList<>();
//                        List<Integer> namesIntList = new ArrayList<>();
//                        String name;
//                        int id;
//                        result.moveToFirst();
//                        Log.d("Compilingdb:","work");
//                        while(result.moveToNext())
//                        {
//                            name = result.getString(1);
//                            id = result.getInt(0);
//                            ArrayList<Bitmap> allImages = imDb.getImg(id);
//                            namesIndexList.add(name);
//                            for(Bitmap img : allImages)
//                            {
//                                Mat mat = new Mat();
//                                Bitmap copyimg = img.copy(Bitmap.Config.ARGB_8888, true);
//                                Utils.bitmapToMat(copyimg, mat);
//                                Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);
//                                namesIntList.add(namesIndexList.indexOf(name));
//                                sourceImages.add(mat);
//                            }
//                        }
//                        Log.d("Completedindexing","done");
//                        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
//                        MatOfInt matOfInt = new MatOfInt();
//                        matOfInt.fromList(namesIntList);
//                        faceRecognizer.train(sourceImages, matOfInt);
//                        faceRecognizer.save("/home/sujoy/set.yml");
//                        Log.d("fullcomplete","rbvj");
                        Intent intent = new Intent(CameraSession.this, FdActivity.class);
                        intent.putExtra("id", editTextId.getText().toString());
                        startActivity(intent);
                    }
                }
        );
    }
}
