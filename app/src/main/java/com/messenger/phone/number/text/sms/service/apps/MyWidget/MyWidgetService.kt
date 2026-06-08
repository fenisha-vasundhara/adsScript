package com.messenger.phone.number.text.sms.service.apps.MyWidget

import android.content.Intent
import android.widget.RemoteViewsService

class MyWidgetService : RemoteViewsService(){
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        return  MyRemoteViewsFactory(this.getApplicationContext());
    }
}