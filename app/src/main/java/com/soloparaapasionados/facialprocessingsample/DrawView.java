package com.soloparaapasionados.facialprocessingsample;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.SurfaceView;

import com.qualcomm.snapdragon.sdk.face.FaceData;

/**
 * Created by USUARIO on 07/01/2017.
 */

public class DrawView extends SurfaceView{

    public FaceData[]mFaceArray;
    boolean _inFrame;
    private Paint leftEyeBrush=new Paint();
    private Paint rightEyeBrush= new Paint();
    private Paint mouthBrush= new Paint();
    private Paint rectBrush=new Paint();
    public Point leftEye,  rightEye, mouth;
    Rect mFaceRect;

    public DrawView(Context context, FaceData[] faceArray,boolean inFrame)
    {
        super(context);
        setWillNotDraw(false);

        mFaceArray=faceArray;
        _inFrame=inFrame;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        if(_inFrame && mFaceArray!= null)
        {
            for(int i=0; i<mFaceArray.length; i++)
            {
                leftEyeBrush.setColor(Color.RED);
                canvas.drawCircle(mFaceArray[i].leftEye.x,mFaceArray[i].leftEye.y,5f,leftEyeBrush);

                rightEyeBrush.setColor(Color.GREEN);
                canvas.drawCircle(mFaceArray[i].rightEye.x,mFaceArray[i].rightEye.y,5f,rightEyeBrush);

                mouthBrush.setColor(Color.WHITE);
                canvas.drawCircle(mFaceArray[i].mouth.x,mFaceArray[i].mouth.y,5f,mouthBrush);

                setRectColor(mFaceArray[i],rectBrush);
                rectBrush.setStrokeWidth(2);
                rectBrush.setStyle(Paint.Style.STROKE);
                canvas.drawRect(mFaceArray[i].rect.left,mFaceArray[i].rect.top,mFaceArray[i].rect.right,mFaceArray[i].rect.bottom,rectBrush);

            }
        }
        else {
            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        }
    }

    private  void setRectColor(FaceData faceData,Paint rectBrush){
        if(faceData.getSmileValue()<40){
            rectBrush.setColor(Color.RED);
        }
        else if(faceData.getSmileValue()<55){
            rectBrush.setColor(Color.parseColor("#FE642E"));
        }else if(faceData.getSmileValue()<70){
            rectBrush.setColor(Color.parseColor("#D7DF01"));
        }else if(faceData.getSmileValue()<85){
            rectBrush.setColor(Color.parseColor("#86B404"));
        }else {
            rectBrush.setColor(Color.parseColor("#5FB404"));
        }
    }
}
