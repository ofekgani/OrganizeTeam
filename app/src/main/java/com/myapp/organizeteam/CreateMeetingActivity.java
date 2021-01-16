package com.myapp.organizeteam;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


public class CreateMeetingActivity extends AppCompatActivity {

    EditText ed_meetingName,ed_meetingDescription,ed_meetingDate, ed_meetingHour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meeting);

        ed_meetingDate = findViewById(R.id.ed_meetingDate);
        ed_meetingDescription = findViewById(R.id.ed_meetingDescription);
        ed_meetingName = findViewById(R.id.ed_meetingName);
        ed_meetingHour = findViewById(R.id.ed_meetingHour);

        ed_meetingDate.setRawInputType(InputType.TYPE_NULL);
        ed_meetingDate.setFocusable(false);
        ed_meetingDate.setKeyListener(null);

        ed_meetingHour.setRawInputType(InputType.TYPE_NULL);
        ed_meetingHour.setFocusable(false);
        ed_meetingHour.setKeyListener(null);

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        ed_meetingDate.setText(day+"/"+month+1+"/"+year);

        ed_meetingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(CreateMeetingActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month++;
                        String date = day+"/"+month+"/"+year;
                        ed_meetingDate.setText(date);
                    }
                },year,month,day);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();
            }
        });

        final int minute = calendar.get(Calendar.MINUTE);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);

        ed_meetingHour.setText(hour+1 + ":"+minute);

        ed_meetingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateMeetingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                        String time = hour + ":"+minute;
                        ed_meetingHour.setText(time);
                    }
                },hour,minute,true);
                timePickerDialog.show();
            }
        });

    }

    public void oc_createMeeting(View view) {

    }
}