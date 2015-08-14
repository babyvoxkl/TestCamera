package com.camera.leo.testcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class PreviewActivity extends Activity {

    private ImageView previewCameraIv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_preview);


        previewCameraIv = (ImageView)this.findViewById(R.id.id_preview_camera_iv);

        Intent intent = this.getIntent();
        String cameraUrl = intent.getStringExtra("cameraUrl").toString();
        if(cameraUrl != null && !TextUtils.isEmpty(cameraUrl)){
            previewCameraIv.setImageBitmap(HelpUtil.getBitmapByUrl(cameraUrl));
        }else{
            Toast.makeText(this, "Í¼Æ¬Â·¾¶´íÎó", Toast.LENGTH_SHORT).show();
        }
    }


}
