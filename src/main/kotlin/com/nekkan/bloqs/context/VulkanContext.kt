package com.nekkan.bloqs.context

import com.nekkan.bloqs.utils.ApplicationVersion

data class VulkanContext(
    val applicationName: String,
    val applicationVersion: ApplicationVersion,
    val engineName: String,
    val engineVersion: ApplicationVersion,
    val apiVersion: Int
): DataContext
