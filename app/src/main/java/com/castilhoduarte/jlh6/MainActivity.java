package com.castilhoduarte.jlh6;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

public final class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.settings_button).setOnClickListener(v -> openAndroidSettings());
    }

    private void openAndroidSettings() {
        Intent explicit = new Intent(Intent.ACTION_MAIN);
        explicit.setComponent(new ComponentName(
                "com.android.settings", "com.android.settings.Settings"));
        explicit.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(explicit);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }
}
