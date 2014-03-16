package ca.cmput301w14t09.Controller;

import java.util.ArrayList;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.EditText;
import android.widget.Toast;
import ca.cmput301w14t09.model.GeoLocation;



public class LocationController {

    private GeoLocation geo = new GeoLocation();
    private GeoLocation mygeo = new GeoLocation();
    
    private double lat;
    private double lng;
    
    private double lat2;
    private double lng2;
    
    LocationManager lm = null;
    int count = 0;
    int count2 = 0;

    public GeoLocation getGeoLocation() {
        return geo;
    }

    public void setGeoLocation() {
        geo.setLatitude(lat);
        geo.setLongitude(lng);
    }
    
    public void setmyGeoLocation() {
        mygeo.setLatitude(lat2);
        mygeo.setLongitude(lng2);
    }
    
    public GeoLocation getmyGeoLocation() {
        return mygeo;
    }


    public ArrayList<String> getLocationNames(){
        return null;
    }


    public void setLocationManager(Context context) {

        // Obtain LocationManager service 
        @SuppressWarnings("static-access")
        LocationManager lm = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);


    }

    public void requestLocationUpdates(LocationListener locationListener){

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

    }





    public void locationchanged(android.location.Location location, EditText tv2, EditText tv3){


        if(location != null){

            if(count<2){

                lat = location.getLatitude();
                lng = location.getLongitude();


                Date date = new Date(location.getTime());
                //tv.setText("your location is " +lat+"\n" + lng +"\n" + date.toString());
                tv2.setText(""+lng);
                tv3.setText(""+lat);
                System.out.println("location changed");
                System.out.println("long: "+lng);
                System.out.println("lat: "+lat);
                //set geolocation to current location
                setGeoLocation();
                count = count + 1;
            }
        }

    }
    
    
    public void mylocationchanged(android.location.Location location){


        if(location != null){

            if(count2<2){

                lat2 = location.getLatitude();
                lng2 = location.getLongitude();


                Date date = new Date(location.getTime());
                //tv.setText("your location is " +lat+"\n" + lng +"\n" + date.toString());
               
                System.out.println("top comment location changed");
                System.out.println("long: "+lng2);
                System.out.println("lat: "+lat2);
                //set geolocation to current location
                setmyGeoLocation();
                count2 = count + 1;
            }
        }

    }


    @SuppressLint("NewApi")
	public void updatelocation(Context context, String longitude, String latitude) {      
        // Fix for passing in blank parameters.
        if(latitude.isEmpty() == true) { 
            lat = 1;
        }
        else {
            lat = Double.parseDouble(latitude);
        }
        
        if(longitude.isEmpty() == true) {
            lng = 1;
        }
        else {
            lng = Double.parseDouble(longitude);
        }
        
        //sets geolocation to new given one
        setGeoLocation();

        String update = "Your location has been updated";
        // When clicked, show a toast with the TextView text Game, Help, Home
        Toast.makeText(context, update, Toast.LENGTH_SHORT).show();  
/*
        System.out.println("count"+count);
        System.out.println("location updated");
        System.out.println("long: "+longitude);
        System.out.println("lat: "+latitude);
  */
    }
}







