package arucoCalibration;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;

public class CalibrationFrameRender extends arucoCalibration.FrameRender {
    public CalibrationFrameRender(arucoCalibration.CameraCalibrator calibrator) {
        mCalibrator = calibrator;
    }

    @Override
    public Mat render(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat rgbaFrame = inputFrame.rgba();
        Mat grayFrame = inputFrame.gray();
        mCalibrator.processFrame(grayFrame, rgbaFrame);

        return rgbaFrame;
    }
}
