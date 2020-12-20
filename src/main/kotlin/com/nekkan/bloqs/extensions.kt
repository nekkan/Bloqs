@file:JvmName("Extensions")

package com.nekkan.bloqs

import com.nekkan.bloqs.vulkan.translateVulkanResult
import org.lwjgl.system.CustomBuffer
import org.lwjgl.system.MemoryUtil
import org.lwjgl.system.Struct
import org.lwjgl.vulkan.VK11
import java.nio.Buffer
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

inline fun free(vararg buffers: CustomBuffer<*>?) = buffers.forEach {
    MemoryUtil.memFree(it)
}

inline fun free(vararg buffers: Buffer?) = buffers.forEach {
    MemoryUtil.memFree(it)
}

inline fun free(vararg structs: Struct) = structs.forEach {
    it.free()
}

@OptIn(ExperimentalContracts::class)
inline fun vulkanCheck(name: String = "Vulkan instance", callback: () -> Int): Int {
    contract {
        callsInPlace(callback, InvocationKind.EXACTLY_ONCE)
    }
    val result = callback()
    check(result == VK11.VK_SUCCESS) {
        val translatedError = translateVulkanResult(result)
        "Failed to create the $name. ($result: $translatedError)"
    }
    return result
}
