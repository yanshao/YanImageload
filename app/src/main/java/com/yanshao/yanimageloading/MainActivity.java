package com.yanshao.yanimageloading;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.yanshao.yanimageload.imageload.YanImageLoad;
import com.yanshao.yanimageload.util.FileUtils;
import com.yanshao.yanimageload.util.Utils;

public class MainActivity extends AppCompatActivity {
    private String[] permissions = {
            //文件
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    ImageView image, image1, image2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image = findViewById(R.id.image);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        PermissionsUtils.getInstance().checkPermissions(this, permissions, new PermissionsUtils.IPermissionsResult() {
            @Override
            public void passPermissions() {
//
             //   FileUtils.compress("/storage/emulated/0/Pictures/IMG_20180526_224123.jpg",image);
               YanImageLoad.getInstance(MainActivity.this).disPlay(image, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553058539054&di=0713321ef5ba49d6983b996061cda040&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fac6eddc451da81cb167b12945466d016082431cd.jpg", 0);
               YanImageLoad.getInstance(MainActivity.this).disPlay(image1, "/storage/emulated/0/Pictures/IMG_20180526_224123.jpg", 1);
              YanImageLoad.getInstance(MainActivity.this).disPlay(image2, "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1553058539054&di=0713321ef5ba49d6983b996061cda040&imgtype=0&src=http%3A%2F%2Fh.hiphotos.baidu.com%2Fzhidao%2Fpic%2Fitem%2Fac6eddc451da81cb167b12945466d016082431cd.jpg", 2);

            }

            @Override
            public void forbidPermissions() {
                finish();
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsUtils.getInstance().onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

}
