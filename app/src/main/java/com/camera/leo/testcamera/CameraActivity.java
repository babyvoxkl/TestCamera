package com.camera.leo.testcamera;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class CameraActivity extends Activity implements OnClickListener,
        SurfaceHolder.Callback {

    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final int MEDIA_TYPE_IMAGE = 1;
    private Button switchCameraBtn, captureBtn;
    private SurfaceView surfaceSv;

    private SurfaceHolder mHolder;
    private Camera mCamera;
    // 0��ʾ���ã�1��ʾǰ��
    private int cameraPosition = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ����ʾ����
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_camera);

        findById();
        initData();
    }

    /**
     * ��ʼ��view
     */
    private void findById() {
        switchCameraBtn = (Button) this.findViewById(R.id.id_switch_camera_btn);
        captureBtn = (Button) this.findViewById(R.id.id_capture_btn);
        surfaceSv = (SurfaceView) this.findViewById(R.id.id_area_sv);

        switchCameraBtn.setOnClickListener(this);
        captureBtn.setOnClickListener(this);
    }

    /**
     * ��ʼ�����data
     */
    private void initData() {
        // ��þ��
        mHolder = surfaceSv.getHolder();
        // ��ӻص�
        mHolder.addCallback(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.checkCameraHardware(this) && (mCamera == null)) {
            // ��camera
            mCamera = getCamera();
            mCamera.setDisplayOrientation(90);
            if (mHolder != null) {
                setStartPreview(mCamera,mHolder);
            }
        }
    }

    private Camera getCamera() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            camera = null;
            Log.e(TAG, "Camera is not available (in use or does not exist)");
        }
        return camera;
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * �ǵ��ͷ�camera����������Ӧ�õ���
         */
        releaseCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * �ͷ�mCamera
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// ͣ��ԭ������ͷ��Ԥ��
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_switch_camera_btn:
                // �л�ǰ������ͷ
                int cameraCount = 0;
                CameraInfo cameraInfo = new CameraInfo();
                cameraCount = Camera.getNumberOfCameras();// �õ�����ͷ�ĸ���

                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);// �õ�ÿһ������ͷ����Ϣ
                    if (cameraPosition == 1) {
                        // �����Ǻ��ã����Ϊǰ��
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            /**
                             * �ǵ��ͷ�camera����������Ӧ�õ���
                             */
                            releaseCamera();
                            // �򿪵�ǰѡ�е�����ͷ
                            mCamera = Camera.open(i);
                            mCamera.setDisplayOrientation(90);
                            // ͨ��surfaceview��ʾȡ������
                            setStartPreview(mCamera,mHolder);
                            cameraPosition = 0;
                            break;
                        }
                    } else {
                        // ������ǰ�ã� ���Ϊ����
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            /**
                             * �ǵ��ͷ�camera����������Ӧ�õ���
                             */
                            releaseCamera();
                            mCamera = Camera.open(i);
                            mCamera.setDisplayOrientation(90);
                            setStartPreview(mCamera,mHolder);
                            cameraPosition = 1;
                            break;
                        }
                    }

                }
                break;
            case R.id.id_capture_btn:
                // ����,������ز���
                Camera.Parameters params = mCamera.getParameters();
                params.setPictureFormat(ImageFormat.JPEG);
                params.setPreviewSize(800, 400);
                // �Զ��Խ�
                params.setFocusMode(Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(params);
                mCamera.takePicture(null, null, picture);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        setStartPreview(mCamera,mHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // ��surfaceview�ر�ʱ���ر�Ԥ�����ͷ���Դ
        /**
         * �ǵ��ͷ�camera����������Ӧ�õ���
         */
        releaseCamera();
        holder = null;
        surfaceSv = null;
    }

    /**
     * ����pngͼƬ�ص����ݶ���
     */
    PictureCallback picture = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG,
                        "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
                returnResult(pictureFile);
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = HelpUtil.getDateFormatString(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".png");
        } else {
            return null;
        }
        return mediaFile;
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * activity����ʽ��������ͼƬ·��
     * @param mediaFile
     */
    private void returnResult(File mediaFile) {
        Intent intent = new Intent();
        intent.setData(Uri.fromFile(mediaFile));
        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * ����camera��ʾȡ������,��Ԥ��
     * @param camera
     */
    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}
