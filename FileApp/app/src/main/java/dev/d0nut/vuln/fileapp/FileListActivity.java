package dev.d0nut.vuln.fileapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import dev.d0nut.vuln.fileapp.databinding.ActivityFilesBinding;

public class FileListActivity extends Activity {
    private ActivityFilesBinding binding;
    private FileListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // do stuff here

        binding = ActivityFilesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        adapter = new FileListAdapter(this, R.layout.item_file);
        binding.fileList.setAdapter(adapter);

        Intent callingIntent = getIntent();
        fetchFiles(callingIntent.getStringExtra("token"));
    }

    private void fetchFiles(String token) {
        String serverAddress = getSharedPreferences(ConfigActivity.PREFERENCES, MODE_PRIVATE).getString(ConfigActivity.SERVER_ADDRESS, null);

        JSONObject body = new JSONObject();

        try {
            body.put("token", token);
        } catch (JSONException e) {
            // whoops
            finish();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, serverAddress + "/api/files", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    ArrayList<AppFile> files = new ArrayList<>();
                    JSONArray array = response.getJSONArray("data");

                    for(int i = 0; i < array.length(); i ++) {
                        JSONObject item = array.getJSONObject(i);
                        String name = item.getString("name");
                        String mime = item.getString("mime");

                        files.add(new AppFile(name, mime));
                    }

                    adapter.addAll(files);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    // error
                    finish();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
            }
        });

        ((FileApplication)getApplication()).requestQueue.add(request);
    }


    class FileListAdapter extends ArrayAdapter<AppFile> {
        ArrayList files = new ArrayList();

        public FileListAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();

            if(convertView == null) {
                convertView = inflater.inflate(R.layout.item_file, parent, false);
            }

            ImageView icon = convertView.findViewById(R.id.item_type_icon);
            TextView name = convertView.findViewById(R.id.file_name);

            AppFile appFile = getItem(position);

            icon.setImageDrawable(getResources().getDrawable(getResourceIdForMime(appFile.mime)));
            name.setText(appFile.name);

            return convertView;
        }

        private int getResourceIdForMime(String mime) {
            if(mime.contains("image/")) {
                // image
                return R.drawable.icon_image;
            } else if(mime.contains("excel")) {
                // excel
                return R.drawable.icon_excel;
            } else if(mime.contains("pdf")) {
                // pdf
                return R.drawable.icon_pdf;
            } else if(mime.contains("document")) {
                // docx
                return R.drawable.icon_document;
            } else {
                return R.drawable.donut;
            }
        }
    }

    class AppFile {
        public String name;
        public String mime;

        public AppFile(String name, String mime) {
            this.name = name;
            this.mime = mime;
        }
    }
}
