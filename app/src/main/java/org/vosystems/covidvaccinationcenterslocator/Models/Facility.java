package org.vosystems.covidvaccinationcenterslocator.Models;

import org.json.JSONObject;

public class Facility {
    public String county, sub_county,name;
    public double latitudes;
    public double longitudes;

    public Facility(String county, String sub_county, String name, double latitudes, double longitudes) {
        this.county = county;
        this.sub_county = sub_county;
        this.name = name;
        this.latitudes = latitudes;
        this.longitudes = longitudes;
    }

    public static Facility fromJSON(JSONObject jsonObject){
        if (jsonObject == null) {
            return null;
        }
//        System.out.println(jsonObject.optLong("Latitudes"));
        return new Facility(
                jsonObject.optString("County"),
                jsonObject.optString("Sub_County"),
                jsonObject.optString("Health_Facility_Name"),
                jsonObject.optDouble("Latitudes"),
                jsonObject.optDouble("Longitudes")
        );


    }
}
