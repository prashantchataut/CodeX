package com.codex.apk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

public class ApiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeManager.setupTheme(this);
        setContentView(R.layout.activity_api);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        com.google.android.material.textfield.TextInputEditText apiKeyEditText = findViewById(R.id.edit_text_api_key);

        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        String savedApiKey = prefs.getString("qwen_api_key", "");

        if (apiKeyEditText != null) apiKeyEditText.setText(savedApiKey);

        setupDebouncedSaver(apiKeyEditText, s -> prefs.edit().putString("qwen_api_key", s).apply());
    }

    private void setupDebouncedSaver(com.google.android.material.textfield.TextInputEditText editText, java.util.function.Consumer<String> onSave) {
		if (editText == null) return;
		editText.addTextChangedListener(new android.text.TextWatcher() {
			private android.os.Handler handler = new android.os.Handler(android.os.Looper.getMainLooper());
			private Runnable saveRunnable;
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			public void afterTextChanged(android.text.Editable s) {
				if (saveRunnable != null) handler.removeCallbacks(saveRunnable);
				saveRunnable = () -> onSave.accept(s.toString().trim());
				handler.postDelayed(saveRunnable, 700);
			}
		});
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
