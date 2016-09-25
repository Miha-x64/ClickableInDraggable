package net.aquadc.interaction;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by miha on 04.08.16
 */
public class DragTouchListener implements View.OnTouchListener {

    private final View draggable;
    private final GestureDetectorCompat gd;
    private final WindowManager windowManager;

    public DragTouchListener(@NonNull View draggable, @NonNull final View.OnClickListener listener) {
        this(draggable, listener, null);
    }

    public DragTouchListener(@NonNull View draggable, @NonNull final View.OnClickListener listener, @Nullable WindowManager windowManager) {
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
        this.windowManager = windowManager;
    }

    private float startTouchX, startTouchY;
    private int initialLayX, initialLayY;
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
                startTouchX = event.getRawX();
                startTouchY = event.getRawY();
                if (draggable.getLayoutParams() instanceof WindowManager.LayoutParams && windowManager != null) {
                    initialLayX = ((WindowManager.LayoutParams) draggable.getLayoutParams()).x;
                    initialLayY = ((WindowManager.LayoutParams) draggable.getLayoutParams()).y;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (frozen) {
                    if (sqr(event.getRawX() - startTouchX) + sqr(event.getRawY() - startTouchY) > 60f) {
                        dX = draggable.getX() - event.getRawX();
                        dY = draggable.getY() - event.getRawY();
                        frozen = false;
                    } else {
                        gd.onTouchEvent(event);
                    }
                } else {
                    if (draggable.getLayoutParams() instanceof WindowManager.LayoutParams && windowManager != null) {
                        WindowManager.LayoutParams lp = (WindowManager.LayoutParams) draggable.getLayoutParams();
                        lp.gravity = Gravity.TOP | Gravity.LEFT;
                        lp.x = initialLayX + (int) (event.getRawX() - startTouchX);
                        lp.y = initialLayY + (int) (event.getRawY() - startTouchY);
                        windowManager.updateViewLayout(draggable, lp);
                    } else {
                        draggable.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                    }
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