package com.bsoft.lwd.ferrysupport;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bsoft.lwd.supportandroid_7.FileProvider;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Permission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PERMISSION_MULTI = 100;
    private static final int REQUEST_CODE_SETTING = 300;
    private static final int REQUEST_CAMERA = 200;
    private TextView tv_photo;

    private File mCameraFile = null;//照相机的File对象
    private File mCropFile = null;//裁剪后的File对象
    private File mGalleryFile = null;//相册的File对象

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_photo = findViewById(R.id.tv_photo);
        tv_photo.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_photo:
                // 申请多个权限。
                AndPermission.with(this)
                        .requestCode(REQUEST_CODE_PERMISSION_MULTI)
                        .permission(Permission.CAMERA, Permission.STORAGE)
                        .callback(this)
                        // rationale作用是：用户拒绝一次权限，再次申请时先征求用户同意，再打开授权对话框；
                        // 这样避免用户勾选不再提示，导致以后无法申请权限。
                        // 你也可以不设置。
                        .rationale(new RationaleListener() {
                            @Override
                            public void showRequestPermissionRationale(int requestCode, Rationale rationale) {
                                // 这里的对话框可以自定义，只要调用rationale.resume()就可以继续申请。
                                AndPermission.rationaleDialog(MainActivity.this, rationale).show();
                            }
                        })
                        .start();
                break;
            default:
                return;
        }
    }

    @PermissionYes(REQUEST_CODE_PERMISSION_MULTI)
    private void getMultiYes(@NonNull List<String> grantedPermissions) {
        openCamera();
    }

    private void openCamera() {
        File file = null;
        Uri imageUri = null;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        String filename = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.CHINA)
                .format(new Date()) + ".png";

        file = new File(Environment.getExternalStorageDirectory(), filename);
        file.getParentFile().mkdirs();
        imageUri = FileProvider.getUriForFile(this, file);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/*");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @PermissionNo(REQUEST_CODE_PERMISSION_MULTI)
    private void getMultiNo(@NonNull List<String> deniedPermissions) {
        Toast.makeText(this, R.string.failure, Toast.LENGTH_SHORT).show();
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第二种：用自定义的提示语。
            AndPermission.defaultSettingDialog(this, REQUEST_CODE_SETTING)
                    .setTitle(R.string.dialog_title)
                    .setMessage(R.string.dialog_desc)
                    .setPositiveButton(R.string.dialog_ok)
                    .show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case REQUEST_CAMERA:
                    Uri newUri = data.getData();
                    Log.e(TAG,newUri.toString());
                    break;
            }
        }
    }
}