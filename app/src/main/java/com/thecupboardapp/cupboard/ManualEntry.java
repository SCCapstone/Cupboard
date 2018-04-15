package com.thecupboardapp.cupboard;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ManualEntry extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Calendar myCalendar = Calendar.getInstance();
    long NO_EXP_DATE = 4133987474999L;
    private int NEW_ENTRY_REQUEST = 0;
    private int UPDATE_ENTRY_REQUEST = 1;
    String mfoodCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Intent intent = getIntent();
        final int requestCode = intent.getIntExtra("requestCode", NEW_ENTRY_REQUEST);
        EditText edittext= (EditText) findViewById(R.id.editText3);
        ImageButton theDateButt = (ImageButton) findViewById(R.id.imageButton);
        EditText editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        TextView textView1 = (TextView) findViewById(R.id.textView5);
        textView1.setText("None");

        Spinner categorySpinner = (Spinner) findViewById(R.id.foodCategorySpinner);
        //ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.food_categories,android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this, R.array.food_categories,R.layout.spinner_layout);
        //categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        final String foodName;
        final long foodExpires;
        final float foodQuantity;
        final String foodCategory;
        final String foodDesc;
        if(requestCode == NEW_ENTRY_REQUEST){
            setTitle("New Food Item");
            foodName = "";
            foodExpires = NO_EXP_DATE;
            foodQuantity = 0;
            foodCategory = "";
            foodDesc = "None";
        }
        else{ //requestCode == UPDATE_ENTRY_REQUEST
            setTitle("Edit Food Item");
            foodName = intent.getStringExtra("foodName");
            foodExpires = intent.getLongExtra("foodExpires",NO_EXP_DATE);
            foodQuantity = intent.getFloatExtra("foodQuantity",0);
            foodCategory = intent.getStringExtra("foodCategory");
            foodDesc = intent.getStringExtra("foodDesc");
            edittext.setText(foodName);
            Date expDate = new Date(foodExpires);
            myCalendar.setTime(expDate);
            editTextQuantity.setText(Float.toString(foodQuantity));
            if (foodCategory != "") categorySpinner.setSelection(categoryAdapter.getPosition(foodCategory));
            textView1.setText(foodDesc);
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
                EditText editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
                TextView textView1 = (TextView) findViewById(R.id.textView5);
                String theName = edittext.getText().toString();
                String theDate = edittext2.getText().toString();
                String theQuantity = editTextQuantity.getText().toString();
                String theDesc = textView1.getText().toString();

                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");

                if(!theName.isEmpty()) {
                    //If user enters in no expiration date, it will default a value far in the future
                    if(theDate.isEmpty()){
                        Date expDate = new Date(NO_EXP_DATE);
                        myCalendar.setTime(expDate);
                    }
                    if(theQuantity.isEmpty()){
                        theQuantity = "1";
                    }
                    float theQuantityFloat = Float.parseFloat(theQuantity);

                    FoodItem theFoodToBeAdded = new FoodItem(theName, myCalendar, theQuantityFloat, mfoodCategory);
                    theFoodToBeAdded.setDescription(theDesc);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.manual_entry_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_camera:
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        EditText edittext= (EditText) findViewById(R.id.editText5);
        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (scanResult != null) {
                String[] upcArr = scanResult.toString().split("\n");
                String[] theUpc = upcArr[1].split(": ");
                Log.i("theTag", theUpc[1]);
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                }
                ;
                String theHTML = "";
                try {
                    theHTML = getHTML("https://api.upcitemdb.com/prod/trial/lookup?upc=" + theUpc[1]);
                    Log.i("theHTML", theHTML);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] theHTMLarr = theHTML.split(":");
                if (theHTMLarr[1].split(",")[0].replaceAll("\"", "") != "INVALID_UPC") {
                    String[] theNamearr = theHTMLarr[6].split(",");
                    String theName = theNamearr[0];
                    String theDesc = theHTMLarr[7].substring(1,theHTMLarr[7].length()-7);
                    theName = theName.replaceAll("\"", "");
                    EditText edittext = (EditText) findViewById(R.id.editText3);
                    edittext.setText(theName);
                    EditText edittext2 = (EditText) findViewById(R.id.editTextQuantity);
                    edittext2.setText("1.0");
                    TextView textView1 = (TextView) findViewById(R.id.textView5);
                    textView1.setText(theDesc);
                    Log.i("tag2", theDesc);
                }
            }
        }
        catch (Exception e) {
            Log.i("theError", e.toString());
        }
        // else continue with any other code you need in the method
    }

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        //parent.getItemAtPosition(pos);
        mfoodCategory = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
