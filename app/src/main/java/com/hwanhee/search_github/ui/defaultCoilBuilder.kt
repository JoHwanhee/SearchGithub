package com.hwanhee.search_github.ui

import coil.request.CachePolicy
import coil.request.ImageRequest

fun defaultCoilBuilder(): ImageRequest.Builder.() -> Unit  {
    return {
        memoryCachePolicy(CachePolicy.ENABLED)
        diskCachePolicy(CachePolicy.ENABLED)
        addHeader("Cache-Control", "max-age=20,public")
        crossfade(true)
    }
}