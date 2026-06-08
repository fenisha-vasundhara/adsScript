package com.messenger.phone.number.text.sms.service.apps.MyWidget


import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.RemoteViews
import android.widget.Toast
import com.demo.adsmanage.Commen.Constants
import com.messenger.phone.number.text.sms.service.apps.CommanClass.Conversation_CLICK
import com.messenger.phone.number.text.sms.service.apps.CommanClass.LIST_UPDATE
import com.messenger.phone.number.text.sms.service.apps.CommanClass.MyOnClick
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.SelectContactActivity
import com.messenger.phone.number.text.sms.service.apps.SendMessageActivity
import com.simplemobiletools.commons.extensions.getLaunchIntent
import dagger.hilt.android.AndroidEntryPoint
//
//
//@AndroidEntryPoint
//class MyWidgetProvider : AppWidgetProvider() {
//
//
//    override fun onUpdate(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetIds: IntArray?) {
//        performUpdate(context)
//    }
//
//    private fun performUpdate(context: Context?) {
//        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return
//        appWidgetManager.getAppWidgetIds(context?.let { getComponentName(it) }).forEach {
//            val views = context?.let { it1 -> getRemoteViews(appWidgetManager, it1, it) }
////            updateColors(context, views)
//            setupButtons(context, views)
//            setupListView(context, views)
////            updateSongInfo(views, PlaybackService.currentMediaItem?.mediaMetadata)
////            updatePlayPauseButton(context, views, PlaybackService.isPlaying)
//            appWidgetManager.notifyAppWidgetViewDataChanged(it, R.id.allmassagelistview)
//            appWidgetManager.updateAppWidget(it, views)
//        }
//    }
//
//
//    private fun setupListView(context: Context?, views: RemoteViews?) {
//        views?.setRemoteAdapter(R.id.allmassagelistview, Intent(context, MyWidgetService::class.java))
//        try {
//            views?.setRemoteAdapter(R.id.allmassagelistview, Intent(context, MyWidgetService::class.java))
//        } catch (e: SecurityException) {
//            Log.e("WidgetSetup", "Security exception while setting up list view adapter. Check widget permissions.", e)
//            throw RuntimeException("Unable to set up widget list view due to security restrictions. Please verify widget permissions.")
//        } catch (e: NullPointerException) {
//            Log.e("WidgetSetup", "Null context or views provided when setting up list view", e)
//            throw IllegalArgumentException("Invalid widget setup parameters: context and views must not be null")
//        } catch (e: Exception) {
//            Log.e("WidgetSetup", "Unexpected error while setting up list view adapter", e)
//            throw RuntimeException("Failed to initialize widget list view. Please try again or contact support if the problem persists.")
//        }
//    }
///****************  1c8f5b394d934e7694592f95298d2d39  ****************/
//    }
//
//    private fun setupIntent(context: Context?, views: RemoteViews?, action: String, id: Int) {
//        val intent = Intent(context, MyWidgetProvider::class.java)
//        intent.action = action
//        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        views?.setOnClickPendingIntent(id, pendingIntent)
//    }
//
//    override fun onEnabled(context: Context) = triggerUpdate(context)
//
//    override fun onReceive(context: Context?, intent: Intent?) {
//        when (val action = intent?.action) {
//            LIST_UPDATE -> {
//                performUpdate(context)
//            }
//
//            Conversation_CLICK -> handlerControls(context, action)
//
//            else -> super.onReceive(context, intent)
//        }
//    }
//
//    private fun handlerControls(context: Context?, action: String) {
//        when (action) {
//            Conversation_CLICK -> {
//                val intent = context?.getLaunchIntent() ?: Intent(context, SelectContactActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context?.startActivity(intent)
//            }
//        }
//    }
//
//    private fun triggerUpdate(context: Context) {
//        performUpdate(context)
//    }
//
//    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, widgetId: Int, newOptions: Bundle) = triggerUpdate(context)
//
//    private fun getComponentName(context: Context) = ComponentName(context, MyWidgetProvider::class.java)
//
//
//    private fun getRemoteViews(appWidgetManager: AppWidgetManager, context: Context, widgetId: Int): RemoteViews {
//        val layoutId = R.layout.widget_message
//        return RemoteViews(context.packageName, layoutId)
//    }
//
