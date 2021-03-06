package com.adamcanady.CS342.unitconverter.app;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class UnitConverterMain extends Activity implements ActionBar.OnNavigationListener {

    /**
     * The serialization (saved instance state) Bundle key representing the
     * current dropdown position.
     */
    private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
    private static final String STATE_SELECTED_INPUT_UNIT = "selected_input_unit";
    private static final String STATE_SELECTED_OUTPUT_UNIT = "selected_output_unit";
    private static final String STATE_INPUT_TEXT = "input_text";
    private static final String STATE_OUTPUT_TEXT = "output_text";
    public static final String PREFERENCES = "SharedPreferences";

    private String current_input = "";
    private Spinner input_units;
    private Spinner output_units;

    private EditText input_text;
    private EditText output_text;

    private UnitConverterCalculator calc = new UnitConverterCalculator();

    private int mode;

    List<String> massUnitArray;
    List<String> distanceUnitArray;
    List<String> dataUnitArray;
    List<String> volumeUnitArray;
    List<String> areaUnitArray;

    ArrayAdapter<String> unitAdapter;


    // from: http://stackoverflow.com/questions/12108893/set-onclicklistener-for-spinner-item
    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            update_results();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_unit_converter_main);

        // Set up the action bar to show a dropdown list.
        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Set up the dropdown list navigation in the action bar.
        actionBar.setListNavigationCallbacks(
                // Specify a SpinnerAdapter to populate the dropdown list.
                new ArrayAdapter<String>(
                        actionBar.getThemedContext(),
                        android.R.layout.simple_list_item_1,
                        android.R.id.text1,
                        new String[] {
                                getString(R.string.mode_data),
                                getString(R.string.mode_distance),
                                getString(R.string.mode_mass),
                                getString(R.string.mode_volume),
                                getString(R.string.mode_area)
                        }),
                this);

        // Bind UI objects
        input_text = (EditText) findViewById(R.id.input_text);
        output_text = (EditText) findViewById(R.id.output_text);
        input_text.setOnKeyListener(null); // make the edittexts not editable
        output_text.setOnKeyListener(null);

        // copy text to clipboard when touching the output view
        output_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                EditText output = (EditText)view;
                ClipData clip = ClipData.newPlainText("unit conversion", output.getText().toString());
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        input_units = (Spinner) findViewById(R.id.input_units);
        output_units = (Spinner) findViewById(R.id.output_units);

        // Populate Spinners
        massUnitArray = new ArrayList<String>();
        massUnitArray.add("kg");
        massUnitArray.add("g");
        massUnitArray.add("mg");
        massUnitArray.add("ug");
        massUnitArray.add("lb");
        massUnitArray.add("oz");

        dataUnitArray = new ArrayList<String>();
        dataUnitArray.add("byte");
        dataUnitArray.add("kb");
        dataUnitArray.add("mb");
        dataUnitArray.add("gb");
        dataUnitArray.add("tb");
        dataUnitArray.add("pb");

        distanceUnitArray = new ArrayList<String>();
        distanceUnitArray.add("m");
        distanceUnitArray.add("km");
        distanceUnitArray.add("cm");
        distanceUnitArray.add("mm");
        distanceUnitArray.add("um");
        distanceUnitArray.add("nm");
        distanceUnitArray.add("mi");
        distanceUnitArray.add("ft");
        distanceUnitArray.add("in");

        volumeUnitArray = new ArrayList<String>();
        volumeUnitArray.add("l");
        volumeUnitArray.add("ml");
        volumeUnitArray.add("ul");
        volumeUnitArray.add("qt");
        volumeUnitArray.add("oz");

        areaUnitArray = new ArrayList<String>();
        areaUnitArray.add("sqm");
        areaUnitArray.add("sqkm");
        areaUnitArray.add("sqmi");
        areaUnitArray.add("acre");

        // from: http://stackoverflow.com/questions/11920754/android-fill-spinner-from-java-code-programmatically
        unitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        input_units.setAdapter(unitAdapter);
        output_units.setAdapter(unitAdapter);

        input_units.setOnItemSelectedListener(itemSelectedListener);
        output_units.setOnItemSelectedListener(itemSelectedListener);
    }

        @Override
    protected void onResume(){
        super.onResume();

        // read back persistent state

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);

        // modes
        mode = settings.getInt("mode", 0);
        getActionBar().setSelectedNavigationItem(mode);


        final int input_unit = settings.getInt("input_unit", 0);
        final int output_unit = settings.getInt("output_unit", 0);

        // from http://stackoverflow.com/questions/1484528/android-setselection-having-no-effect-on-spinner
        // I think this works because it won't run until the view is created
        input_units.post(new Runnable() {
            @Override
            public void run() {
                input_units.setSelection(input_unit);
            }
        });
        output_units.post(new Runnable() {
            @Override
            public void run() {
                output_units.setSelection(output_unit);
            }
        });

        // stupid hack to put a double in shared prefs
        current_input = doubleToPrettyString(Double.longBitsToDouble(settings.getLong("current_input", 0)));
        input_text.setText(current_input);


        update_results();

    }

    public String doubleToPrettyString(double input) {
        // format the output string (i.e. get rid of decimal if it is .0)
        double decimal = input - (int)input;
        String ret;
        if (decimal == 0) {
            ret = Integer.toString((int)input);
        }
        else {
            ret = Double.toString(input);
        }
        return ret;
    }


    public void onPause() {
//        Save state
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = settings.edit();

        editor.putLong("current_input", Double.doubleToRawLongBits(Double.parseDouble(current_input))); // because you can't put a double in sharedprefs
        editor.putInt("mode", mode);
        editor.putInt("input_unit", input_units.getSelectedItemPosition());
        editor.putInt("output_unit", output_units.getSelectedItemPosition());
        editor.commit();

        super.onPause();
    }

    public void buttonClick(View v) {
        Button dot;
        switch (v.getId()) {
            case R.id.button_0: current_input = current_input + "0"; break;
            case R.id.button_1: current_input = current_input + "1"; break;
            case R.id.button_2: current_input = current_input + "2"; break;
            case R.id.button_3: current_input = current_input + "3"; break;
            case R.id.button_4: current_input = current_input + "4"; break;
            case R.id.button_5: current_input = current_input + "5"; break;
            case R.id.button_6: current_input = current_input + "6"; break;
            case R.id.button_7: current_input = current_input + "7"; break;
            case R.id.button_8: current_input = current_input + "8"; break;
            case R.id.button_9: current_input = current_input + "9"; break;
            case R.id.button_dot:
                current_input = current_input + ".";
                // disable the period if it has been typed

                dot = (Button)findViewById(R.id.button_dot);
                dot.setEnabled(false);
                break;
            case R.id.button_clear:
                current_input = "";
                // re-enable the period
                dot = (Button)findViewById(R.id.button_dot);
                dot.setEnabled(true);
                break;
        }

        // update text of input box
        try {
            // get rid of decimal if it is .0
            double inputNumber = Double.parseDouble(current_input);
            input_text.setText(doubleToPrettyString(inputNumber));
        } catch (NumberFormatException e) {
            input_text.setText(Integer.toString(0));
        }
        // move cursor to end
        input_text.setSelection(input_text.getText().length());

        // Try to do calculation and update output box
        update_results();
    }

    public void update_results(){

       // Save state

        // try to update conversion now that a button has been clicked if it makes sense
        if(!current_input.equals("")){

            double input = Double.parseDouble(current_input);

            String input_unit;
            String output_unit;

            try {
                input_unit = input_units.getSelectedItem().toString();
                output_unit = output_units.getSelectedItem().toString();
            } catch (Exception e) {
                input_unit = "";
                output_unit = "";
            }
            double conversion = calc.convert_units(input_unit, output_unit, input, mode); // figure out how to get the text out of the buttons
            conversion = Math.round(conversion * 100000.) / 100000.; // round to 6 decimal places
            output_text.setText(doubleToPrettyString(conversion));

        } else {
            current_input = "0";
            output_text.setText(current_input);
            input_text.setText("0");
            input_text.setSelection(input_text.getText().length());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.unit_converter_main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_share) {
            String message = input_text.getText().toString() + " " + input_units.getSelectedItem().toString() + "s in " +
                    output_units.getSelectedItem().toString() + "s is " +
                    output_text.getText().toString() + " " + output_units.getSelectedItem().toString() + "s";
            Intent shareIntent = new Intent();
            shareIntent.setType("text/plain");
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Unit Conversion Results");
            shareIntent.putExtra(Intent.EXTRA_TEXT, message);
            startActivity(Intent.createChooser(shareIntent, "Share to..."));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(int position, long id) {
        // switch mode
        mode = position;

        unitAdapter.clear();

        switch (mode){
            case 0: // data
                unitAdapter.addAll(dataUnitArray);
                break;
            case 1: // distance
                unitAdapter.addAll(distanceUnitArray);
                break;
            case 2: // mass
                unitAdapter.addAll(massUnitArray);
                break;
            case 3: // volume
                unitAdapter.addAll(volumeUnitArray);
                break;
            case 4: // area
                unitAdapter.addAll(areaUnitArray);
                break;
        }

//        current_input = "";
     // input_text.setText(current_input);
        update_results();

        return true;
    }
}
