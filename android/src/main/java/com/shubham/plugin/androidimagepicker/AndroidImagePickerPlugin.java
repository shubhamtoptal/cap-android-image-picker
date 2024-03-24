package com.shubham.plugin.androidimagepicker;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.annotation.Nullable;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.util.ArrayList;
import java.util.List;

@CapacitorPlugin(name = "AndroidImagePicker")
public class AndroidImagePickerPlugin extends Plugin {

    public static final String ERROR_PICK_FILE_FAILED = "pickFiles failed.";
    public static final String ERROR_PICK_FILE_CANCELED = "pickFiles canceled.";

    private AndroidImagePicker implementation;
    public void load() {
        implementation = new AndroidImagePicker(this.getBridge());
    }

    @PluginMethod
    public void pickImages(PluginCall call) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(call, intent, "pickFilesResult");
    }

    @ActivityCallback
    private void pickFilesResult(PluginCall call, ActivityResult result) {
        try {
            if (call == null) {
                return;
            }
            int resultCode = result.getResultCode();
            Intent data = result.getData();

            switch (resultCode) {
                case Activity.RESULT_OK:
                    JSObject callResult = createPickFilesResult(result.getData(), false);
                    call.resolve(callResult);
                    break;
                case Activity.RESULT_CANCELED:
                    call.reject(ERROR_PICK_FILE_CANCELED);
                    break;
                default:
                    call.reject(ERROR_PICK_FILE_FAILED);
            }

        } catch (Exception ex) {
            String message = ex.getMessage();
            Log.e("AndroidImagePicker", message);
            call.reject(message);
        }
    }

    private JSObject createPickFilesResult(@Nullable Intent data, boolean readData) {
        JSObject callResult = new JSObject();
        List<JSObject> filesResultList = new ArrayList<>();
        if (data == null) {
            callResult.put("files", JSArray.from(filesResultList));
            return callResult;
        }
        List<Uri> uris = new ArrayList<>();
        if (data.getClipData() == null) {
            Uri uri = data.getData();
            uris.add(uri);
        } else {
            for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                Uri uri = data.getClipData().getItemAt(i).getUri();
                uris.add(uri);
            }
        }
        for (int i = 0; i < uris.size(); i++) {
            Uri uri = uris.get(i);
            if (uri == null) {
                continue;
            }
            JSObject fileResult = new JSObject();
            if (readData) {
                fileResult.put("data", implementation.getDataFromUri(uri));
            }
            Long duration = implementation.getDurationFromUri(uri);
            if (duration != null) {
                fileResult.put("duration", duration);
            }
            FileResolution resolution = implementation.getHeightAndWidthFromUri(uri);
            if (resolution != null) {
                fileResult.put("height", resolution.height);
                fileResult.put("width", resolution.width);
            }
            fileResult.put("mimeType", implementation.getMimeTypeFromUri(uri));
            Long modifiedAt = implementation.getModifiedAtFromUri(uri);
            if (modifiedAt != null) {
                fileResult.put("modifiedAt", modifiedAt);
            }
            fileResult.put("name", implementation.getNameFromUri(uri));
            fileResult.put("path", implementation.getPathFromUri(uri));
            fileResult.put("size", implementation.getSizeFromUri(uri));
            filesResultList.add(fileResult);
        }
        callResult.put("files", JSArray.from(filesResultList.toArray()));
        return callResult;
    }
}
