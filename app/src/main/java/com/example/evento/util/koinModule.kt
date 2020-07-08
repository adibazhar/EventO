package com.example.evento.util

import com.example.evento.data.UserViewModel
import com.example.evento.main.EventViewModel
import com.example.evento.data.model.Events
import com.example.evento.data.model.FirebaseAuthResponse
import com.example.evento.data.model.FirebaseDbResponse
import com.example.evento.data.model.UserInfo
import com.example.evento.data.repo.UserRepo
import com.example.evento.data.repo.FirebaseRepo
import com.example.evento.data.FirebaseViewModel
import com.example.evento.data.repo.EventRepo
import com.example.evento.main.adapter.EventAdapter
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module

val viewModelModule: Module = module {

    factory { UserInfo() }
    factory { UserRepo(get()) }

    factory { Events() }
    factory { EventAdapter() }
    factory { EventRepo(get()) }

    factory { FirebaseAuthResponse() }
    factory { FirebaseDbResponse() }
    factory { FirebaseRepo(get(),get()) }

    viewModel { UserViewModel(get(),get())}
    viewModel { EventViewModel(get(),get(),get()) }



    viewModel { FirebaseViewModel(get(),get()) }
}