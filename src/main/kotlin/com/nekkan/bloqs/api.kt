@file:JvmName("Extensions")

package com.nekkan.bloqs

import org.lwjgl.PointerBuffer
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil.NULL
import org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT
import org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT
import org.lwjgl.vulkan.VK11.*
import org.lwjgl.vulkan.VkAllocationCallbacks
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT
import org.lwjgl.vulkan.VkInstance
import org.lwjgl.vulkan.VkLayerProperties
import java.nio.LongBuffer

typealias Vulkan = VkInstance

fun Vulkan.createDebugUtilsMessengerEXT(
    createInfo: VkDebugUtilsMessengerCreateInfoEXT,
    allocationCallbacks: VkAllocationCallbacks?,
    pDebugMessenger: LongBuffer
): Int {
    if(vkGetInstanceProcAddr(this, "vkCreateDebugUtilsMessengerEXT") != NULL) {
        return vkCreateDebugUtilsMessengerEXT(this, createInfo, allocationCallbacks, pDebugMessenger)
    }
    return VK_ERROR_EXTENSION_NOT_PRESENT
}

fun Vulkan.destroyDebugUtilsMessengerEXT(debugMessenger: Long, allocationCallbacks: VkAllocationCallbacks?) {
    if(vkGetInstanceProcAddr(this, "vkDestroyDebugUtilsMessengerEXT") != NULL) {
        vkDestroyDebugUtilsMessengerEXT(this, debugMessenger, allocationCallbacks)
    }
}

inline fun <T: CharSequence> List<T>.toPointerBuffer(): PointerBuffer {
    val stack = MemoryStack.stackGet()
    val buffer = stack.mallocPointer(size)
    forEach { buffer.put(stack.UTF8(it)) }
    return buffer.rewind()
}

fun checkValidationLayerSupport(layers: List<String>): Boolean {
    val stack = MemoryStack.stackGet()
    val layerCount = stack.ints(0)
    vkEnumerateInstanceLayerProperties(layerCount, null)
    val availableLayers = VkLayerProperties.mallocStack(layerCount[0], stack)
    vkEnumerateInstanceLayerProperties(layerCount, availableLayers)
    return availableLayers.map(VkLayerProperties::layerNameString).containsAll(layers)
}
