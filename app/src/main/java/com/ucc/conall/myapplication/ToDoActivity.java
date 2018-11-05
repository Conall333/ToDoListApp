package com.ucc.conall.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ToDoActivity extends AppCompatActivity {

    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;
    ArrayList<String> taskList = new ArrayList<>();
    ArrayList<String> infoList = new ArrayList<>();
    public String endpoint = "http://todo.eu-west-1.elasticbeanstalk.com/TaskListHandler";
    // "http://todo.eu-west-1.elasticbeanstalk.com/TaskListHandler";
    // http://10.0.2.2:8080//X/TaskListHandler


    SharedPrefs session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        mTaskListView = findViewById(R.id.todo_list);

        session = new SharedPrefs(getApplicationContext());
        // checks if user login is still valid
        session.checkLoginStatus();

        // initial list data fetch from server
        updateData();


        // shows login successful if user came from login screen

        try {
            Intent intent = getIntent();
            intent.getExtras();
            if (intent.hasExtra("user_cancelled")) {
                Toast toast = Toast.makeText(ToDoActivity.this, "Login Successful", Toast.LENGTH_SHORT);
                toast.show();

            }
        }
        catch(Exception e) {

        }

    }


    private void updateTaskViews() {

        Log.d("X20d", taskList.toString());

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            Log.d("X10",taskList.toString());

            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());

        // deletetask of string "task" from database
        sendDeletions(task,"delete");

        // Log.d("X25", "test");
    }

    public void checkTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());

        if(!taskTextView.getPaint().isStrikeThruText()) {
            taskTextView.setPaintFlags(taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            checkTaskOnDB(task, "check");
        }
        else {

            taskTextView.setPaintFlags(taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            checkTaskOnDB(task, "uncheck");
        }

        Log.d("X25", "test");
    }





    public void deleteAll(View view) {

        sendDeletions("task", "deleteAll");

    }

    public void viewMore(String qtitle, String qinfo, String qdate) {

        // extension of the getTaskInfo function, shows the info in a alert dialog

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setTitle(qtitle);

        View view = inflater.inflate(R.layout.custom_view2, null);

        final TextView infoEditText =  view.findViewById(R.id.infoText2);
        final TextView dateEditText =  view.findViewById(R.id.dateText2);

        infoEditText.setText("Info:\n" + qinfo);
        dateEditText.setText("Date:\n" + qdate);

        builder.setView(view);

        builder.setNegativeButton("Cancel", null);
        builder.create();
        builder.show();

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_task:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = this.getLayoutInflater();
                builder.setTitle("Add a Task");
                builder.setMessage("What is your task?");
                View view = inflater.inflate(R.layout.custom_view, null);
                builder.setView(view);
                final EditText taskEditText =  view.findViewById(R.id.taskText);
                final EditText infoEditText =  view.findViewById(R.id.infoText);
                final EditText dateEditText =  view.findViewById(R.id.dateText);

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String task = taskEditText.getText().toString();
                                String info = infoEditText.getText().toString();
                                String date = dateEditText.getText().toString();

                                insertTask(task,info,date);

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                builder.show();
                return true;

            case R.id.log_out:
                session.logoutUser();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }





    public void updateData() {
        try {

            JSONObject d = new JSONObject();
            String action = "update";

            HashMap<String, String> user = session.getUserDetails();
            String uname = user.get("name");

            d.put("action",action);
            d.put("username",uname);

            Log.d("X20", d.toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(endpoint, d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Context context = getApplicationContext();

                    try {

                        JSONObject json_data = response.getJSONObject("values");
                        JSONArray jsonArray = json_data.getJSONArray("titles");

                        ArrayList<String> list = new ArrayList<String>();
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            for (int i=0;i<len;i++){
                                list.add(jsonArray.get(i).toString());
                            }
                        }

                        taskList = list;

                        Log.d("X20c", taskList.toString());

                        updateTaskViews();
                    }
                    catch (JSONException e) {
                        Log.d("X20g", "error: " + e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("X20f",error.toString());
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void updatedResponse(JSONObject response) {

        Log.d("X27","update response");
        Log.d("X27-",response.toString());

        try {
            JSONObject json_data = response.getJSONObject("values");
            JSONArray jsonArray = json_data.getJSONArray("titles");


            ArrayList<String> list = new ArrayList<String>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    list.add(jsonArray.get(i).toString());
                }
            }


            taskList = list;

            Log.d("X27c", taskList.toString());

            updateTaskViews();

        }
        catch(JSONException e){
            Log.d("X27:",e.toString());

        }



    }



    public void sendDeletions(String title, String action) {
        try {

            JSONObject d = new JSONObject();

            HashMap<String, String> user = session.getUserDetails();
            String uname = user.get("name");

            Log.d("X21", action);

            d.put("action",action);
            d.put("username",uname);
            d.put("title", title );

            Log.d("X21", d.toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(endpoint, d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {

                        Log.d("X27", response.toString());
                        updatedResponse(response);
                    }
                    catch(Exception e) {}

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("X20f",error.toString());
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }



    public void insertTask(String title,String info,String date) {
        try {

            JSONObject d = new JSONObject();
            String action = "insert";

            HashMap<String, String> user = session.getUserDetails();
            String uname = user.get("name");

            d.put("action",action);
            d.put("username",uname);
            d.put("title", title );
            d.put("info", info );
            d.put("date", date );


            Log.d("X20", d.toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(endpoint, d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    updatedResponse(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("X20f",error.toString());
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void checkTaskOnDB(String task,String action) {
        try {

            JSONObject d = new JSONObject();

            HashMap<String, String> user = session.getUserDetails();
            String uname = user.get("name");

            d.put("action",action);
            d.put("username",uname);
            d.put("title", task );

            Log.d("X20", d.toString());

            JsonObjectRequest jsonRequest = new JsonObjectRequest(endpoint, d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    updatedResponse(response);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("X20f",error.toString());
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void getTaskInfo(View v) {

        try {


            final TextView taskTextView = v.findViewById(R.id.task_title);
            String task = String.valueOf(taskTextView.getText());


            JSONObject d = new JSONObject();
            String action = "query";

            HashMap<String, String> user = session.getUserDetails();
            String uname = user.get("name");

            d.put("action",action);
            d.put("username",uname);
            d.put("title", task );

            Log.d("X20t", task);

            JsonObjectRequest jsonRequest = new JsonObjectRequest(endpoint, d, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    Context context = getApplicationContext();

                    try {
                        JSONObject json_data = response.getJSONObject("values");

                        Log.d("X20y", json_data.toString());
                        JSONArray jsonArray = json_data.getJSONArray("taskInfo");

                        ArrayList<String> list = new ArrayList<String>();
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            for (int i=0;i<len;i++){
                                list.add(jsonArray.get(i).toString());
                            }
                        }

                        infoList = list;

                        String qtitle = infoList.get(0);
                        String qinfo = infoList.get(1);
                        String qdate = infoList.get(2);

                        viewMore(qtitle,qinfo,qdate);

                    }
                    catch (JSONException e) {
                        Log.d("X20g", "error: " + e);
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("X20f",error.toString());
                }
            });

            VolleySingleton.getInstance(this).addToRequestQueue(jsonRequest);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


























}



