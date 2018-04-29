package com.thecupboardapp.cupboard.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.models.FoodItem;
import com.thecupboardapp.cupboard.models.viewmodels.ManualEntryViewModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ManualEntryActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final int NEW_ENTRY_REQUEST = 0;
    public static final int EDIT_ENTRY_REQUEST = 1;
    private static final int CAMERA_PERMISSION_REQUEST = 0;
    public static final String FOOD_ID_REQUEST_KEY = "com.thecupboardapp.cupboard.foodId";

    private EditText mNameEditText;
    private EditText mExpirationEditText;
    private EditText mUnitsEditText;
    private ImageButton mCalendarButton;
    private EditText mQuantityEditText;
    private Spinner mCategorySpinner;
    private TextView mDescriptionEditText;
    private Button mCancelButton;
    private Button mAddUpdateButton;

    private Disposable mFoodDisposable;
    private ManualEntryViewModel mManualEntryViewModel;

    private long mRequestCode;
    private Calendar mCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_entry);

        mNameEditText = findViewById(R.id.edit_food_name);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mUnitsEditText = findViewById(R.id.edit_units);
        mCalendarButton = findViewById(R.id.image_button_calendar);
        mDescriptionEditText = findViewById(R.id.edit_description);
        mCategorySpinner = findViewById(R.id.spinner_category);
        mAddUpdateButton = findViewById(R.id.button_add_update_food);
        mCancelButton = findViewById(R.id.button_cancel_food);
        mExpirationEditText = findViewById(R.id.edit_expiration);

        mManualEntryViewModel = ViewModelProviders.of(this).get(ManualEntryViewModel.class);

        //Set up initial values for variables
        mCalendar = Calendar.getInstance();
        mRequestCode = getIntent().getLongExtra(FOOD_ID_REQUEST_KEY, NEW_ENTRY_REQUEST);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(this,
                R.array.food_categories, R.layout.spinner_layout);
        categoryAdapter.setDropDownViewResource(R.layout.spinner_item_layout);
        mCategorySpinner.setAdapter(categoryAdapter);
        mCategorySpinner.setOnItemSelectedListener(this);

        if (mRequestCode == NEW_ENTRY_REQUEST) {
            //Fill non-required fields with values if new item
            setTitle("New Food");
            mDescriptionEditText.setText("None");
            mExpirationEditText.setText("Never");
            mUnitsEditText.setText("Units");
        } else {
            //Fill all fields with previous values if editing item
            setTitle("Edit Food");
            mAddUpdateButton.setText("Update");
            mManualEntryViewModel.setFoodItemFlowable(mRequestCode);
            mFoodDisposable = mManualEntryViewModel.getFoodItemFlowable()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(foodItem -> {
                        mNameEditText.setText(foodItem.getName());
                        if (foodItem.getExpiration() == 0) {
                            mExpirationEditText.setText("Never");
                        } else {
                            mCalendar.setTime(new Date(foodItem.getExpiration()));
                            updateExpirationLabel();
                        }
                        mQuantityEditText.setText(Float.toString(foodItem.getQuantity()));
                        mUnitsEditText.setText(foodItem.getUnits());
                        if (!foodItem.getCategory().isEmpty()) {
                            mCategorySpinner.setSelection(categoryAdapter.getPosition(foodItem.getCategory()));
                        }
                        mDescriptionEditText.setText(foodItem.getDescription());
                    });
        }
    }

    //Sets all onClickListeners for all buttons
    @Override
    protected void onStart() {
        super.onStart();

        mCalendarButton.setOnClickListener(v -> {
            new DatePickerDialog(ManualEntryActivity.this, date, mCalendar
                    .get(Calendar.YEAR), mCalendar.get(Calendar.MONTH),
                    mCalendar.get(Calendar.DAY_OF_MONTH)).show();
        });


        mAddUpdateButton.setOnClickListener(v -> {
            if (!inputsAreCorrect()) {
                return;
            }

            FoodItem item = new FoodItem();
            item.setName(mNameEditText.getText().toString());
            if (!mExpirationEditText.getText().toString().equals("Never")) {
                item.setExpiration(mCalendar.getTimeInMillis());
            }
            item.setQuantity(Float.parseFloat(mQuantityEditText.getText().toString()));
            item.setUnits(mUnitsEditText.getText().toString());
            item.setCategory(mCategorySpinner.getSelectedItem().toString());
            item.setDescription(mDescriptionEditText.getText().toString());
            item.setDateAdded(System.currentTimeMillis());

            if (mRequestCode == NEW_ENTRY_REQUEST) {
                mManualEntryViewModel.insertFood(item);
            } else {
                item.setId(mRequestCode);
                mManualEntryViewModel.updateFood(item);
            }

            Intent intent = new Intent();
            intent.putExtra("result", "done");

            setResult(RESULT_OK, intent);
            finish();
        });

        mCancelButton.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    //Checks if the required inputs have been entered in
    private boolean inputsAreCorrect() {
        if (mNameEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please specify a name.", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mQuantityEditText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please specify an amount.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

    //Calendar Dialog for user when selecting an expiration date
    DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, monthOfYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        updateExpirationLabel();
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.manual_entry_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Barcode Scanner
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_camera:
                return openCamera();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Checks if permissions are valid to use barcode scanner and starts scan
    public boolean openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.initiateScan();
            return true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    //Used to set label beside Calendar image button to correct date
    private void updateExpirationLabel() {
        String date = SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM)
                .format(mCalendar.getTimeInMillis());
        mExpirationEditText.setText(date);
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
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String[] theHTMLarr = theHTML.split(":");
                if (theHTMLarr[1].split(",")[0].replaceAll("\"", "") != "INVALID_UPC") {
                    String[] theNamearr = theHTMLarr[6].split(",");
                    String theName = theNamearr[0];
                    String theDesc = theHTMLarr[7].substring(1,theHTMLarr[7].length()-7);
                    theName = theName.replaceAll("\"", "");
                    mNameEditText.setText(theName);
                    mQuantityEditText.setText("1.0");
                    mDescriptionEditText.setText(theDesc);
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

    //Required method to implement AdapterView.OnItemSelectedListener
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos);
        // mFoodCategory = parent.getItemAtPosition(pos).toString();
    }

    //Required method to implement AdapterView.OnItemSelectedListener
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}
