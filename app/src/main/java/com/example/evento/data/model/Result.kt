package com.example.evento.data.model

import com.google.firebase.auth.AuthResult

data class FirebaseAuthResponse(
    var authResult: AuthResult? = null,
    var errorMessages: String? = null) {
}

data class FirebaseDbResponse(
    var error: String? = null,
    var userInfo: UserInfo? = null
)

sealed class FirebaseResult{
    class Failure(val t:String):FirebaseResult()
    class Success<T>(val r:T):FirebaseResult()
}
