package com.codepath.codepathtodolistapp;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class EditItemActivity extends AppCompatActivity {

    EditText editText;
    EditText etPriority;
    DatePicker datePicker;
    TimePicker timePicker;

    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_PRI = "priority";
    public static final String EXTRA_DUE_DATE = "due_date";
    public static final String EXTRA_POSITION = "position";
    public static final String EXTRA_ID = "extra_id";


    String mName="";
    String mPri="High";
    Long mDate= new Date().getTime();
    String mId = "";

    int position;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);
        Bundle intent = getIntent().getExtras();
        if(intent.containsKey(EXTRA_NAME))
            mName = intent.getString(EXTRA_NAME);
        if(intent.containsKey(EXTRA_ID))
            mId = intent.getString(EXTRA_ID);
        if(intent.containsKey(EXTRA_PRI))
            mPri = intent.getString(EXTRA_PRI);
        if(intent.containsKey(EXTRA_DUE_DATE))
            mDate = intent.getLong(EXTRA_DUE_DATE);

        if(intent.containsKey(EXTRA_POSITION))
            position = intent.getInt(EXTRA_POSITION);
        editText = (EditText) findViewById(R.id.etEditText);
        editText.setText(mName);
        editText.setSelection(mName.length());
        etPriority = (EditText) findViewById(R.id.etPriority);
        etPriority.setText(mPri);
        etPriority.setSelection(mPri.length());
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        Calendar dueDate = Calendar.getInstance();
        dueDate.setTimeInMillis(mDate);
        datePicker.updateDate(dueDate.get(Calendar.YEAR), dueDate.get(Calendar.MONTH), dueDate.get(Calendar.DAY_OF_MONTH));

        timePicker = (TimePicker) findViewById(R.id.timePicker);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            timePicker.setCurrentHour(dueDate.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(dueDate.get(Calendar.MINUTE));
        } else {
            timePicker.setHour(dueDate.get(Calendar.HOUR_OF_DAY));
            timePicker.setMinute(dueDate.get(Calendar.MINUTE));
        }

    }

    public void onSaveBtn(View view) {

        Intent data = new Intent();
        data.putExtra(EditItemActivity.EXTRA_NAME, editText.getText().toString());
        data.putExtra(EditItemActivity.EXTRA_PRI,etPriority.getText().toString());
        Calendar dueDate = Calendar.getInstance();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            dueDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute());
        } else{
            dueDate.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getHour(), timePicker.getMinute());
        }
        data.putExtra(EditItemActivity.EXTRA_DUE_DATE,dueDate.getTimeInMillis());
        data.putExtra(EditItemActivity.EXTRA_POSITION, position);
        data.putExtra(EditItemActivity.EXTRA_ID, mId);
        setResult(RESULT_OK, data); // set result code and bundle data for response
        finish();
    }
}
