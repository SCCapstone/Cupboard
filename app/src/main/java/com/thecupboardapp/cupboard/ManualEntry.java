package com.thecupboardapp.cupboard;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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

public class ManualEntry extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static final long NO_EXP_DATE = 4133987474999L;
    public static final int NEW_ENTRY_REQUEST = 0;
    public static final int UPDATE_ENTRY_REQUEST = 1;
    Calendar myCalendar = Calendar.getInstance();
    String mfoodCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);
        Intent intent = getIntent();
        final int requestCode = intent.getIntExtra(getString(R.string.request_code),
                NEW_ENTRY_REQUEST);
        EditText editTextName = findViewById(R.id.edit_text_name);
        ImageButton dateButton = findViewById(R.id.image_button_calendar_expires);
        EditText editTextQuantity = findViewById(R.id.edit_text_quantity);
        TextView textViewDescription = findViewById(R.id.text_view_description);
        textViewDescription.setText(getString(R.string.description_none));

        Spinner categorySpinner = findViewById(R.id.spinner_category);
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_categories,R.layout.spinner_layout);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        categorySpinner.setAdapter(categoryAdapter);
        categorySpinner.setOnItemSelectedListener(this);

        final String foodName;
        final long foodExpires;
        final float foodQuantity;
        final String foodCategory;
        final String foodDesc;
        if(requestCode == NEW_ENTRY_REQUEST){ // adding a new food item
            setTitle(getString(R.string.title_new_food));
            foodName = "";
        }
        else{ //requestCode == UPDATE_ENTRY_REQUEST, fill with current values of FoodItem
            setTitle(getString(R.string.title_edit_food));
            foodName = intent.getStringExtra(getString(R.string.food_name));
            foodExpires = intent.getLongExtra(getString(R.string.food_expires),NO_EXP_DATE);
            foodQuantity = intent.getFloatExtra(getString(R.string.food_quantity),0);
            foodCategory = intent.getStringExtra(getString(R.string.food_category));
            foodDesc = intent.getStringExtra(getString(R.string.food_description));

            editTextName.setText(foodName);
            Date expDate = new Date(foodExpires);
            myCalendar.setTime(expDate);
            updateLabel();
            editTextQuantity.setText(Float.toString(foodQuantity));
            if (foodCategory != "") categorySpinner.setSelection(categoryAdapter
                    .getPosition(foodCategory));
            textViewDescription.setText(foodDesc);
        }


        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(ManualEntry.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        Button buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextName = findViewById(R.id.edit_text_name);
                EditText editTextExpires = findViewById(R.id.edit_text_expires);
                EditText editTextQuantity = findViewById(R.id.edit_text_quantity);
                EditText textViewDescription = findViewById(R.id.text_view_description);
                String theName = editTextName.getText().toString();
                String theDate = editTextExpires.getText().toString();
                String theQuantity = editTextQuantity.getText().toString();
                String theDesc = textViewDescription.getText().toString();

                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");

                if(!theName.isEmpty()) {
                    if(theDate.isEmpty()){
                        Date expDate = new Date(NO_EXP_DATE);
                        myCalendar.setTime(expDate);
                    }
                    if(theQuantity.isEmpty()){
                        theQuantity = "1";
                    }
                    float theQuantityFloat = Float.parseFloat(theQuantity);

                    FoodItem theFoodToBeAdded = new FoodItem(theName, myCalendar, theQuantityFloat,
                            mfoodCategory);
                    theFoodToBeAdded.setDescription(theDesc);

                    Calendar theDateAdded = Calendar.getInstance();
                    theDateAdded.getTime();
                    theFoodToBeAdded.setDateAdded(theDateAdded);

                    if(requestCode == NEW_ENTRY_REQUEST) UserData.get(ManualEntry.this)
                            .addFoodItem(theFoodToBeAdded);
                    else if(requestCode == UPDATE_ENTRY_REQUEST) {
                        FoodItem oldFoodItem = UserData.get(ManualEntry.this).getFoodItem(foodName);
                        UserData.get(ManualEntry.this).updateFoodItem(theFoodToBeAdded,
                                oldFoodItem);
                    }
                    //return to My Cupboard screen
                    setResult(RESULT_OK, resultInt);
                    finish();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ManualEntry.this);
                    builder.setTitle(getString(R.string.alert_name_required));
                    builder.setNeutralButton(getString(R.string.alert_ok),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();
                }

            }

        });

        Button buttonCancel = findViewById(R.id.button_cancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultInt = new Intent();
                resultInt.putExtra("Result", "Done");
                setResult(RESULT_CANCELED, resultInt);
                finish();
            }
        });
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
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
        EditText editTextExpires = findViewById(R.id.edit_text_expires);
        if(myCalendar.getTimeInMillis()!=NO_EXP_DATE)
        editTextExpires.setText(sdf.format(myCalendar.getTime()));
        else editTextExpires.setText(getString(R.string.msg_never));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode,
                    intent);
            if (scanResult != null) {
                String[] upcArr = scanResult.toString().split("\n");
                String[] theUpc = upcArr[1].split(": ");
                Log.i("theTag", theUpc[1]);
                if (android.os.Build.VERSION.SDK_INT > 9) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll()
                            .build();
                    StrictMode.setThreadPolicy(policy);
                }
                ;
                String theHTML = "";
                try {
                    theHTML = getHTML("https://api.upcitemdb.com/prod/trial/lookup?upc="
                            + theUpc[1]);
                    Log.i("theHTML", theHTML);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] theHTMLarr = theHTML.split(":");
                if (theHTMLarr[1].split(",")[0]
                        .replaceAll("\"", "") != "INVALID_UPC") {
                    String[] theNamearr = theHTMLarr[6].split(",");
                    String theName = theNamearr[0];
                    String theDesc = theHTMLarr[7].substring(1,theHTMLarr[7].length()-7);
                    theName = theName.replaceAll("\"", "");
                    EditText editTextName = findViewById(R.id.edit_text_name);
                    editTextName.setText(theName);
                    EditText editTextQuantity = findViewById(R.id.edit_text_quantity);
                    editTextQuantity.setText("1.0");
                    EditText textViewDesc = findViewById(R.id.text_view_description);
                    textViewDesc.setText(theDesc);
                    Log.i("tag2", theDesc);
                }
            }
        }
        catch (Exception e) {
            Log.i("theError", e.toString());
        }
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
        mfoodCategory = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
