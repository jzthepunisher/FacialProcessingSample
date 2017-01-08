package com.soloparaapasionados.facialprocessingsample;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.support.annotation.Size;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.qualcomm.snapdragon.sdk.face.FaceData;
import com.qualcomm.snapdragon.sdk.face.FacialProcessing;



public class MainActivity extends AppCompatActivity implements Camera.PreviewCallback{
    static Camera cameraObj=null;
    FrameLayout preview;
    private CameraSurfacePreview mPreview;
    private int FRONT_CAMERA_INDEX=1;
    private int BACK_CAMERA_INDEX=0;
    private static boolean switchCamera=false;
    private boolean _qcSDKEnabled;
    FacialProcessing faceProc;
    Display display;
    private int displayAngle;
    private int numFaces;
    FaceData[]faceArray=null;
    DrawView drawView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview=(FrameLayout)findViewById(R.id.camera_preview);
        startCamera();

        Button switchCameraButton=(Button)findViewById(R.id.switchCamera);
        switchCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!switchCamera)
                {
                    stopCamera();
                    switchCamera=true;
                    startCamera();
                }else{
                    stopCamera();
                    switchCamera=false;
                    startCamera();
                }
            }
        });

        display=((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    }

    private void startCamera(){
        _qcSDKEnabled=FacialProcessing.isFeatureSupported(FacialProcessing.FEATURE_LIST.FEATURE_FACIAL_RECOGNITION);

        if(_qcSDKEnabled && faceProc==null){
            Toast.makeText(this,"Feature is supported",Toast.LENGTH_LONG).show();
            faceProc=FacialProcessing.getInstance();

        }else if(!_qcSDKEnabled){
            Toast.makeText(this,"Feature Processing Not is Supported",Toast.LENGTH_LONG).show();

        }


        if(!switchCamera){
            cameraObj=Camera.open(FRONT_CAMERA_INDEX);
        }else{
            cameraObj=Camera.open(BACK_CAMERA_INDEX);
        }


        //cameraObj=Camera.open(FRONT_CAMERA_INDEX);
        mPreview=new CameraSurfacePreview(MainActivity.this,cameraObj);
        preview=(FrameLayout)findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        cameraObj.setPreviewCallback(MainActivity.this);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {

        numFaces=faceProc.getNumFaces();
        if(numFaces>0)
        {
            Log.d("TAG","Face Detected");
            faceArray=faceProc.getFaceData();
        }

        int dRotation =display.getRotation();
        FacialProcessing.PREVIEW_ROTATION_ANGLE angleEnum= FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_0;
        switch (dRotation)
        {
            case 0:
                displayAngle=90;
                angleEnum= FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_90;
                break;
            case 1:
                displayAngle=0;
                angleEnum= FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_0;
                break;
            case 2:
                displayAngle=270;
                angleEnum= FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_270;
                break;
            case 3:
                displayAngle=180;
                angleEnum= FacialProcessing.PREVIEW_ROTATION_ANGLE.ROT_180;
                break;
        }
        cameraObj.setDisplayOrientation(displayAngle);

        if(_qcSDKEnabled)
        {
            if(faceProc==null){
                faceProc=FacialProcessing.getInstance();
            }

            Camera.Parameters params=cameraObj.getParameters();
            Camera.Size previewSize=  params.getPreviewSize();

            if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE && !switchCamera){
                faceProc.setFrame(bytes,previewSize.width,previewSize.height,true,angleEnum);
            }else if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE && switchCamera){
                faceProc.setFrame(bytes,previewSize.width,previewSize.height,false,angleEnum);
            }else  if(this.getResources().getConfiguration().orientation==Configuration.ORIENTATION_PORTRAIT && !switchCamera){
                faceProc.setFrame(bytes,previewSize.width,previewSize.height,true,angleEnum);
            }else
            {
                faceProc.setFrame(bytes,previewSize.width,previewSize.height,false,angleEnum);
            }
        }

        int surfaceWidth=mPreview.getWidth();
        int surfaceHeight=mPreview.getHeight();
        faceProc.normalizeCoordinates(surfaceWidth,surfaceHeight);

        if(numFaces>0)
        {
            Log.d("TAG","Face Detected");
            faceArray=faceProc.getFaceData();
            preview.removeView(drawView);

            drawView=new DrawView(this,faceArray,true);
            preview.addView(drawView);
        }else
        {
            preview.removeView(drawView);
            drawView=new DrawView(this,null,false);
            preview.addView(drawView);
        }
    }

    private void stopCamera(){
        if(cameraObj!=null)
        {
            cameraObj.stopPreview();
            cameraObj.setPreviewCallback(null);
            preview.removeView(mPreview);
            cameraObj.release();
            if(_qcSDKEnabled){
                faceProc.release();
                faceProc=null;
            }

        }
        cameraObj=null;
    }

    @Override
    protected void onPause(){
        super.onPause();
        stopCamera();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(cameraObj!=null){
            stopCamera();
        }

        startCamera();

    }

}
