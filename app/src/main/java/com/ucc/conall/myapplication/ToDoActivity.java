package com.ucc.conall.myapplication;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.ArrayList;

// import static com.ucc.conall.myapplication.MainActivity.Email;
// import static com.ucc.conall.myapplication.MainActivity.Name;
// import static com.ucc.conall.myapplication.MainActivity.mypreference;

public class ToDoActivity extends AppCompatActivity {

    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private ArrayAdapter<String> mAdapter;

    // SharedPreferences sharedpreferences;

    SharedPrefs session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        mTaskListView = findViewById(R.id.todo_list);
        mHelper = new TaskDbHelper(this);
        UpdateTasks();

        Context context = getApplicationContext();


        session = new SharedPrefs(getApplicationContext());
        session.checkLoginStatus();


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



    private void UpdateTasks() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.delete(TaskContract.TaskEntry.TABLE,
                TaskContract.TaskEntry.COL_TASK_TITLE + " = ?",
                new String[]{task});
        db.close();
        UpdateTasks();
        Log.d("X25", "test");
    }

    public void checkTask(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = parent.findViewById(R.id.task_title);

        if(!taskTextView.getPaint().isStrikeThruText()) {

            taskTextView.setPaintFlags(taskTextView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else {

            taskTextView.setPaintFlags(taskTextView.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        UpdateTasks();
        Log.d("X25", "test");
    }





    public void deleteAll(View view) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        db.execSQL("delete from "+ TaskContract.TaskEntry.TABLE);
        db.close();
        UpdateTasks();
    }

    public void viewMore(View v) {
        TextView taskTextView = v.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        SQLiteDatabase db = mHelper.getWritableDatabase();

        String query= "SELECT * FROM tasks where title = '"+task+"'";
        Cursor cursor1= db.rawQuery(query, null);

        ArrayList<String> stringArrayList = new ArrayList<String>();

        while (cursor1.moveToNext()) {

            for (int i=1; i<cursor1.getColumnCount(); i++) {
                stringArrayList.add(cursor1.getString(i));
            }
        }

        // Log.d("X25", stringArrayList.toString());
        String qtitle = stringArrayList.subList(0,1).toString().replace("[", "").replace("]", "");
        String qinfo = stringArrayList.subList(1,2).toString().replace("[", "").replace("]", "");
        String qdate = stringArrayList.subList(2,3).toString().replace("[", "").replace("]", "");
        ;

       // Log.d("X25", qtitle);
       // Log.d("X25", qinfo);
       // Log.d("X25", qdate);

        cursor1.close();
        db.close();



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

        db.close();


    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);


    }


   /*
    public void Logout() {

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

            String n = "";
            String e = "";
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Name, n);
            editor.putString(Email, e);
            editor.apply();

            startActivity(new Intent(ToDoActivity.this, MainActivity.class));

        }

        */









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

                                Log.d("x26", info);
                                Log.d("x26", date);

                                SQLiteDatabase db = mHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put(TaskContract.TaskEntry.COL_TASK_TITLE, task);
                                values.put(TaskContract.TaskEntry.COL_TASK_INFO, info);
                                values.put(TaskContract.TaskEntry.COL_TASK_DATE, date);
                                db.insertWithOnConflict(TaskContract.TaskEntry.TABLE,
                                        null,
                                        values,
                                        SQLiteDatabase.CONFLICT_REPLACE);
                                UpdateTasks();
                                db.close();
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
    }









