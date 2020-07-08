package com.example.evento.data.repo

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.evento.data.model.*
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.example.evento.util.randomID
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.*
import kotlin.coroutines.resume

const val TAG = "LoginRepo"

class FirebaseRepo(
    private val firebaseAuthResponse: FirebaseAuthResponse,
    private val firebaseDbResponse: FirebaseDbResponse
) {
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var successResult = MutableLiveData<AuthResult>()
    private val firebaseDatabaseReference = Firebase.database.reference
    private val firebaseStorageReference = Firebase.storage.reference

    fun currentUser() = auth.currentUser

    fun signupUserWithEmailandPassword(
        email: String,
        password: String
    ): LiveData<FirebaseAuthResponse> {
        val result = MutableLiveData<FirebaseAuthResponse>()
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, it.user.toString())
                firebaseAuthResponse.authResult = it
                result.value = firebaseAuthResponse
                signOut()
            }
            .addOnFailureListener {
                Log.d(TAG, it.message.toString())
                firebaseAuthResponse.errorMessages = it.message.toString()
                result.value = firebaseAuthResponse
            }
        return result
    }

    suspend fun signupUserWithEmailandPassword2(
        email: String,
        password: String
    ) = suspendCancellableCoroutine<FirebaseResult> { continuation ->
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                continuation.resume(FirebaseResult.Success(it))
                //signOut()
            }
            .addOnFailureListener {
                Log.d(TAG, it.message.toString())
                continuation.resume(FirebaseResult.Failure(it.message!!))
            }
    }

    fun signinWithEmailandPassword(
        email: String,
        password: String
    ): LiveData<FirebaseAuthResponse> {
        val result = MutableLiveData<FirebaseAuthResponse>()
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                firebaseAuthResponse.authResult = it
                result.value = firebaseAuthResponse
            }
            .addOnFailureListener {
                firebaseAuthResponse.errorMessages = it.message.toString()
                result.value = firebaseAuthResponse
            }
        return result
    }

    suspend fun signinWithEmailandPassword2(email: String, password: String) =
        suspendCancellableCoroutine<FirebaseResult> { continuation ->
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                continuation.resume(FirebaseResult.Success(it))
            }
                .addOnFailureListener {
                    continuation.resume(FirebaseResult.Failure(it.message!!))
                }
        }

    fun signOut() {
        if (auth.currentUser == null) return
        auth.signOut()
    }

    fun uploadUserInfoToFirebase(userInfo: UserInfo) {
        val uid = auth.uid
        // val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")
        firebaseDatabaseReference.child("/users/$uid").setValue(userInfo)
        //reference.setValue(userInfo)
    }

    suspend fun readUserInfoFromFirebase() =
        suspendCancellableCoroutine<FirebaseResult> { continuation ->
            val uid = auth.uid
            // val reference = FirebaseDatabase.getInstance().getReference("/users/$uid")

            firebaseDatabaseReference.child("/users/$uid")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        if (!continuation.isActive) {
                            continuation.resume(FirebaseResult.Failure(p0.message))
                        }
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val data = p0.getValue(UserInfo::class.java)
                        if (!continuation.isActive) {
                            continuation.resume(FirebaseResult.Success(data))
                        }
                    }
                })
        }

    suspend fun uploadImageToFirebase(uri: Uri) =
        suspendCancellableCoroutine<FirebaseResult> { continuation ->

            val filename = String().randomID("image")
            val ref = firebaseStorageReference.child("images/$filename")


            ref.putFile(uri)
                .addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        continuation.resume(FirebaseResult.Success(it))
                    }
                }.addOnFailureListener {
                    // continuation.resume(FirebaseResult.Failure(Throwable(it.message)))
                }

        }

    suspend fun uploadEventsToFirebase(events: Events) =
        suspendCancellableCoroutine<FirebaseResult> { cancellable ->
            val uid = auth.uid
            val db = Firebase.database.reference
            val key = db.child("events").push().key

            if (key == null) {
                Log.d("FirebaseRepo", "Cant get key for pus")
                // cancellable.resume(FirebaseResult.Failure(Throwable("Something going wrong when trying to create event")))
                return@suspendCancellableCoroutine
            }
            // val imageLink = uploadImageToFirebase(events.imageUrl.toUri())


            Log.d("FirebaseRepo", "$key")
            events.timeCreated = ServerValue.TIMESTAMP
            val childUpdate = HashMap<String, Any>()
            childUpdate["/events/${events.id}"] = events
            println("EVENTSID = ${events.id}")
            childUpdate["/users/$uid/events/${events.id}"] = events.id

            db.updateChildren(childUpdate).addOnSuccessListener {
                cancellable.resume(FirebaseResult.Success(it))
            }
        }

    //LiveData<List<Events>>
    suspend fun fetchEventsFromFirebase(eventsId: MutableList<String> = mutableListOf()) =
        suspendCancellableCoroutine<FirebaseResult> { continuation ->
            val eventList = mutableListOf<Events>()
            firebaseDatabaseReference.child("events")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        continuation.resume(FirebaseResult.Failure(p0.message))
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (event in p0.children) {
                            if (event == null) return

                            if (eventsId.isNullOrEmpty()) eventList.add(event.getValue(Events::class.java)!!)
                            else {
                                if (eventsId.contains(event.key)) {
                                    eventList.add(event.getValue(Events::class.java)!!)
                                    println("EVENTS3 = ${event.getValue(Events::class.java)!!}")
                                }
                            }
                            //Log.d("FirebaseRepo", "${eventList}")
                        }
                      //  continuation.resume(FirebaseResult.Success(eventList))
                    }
                })
           continuation.resume(FirebaseResult.Success(eventList))
        }

    suspend fun fetchEventsIdFromUser() =
        suspendCancellableCoroutine<FirebaseResult> { continuation ->
            val events = mutableListOf<String>()
            firebaseDatabaseReference.child("users/${currentUser()!!.uid}/events")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        continuation.resume(FirebaseResult.Failure(p0.message))
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0 == null) return
                        for (event in p0.children) {
                            events.add(event.getValue().toString())
                        }
                       // continuation.resume(FirebaseResult.Success(events)) -- 1
                    }
                })
            continuation.resume(FirebaseResult.Success(events)) // -- 2
        }

    suspend fun fetchEventsIdFromUser2() {
            val events = mutableListOf<String>()
            firebaseDatabaseReference.child("users/${currentUser()!!.uid}/events")
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        val errormessages = p0.message
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        if (p0 == null) return
                        for (event in p0.children) {
                            events.add(event.getValue().toString())
                        }
                    }
                })
        }

    suspend fun fetchfromdb() = withContext(Dispatchers.IO) {
        firebaseDatabaseReference.child("users/${currentUser()!!.uid}/events")
    }
//    fun uploadUserEventToFirebase(events: Events) {
//        val uid = auth.uid
//        val reference = FirebaseDatabase.getInstance().getReference("/users/$uid/events/${events.id}")
//
//        reference.setValue(events.id)
//    }

//    fun uploadEventToFirebase(events: Events){
//        val uid = auth.uid
//        val reference = FirebaseDatabase.getInstance().getReference("/events/${events.id}")
//
//        reference.setValue(events)
//    }


}
