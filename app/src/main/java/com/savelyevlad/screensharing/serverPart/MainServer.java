package com.savelyevlad.screensharing.serverPart;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.widget.Button;
import android.widget.TextView;

import com.savelyevlad.screensharing.PublicStaticObjects;
import com.savelyevlad.screensharing.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Date;

@SuppressLint("Registered")
public class MainServer extends Activity {

    private Button buttonShare;
    private TextView textView;

    private MediaProjectionManager mediaProjectionManager;
    private MediaProjection mediaProjection;
    private int displayWidth;
    private int displayHeight;

    private ImageReader imageReader;
    private Handler handler;

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.server);

        buttonShare = findViewById(R.id.buttonShare);
        textView = findViewById(R.id.textViewServer);

        textView.setText("53");

        buttonShare.setOnClickListener((v) -> {
            synchronized (this) {
                textView.setText(textView.getText() + "\n56");
            }
            Log.e("kek", (PublicStaticObjects.getDatagramSocket() == null) + " ");
            IdReceiver idReceiver = new IdReceiver(PublicStaticObjects.getDatagramSocket());
            Thread thread1 = new Thread(idReceiver);
            thread1.start();
            textView.setText(textView.getText() + "\n60");
            try {
                thread1.join();
                synchronized (this) {
                    textView.setText("Your ID = " + idReceiver.getId());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Thread thread = new Thread(imageSender);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
            started = true;
            mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);

            if (mediaProjectionManager != null) {
                startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), 228);
            }
            else {
                textView.setText("Something is wrong");
            };
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        server.onDestroy();
    }

    private boolean started = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 228) {
            mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data);

            if(mediaProjection != null) {
                DisplayMetrics metrics = getResources().getDisplayMetrics();
                int density = metrics.densityDpi;
                int flags = DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY
                          | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;

                Display display = getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                displayHeight = size.y;
                displayWidth = size.x;


                imageReader = ImageReader.newInstance(size.x, size.y, PixelFormat.RGBA_8888, 2);

                mediaProjection.createVirtualDisplay("screening",
                        size.x, size.y, density,
                        flags, imageReader.getSurface(), null, handler);
                imageReader.setOnImageAvailableListener(new ImageAvailableListener(), handler);
            }
        }
    }

    private ImageSender imageSender = new ImageSender(PublicStaticObjects.getDatagramSocket(), PublicStaticObjects.getInetAddress(), PublicStaticObjects.getPORT());

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {

        @Override
        public void onImageAvailable(ImageReader imageReader) {
            Image image;
            Bitmap bitmap;

            try {
                image = imageReader.acquireLatestImage();
            }
            catch (IllegalStateException e) {
                imageSender.getQueue().add(bitmapToByteArray(oldBitmap));
                return;
            }

            if(image != null) {
                Image.Plane[] planes = image.getPlanes();
                ByteBuffer buffer = planes[0].getBuffer();

                int pixelStride = planes[0].getPixelStride();
                int rowStride = planes[0].getRowStride();
                int rowPadding = rowStride - pixelStride * displayWidth;

                // create bitmap
                bitmap = Bitmap.createBitmap(displayWidth + rowPadding / pixelStride,
                        displayHeight, Bitmap.Config.ARGB_8888);
                bitmap.copyPixelsFromBuffer(buffer);

                writeToFile(bitmap);

                if(started) {
                    imageSender.getQueue().add(bitmapToByteArray(bitmap));
                    oldBitmap = bitmap;
                }
            }
        }

        private void writeToFile(Bitmap bitmap) {
            byte[] buf = bitmapToByteArray(bitmap);
            File file1 = new File(Environment.getExternalStorageDirectory() +"/MYSCREENSHOTS");
            file1.mkdir();

            File file = new File(Environment.getExternalStorageDirectory() +
                                           "/MYSCREENSHOTS/screen" + new Date().getTime() + ".jpg");
            try {
                file.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(file);
                outputStream.write(buf);
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private Bitmap oldBitmap;

        private byte[] bitmapToByteArray(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, 5, stream);
            return stream.toByteArray();
        }
    }
}
