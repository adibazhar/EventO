package com.example.evento.util

import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class EventOGlideModule:AppGlideModule(){
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}