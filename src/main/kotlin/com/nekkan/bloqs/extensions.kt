@file:JvmName("Extensions")

package com.nekkan.bloqs

import org.lwjgl.system.CustomBuffer
import org.lwjgl.system.MemoryUtil
import java.nio.Buffer

inline fun free(vararg buffers: CustomBuffer<*>?) = buffers.forEach {
    MemoryUtil.memFree(it)
}

inline fun free(vararg buffers: Buffer?) = buffers.forEach {
    MemoryUtil.memFree(it)
}

