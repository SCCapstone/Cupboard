package com.thecupboardapp.cupboard.activities;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.UserData;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.viewmodels.ManualEntryViewModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ManualEntryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final long NO_EXP_DATE = 4133987474999L;
    public static final long NEW_ENTRY_REQUEST = 0;
    public static final long UPDATE_ENTRY_REQUEST = 1;

    private EditText mNameEditText;
    private ImageButton mCalendarButton;
    private EditText mQuantityEditText;
    private TextView mDescription;
    private Spinner mCategorySpinner;
    private Button mAddButton;
    private Button mCancelButton;

    private Disposable mFoodDisposable;
    private ManualEntryViewModel mManualEntryViewModel;

    private long mRequestCode;
    private Calendar mCalendar;
    private String mFoodCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        mNameEditText = findViewById(R.id.edit_food_name);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mCalendarButton = findViewById(R.id.image_button_calendar);
        mDescription = findViewById(R.id.text_description);
        mCategorySpinner = findViewById(R.id.spinner_category);
        mAddButton = findViewById(R.id.button_add_food);
        mCancelButton = findViewById(R.id.button_cancel_food);

        mManualEntryViewModel = ViewModelProviders.of(this).get(ManualEntryViewModel.class);

        mCalendar = Calendar.getInstance();
        mFoodCategory = "";
        mRequestCode = getIntent().getLongExtra("mRequestCode", NEW_ENTRY_REQUEST);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_categories, R.layout.spinner_layout);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        mCategorySpinner.setAdapter(categoryAdapter);
        mCategorySpinner.setOnItemSelectedListener(this);

        if (mRequestCode == NEW_ENTRY_REQUEST) {
            setTitle("New Food");
            mDescription.setText("None");
        } else {
            setTitle("Edit Food");
            mManualEntryViewModel.setFoodItemFlowable(mRequestCode);
            mFoodDisposable = mManualEntryViewModel.getFoodItemFlowable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(foodItem -> {
                        mNameEditText.setText(foodItem.getName());
                        mCalendar.setTime(new Date(foodItem.getExpiration()));
                        mQuantityEditText.setText(Float.toString(foodItem.getQuantity()));
                        if (!foodItem.getCategory().isEmpty()) {
                            mCategorySpinner.setSelection(categoryAdapter.getPosition(foodItem.getCategory()));
                        }
                        mDescription.setText(foodItem.getDescription());
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mCalendarButton.setOnClickListener(v -> {
            new DatePickerDialog(ManualEntryActivity.this, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        mAddButton.setOnClickListener(v -> {
            if (mRequestCode == NEW_ENTRY_REQUEST) {
                FoodItem item = new FoodItem();
                item.setName(mNameEditText.getText().toString());
                item.setExpiration(mCalendar.getTimeInMillis());
                item.setQuantity(Float.parseFloat(mQuantityEditText.getText().toString()));
                item.setCategory(mCategorySpinner.getSelectedItem().toString());
                item.setDescription(mDescription.getText().toString());
                item.setDateAdded(System.currentTimeMillis());

                mManualEntryViewModel.insertFood(item);
                finish();
            } else {

            }
        });

        mCancelButton.setOnClickListener(v -> {
            Intent resultInt = new Intent();
            resultInt.putExtra("Result", "Done");

            setResult(RESULT_CANCELED, resultInt);
            finish();
        });
    }

    @Override
    protected void onStop() {
        if (mFoodDisposable != null) {
            mFoodDisposable.dispose();
        }
        super.onStop();
    }

    public static Intent newIntent(Context packageContext, long id) {
        Intent intent = new Intent(packageContext, ManualEntryActivity.class);
        return intent;
    }

    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateLabel();
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
        EditText edittext= (EditText) findViewById(R.id.edit_expiration);
        edittext.setText(sdf.format(mCalendar.getTime()));
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
                    EditText edittext = (EditText) findViewById(R.id.edit_food_name);
                    edittext.setText(theName);
                    EditText edittext2 = (EditText) findViewById(R.id.edit_quantity);
                    edittext2.setText("1.0");
                    TextView textView1 = (TextView) findViewById(R.id.text_description);
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
        mFoodCategory = parent.getItemAtPosition(pos).toString();
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
