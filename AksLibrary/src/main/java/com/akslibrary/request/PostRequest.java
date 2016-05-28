package com.akslibrary.request;

import android.content.Context;

import com.akslibrary.R;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class PostRequest<T> extends Request<T> {

    private Priority priority = Priority.HIGH;
    private Type type;
    private Response.Listener<T> onSuccess = null;
    private Gson mGson;
    HashMap<String, String> hashMap;

    public PostRequest(Context context, Type type, String url, HashMap<String, String> hashMap, Response.Listener<T> onSuccess, Response.ErrorListener errorListener,
                       Priority priority) {
        super(Method.POST, context.getString(R.string.URL_BASE) + url, errorListener);
        this.priority = priority;
        this.type = type;
        this.onSuccess = onSuccess;
        this.mGson = new Gson();
        this.priority = priority;
        this.hashMap = hashMap;
        setRetryPolicy(new DefaultRetryPolicy(context.getResources().getInteger(R.integer.api_timeout), 2, 1));
    }

    public PostRequest(Context context, Type type, String url, HashMap<String, String> hashMap, Response.Listener<T> onSuccess, Response.ErrorListener errorListener,
                       Priority priority, boolean disableRetry) {
        super(Method.POST, context.getString(R.string.URL_BASE) + url, errorListener);
        this.priority = priority;
        this.type = type;
        this.onSuccess = onSuccess;
        this.mGson = new Gson();
        this.priority = priority;
        this.hashMap = hashMap;
        if (!disableRetry)
            setRetryPolicy(new DefaultRetryPolicy(context.getResources().getInteger(R.integer.api_timeout), 2, 1));
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();

        for (Map.Entry<String, String> e : hashMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            params.put(key, value);
        }
        return params;
    }

    @Override
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @SuppressWarnings("unchecked")
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
