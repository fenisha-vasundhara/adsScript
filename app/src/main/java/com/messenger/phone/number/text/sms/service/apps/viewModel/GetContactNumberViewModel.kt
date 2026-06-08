package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.messenger.phone.number.text.sms.service.apps.Repo.GetContactNumberRepo
import com.messenger.phone.number.text.sms.service.apps.modelClass.Contact
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetContactNumberViewModel @Inject constructor(var getContactNumberRepo: GetContactNumberRepo) : ViewModel() {

    val ccontactlist: LiveData<List<Contact>>
        get() = getContactNumberRepo.livelist

    init {
        viewModelScope.launch(Dispatchers.IO){
            getContactNumberRepo.getContactNumberRepo()
        }
    }

}