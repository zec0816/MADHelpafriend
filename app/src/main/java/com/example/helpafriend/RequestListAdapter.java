package com.example.helpafriend;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RequestListAdapter extends ArrayAdapter<JSONObject> {

    private final Context context;
    private final ArrayList<JSONObject> okuRequestData;

    public RequestListAdapter(Context context, ArrayList<JSONObject> okuRequestData) {
        super(context, R.layout.list_item_request_with_button, okuRequestData);
        this.context = context;
        this.okuRequestData = okuRequestData;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_request_with_button, parent, false);
        }

        TextView requestText = convertView.findViewById(R.id.request_text);
        Button goButton = convertView.findViewById(R.id.go_button);

        JSONObject request = okuRequestData.get(position);
        try {
            int idLocation = request.getInt("id_location");
            double latitude = request.getDouble("latitude");
            double longitude = request.getDouble("longitude");

            requestText.setText("Request ID: " + idLocation);

            goButton.setOnClickListener(v -> {
                updateRequestStatus(idLocation, "accepted");

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latitude + "," + longitude));
                context.startActivity(intent);
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private void updateRequestStatus(int idLocation, String status) {
        String url = Db_Contract.urlUpdateStatus;

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("username", null); // Get the username, default is null if not found

        if (username == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> Toast.makeText(context, "Status updated to " + status, Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(context, "Failed to update status", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_location", String.valueOf(idLocation));
                params.put("status", status);
                params.put("username", username);
                return params;
            }
        };

        Volley.newRequestQueue(context).add(stringRequest);
    }

}
