package org.vosystems.covidvaccinationcenterslocator.Network.Requests;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONArray;
import org.vosystems.covidvaccinationcenterslocator.Network.ApiRequest;
import org.vosystems.covidvaccinationcenterslocator.Network.Urls;

public class GetLocationsRequest extends ApiRequest {
    public GetLocationsRequest(Context context,String keyword, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(context, getUrl(keyword), Request.Method.GET, listener, errorListener);

                System.out.println(keyword);

    }

    public static String getUrl(String keyword){

        if (keyword == null){
            return Urls.GET_FACILIITES;
        }
        return Urls.GET_FACILIITES + "?keyword=" + keyword;
    }
}
