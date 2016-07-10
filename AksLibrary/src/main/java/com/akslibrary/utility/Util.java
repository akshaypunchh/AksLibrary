package com.akslibrary.utility;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Util {
    private static ConnectivityManager mCM;
   // public static String KEY_LOGIN_USER = "KEY_LOGIN_USER";

    public static boolean hasInternetAccess(Context context) {
        if (mCM == null) {
            mCM = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo netInfo = mCM.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static void hideKeyBoard(Context con, View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static Boolean isEditTextEmpty(EditText... editTexts) {
        for (EditText editText : editTexts) {
            String editTextValue = editText.getText().toString().trim();
            String hintString = "";
            if (editText.getHint() != null) {
                hintString = editText.getHint().toString();
            }
            if ((editTextValue == null || editTextValue.length() == 0 || (editTextValue.compareTo(hintString) == 0))) {
                return true;
            }
        }
        return false;
    }


    public static void showErrorBox(Activity context, String title, String msg) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (title != null)
            builder.setTitle(title);

        builder.setMessage(msg);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        // Create the AlertDialog object and return it
        builder.create();

        builder.show();
    }

    public static String getDateFromCurrent(String mydate, String dateFormate) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormate); //"yyyy-MM-dd hh:mm:ss"
        Calendar cal = Calendar.getInstance();


        try {

            Date date1 = simpleDateFormat.parse(mydate);
            Date date2 = simpleDateFormat.parse(simpleDateFormat.format(cal.getTime()));
            long diff = date2.getTime() - date1.getTime();
            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            String result = " ago";
            if (days >= 1) {
                return days + (days == 1 ? " day " : " days") + result;
            } else if (hours >= 1) {
                return hours + (hours == 1 ? " hour " : " hours") + result;
            } else if (minutes >= 1) {
                return minutes + (minutes == 1 ? " minute " : " minutes") + result;
            } else if (seconds >= 1) {
                return seconds + (seconds == 1 ? " second " : " seconds") + result;
            } else {
                return "just now";
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    public static Bitmap decodeUri(Context context, Uri selectedImage, final int REQUIRED_SIZE) throws FileNotFoundException {

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o);


        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(selectedImage), null, o2);
    }


    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        boolean isDocument = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            isDocument = DocumentsContract.isDocumentUri(context, uri);
        }
        // DocumentProvider
        if (isKitKat && isDocument) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                String id = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    id = DocumentsContract.getDocumentId(uri);
                }

                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }

            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}


