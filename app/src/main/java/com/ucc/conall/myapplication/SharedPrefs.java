package com.ucc.conall.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SharedPrefs {

        private SharedPreferences pref;
        private SharedPreferences.Editor editor;
        private Context _context;
        private int PRIVATE_MODE = 0;


        private static final String PREF_NAME = "MyPref";
        private static final String IS_LOGIN = "IsLoggedIn";
        public static final String KEY_NAME = "name";
        public static final String KEY_PASSWORD = "password";

        public SharedPrefs(Context context){
            this._context = context;
            pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
            editor = pref.edit();
        }


        public void createLoginSession(String name, String password){

            editor.putBoolean(IS_LOGIN, true);
            editor.putString(KEY_NAME, name);
            editor.putString(KEY_PASSWORD, password);
            editor.commit();
        }


        public void checkLoginStatus(){

            if(!this.isLoggedIn()){
                // user is not logged in redirect him to Login Activity
                Intent i = new Intent(_context, MainActivity.class);
                // Closing all the Activities
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // Add new Flag to start new Activity
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                _context.startActivity(i);
            }

        }



        public HashMap<String, String> getUserDetails(){
            HashMap<String, String> user = new HashMap<String, String>();
            user.put(KEY_NAME, pref.getString(KEY_NAME, null));
            user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));
            return user;
        }


        public void logoutUser(){
            // Clearing all data from Shared Preferences
            editor.clear();
            editor.commit();

            // After logout redirect user to Loing Activity
            Intent i = new Intent(_context, MainActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

        /**
         * Quick check for login
         * **/
        // Get Login State
        public boolean isLoggedIn(){
            return pref.getBoolean(IS_LOGIN, false);
        }

        public void backgroundLoginCheck() {

            JSONObject userData = new JSONObject();

            HashMap<String, String> user = new HashMap<String, String>();
            // user name
            user.put(KEY_NAME, pref.getString(KEY_NAME, null));

            // user email id
            user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD, null));

            String uname = user.get("name");
            String upass = user.get("password");

            Log.d("X233", uname + upass);

            try {
                userData.put("User", uname);
                userData.put("Pass", upass);
            }
            catch (JSONException e) {
                Log.d("X232", e.toString());
            }


            try {

                Log.d("X235", userData.toString());

                    JsonObjectRequest jsonRequest = new JsonObjectRequest("http://todo.eu-west-1.elasticbeanstalk.com/Login", userData, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {

                            // Log.d("X25", response.toString());
                            try {
                                JSONObject json_data = response.getJSONObject("values");

                                String str_value = json_data.getString("login");

                                Log.d("X231", str_value);

                                if (str_value.equals("0") ) {

                                    CharSequence text = "Login Not Valid";
                                    int duration = Toast.LENGTH_SHORT;

                                    Toast toast = Toast.makeText(_context, text, duration);
                                    toast.show();

                                    logoutUser();

                                }

                            }
                            catch (JSONException e) {
                                Log.d("X25x", "test: " + e);
                            }

                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("XError:",error.toString());
                        }
                    });

                    VolleySingleton.getInstance(_context).addToRequestQueue(jsonRequest);

                } catch (Exception e) {
                    e.printStackTrace();
                }



        }









    }

