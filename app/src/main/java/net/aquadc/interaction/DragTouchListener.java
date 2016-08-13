package net.aquadc.interaction;

import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by miha on 04.08.16
 */
public class DragTouchListener implements View.OnTouchListener {

    private final View draggable;
    private final GestureDetectorCompat gd;

    DragTouchListener(@NonNull View draggable, @NonNull final View.OnClickListener listener) {
        this.draggable = draggable;

        this.gd = new GestureDetectorCompat(draggable.getContext(),
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e) {
                        listener.onClick(pressed);
                        pressed = null;
                        return true;
                    }
                });
    }

    private float startX, startY;
    private float dX, dY;
    private boolean frozen;
    private View pressed;

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int act = event.getAction();
        switch (act) {
            case MotionEvent.ACTION_DOWN:
                frozen = true;
                pressed = view;
                gd.onTouchEvent(event);
                startX = event.getRawX();
                startY = event.getRawY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (frozen) {
                    if (sqr(event.getRawX() - startX) + sqr(event.getRawY() - startY) > 60f) {
                        dX = draggable.getX() - event.getRawX();
                        dY = draggable.getY() - event.getRawY();
                        frozen = false;
                    } else {
                        gd.onTouchEvent(event);
                    }
                } else {
                    draggable.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                }
                return true;

            default:
                if (frozen) {
                    gd.onTouchEvent(event);
                } else if (act == MotionEvent.ACTION_UP) {
                    pressed = null;
                }
                return false;
        }
    }

    private static float sqr(float a) {
        return a * a;
    }
}