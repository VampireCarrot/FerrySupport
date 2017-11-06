package com.bsoft.lwd.supportandroid_7;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.File;

/**
 * Created by User on 2017/11/6.
 */

public class FileProvider {
    public static Uri getUriForFile(Context context, File file) {
        Uri fileUri = null;
        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = getUriForFile24(context, file);
        } else {
            fileUri = Uri.fromFile(file);
        }
        return fileUri;
    }

    public static Uri getUriForFile24(Context context, File file) {
        Uri fileUri = android.support.v4.content.FileProvider.getUriForFile(context,
                context.getPackageName() + ".android7.fileprovider",
                file);
        return fileUri;
    }
}
