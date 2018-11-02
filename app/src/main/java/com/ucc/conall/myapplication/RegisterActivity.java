package com.ucc.conall.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.android.volley.toolbox.JsonObjectRequest;



import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    public EditText usernameField = null;
    public EditText passwordField = null;
    public Button button = null;

    JSONObject userData = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameField = findViewById(R.id.usernameRegField);
        passwordField = findViewById(R.id.passwordRegField);
        button = findViewById(R.id.RButton);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    userData.put("User", usernameField.getText());
                    userData.put("Pass", passwordField.getText());
                    postData(userData);

                }
                catch (JSONException e){}
            }


        });

    }



    public void postData(JSONObject d) {
        try {

            Log.d("X25", "test5");

            JsonObjectRequest jsonRequest = new JsonObjectRequest("http://todo.eu-west-1.elasticbeanstalk.com/Register", d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Context context = getApplicationContext();
                    CharSequence text = null;
                    int duration = Toast.LENGTH_SHORT;

                    // Log.d("X25", response.toString());
                    try {

                        JSONObject json_data = response.getJSONObject("values");

                        String str_value = json_data.getString("register");


                        Log.d("X25", str_value);

                        if (str_value.equals("1") ) {
                            Log.d("X25", "IFstring sucess");


                            Intent intent = new Intent(getBaseContext(), MainActivity.class);
                            String cancel = "cancel";
                            intent.putExtra("user_cancelled",cancel);
                            startActivity(intent);
                            Toast.makeText(RegisterActivity.this, "Account Created", Toast.LENGTH_LONG).show();


                        }

                        else if (str_value.equals("0") ) {

                            text = "Registration Unsuccessful";

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                        }

                        else if (str_value.equals("2") ) {

                            text = "Username is already taken";

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();


                        }


                    }
                    catch (JSONException e) {
                        Log.d("X25", "test: " + e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("XError:",error.toString());
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
