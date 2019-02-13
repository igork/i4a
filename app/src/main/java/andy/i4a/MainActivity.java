package andy.i4a;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private Button btnSubmit;
    static String responseText;
    static StringBuffer response;
    static URL url;
    static Activity activity;
    private static ProgressDialog progressDialog;

    static TextView tv;
    //TextView tv1;
    //TextView tv2;

    //Direct Web services URL
    //private String path = "https://cdn.rawgit.com/arpitmandliya/AndroidRestJSONExample/master/countries.json";

    private static String path = "http://igorkourski.000webhostapp.com/sandbox/one.php?ig=kr&kr=ig";

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.setTitle("Welcome to i4a aka IgorForAndroid");

        // Example of a call to a native method
        tv = findViewById(R.id.sample_text1);
        tv.setText(stringFromJNI());

        //tv1 = (TextView) findViewById(R.id.sample_text1);
        //tv2 = (TextView) findViewById(R.id.sample_text2);

        tv.setText("");
        //tv1.setText("");
        //tv2.setText("");

        activity = this;

        btnSubmit = findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseText="";
                tv.setText("");
                   //Call WebService
                new GetServerData().execute();
            }
        });
    }

    static class GetServerData extends AsyncTask
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            progressDialog = new ProgressDialog(activity);
            progressDialog.setMessage("Fetching request info");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            return getWebServiceResponseData();
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            // Dismiss the progress dialog
            if (progressDialog!=null && progressDialog.isShowing())
                progressDialog.dismiss();

            // For populating list data
            tv.setText(responseText);
            /*
            CustomCountryList customCountryList = new CustomCountryList(activity, countries);
            listView.setAdapter(customCountryList);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Country country = (Country)countries.get(position);
                    Toast.makeText(getApplicationContext(),"You Selected "+country.getCountryName()+ " as Country",Toast.LENGTH_SHORT).show();        }
            });
            */
        }
    }


    protected static Void getWebServiceResponseData() {

        try {

            url = new URL(path);
            Log.d(TAG, "ServerData: " + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            Log.d(TAG, "Response code: " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                // Reading response from input Stream
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String output;
                response = new StringBuffer();

                while ((output = in.readLine()) != null) {
                    response.append(output);
                }
                in.close();
            }
        } catch(Exception e){
            e.printStackTrace();
        }

        responseText = response.toString();
        //Call ServerData() method to call webservice and store result in response
        //  response = service.ServerData(path, postDataParams);
        Log.d(TAG, "data:" + responseText);

        responseText = parseResponse(responseText);
        Log.d(TAG, "parsed data:" + responseText);
        /*
        try {
            JSONArray jsonarray = new JSONArray(responseText);
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                int id = jsonobject.getInt("id");
                String country = jsonobject.getString("countryName");
                Log.d(TAG, "id:" + id);
                Log.d(TAG, "country:" + country);
                Country countryObj=new Country(id,country);
                countries.add(countryObj);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        */
        return null;
    }

    /*
       "request_headers": {
        "Connection": "Keep-Alive",
        "Proxy-Connection": "Keep-Alive",
        "HOST": "igorkourski.000webhostapp.com",
        "X-Forwarded-Proto": "http",
        "X-Real-IP": "24.4.171.103",
        "X-Forwarded-For": "24.4.171.103",
        "X-Document-Root": "/storage/ssd4/429/8455429/public_html",
        "X-Server-Admin": "webmaster@000webhost.io",
        "X-Server-Name": "igorkourski.000webhostapp.com",
        "cache-control": "no-cache",
        "Postman-Token": "0f7c19ea-02bb-4fcc-9d32-17e5868f3dad",
        "User-Agent": "PostmanRuntime/7.6.0",
        "Accept": "*//*",
            "accept-encoding": "gzip, deflate"},
     */
    /*
    public static String getClientIpAddr(HttpURLConnection conn) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    */
    private static String parseResponse(String resp){
        String result = "API:" + path + "\n";
        try {
            JSONObject obj = new JSONObject(resp);
            JSONObject headers = obj.getJSONObject("request_headers");
            String agent = headers.getString("User-Agent");
            if (agent!=null){
                result+="User-Agent:" + agent + "\n";
            }
            String server = headers.getString("X-Server-Name");
            if (server!=null){
                result+="X-Server-Name:" + server + "\n";
            }
            String admin = headers.getString("X-Server-Admin");
            if (admin!=null){
                result+="X-Server-Admin:" + admin + "\n";
            }
            //String req = (String)obj.get("RequestorIp");
            //if (req!=null){
            //    result+="RequestorIP:" + req + "\n";
            //}
            String req2 = headers.getString("X-Forwarded-For");
            if (req2!=null){
                result+="X-Forwarded-For:" + req2 + "\n";
            } else {
                result+="X-Forwarded-For:" + "Unknown" + "\n";
            }

            //date_completed
            String date = (String)obj.get("date_completed");
            if (date!=null){
                result+="Date:" + date + "\n";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
    }
}
