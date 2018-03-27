
package com.example.spinusoidsolutions;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.example.spinusoidsolutions.aruco.CameraParameters;
import com.example.spinusoidsolutions.aruco.Marker;
import com.example.spinusoidsolutions.aruco.MarkerDetector;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static com.example.spinusoidsolutions.CameraCalibrationActivity.DATA_FILEPATH;

public class CameraActivity extends Activity implements CvCameraViewListener2 {

    //Constants
    private static final String TAG = "CameraActivity";
    private static final float MARKER_SIZE = (float) 0.05;

    //image containers
    Mat mRgba;

    //calibration
    Button mBtLaunchActivity;

    //You must run a calibration prior to detection
    //camera parameters
    CameraParameters mCamParams;
    MarkerDetector mDetector;
    Vector<Marker> mDetectedMarkers;

    Boolean SHOW_MARKERID = true;

    Button mBtnCapture;
    Boolean mToSave;

    // The activity to run calibration is provided in the repository


    private CameraBridgeViewBase mOpenCvCameraView;

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        mOpenCvCameraView = findViewById(R.id.tutorial1_activity_java_surface_view);

        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);

        mOpenCvCameraView.setCvCameraViewListener(this);

        mBtLaunchActivity = findViewById(R.id.launch_calibrationActivity);
        mBtLaunchActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (mOpenCvCameraView != null)
                    mOpenCvCameraView.disableView();
                launchCalibrationActivity();
            }
        });

        mBtnCapture = findViewById(R.id.capture_btn);
        mBtnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToSave = true;
            }
        });



    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_3_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mRgba = new Mat(height, width, CvType.CV_8UC4);

        mDetector = new MarkerDetector();
        mDetectedMarkers = new Vector<>();
        mCamParams = new CameraParameters();
        mToSave = false;

       mCamParams.readFromFile(Environment.getExternalStorageDirectory().toString() + "/" + DATA_FILEPATH);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {

        Core.transpose(inputFrame.rgba(),mRgba);
        Core.flip(mRgba,mRgba, 1);

        //Populate detectedMarkers
        Log.d(TAG, "detecting markers");
        Imgproc.rectangle(mRgba, new Point(0,0), new Point(720,450), new Scalar(255, 255, 0, 255));
        mDetector.detect(mRgba.submat(0, 450, 0, 720), mDetectedMarkers, mCamParams, MARKER_SIZE);
        ArrayList<List<Point>> pts = new ArrayList<>();
        //Draw Axis for each marker detected
        if (mDetectedMarkers.size() != 0) {
            Log.d(TAG, "onCameraFrame: markers detected");
            for (int i = 0; i < mDetectedMarkers.size(); i++) {
                Marker marker = mDetectedMarkers.get(i);
                mDetectedMarkers.get(i).draw3dAxis(mRgba, mCamParams, new Scalar(0,0,0));

                if (SHOW_MARKERID) {
                    //Setup
                    int idValue = mDetectedMarkers.get(i).getMarkerId();
                    Vector<Point3> points = new Vector<>();
                    points.add(new Point3(0, 0, 0));
                    MatOfPoint3f pointMat = new MatOfPoint3f();
                    pointMat.fromList(points);
                    MatOfPoint2f outputPoints = new MatOfPoint2f();

                    //Project point to marker origin
                    Calib3d.projectPoints(pointMat, marker.getRvec(), marker.getTvec(), mCamParams.getCameraMatrix(), mCamParams.getDistCoeff(), outputPoints);
                    List<Point> pts1 = outputPoints.toList();

                    pts.add(outputPoints.toList());

                    //Draw id number
                    Imgproc.putText(mRgba, Integer.toString(idValue), pts1.get(0), Core.FONT_HERSHEY_SIMPLEX, 2, new Scalar(0,0,1));
                    marker.draw3dAxis(mRgba, mCamParams, new Scalar(255, 255, 0, 255));
                }
            }
        }
        if(mToSave && mDetectedMarkers.size() == 2){
            Double distance  = Math.abs((pts.get(1).get(0).y - pts.get(0).get(0).y)/(mDetectedMarkers.get(0).perimeter()/4/5));
            Log.d(TAG, "Distance between markers: " + distance + " cm.");
            //File myFile = new File(getAssets() ------------ +"/" + "SpinusoidData.json");
            //To do: add filestream stuff to overwrite/store newest distance measurement

            mToSave = false;
        }
        return mRgba;
    }

    private void launchCalibrationActivity() {
        Intent intent = new Intent(this, CameraCalibrationActivity.class);
        startActivity(intent);
    }

}

