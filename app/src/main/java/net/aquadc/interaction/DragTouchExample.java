package net.aquadc.interaction;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DragTouchExample extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View draggable = findViewById(R.id.draggable_layout);

        // pass a draggable container as first constructor argument
        View.OnTouchListener listener = new DragTouchListener(draggable, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.button) {
                    Toast.makeText(DragTouchExample.this, "The button has been clicked on.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set as OnTouchListener to draggable container…
        draggable.setOnTouchListener(listener);

        // …and to clickable button.
        View button = draggable.findViewById(R.id.button);
        button.setOnTouchListener(listener);
    }
}
