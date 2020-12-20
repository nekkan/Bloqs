@file:JvmMultifileClass
@file:JvmName("VulkanApi")

package com.nekkan.bloqs.vulkan

import com.nekkan.bloqs.free
import com.nekkan.bloqs.vulkanCheck
import org.lwjgl.system.MemoryUtil.memAllocInt
import org.lwjgl.system.MemoryUtil.memAllocPointer
import org.lwjgl.vulkan.VK11.*
import org.lwjgl.vulkan.VkPhysicalDevice
import org.lwjgl.vulkan.VkQueueFamilyProperties

/**
 * After initializing the Vulkan library through a [Vulkan] instance we need to look for and select a graphics
 * card  in the system that supports the features we need. In fact we can select any number of graphics cards
 * and use them simultaneously, but we'll stick to the first graphics card that suits our needs.
 */
fun Vulkan.findPhysicalDevice(): VkPhysicalDevice {
    /**
     * The graphics card that we'll end up selecting will be stored in a VkPhysicalDevice handle that is added
     * as a new class member. This object will be implicitly destroyed when the [Vulkan] instance is destroyed,
     * so we won't need to do anything new in the cleanup function.
     */
    val bufferDeviceCount = memAllocInt(1)

    /**
     * Listing the graphics cards is very similar to listing extensions and starts with querying just the
     * number.
     */
    val result = vulkanCheck { vkEnumeratePhysicalDevices(this, bufferDeviceCount, null) }

    // If there are 0 devices with Vulkan support then there is no point going further.
    val deviceCount = bufferDeviceCount[0]
    check(deviceCount != 0) { "Failed to find GPUs with Vulkan support!" }

    val pointerPhysicalDevices = memAllocPointer(deviceCount)
    vkEnumeratePhysicalDevices(this, bufferDeviceCount, pointerPhysicalDevices)

    free(bufferDeviceCount)
    free(pointerPhysicalDevices)

    for(index in 0..pointerPhysicalDevices.capacity()) {
        val device = VkPhysicalDevice(pointerPhysicalDevices[index], this)
        if(isDeviceSuitable(device)) {
            return device
        }
    }

    throw IllegalStateException("Failed to find a suitable GPU.")
}

/**
 * Function used in [findPhysicalDevice] to evaluate all devices and check if they are suitable for the
 * operations we want to perform, because not all graphics cards are created equal.
 * @return Whether the given device is suitable to [Vulkan].
 */
private fun isDeviceSuitable(device: VkPhysicalDevice): Boolean {
    val queueFamilyCount = memAllocInt(1)
    vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, null)

    val queueFamilyProperties = VkQueueFamilyProperties.malloc(queueFamilyCount[0])
    vkGetPhysicalDeviceQueueFamilyProperties(device, queueFamilyCount, queueFamilyProperties)

    return queueFamilyProperties
        .map(VkQueueFamilyProperties::queueFlags)
        .any { it and VK_QUEUE_GRAPHICS_BIT != 0 }
        .also {
            queueFamilyProperties.free()
            free(queueFamilyCount)
        }
}
