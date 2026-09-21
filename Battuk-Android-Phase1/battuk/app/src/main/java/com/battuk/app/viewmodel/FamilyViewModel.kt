package com.battuk.app.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.battuk.app.data.BattukRepository
import com.battuk.app.data.entity.FamilyMember
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

val RELATIONS = listOf(
    "Self", "Father", "Mother", "Husband", "Wife", "Son", "Daughter",
    "Brother", "Sister", "Grandmother", "Grandfather", "Uncle", "Aunt", "Other"
)

class FamilyViewModel(private val repository: BattukRepository) : ViewModel() {

    val members: StateFlow<List<FamilyMember>> = repository.observeFamilyMembers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var name by mutableStateOf("")
    var relation by mutableStateOf(RELATIONS.first())
    var dobMillis by mutableStateOf<Long?>(null)
    var photoUri by mutableStateOf<String?>(null)
    var isEarningMember by mutableStateOf(false)

    fun resetForm() {
        name = ""
        relation = RELATIONS.first()
        dobMillis = null
        photoUri = null
        isEarningMember = false
    }

    fun addMember(isSelf: Boolean = false, onDone: () -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.addFamilyMember(
                FamilyMember(
                    name = name.trim(),
                    dobMillis = dobMillis,
                    relation = relation,
                    photoUri = photoUri,
                    isEarningMember = isEarningMember,
                    isSelf = isSelf
                )
            )
            resetForm()
            onDone()
        }
    }

    fun deleteMember(member: FamilyMember) {
        viewModelScope.launch { repository.deleteFamilyMember(member) }
    }
}
