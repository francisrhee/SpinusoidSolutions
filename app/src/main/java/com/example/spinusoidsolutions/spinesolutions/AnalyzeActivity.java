package com.example.spinusoidsolutions.spinesolutions;

//package com.javacreed.examples.gson.part2;

        import android.content.Context;
        import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
        import android.view.View;
        import android.widget.Button;
        import android.widget.DatePicker;
        import android.widget.EditText;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;


        import org.jdatepicker.impl.JDatePanelImpl;
        import org.jdatepicker.impl.JDatePickerImpl;
        import org.jdatepicker.impl.UtilDateModel;
        import org.json.JSONObject;

        import java.io.BufferedReader;
        import java.io.FileReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
import java.io.Reader;
        import java.lang.reflect.Type;
        import java.nio.file.Files;
        import java.nio.file.Paths;
        import java.text.ParseException;
import java.text.SimpleDateFormat;
        import java.time.LocalDate;
        import java.time.LocalDateTime;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Locale;



//Following imports are necessary for JSON parsing

public class AnalyzeActivity extends AppCompatActivity {
//    ArrayList<String> numberlist = new ArrayList<>();

//    @SuppressLint("SimpleDateFormat")
    Button submitDatebtn;
    EditText startDateTxt;
    EditText endDateTxt;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);
        SimpleDateFormat neatDateFormatter= new SimpleDateFormat("MM/dd/yyyy");


//
        startDateTxt = (EditText) findViewById(R.id.startDate);
        endDateTxt = (EditText) findViewById(R.id.endDate);
        submitDatebtn = (Button) findViewById(R.id.submitDates);
        submitDatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    createGraphs();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        try {
            createGraphs();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createGraphs() throws Exception {

        String fileName = "SpinusoidData.json";
//      reading json file, converting to string
        String json = loadJSONFromAsset(this);
        Moshi moshi = new Moshi.Builder().build();

        Type spineDataType = Types.newParameterizedType(List.class, SpineData.class);
        JsonAdapter<List<SpineData>> jsonAdapter = moshi.adapter(spineDataType);

//      creating list of objects
        List<SpineData> dataArray = jsonAdapter.fromJson(json);

        SimpleDateFormat dateFormatter=new SimpleDateFormat("MM/dd/yyyy");
        Date beginningFilterDate = new Date();
        Date endingFilterDate = new Date();
        try {
             beginningFilterDate = dateFormatter.parse(startDateTxt.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            endingFilterDate = dateFormatter.parse(endDateTxt.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>();

        List<FormattedSpineData> validDataArray = new ArrayList<>();
        Date tempDate = new Date();
        //making new array only including the dates in between the desired ranges.
        for(int i =0; i < dataArray.size(); i++)
        {
            try {
                tempDate = dateFormatter.parse(dataArray.get(i).date);
                if(tempDate.after(beginningFilterDate) && tempDate.before(endingFilterDate))
                {
                    validDataArray.add(new FormattedSpineData(tempDate, dataArray.get(i).difference));
                }
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        for(int i =0; i < validDataArray.size(); i++)
        {
//           series.appendData(new DataPoint(i, validDataArray.get(i).difference), true, validDataArray.size());;
            series.appendData(new DataPoint(validDataArray.get(i).formattedDate, validDataArray.get(i).difference), true, validDataArray.size());
        }
        graph.addSeries(series);
        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));


        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(validDataArray.get(0).formattedDate.getTime());
        graph.getViewport().setMaxX(validDataArray.get(validDataArray.size()-1).formattedDate.getTime());
        graph.getViewport().setXAxisBoundsManual(true);

        graph.getGridLabelRenderer().setHumanRounding(true);
        graph.getGridLabelRenderer().setNumHorizontalLabels(validDataArray.size()); // only 4 because of the space
        graph.getGridLabelRenderer().setHorizontalLabelsAngle(90);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Date");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Height Difference (mm)");

        graph.setTitle("Height Difference Tracker");
//        graph.getGridLabelRenderer().setHorizontalA("Date");
//
    }
    public String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("SpinusoidData.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
