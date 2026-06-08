package com.messenger.phone.number.text.sms.service.apps.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.messenger.phone.number.text.sms.service.apps.Repo.MessagerDatabaseRepo

class GetCatMobilenumberRepoViewModelFactory(var repo: MessagerDatabaseRepo, var catName: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GetCatMobilenumberRepoViewModel(repo, catName) as T
    }
}