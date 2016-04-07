package com.example.parmiss.finalproject;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import my.bgmailer.lib.Mailer;
import my.bgmailer.lib.MailerBackgroundImpl;

public class PhotoTakingService extends Service {
    String emailTo="";



    @Override
    public void onCreate() {
        super.onCreate();
        // takePhoto(this);
    }


    public int onStartCommand(Intent intent, int flags, int startId) {
        emailTo = intent.getStringExtra("email");
        takePhoto(this);

        return START_STICKY;
    }



    @SuppressWarnings("deprecation")
    private void takePhoto(final Context context) {
        final SurfaceView preview = new SurfaceView(context);
        SurfaceHolder holder = preview.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
                showMessage("Surface created");

                Camera camera = null;

                try {
                    camera = Camera.open();
                    showMessage("Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
                    showMessage("Started preview");

                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            File pictureFile = getOutputMediaFile();

                            if (pictureFile == null) {
                                return;
                            }
                            try {
                                //write the file
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                fos.write(data);
                                fos.close();

                            } catch (FileNotFoundException e) {
                            } catch (IOException e) {
                            }

                            showMessage("Took picture");
                            send("Ultimate Baby Moniter Picture ", pictureFile.getAbsolutePath(), "picture.jpg");

//                            Uri U = Uri.fromFile(pictureFile);
//
//                            //Intent sendIntent = new Intent(Intent.ACTION_SEND);
//                            //sendIntent.putExtra("sms_body", "some text");
//                            //sendIntent.putExtra(Intent.EXTRA_STREAM, U);
//                            //sendIntent.setType("image/jpg");
//
//                            //startActivity(sendIntent);

                            camera.release();


                        }

                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });



        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, //Must be at least 1x1
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                0,
                //Don't know if this is a safe default
                PixelFormat.UNKNOWN);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(preview, params);
    }

    private static void showMessage(String message) {
        Log.i("CameraStages", message);
    }


    private static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File("/sdcard/", "UBM Camera");

        //if this "JCGCamera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }


    @Override public IBinder onBind(Intent intent) { return null; }




    public void send(String subject, String FilePath, String FileName){
        Mailer m = new MailerBackgroundImpl(this);
        String[] address = { emailTo };
        m.addRecepients(address);
        m.setUserName("ultimatebabymoniter");
        m.setPassword("pewpewpew");
        m.setSubject(subject);
        m.setBody(getBaseContext().getString(R.string.email_msg));
        m.addAttachment(FilePath, FileName);
        m.send();
    }


}