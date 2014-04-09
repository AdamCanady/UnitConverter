package com.adamcanady.CS342.unitconverter.app;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AdamCanady on 4/5/14.
 */
public class UnitConverterCalculator {

    private Map<String, Double> volume;
    private Map<String, Double> mass;
    private Map<String, Double> distance;
    private Map<String, Double> data;
    private Map<String, Double> area;

    public UnitConverterCalculator(){

        // initialize dictionaries
        this.volume = new HashMap<String, Double>();
        this.volume.put("l", 1.0);
        this.volume.put("ml", 1/0.001);
        this.volume.put("ul", 1/0.000001);
        this.volume.put("qt", 1/0.946);
        this.volume.put("oz", 1/0.029575);

        this.mass = new HashMap<String, Double>();
        this.mass.put("kg", 1.0);
        this.mass.put("g", 1000.0);
        this.mass.put("mg", 1000000.0);
        this.mass.put("ug", 1000000000.0);
        this.mass.put("lb", 0.454);
        this.mass.put("oz", 0.02834);

        this.distance = new HashMap<String, Double>();
        this.distance.put("m", 1.0);
        this.distance.put("km", 1/1000.0);
        this.distance.put("cm", 1/0.01);
        this.distance.put("mm", 1/0.001);
        this.distance.put("um", 1/0.000001);
        this.distance.put("nm", 1/0.000000001);
        this.distance.put("mi", 1/1609.0);
        this.distance.put("ft", 1/0.305);
        this.distance.put("in", 1/0.0254);

        this.data = new HashMap<String, Double>();
        this.data.put("byte", 1.0);
        this.data.put("kb",1/1024.0);
        this.data.put("mb",1/1048576.0);
        this.data.put("gb",1/1073741824.0);
        this.data.put("tb",1/1099511627776.0);
        this.data.put("pb",1/1125899906842624.0);

        this.area = new HashMap<String, Double>();
        this.area.put("sqm", 1.0);
        this.area.put("sqkm", 0.000001);
        this.area.put("sqmi", 0.0000003861);
        this.area.put("acre", 0.0002471);

    }

    public double convert_units(String input_unit, String output_unit, double input, int mode){

        double input_conversion = 0;
        double output_conversion = 0;

        try {
            if (mode == 3) { // Volume
                input_conversion = this.volume.get(input_unit);
                output_conversion = this.volume.get(output_unit);
            } else if (mode == 2) { // Mass
                input_conversion = this.mass.get(input_unit);
                output_conversion = this.mass.get(output_unit);
            } else if (mode == 1) { // Distance
                input_conversion = this.distance.get(input_unit);
                output_conversion = this.distance.get(output_unit);
            } else if (mode == 0) { // Data
                input_conversion = this.data.get(input_unit);
                output_conversion = this.data.get(output_unit);
            } else if (mode == 4) { // Area
                input_conversion = this.area.get(input_unit);
                output_conversion = this.area.get(output_unit);
            }
        } catch (NullPointerException e) {
            Log.d("Debug: ","Caught Null Pointer Exception in convert_units");
            return (double) 0;
        }

        double result = input / input_conversion * output_conversion;
        return result;
    }
}
