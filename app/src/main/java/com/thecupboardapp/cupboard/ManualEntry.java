package com.thecupboardapp.cupboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ManualEntry extends AppCompatActivity {

    Calendar myCalendar = Calendar.getInstance();
    long NO_EXP_DATE = 4133987474999L;
    private int NEW_ENTRY_REQUEST = 0;
    private int UPDATE_ENTRY_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Intent intent = getIntent();
        final int requestCode = intent.getIntExtra("requestCode", NEW_ENTRY_REQUEST);
        EditText edittext= (EditText) findViewById(R.id.editText3);
        ImageButton theDateButt = (ImageButton) findViewById(R.id.imageButton);
        final String foodName;
        final long foodExpires;
        if(requestCode == NEW_ENTRY_REQUEST){
            setTitle("New Food Item");
            foodName = "";
            foodExpires = NO_EXP_DATE;
        }
        else{ //requestCode == UPDATE_ENTRY_REQUEST
            setTitle("Edit Food Item");
            foodName = intent.getStringExtra("foodName");
            foodExpires = intent.getLongExtra("foodExpires",NO_EXP_DATE);
            edittext.setText(foodName);
            Date expDate = new Date(foodExpires);
            myCalendar.setTime(expDate);
        }


        theDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ManualEntry.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button addButt = (Button)findViewById(R.id.button2);
        addButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText edittext= (EditText) findViewById(R.id.editText3);
                EditText edittext2= (EditText) findViewById(R.id.editText5);
                String theName = edittext.getText().toString();
                String theDate = edittext2.getText().toString();

                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");

                if(!theName.isEmpty()) {
                    //If user enters in no expiration date, it will default a value far in the future
                    if(theDate.isEmpty()){
                        Date expDate = new Date(NO_EXP_DATE);
                        myCalendar.setTime(expDate);
                    }
                    FoodItem theFoodToBeAdded = new FoodItem(theName, myCalendar);

                    Calendar theDateAdded = Calendar.getInstance();
                    theDateAdded.getTime();
                    theFoodToBeAdded.setDateAdded(theDateAdded);

                    if(requestCode == NEW_ENTRY_REQUEST) UserData.get(ManualEntry.this).addFoodItem(theFoodToBeAdded);
                    else if(requestCode == UPDATE_ENTRY_REQUEST) {
                        FoodItem oldFoodItem = UserData.get(ManualEntry.this).getFoodItem(foodName);
                        UserData.get(ManualEntry.this).updateFoodItem(theFoodToBeAdded, oldFoodItem);
                    }
                    //go to the next screen passing FoodItem in...
                    setResult(RESULT_OK, resultInt);
                    finish();
                }
                else {
                    //signal to user required fields...
                }

            }

        });

        Button cancelButt = (Button)findViewById(R.id.button3);
        cancelButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");

                setResult(RESULT_CANCELED, resultInt);
                finish();
            }
        });
    }

    public static Intent newIntent(Context packageContext, UUID manualEntryId) {
        Intent intent = new Intent(packageContext, ManualEntry.class);
        return intent;
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        EditText edittext= (EditText) findViewById(R.id.editText5);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }
}
