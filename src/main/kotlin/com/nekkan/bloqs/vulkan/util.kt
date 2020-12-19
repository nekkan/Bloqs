@file:JvmMultifileClass
@file:JvmName("VulkanApi")

package com.nekkan.bloqs.vulkan

import com.nekkan.bloqs.utils.ApplicationVersion
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.*
import java.nio.ByteBuffer

typealias Vulkan = VkInstance

/**
 * @return A [ByteBuffer] containing the receiver [String] UTF-8 encoded and null-terminated.
 */
inline operator fun CharSequence.unaryPlus(): ByteBuffer = MemoryUtil.memUTF8(this)

/**
 * Translates a Vulkan `VkResult` value to a String describing the result.
 *
 * @param result the `VkResult` value
 * @return the result description
 *
 * https://github.com/LWJGL/lwjgl3-demos/blob/6a113ceb5011ce68e8336d3f2e6c46db03f52250/src/org/lwjgl/demo/vulkan/VKUtil.java#L184
 */
fun translateVulkanResult(result: Int): String = when(result) {
    VK11.VK_SUCCESS -> "Command successfully completed."
    VK11.VK_NOT_READY -> "A fence or query has not yet completed."
    VK11.VK_TIMEOUT -> "A wait operation has not completed in the specified time."
    VK11.VK_EVENT_SET -> "An event is signaled."
    VK11.VK_EVENT_RESET -> "An event is unsignaled."
    VK11.VK_INCOMPLETE -> "A return array was too small for the result."
    KHRSwapchain.VK_SUBOPTIMAL_KHR -> "A swapchain no longer matches the surface properties exactly, but can still be used to present to the surface successfully."
    VK11.VK_ERROR_OUT_OF_HOST_MEMORY -> "A host memory allocation has failed."
    VK11.VK_ERROR_OUT_OF_DEVICE_MEMORY -> "A device memory allocation has failed."
    VK11.VK_ERROR_INITIALIZATION_FAILED -> "Initialization of an object could not be completed for implementation-specific reasons."
    VK11.VK_ERROR_DEVICE_LOST -> "The logical or physical device has been lost."
    VK11.VK_ERROR_MEMORY_MAP_FAILED -> "Mapping of a memory object has failed."
    VK11.VK_ERROR_LAYER_NOT_PRESENT -> "A requested layer is not present or could not be loaded."
    VK11.VK_ERROR_EXTENSION_NOT_PRESENT -> "A requested extension is not supported."
    VK11.VK_ERROR_FEATURE_NOT_PRESENT -> "A requested feature is not supported."
    VK11.VK_ERROR_INCOMPATIBLE_DRIVER -> "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons."
    VK11.VK_ERROR_TOO_MANY_OBJECTS -> "Too many objects of the type have already been created."
    VK11.VK_ERROR_FORMAT_NOT_SUPPORTED -> "A requested format is not supported on this device."
    KHRSurface.VK_ERROR_SURFACE_LOST_KHR -> "A surface is no longer available."
    KHRSurface.VK_ERROR_NATIVE_WINDOW_IN_USE_KHR -> "The requested window is already connected to a VkSurfaceKHR, or to some other non-Vulkan API."
    KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR ->
        "A surface has changed in such a way that it is no longer compatible with the swapchain, and further presentation" +
            "requests using the swapchain will fail. Applications must query the new surface properties and recreate their" +
            "swapchain if they wish to continue presenting to the surface."
    KHRDisplaySwapchain.VK_ERROR_INCOMPATIBLE_DISPLAY_KHR -> "The display used by a swapchain does not use the same presentable image layout," +
        "or is incompatible in a way that prevents sharing an image."
    EXTDebugReport.VK_ERROR_VALIDATION_FAILED_EXT -> "A validation layer found an error."
    else -> String.format("%s [%d]", "Unknown", Integer.valueOf(result))
}

/**
 * Applies the application version if the [ApplicationVersion] is not equals to [ApplicationVersion.zero].
 */
inline fun VkApplicationInfo.versionNonZero(version: ApplicationVersion) {
    if(version != ApplicationVersion.zero) {
        applicationVersion(version.vulkanVersion)
    }
}

/**
 * Applies the engine version if the [ApplicationVersion] is not equals to [ApplicationVersion.zero].
 */
inline fun VkApplicationInfo.engineVersionNonZero(version: ApplicationVersion) {
    if(version != ApplicationVersion.zero) {
        engineVersion(version.vulkanVersion)
    }
}
