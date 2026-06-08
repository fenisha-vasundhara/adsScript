package com.messenger.phone.number.text.sms.service.apps.CustomEditText.model;


import android.graphics.drawable.Drawable;
import android.net.Uri;

public interface ChipInterface {

    Object getId();
    Uri getAvatarUri();
    Drawable getAvatarDrawable();
    String getLabel();
    String getInfo();
}
