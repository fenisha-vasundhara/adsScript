package com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.fragment

import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

import android.app.AlarmManager
import android.os.Bundle
import android.text.format.DateUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.ALARM_SERVICE
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.demo.adsmanage.Commen.beGone
import com.demo.adsmanage.Commen.beVisible
import com.demo.adsmanage.Commen.log
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.Dialog.Signature_Setting_Dialog_AfterCall
import com.messenger.phone.number.text.sms.service.apps.AfterCallScreen.afteradapter.RemainderAdapter
import com.messenger.phone.number.text.sms.service.apps.CommanClass.baseConfig
import com.messenger.phone.number.text.sms.service.apps.CommanClass.cancelRemainderPendingIntent
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.CommanClass.openRequestExactAlarmSettings
import com.messenger.phone.number.text.sms.service.apps.CommanClass.scheduleRemainder
import com.messenger.phone.number.text.sms.service.apps.CommanClass.setLocal
import com.messenger.phone.number.text.sms.service.apps.CommanClass.toastMess
import com.messenger.phone.number.text.sms.service.apps.Dialog.PermissionRequiredDialog
import com.messenger.phone.number.text.sms.service.apps.Dialog.ScheduleMessageDialog
import com.messenger.phone.number.text.sms.service.apps.R
import com.messenger.phone.number.text.sms.service.apps.databinding.FragmentAfterCallRemainderBinding
import com.messenger.phone.number.text.sms.service.apps.modelClass.Remindermodel
import com.simplemobiletools.commons.extensions.formatDate
import com.simplemobiletools.commons.helpers.isSPlus
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.joda.time.DateTime
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@AndroidEntryPoint
class AfterCallRemainderFragment : Fragment() {

    lateinit var binding: FragmentAfterCallRemainderBinding
    private lateinit var scheduledDateTime: DateTime


    @Inject
    lateinit var reminderAdapter: RemainderAdapter

    @Inject
    lateinit var messagerDatabaseRepo: MessagerDatabaseRepo

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        requireActivity().setLocal()
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_after_call_remainder,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            remainderlist.adapter = reminderAdapter


            if (requireActivity().config.activeThemeSelection == 1) {
                binding.notFount.setImageDrawable(requireActivity().resources.getDrawable(R.drawable.light_notificarion))
            } else {
                binding.notFount.setImageDrawable(requireActivity().resources.getDrawable(R.drawable.dark_notificarion))
            }

            messagerDatabaseRepo.getalldataRemainderrepo().observe(requireActivity(), Observer {
                if (it.isEmpty()) {
                    binding.notFount.beVisible()
                } else {
                    binding.notFount.beGone()
                }
                reminderAdapter.remainderdata = ArrayList(it)
            })

            reminderAdapter.onItemClickListener = { it ->
                AddRemainder(it.reminderenddate?.let { it1 -> parseDateTime(it1) })
            }

            reminderAdapter.onDeleteClickListener = { it ->
                CoroutineScope(Dispatchers.IO).launch {
                    messagerDatabaseRepo.deleteRemainderRepo(it.id)
                    requireActivity().cancelRemainderPendingIntent(it.id.toLong())
                }
            }

            reminderAdapter.onEditClickListener = { it ->
                it.remindertitle?.let { it1 ->
                    Signature_Setting_Dialog_AfterCall(
                        getString(R.string.remainder),
                        getString(R.string.enter_remainder_text),
                        it1
                    ) { text ->
                        CoroutineScope(Dispatchers.IO).launch {
                            messagerDatabaseRepo.updateremaindertitleRepo(it.id.toLong(), text)
                        }
                    }.show(
                        requireActivity().supportFragmentManager,
                        "Signature_Setting_Dialog_AfterCall"
                    )
                }
            }

            addRemainder.setOnClickListener {
                if (isSPlus()) {
                    val alarmManager: AlarmManager =
                        requireActivity().getSystemService(ALARM_SERVICE) as AlarmManager
                    if (alarmManager.canScheduleExactAlarms()) {
                        AddRemainder()
                    } else {
                        PermissionRequiredDialog(
                            activity = requireActivity(),
                            textId = R.string.allow_alarm_scheduled_messages,
                            positiveActionCallback = {
                                requireActivity().openRequestExactAlarmSettings("com.messenger.phone.number.text.sms.service.apps")
                            },
                        )
                    }
                } else {
                    AddRemainder()
                }
            }
        }
    }

    private fun AddRemainder(originalDateTime: DateTime? = null) {
        ScheduleMessageDialog(requireActivity(), originalDateTime) { newDateTime ->
            if (newDateTime != null) {
                scheduledDateTime = newDateTime
                AddRemainderSet()
            }
        }
    }

    fun parseDateTime(dateString: String): DateTime? {
        try {
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            val offsetDateTime = OffsetDateTime.parse(dateString, formatter)
            return DateTime(offsetDateTime.toInstant().toEpochMilli()) // Convert to Joda DateTime
        } catch (e: Exception) {
            return null
        }
    }

    private fun AddRemainderSet() {
        Signature_Setting_Dialog_AfterCall(
            getString(R.string.remainder),
            getString(R.string.enter_remainder_text),
            ""
        ) { text ->
            CoroutineScope(Dispatchers.IO).launch {
                val data = Remindermodel(
                    remindertitle = text,
                    reminderstartdate = timeformate(scheduledDateTime),
                    reminderenddate = scheduledDateTime.millis.toString()
                )
                val id = messagerDatabaseRepo.insertOrUpdateRemainderRepo(
                    data
                )
                val dataADD = messagerDatabaseRepo.getRemainderMessageRepo(id)
                "remainder data <--------------------> id <-----> ${dataADD}".log()
                if (dataADD != null) {
                    requireActivity().scheduleRemainder(dataADD)
                }
            }
        }.show(requireActivity().supportFragmentManager, "Signature_Setting_Dialog_AfterCall")
    }

    suspend fun timeformate(scheduledDateTime: DateTime): String? = withContext(Dispatchers.IO) {
        val dateTime = scheduledDateTime
        val millis = dateTime.millis
        val formatedate =
            if (dateTime.yearOfCentury().get() > DateTime.now().yearOfCentury().get()) {
                millis.formatDate(requireActivity())
            } else {
                val flags =
                    DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_YEAR
                DateUtils.formatDateTime(requireActivity(), millis, flags)
            }
        return@withContext formatedate
    }
}