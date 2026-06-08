package com.messenger.phone.number.text.sms.service.apps.viewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.phone.number.text.sms.service.apps.CommanClass.changelang
import com.messenger.phone.number.text.sms.service.apps.CommanClass.config
import com.messenger.phone.number.text.sms.service.apps.modelClass.Languagemodel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguageViewModel @Inject constructor(@ApplicationContext var context: Context) : ViewModel() {

    val lanlist: ArrayList<Languagemodel> = arrayListOf()
    var lanlistmu = MutableLiveData<List<Languagemodel>>()
    val lanlistlive: LiveData<List<Languagemodel>>
        get() = lanlistmu


    fun refreshdata() {

        viewModelScope.launch(Dispatchers.IO) {
            lanlist.clear()
            lanlist.add(Languagemodel(language = "English", languagecode = "en", languagereal = "English"))
            lanlist.add(Languagemodel(language = "Hindi", languagecode = "hi", languagereal = "हिन्दी"))
            lanlist.add(Languagemodel(language = "Spanish", languagecode = "es", languagereal = "Española"))
            lanlist.add(Languagemodel(language = "French", languagecode = "fr", languagereal = "Français"))
            lanlist.add(Languagemodel(language = "Portuguese", languagecode = "pt", languagereal = "Português"))
            lanlist.add(Languagemodel(language = "German", languagecode = "de", languagereal = "Deutsch"))
            lanlist.add(Languagemodel(language = "Arabic", languagecode = "ar", languagereal = "عربي"))
            lanlist.add(Languagemodel(language = "Japanese", languagecode = "ja", languagereal = "日本語"))
            lanlist.add(Languagemodel(language = "Korean", languagecode = "ko", languagereal = "한국인"))
            lanlist.add(Languagemodel(language = "Indonesian", languagecode = "in", languagereal = "bahasa Indonesia"))
            lanlist.add(Languagemodel(language = "Swedish", languagecode = "sv", languagereal = "svenska"))
            lanlist.add(Languagemodel(language = "Turkish", languagecode = "tr", languagereal = "Türkçe"))
//            lanlist.add(Languagemodel(language = "Gujarati", languagecode = "gu", languagereal = "ગુજરાતી"))
//            lanlist.add(Languagemodel(language = "Chinese", languagecode = "zh", languagereal = "中国人"))
            lanlist.add(Languagemodel(language = "Russian", languagecode = "ru", languagereal = "Русский"))

//            lanlist.forEachIndexed { index, languagemodel ->
//                Log.d("fefdefdsfdsfds", "refreshdata: ${ context.config.SelectedLanguage}")
//                if (languagemodel.languagecode == changelang) {
//
//                    lanlist[index] = Languagemodel(languagemodel.language, true, languagemodel.languagecode,languagemodel.languagereal)
//                } else {
//                    lanlist[index] = Languagemodel(languagemodel.language, false, languagemodel.languagecode,languagemodel.languagereal)
//                }
//            }

            try {
                for (index in lanlist.indices) {
                    val languagemodel = lanlist[index]
                    if (languagemodel.languagecode == changelang) {
                        lanlist[index] = Languagemodel(languagemodel.language, true, languagemodel.languagecode, languagemodel.languagereal)
                    } else {
                        lanlist[index] = Languagemodel(languagemodel.language, false, languagemodel.languagecode, languagemodel.languagereal)
                    }
                }

                val sortedlangList = lanlist.sortedBy { it.language.toString() }
                lanlistmu.postValue(sortedlangList)
            } catch (E: Exception) {
                lanlistmu.postValue(arrayListOf())
            }
        }
    }
}