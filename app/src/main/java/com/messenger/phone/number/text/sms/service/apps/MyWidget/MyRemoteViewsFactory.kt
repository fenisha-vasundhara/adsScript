package com.messenger.phone.number.text.sms.service.apps.MyWidget

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.messenger.phone.number.text.sms.service.apps.ApplicationClass.MessagerApplication.Companion.singleton
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.modelClass.Conversation


open class MyRemoteViewsFactory : RemoteViewsService.RemoteViewsFactory {

    lateinit var repo: MessagerDatabaseRepo

    var contactlist: ArrayList<Conversation> = arrayListOf()

    constructor(applicationContext: Context?) {
        context = applicationContext
    }

    private var context: Context? = null
    private var conversationLiveData: LiveData<List<Conversation>>? = null
    private val conversationObserver = Observer<List<Conversation>> {
        contactlist = ArrayList(it.distinctBy { conversation -> conversation.threadId })
    }


    override fun onCreate() {

        repo = singleton?.messagerDatabaseRepo!!
        conversationLiveData = repo.getallconversationunarchivrepo().also {
            it.observeForever(conversationObserver)
        }
    }

    override fun onDataSetChanged() {

    }

    override fun onDestroy() {
        conversationLiveData?.removeObserver(conversationObserver)
        conversationLiveData = null
    }

    override fun getCount(): Int {
        return contactlist.size
    }

    @SuppressLint("RemoteViewLayout")
    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context!!.packageName, R.layout.widgets_conversation_item)

        remoteViews.setTextViewText(R.id.number, contactlist[position].title)
        remoteViews.setTextViewText(R.id.lastmessageshow, contactlist[position].snippet)
        remoteViews.setTextColor(
            R.id.number, (
                    if (contactlist[position].isnewmessage == true)
                        context!!.resources.getColor(R.color.newmessage)
                    else
                        context!!.resources.getColor(R.color.oldmessage))
        )
        remoteViews.setTextColor(
            R.id.lastmessageshow, (
                    if (contactlist[position].isnewmessage == true)
                        context!!.resources.getColor(R.color.newmessageTwo)
                    else
                        context!!.resources.getColor(R.color.oldmessagesecound))
        )

        return remoteViews
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
        val intent = Intent(context, javaClass)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}
