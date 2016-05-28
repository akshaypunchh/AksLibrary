package com.akslibrary.request;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.akslibrary.R;
import com.akslibrary.utility.Util;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImagePostRequest<T> extends Request<T> {


    private String keyWordImg;

    private MultipartEntityBuilder mBuilder = MultipartEntityBuilder.create();
    private Response.Listener<T> onSuccess;
    protected Map<String, String> headers;
    HashMap<String, String> hashMap;
    private Type type;
    private Gson mGson;
    Context context;
    Uri uri;
    int imageSize;

    public ImagePostRequest(Context context, String url, Type type, HashMap<String, String> hashMap, String keyWordImg, Uri uri, int imageSize, Response.Listener<T> onSuccess, ErrorListener errorListener) {
        super(Method.POST, context.getString(R.string.URL_BASE) + url, errorListener);

        this.onSuccess = onSuccess;
        this.hashMap = hashMap;
        this.type = type;
        this.mGson = new Gson();
        this.keyWordImg = keyWordImg;
        this.context = context;
        this.uri = uri;
        this.imageSize = imageSize;
        buildMultipartEntity();
        setRetryPolicy(new DefaultRetryPolicy(context.getResources().getInteger(R.integer.api_timeout), 2, 1));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = super.getHeaders();

        if (headers == null
                || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        headers.put("Accept", "application/json");

        return headers;
    }

    private void buildMultipartEntity() {

        // mBuilder.addBinaryBody(keyWordImg, mImageFile, ContentType.create("image/jpeg"), mImageFile.getName());
        Bitmap bmp = null;
        try {
            bmp = Util.decodeUri(context, uri, imageSize);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            File ff = new File(uri.getPath());

            mBuilder.addBinaryBody(keyWordImg, byteArray, ContentType.create("image/jpeg"), ff.getName());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        for (Map.Entry<String, String> e : hashMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            mBuilder.addTextBody(key, value);
        }
        mBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        mBuilder.setLaxMode().setBoundary("xx").setCharset(Charset.forName("UTF-8"));
    }

    @Override
    public String getBodyContentType() {
        String contentTypeHeader = mBuilder.build().getContentType().getValue();
        return contentTypeHeader;
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            mBuilder.build().writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream bos, building the multipart request.");
        }

        return bos.toByteArray();
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            return (Response<T>) Response.success(mGson.fromJson(json, type),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        if (response != null) {
            onSuccess.onResponse(response);
        }
    }
}