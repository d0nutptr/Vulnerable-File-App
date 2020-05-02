package dev.d0nut.vuln.fileapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import dev.d0nut.vuln.fileapp.databinding.ActivityConfigBinding;

public class ConfigActivity extends Activity {
    public static final String PREFERENCES = "preferences";
    public static final String SERVER_ADDRESS = "server_address";
    private ActivityConfigBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        String serverAddress = getSharedPreferences(ConfigActivity.PREFERENCES, MODE_PRIVATE).getString(ConfigActivity.SERVER_ADDRESS, "");
        binding.serverAddressField.setText(serverAddress);
    }

    @Override
    protected void onPause() {
        super.onPause();

        String serverAddress = binding.serverAddressField.getText().toString();

        saveServerAddress(serverAddress);
    }

    private void saveServerAddress(String serverAddress) {
        SharedPreferences.Editor editor = getSharedPreferences(ConfigActivity.PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(ConfigActivity.SERVER_ADDRESS, serverAddress);
        editor.apply();
    }
}
