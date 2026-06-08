package com.messenger.phone.number.text.sms.service.apps.lockscreen;

import android.text.Selection;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

class DefaultMovementMethod implements MovementMethod {

  private static DefaultMovementMethod sInstance;

  public static MovementMethod getInstance() {
    if (sInstance == null) {
      sInstance = new DefaultMovementMethod();
    }

    return sInstance;
  }

  private DefaultMovementMethod() {
  }

  @Override
  public void initialize(TextView widget, Spannable text) {
    if (text == null) {
      return;
    }
    final int safeOffset = Math.max(0, Math.min(text.length(), widget.getText() != null ? widget.getText().length() : text.length()));
    try {
      Selection.setSelection(text, safeOffset);
    } catch (IndexOutOfBoundsException ignored) {
      // Avoid crashing if spans/layout are in transient inconsistent state.
    }
  }

  @Override
  public boolean onKeyDown(TextView widget, Spannable text, int keyCode, KeyEvent event) {
    return false;
  }

  @Override
  public boolean onKeyUp(TextView widget, Spannable text, int keyCode, KeyEvent event) {
    return false;
  }

  @Override
  public boolean onKeyOther(TextView view, Spannable text, KeyEvent event) {
    return false;
  }

  @Override
  public void onTakeFocus(TextView widget, Spannable text, int direction) {
    //Intentionally Empty
  }

  @Override
  public boolean onTrackballEvent(TextView widget, Spannable text, MotionEvent event) {
    return false;
  }

  @Override
  public boolean onTouchEvent(TextView widget, Spannable text, MotionEvent event) {
    return false;
  }

  @Override
  public boolean onGenericMotionEvent(TextView widget, Spannable text, MotionEvent event) {
    return false;
  }

  @Override
  public boolean canSelectArbitrarily() {
    return false;
  }
}
