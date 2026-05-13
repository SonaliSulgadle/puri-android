package com.puri.app.domain.usecase

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class InitAnonymousAuthUseCase @Inject constructor() {
    suspend operator fun invoke() {
        runCatching {
            if (Firebase.auth.currentUser == null) {
                Firebase.auth.signInAnonymously().await()
            }
        }
    }
}