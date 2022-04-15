package com.example.sgp6th;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.sgp6th.ml.BestModel;


import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;

import org.tensorflow.lite.support.common.TensorProcessor;

import org.tensorflow.lite.support.image.TensorImage;

import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {

    Button select,pred;
    ImageView img;
    Bitmap bt;
    TextView tv;

    Interpreter inter;
    TensorImage inputImageBuffer;
    ArrayList<String> Als =new ArrayList<String>();
    TensorProcessor ProbPro;
    TensorBuffer OPB;
    int ind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv =findViewById(R.id.tv);
        img = (ImageView) findViewById(R.id.imageView);
        select = findViewById(R.id.select);
        pred = findViewById(R.id.show);


        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(Intent.ACTION_GET_CONTENT);
                in.setType("image/*");
                startActivityForResult(in,100);
            }
        });

        pred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt = Bitmap.createScaledBitmap(bt, 256, 256, true);


                try {
                    BestModel model = BestModel.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 256, 256, 3}, DataType.FLOAT32);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bt);
                    ByteBuffer byteBuffer = tensorImage.getBuffer();


                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    BestModel.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();

                    System.out.println(outputFeature0.getDataType());
                    float ff[] = new float[outputFeature0.getFloatArray().length];
                    for(int pp = 0 ; pp<outputFeature0.getFloatArray().length;pp++) {
                        ff[pp] = outputFeature0.getFloatValue(pp);

                    }
                    float index = maximum(ff);
                    System.out.println(index);
                    Context cn = MainActivity.this;
                    InputStream in=null;
                    try{
                         in =  cn.getAssets().open("best_model2.txt");
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }

                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    String receiveString = "";

                    try {
                    while ((receiveString = reader.readLine()) != null) {

                        Als.add(receiveString);


                    }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                    in.close();
                    tv.setText(Als.get((int) index));







                } catch (IOException e) {
                    // TODO Handle the exception
                }



            }
        });





            }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 100)
        {
            img.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                bt = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static float maximum(float[] array) {
        int a=0;
        if (array.length <= 0)
            throw new IllegalArgumentException("The array is empty");

        float max = array[0];
        for (int i = 1; i < array.length; i++)
            if (array[i] > max) {
                max = array[i];
                System.out.println("max is"+max);
                System.out.println("index is "+i);
                a = i;
            }
        return a;
    }

    public static String label(String[] array,int in) {
        int a=0;
        if (array.length <= 0)
            throw new IllegalArgumentException("The array is empty");

         String max = new String();
        for (int i = 1; i < array.length; i++)
            if (i == in ) {
                max = array[i];

            }
        return max;
    }
}























