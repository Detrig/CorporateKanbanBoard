package com.example.disputer.authentication.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface UserRepository {
    fun getCurrentUser(): FirebaseUser?
    fun isLoggedIn(): Boolean
    suspend fun isEmailVerified(): Boolean
    suspend fun isCoach(): Boolean

    class Base(
        private val firebaseAuth: FirebaseAuth,
        private val firestore: FirebaseFirestore
    ): UserRepository {

        override fun getCurrentUser(): FirebaseUser? {
            return firebaseAuth.currentUser
        }

        override fun isLoggedIn(): Boolean {
            return firebaseAuth.currentUser != null
        }

        override suspend fun isEmailVerified(): Boolean {
            return suspendCoroutine { continuation ->
                val currentUser = firebaseAuth.currentUser
                if (currentUser != null) {
                    currentUser.reload().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(currentUser.isEmailVerified)
                        } else {
                            continuation.resumeWithException(task.exception ?: Exception("Failed to reload user"))
                        }
                    }
                } else {
                    continuation.resumeWithException(Exception("User is not logged in"))
                }
            }
        }

        override suspend fun isCoach(): Boolean {
            return suspendCoroutine { continuation ->
                val userId = firebaseAuth.currentUser?.uid ?: return@suspendCoroutine continuation.resume(false)
                val firestore = FirebaseFirestore.getInstance()

                firestore.collection("users").document(userId).get()
                    .addOnSuccessListener { document ->
                        val isCoach = document.getBoolean("coach") ?: false
                        continuation.resume(isCoach)
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(false)
                    }
            }
        }

    }
}