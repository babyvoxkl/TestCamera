package com.camera.leo.testcamera;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private Button goCameraBtn;
    private ImageView showCameraIv;
    private static final int CAMERA_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        goCameraBtn = (Button)this.findViewById(R.id.id_go_camera_btn);
        goCameraBtn.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                processGoCamera();
            }

        });

        showCameraIv = (ImageView)this.findViewById(R.id.id_show_camera_iv);
        showCameraIv.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {
                processShowCamera(v);
            }

        });
    }

    /**
     * �������camera�¼�
     */
    private void processGoCamera(){
        Intent intent = new Intent();
        intent.setClass(this, CameraActivity.class);
        startActivityForResult(intent,CAMERA_CODE);
    }

    /**
     * ����ͼƬ��ת����Ԥ������
     */
    private void processShowCamera(View v){
        Intent intent = new Intent();
        intent.setClass(this, PreviewActivity.class);
        /**
         * ��ͼƬurl����PreviewActivity
         */
        intent.putExtra("cameraUrl", v.getContentDescription().toString());
        startActivity(intent);
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(RESULT_OK == resultCode){
            if(CAMERA_CODE == requestCode){
                /**
                 * ��ȡactivity���ص�url
                 */
                Uri uri = data.getData();
                String url = uri.toString().substring(uri.toString().indexOf("///")+2);
                if(url != null && !TextUtils.isEmpty(url)){
                    showCameraIv.setContentDescription(url);
                    showCameraIv.setImageBitmap(HelpUtil.getBitmapByUrl(url));
                }
            }
        }

    }

}
