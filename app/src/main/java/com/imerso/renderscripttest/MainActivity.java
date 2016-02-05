package com.imerso.renderscripttest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;


        public static final int W = 540;
        public static final int H = 360;
        private RenderScript rs;
        private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    Bitmap myBitmap;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
            File newdir = new File(dir);
            newdir.mkdirs();

            Button capture = (Button) findViewById(R.id.btnCapture);
            capture.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    // Here, the counter will be incremented each time, and the
                    // picture taken by camera will be stored as 1.jpg,2.jpg
                    // and likewise.
                    count++;
                    String file = dir + count + ".jpg";
                    File newfile = new File(file);
                    try {
                        newfile.createNewFile();
                    } catch (IOException e) {
                    }

                    Uri outputFileUri = Uri.fromFile(newfile);

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                    startActivityForResult(cameraIntent, TAKE_PHOTO_CODE);

                    if(newfile.exists()){

                        myBitmap = BitmapFactory.decodeFile(newfile.getAbsolutePath());

                        ImageView myImage = (ImageView) findViewById(R.id.TopImage);

                        myImage.setImageBitmap(myBitmap);

                    }
                }
            });

            ImageView iv = (ImageView) findViewById(R.id.BotImage);
            rs = RenderScript.create(this);
            yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));


            Type.Builder yuvType = new Type.Builder(rs, Element.U8_4(rs))
                    .setX(W).setY(H)
                    .setYuvFormat(android.graphics.ImageFormat.NV21);
            Allocation in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);


            Type.Builder rgbaType = new Type.Builder(rs, Element.U8_4(rs))
                    .setX(W).setY(H);
            Allocation out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);

            byte[] yuvByteArray = new byte[291600];
            byte[] outBytes = new byte[W * H * 4];

            in.copyFrom(myBitmap.);

            yuvToRgbIntrinsic.setInput(in);
            yuvToRgbIntrinsic.forEach(out);

            out.copyTo(outBytes);

            Bitmap bmpout = Bitmap.createBitmap(W, H, Bitmap.Config.ARGB_8888);
            out.copyTo(bmpout);

            iv.setImageBitmap(bmpout);
        }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
