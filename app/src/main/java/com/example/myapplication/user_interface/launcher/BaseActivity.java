package com.example.myapplication.user_interface.launcher;

import android.content.Intent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BaseActivity extends AppCompatActivity {

    private boolean keyboardListenersAttached = false;
    private ViewGroup rootLayout;

    private ViewTreeObserver.OnGlobalLayoutListener keyboardLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int heightDiff = rootLayout.getRootView().getHeight() - rootLayout.getHeight();
            int contentViewTop = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
            LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(BaseActivity.this);
            if (heightDiff <= contentViewTop) {
                onHideKeyboard();
                Intent intent = new Intent("KeyboardWillHide");
                broadcastManager.sendBroadcast(intent);
            } else {
                int keyboardHeight = heightDiff - contentViewTop;
                onShowKeyboard(keyboardHeight);

                Intent intent = new Intent("KeyboardWillShow");
                intent.putExtra("KeyboardHeight", keyboardHeight);
                broadcastManager.sendBroadcast(intent);
            }
        }
    };

    protected void onShowKeyboard(int keyboardHeight) {
    }

    protected void onHideKeyboard() {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (keyboardListenersAttached) {
            rootLayout.getViewTreeObserver().removeGlobalOnLayoutListener(keyboardLayoutListener);
        }
    }
}
