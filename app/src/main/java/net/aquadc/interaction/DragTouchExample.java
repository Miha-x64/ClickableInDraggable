package net.aquadc.interaction;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class DragTouchExample extends AppCompatActivity implements View.OnClickListener {

    private static final int OVERLAYS_CODE = 31700;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View button = findViewById(R.id.draw_trigger);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (Build.VERSION.SDK_INT < 23 || Settings.canDrawOverlays(this)) {
            drawOverlay();
        } else {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAYS_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAYS_CODE) {
            if (Settings.canDrawOverlays(this)) {
                drawOverlay();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void drawOverlay() {
        View draggable = getLayoutInflater().inflate(R.layout.overlay, null);

        // pass a draggable container as first constructor argument
        View.OnTouchListener listener = new DragTouchListener(draggable, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button) {
                    Toast.makeText(DragTouchExample.this, "The button has been clicked on.", Toast.LENGTH_SHORT).show();
                }
            }
        }, getWindowManager());

        // set as OnTouchListener to draggable container…
        draggable.setOnTouchListener(listener);

        // …and to clickable button.
        View button = draggable.findViewById(R.id.button);
        button.setOnTouchListener(listener);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 100;
        params.y = 100;

        getWindowManager().addView(draggable, params);
    }
}
