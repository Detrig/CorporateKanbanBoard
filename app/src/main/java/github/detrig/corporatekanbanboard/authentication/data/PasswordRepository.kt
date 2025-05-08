package com.example.disputer.authentication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface PasswordRepository {

    suspend fun forgotPassword(email: String)

    class Base(
        private val firebaseAuth: FirebaseAuth,
    ) : PasswordRepository {

        override suspend fun forgotPassword(email: String) {
            return suspendCoroutine { continuation ->
                try {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            continuation.resume(Unit)
                        }
                        .addOnFailureListener { e ->
                            continuation.resumeWithException(e)
                        }
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }
        }

    }
}