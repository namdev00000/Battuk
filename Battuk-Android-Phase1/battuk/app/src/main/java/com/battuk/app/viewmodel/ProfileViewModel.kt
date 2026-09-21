package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.Profile
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: BattukRepository) : ViewModel() {

    val profile: StateFlow<Profile?> = repository.observeProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var dobMillis by mutableStateOf<Long?>(null)
    var photoUri by mutableStateOf<String?>(null)

    fun loadIntoForm(profile: Profile) {
        firstName = profile.firstName
        lastName = profile.lastName
        dobMillis = profile.dobMillis
        photoUri = profile.photoUri
    }

    fun save(onDone: () -> Unit) {
        viewModelScope.launch {
            repository.saveProfile(
                Profile(
                    id = 1,
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    dobMillis = dobMillis,
                    photoUri = photoUri
                )
            )
            onDone()
        }
    }
}
