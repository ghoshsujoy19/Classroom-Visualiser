package org.opencv.samples.facedetect;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.text.Text;

import java.util.ArrayList;

public class DisplayImageActivity extends AppCompatActivity {

    DBImgHelper imDb;
    EditText editTextId;
    Button button;
    ImageView imgTakenPic;
    TextView numImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        imDb = new DBImgHelper(this);

        editTextId = (EditText)findViewById(R.id.editText_id);
        imgTakenPic = (ImageView)findViewById(R.id.imageView);
        button = (Button) findViewById(R.id.Button);
        numImg = (TextView) findViewById(R.id.imgCounter);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                ArrayList<Bitmap> allImages = imDb.getImg(Integer.parseInt( editTextId.getText().toString()));
                Log.d("images: ", Integer.toString(allImages.size()));
                if(!allImages.isEmpty())
                {
                    int numOfImages = allImages.size();
                    String display = "Number of images : " + numOfImages;
                    Bitmap bitmap = allImages.get(numOfImages-1);
                    imgTakenPic.setImageBitmap(bitmap);
                    numImg.setVisibility(View.VISIBLE);
                    numImg.setText(display);
                }

            }
        });
    }

}