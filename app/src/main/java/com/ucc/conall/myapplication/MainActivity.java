package com.ucc.conall.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    public EditText usernameField = null;
    public EditText passwordField = null;
    public Button button = null;
    public Button reg_button = null;

    public SharedPrefs session;

    public JSONObject userData = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        button = findViewById(R.id.button);
        reg_button = findViewById(R.id.reg_button);
        session = new SharedPrefs(getApplicationContext());

        if (session.isLoggedIn()){

            try {
                session.backgroundLoginCheck();
            }

            catch(Exception e) {
                Log.d("X234", e.toString());
        }
            startActivity(new Intent(MainActivity.this,ToDoActivity.class));

        }


        try {
            Intent intent = getIntent();
            intent.getExtras();

            if (intent.hasExtra("user_cancelled")) {

               Toast toast = Toast.makeText(MainActivity.this, "Please Login", Toast.LENGTH_SHORT);
               toast.show();

            }
        }
        catch(Exception e) {
            Log.d("X234", e.toString());

        }

        reg_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d("X25", "test");

                Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
                startActivity(intent);

        }});


        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {



                if (usernameField.getText().toString().trim().length() > 0 && passwordField.getText().toString().trim().length() > 0) {

                    try {
                        userData.put("User", usernameField.getText());
                        userData.put("Pass", passwordField.getText());
                        postData(userData);

                    } catch (JSONException e) {
                    }
                }

            }
    });

    }

    public void postData(JSONObject d) {

        try {

            JsonObjectRequest jsonRequest = new JsonObjectRequest("http://todo.eu-west-1.elasticbeanstalk.com/Login", d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                   // Log.d("X25", response.toString());
                    try {
                        JSONObject json_data = response.getJSONObject("values");

                        String str_value = json_data.getString("login");

                        // TODO  id str_value = 0 , toast login unsuccessful, otherwise send to App

                        Log.d("X25", str_value);

                        if (str_value.equals("1") ) {
                            Log.d("X25", "IFstring sucess");


                            session.createLoginSession(usernameField.getText().toString(), passwordField.getText().toString());

                            Intent intent = new Intent(getBaseContext(), ToDoActivity.class);
                            String cancel = "cancel";
                            intent.putExtra("user_cancelled",cancel);
                            startActivity(intent);

                        }

                        else if (str_value.equals("0") ) {


                            Context context = getApplicationContext();
                            CharSequence text = "Login Unsuccessful";
                            int duration = Toast.LENGTH_SHORT;

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
