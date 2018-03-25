// This sample is based on "Camera calibration With OpenCV" tutorial:
// http://docs.opencv.org/doc/tutorials/calib3d/camera_calibration/camera_calibration.html
//
// It uses standard OpenCV asymmetric circles grid pattern 11x4:
// https://github.com/opencv/opencv/blob/2.4/doc/acircles_pattern.png.
// The results are the camera matrix and 5 distortion coefficients.
//
// Tap on highlighted pattern to capture pattern corners for calibration.
// Move pattern along the whole screen and capture data.
//
// When you've captured necessary amount of pattern corners (usually ~20 are enough),
// press "Calibrate" button for performing camera calibration.

package com.example.spinusoidsolutions;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import arucoCalibration.CalibrationFrameRender;
import arucoCalibration.CalibrationResult;
import arucoCalibration.CameraCalibrator;
import arucoCalibration.ComparisonFrameRender;
import arucoCalibration.OnCameraFrameRender;
import arucoCalibration.PreviewFrameRender;
import arucoCalibration.UndistortionFrameRender;

public class CameraCalibrationActivity extends AppCompatActivity implements CvCameraViewListener2, OnTouchListener {
	private static final String TAG = "OCVSample::Activity";

	private CameraBridgeViewBase mOpenCvCameraView;
	private CameraCalibrator mCalibrator;
	private OnCameraFrameRender mOnCameraFrameRender;
	private int mWidth;
	private int mHeight;
	public static final String DATA_FILEPATH = "cameraCalibrationParams";

	Mat mRgba;
	Mat mGray;
	CvCameraViewFrame mXPosedMat = new CvCameraViewFrame() {
		@Override
		public Mat rgba() {
			return mRgba;
		}

		@Override
		public Mat gray() {
			return mGray;
		}
	};

	private BaseLoaderCallback mLoaderCallback = new
			BaseLoaderCallback(this) {
				@Override
				//This is the callback method called once the OpenCV manager is connected
				public void onManagerConnected(int status) {
					switch (status) {
						//Once the OpenCV manager is successfully connected we can enable the camera interaction with the defined OpenCV camera view
						case LoaderCallbackInterface.SUCCESS:
						{
							Log.i(TAG, "OpenCV loaded successfully");
							mOpenCvCameraView.enableView();
						} break;
						default:
						{
							Log.e(TAG, "Failed to load OpenCV Manager)");
							super.onManagerConnected(status);
						} break;
					}
				}
			};

	public CameraCalibrationActivity() {
		Log.i(TAG, "Instantiated new " + this.getClass());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "called onCreate");
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//System.loadLibrary("native-lib");
		setContentView(R.layout.activity_camera_calibration);

		mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.camera_calibration_java_surface_view);
		mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
		mOpenCvCameraView.setCvCameraViewListener(this);

		Button btCapture = (Button) findViewById(R.id.capture);

		btCapture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d(TAG, "onTouch invoked");
				mCalibrator.addCorners();
			}
		});
		Button btCalibrate = (Button) findViewById(R.id.calibrateSamples);
		btCalibrate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final Resources res = getResources();
				if (mCalibrator.getCornersBufferSize() < 2) {
					(Toast.makeText(getApplicationContext(), res.getString(R.string.more_samples), Toast.LENGTH_SHORT)).show();
				}else {
					mOnCameraFrameRender = new OnCameraFrameRender(new PreviewFrameRender());
					new AsyncTask<Void, Void, Void>() {
						private ProgressDialog calibrationProgress;

						@Override
						protected void onPreExecute() {
							calibrationProgress = new ProgressDialog(CameraCalibrationActivity.this);
							calibrationProgress.setTitle(res.getString(R.string.calibrating));
							calibrationProgress.setMessage(res.getString(R.string.please_wait));
							calibrationProgress.setCancelable(false);
							calibrationProgress.setIndeterminate(true);
							calibrationProgress.show();
						}

						@Override
						protected Void doInBackground(Void... arg0) {
							mCalibrator.calibrate();
							return null;
						}

						@Override
						protected void onPostExecute(Void result) {
							calibrationProgress.dismiss();
							mCalibrator.clearCorners();
							mOnCameraFrameRender = new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
							String resultMessage = (mCalibrator.isCalibrated()) ?
									res.getString(R.string.calibration_successful)  + " " + mCalibrator.getAvgReprojectionError() :
									res.getString(R.string.calibration_unsuccessful);
							(Toast.makeText(CameraCalibrationActivity.this, resultMessage, Toast.LENGTH_SHORT)).show();

							if (mCalibrator.isCalibrated()) {
								CalibrationResult.save(CameraCalibrationActivity.this,
										mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());
								Intent intent = new Intent(CameraCalibrationActivity.this, CameraActivity.class);
								finish();
								CameraCalibrationActivity.this.startActivity(intent);
							}
						}
					}.execute();
				}
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
			OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "onCreateOptionsMenu: CREATING MENU");

		getMenuInflater().inflate(R.menu.calibration, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.findItem(R.id.preview_mode).setEnabled(true);
		if (!mCalibrator.isCalibrated())
			menu.findItem(R.id.preview_mode).setEnabled(false);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.calibration:
				mOnCameraFrameRender =
						new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
				item.setChecked(true);
				return true;
			case R.id.undistortion:
				mOnCameraFrameRender =
						new OnCameraFrameRender(new UndistortionFrameRender(mCalibrator));
				item.setChecked(true);
				return true;
			case R.id.comparison:
				mOnCameraFrameRender =
						new OnCameraFrameRender(new ComparisonFrameRender(mCalibrator, mWidth, mHeight, getResources()));
				item.setChecked(true);
				return true;
			case R.id.calibrate:
				final Resources res = getResources();
				if (mCalibrator.getCornersBufferSize() < 2) {
					(Toast.makeText(this, res.getString(R.string.more_samples), Toast.LENGTH_SHORT)).show();
					return true;
				}

				mOnCameraFrameRender = new OnCameraFrameRender(new PreviewFrameRender());
				new AsyncTask<Void, Void, Void>() {
					private ProgressDialog calibrationProgress;

					@Override
					protected void onPreExecute() {
						calibrationProgress = new ProgressDialog(CameraCalibrationActivity.this);
						calibrationProgress.setTitle(res.getString(R.string.calibrating));
						calibrationProgress.setMessage(res.getString(R.string.please_wait));
						calibrationProgress.setCancelable(false);
						calibrationProgress.setIndeterminate(true);
						calibrationProgress.show();
					}

					@Override
					protected Void doInBackground(Void... arg0) {
						mCalibrator.calibrate();
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						calibrationProgress.dismiss();
						mCalibrator.clearCorners();
						mOnCameraFrameRender = new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
						String resultMessage = (mCalibrator.isCalibrated()) ?
								res.getString(R.string.calibration_successful)  + " " + mCalibrator.getAvgReprojectionError() :
								res.getString(R.string.calibration_unsuccessful);
						(Toast.makeText(CameraCalibrationActivity.this, resultMessage, Toast.LENGTH_SHORT)).show();

						if (mCalibrator.isCalibrated()) {
							CalibrationResult.save(CameraCalibrationActivity.this,
									mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients());
							Intent intent = new Intent(CameraCalibrationActivity.this, CameraActivity.class);
							finish();
							CameraCalibrationActivity.this.startActivity(intent);
						}
					}
				}.execute();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	public void onCameraViewStarted(int width, int height) {
		if (mWidth != width || mHeight != height) {
			mWidth = width;
			mHeight = height;
			mRgba = new Mat(height, width, CvType.CV_8UC4);
			mGray = new Mat(height, width, CvType.CV_8UC4);
			mCalibrator = new CameraCalibrator(mWidth, mHeight);
			if (CalibrationResult.tryLoad(this, mCalibrator.getCameraMatrix(), mCalibrator.getDistortionCoefficients())) {
				mCalibrator.setCalibrated();
			}

			mOnCameraFrameRender = new OnCameraFrameRender(new CalibrationFrameRender(mCalibrator));
		}
	}

	public void onCameraViewStopped() {
	}

	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//Log.d(TAG, "onCameraFrame: LOL");


		Core.transpose(inputFrame.rgba(),mRgba);
		Core.transpose(inputFrame.gray(),mGray);
		//Core.flip(mRgba, mRgba, 0);
		Core.flip(mRgba, mRgba, 1);
		//Core.flip(mGray, mGray, 0);
		Core.flip(mGray, mGray, 1);

		return mOnCameraFrameRender.render(mXPosedMat);
	}

	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called");
		Intent intent = new Intent(CameraCalibrationActivity.this, CameraActivity.class);
		finish();
		startActivity(intent);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Log.d(TAG, "onTouch invoked");

		mCalibrator.addCorners();
		return false;
	}
}
