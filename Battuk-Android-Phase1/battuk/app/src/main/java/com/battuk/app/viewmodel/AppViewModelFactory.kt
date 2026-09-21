package com.battuk.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.battuk.app.data.BattukRepository
import com.battuk.app.util.PreferencesManager
import com.battuk.app.util.SoundManager

/**
 * Minimal manual-DI factory: builds any ViewModel whose constructor takes
 * (repository, preferencesManager, soundManager) or a subset of those,
 * by trying constructors in order of most to least arguments.
 */
class AppViewModelFactory(
    private val repository: BattukRepository,
    private val preferencesManager: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        val constructors = modelClass.constructors
        for (constructor in constructors.sortedByDescending { it.parameterCount }) {
            val params = constructor.parameterTypes
            val args = params.mapNotNull { type ->
                when {
                    type.isAssignableFrom(BattukRepository::class.java) -> repository
                    type.isAssignableFrom(PreferencesManager::class.java) -> preferencesManager
                    type.isAssignableFrom(SoundManager::class.java) -> soundManager
                    else -> null
                }
            }
            if (args.size == params.size) {
                return constructor.newInstance(*args.toTypedArray()) as T
            }
        }
        error("Unable to construct ViewModel of type $modelClass")
    }
}
