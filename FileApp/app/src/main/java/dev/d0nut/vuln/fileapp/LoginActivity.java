package dev.d0nut.vuln.fileapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import dev.d0nut.vuln.fileapp.databinding.ActivityMainBinding;

public class LoginActivity extends Activity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username = binding.usernameField.getText().toString();
                String password = binding.passwordField.getText().toString();

                if (username.isEmpty() || password.isEmpty()) {
                    // do nothing?
                    return;
                }

                binding.passwordField.setText("");

                binding.loginProgress.setVisibility(View.VISIBLE);
                dismissKeyboard();

                doLogin(username, password);
            }
        });

        binding.buttonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ConfigActivity.class));
            }
        });
    }

    private void doLogin(String username, String password) {
        // do login
        String serverAddress = getSharedPreferences(ConfigActivity.PREFERENCES, MODE_PRIVATE).getString(ConfigActivity.SERVER_ADDRESS, null);

        JSONObject body = new JSONObject();

        try {
            body.put("username", username);
            body.put("password", password);
        } catch (JSONException e) {
            // whoops
            binding.loginProgress.setVisibility(View.INVISIBLE);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, serverAddress + "/api/auth", body, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                binding.loginProgress.setVisibility(View.INVISIBLE);

                try {
                    if(response.getBoolean("success")) {
                        // successful login
                        String token = response.getJSONObject("data").getString("token");

                        Intent intent = new Intent(LoginActivity.this, FileListActivity.class);
                        intent.putExtra("token", token);
                        startActivity(intent);
                    } else {
                        // error?
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    // error
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                binding.loginProgress.setVisibility(View.INVISIBLE);

                if(error.networkResponse.statusCode == 401) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_invalid_auth), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.error_unknown_error), Toast.LENGTH_SHORT).show();
                }
            }
        });

        ((FileApplication)getApplication()).requestQueue.add(request);
    }

    private void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.passwordField.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(binding.usernameField.getWindowToken(), 0);
    }
}
