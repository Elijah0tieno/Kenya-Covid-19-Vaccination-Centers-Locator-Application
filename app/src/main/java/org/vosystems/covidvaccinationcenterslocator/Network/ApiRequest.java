package org.vosystems.covidvaccinationcenterslocator.Network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

public class ApiRequest extends JsonArrayRequest {
    protected String url;
    protected int method;
    protected Map<String, String> params;
    protected Response.Listener<JSONArray> listener;
    protected Response.ErrorListener errorListener;
    Context context;

    public ApiRequest(Context context, String url, int method, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener){
        super(method, url, null, listener, errorListener);

        this.url = url;
        this.method = method;
        this.listener = listener;
        this.errorListener = errorListener;
        this.context = context;
    }



    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
//TODO
//        if(AuthHelper.isLoggedIn(context)){
//            Map<String, Object> user = AuthHelper.get(context);
//            headers.put("Authorization", "Bearer " + user.get(AuthHelper.API_TOKEN));
//        }

        return headers;
    }


    public ApiRequest after(Response.Listener<JSONArray> listener){
        this.listener = listener;
        return this;
    }

    public ApiRequest onError(Response.ErrorListener errorListener){
        this.errorListener = errorListener;
        return this;
    }
}

