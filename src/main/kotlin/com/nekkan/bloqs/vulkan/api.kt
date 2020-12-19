@file:JvmName("VulkanApi")

package com.nekkan.bloqs.vulkan

import com.nekkan.bloqs.context.GlfwContext
import com.nekkan.bloqs.utils.ApplicationVersion
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.system.MemoryUtil
import org.lwjgl.vulkan.EXTDebugReport.VK_ERROR_VALIDATION_FAILED_EXT
import org.lwjgl.vulkan.KHRDisplaySwapchain.VK_ERROR_INCOMPATIBLE_DISPLAY_KHR
import org.lwjgl.vulkan.KHRSurface.VK_ERROR_NATIVE_WINDOW_IN_USE_KHR
import org.lwjgl.vulkan.KHRSurface.VK_ERROR_SURFACE_LOST_KHR
import org.lwjgl.vulkan.KHRSwapchain.VK_ERROR_OUT_OF_DATE_KHR
import org.lwjgl.vulkan.KHRSwapchain.VK_SUBOPTIMAL_KHR
import org.lwjgl.vulkan.VK10.VK_ERROR_DEVICE_LOST
import org.lwjgl.vulkan.VK10.VK_ERROR_EXTENSION_NOT_PRESENT
import org.lwjgl.vulkan.VK10.VK_ERROR_FEATURE_NOT_PRESENT
import org.lwjgl.vulkan.VK10.VK_ERROR_FORMAT_NOT_SUPPORTED
import org.lwjgl.vulkan.VK10.VK_ERROR_INCOMPATIBLE_DRIVER
import org.lwjgl.vulkan.VK10.VK_ERROR_INITIALIZATION_FAILED
import org.lwjgl.vulkan.VK10.VK_ERROR_LAYER_NOT_PRESENT
import org.lwjgl.vulkan.VK10.VK_ERROR_MEMORY_MAP_FAILED
import org.lwjgl.vulkan.VK10.VK_ERROR_OUT_OF_DEVICE_MEMORY
import org.lwjgl.vulkan.VK10.VK_ERROR_OUT_OF_HOST_MEMORY
import org.lwjgl.vulkan.VK10.VK_ERROR_TOO_MANY_OBJECTS
import org.lwjgl.vulkan.VK10.VK_EVENT_RESET
import org.lwjgl.vulkan.VK10.VK_EVENT_SET
import org.lwjgl.vulkan.VK10.VK_INCOMPLETE
import org.lwjgl.vulkan.VK10.VK_NOT_READY
import org.lwjgl.vulkan.VK10.VK_SUCCESS
import org.lwjgl.vulkan.VK10.VK_TIMEOUT
import org.lwjgl.vulkan.VK11.*
import org.lwjgl.vulkan.VkApplicationInfo
import org.lwjgl.vulkan.VkInstance
import org.lwjgl.vulkan.VkInstanceCreateInfo
import java.nio.ByteBuffer
import kotlin.contracts.ExperimentalContracts

typealias Vulkan = VkInstance

/**
 * @return A [ByteBuffer] containing the receiver [String] UTF-8 encoded and null-terminated.
 */
inline operator fun CharSequence.unaryPlus(): ByteBuffer = MemoryUtil.memUTF8(this)

/**
 * @return A [Int] Vulkan representation of the receiver [ApplicationVersion].
 */
inline val ApplicationVersion.vulkanVersion: Int
    get() = VK_MAKE_VERSION(major, minor, patch)

/**
 * Apply both [GlfwContext.clientApi] and [GlfwContext.resizable] to the GLFW window hints.
 */
inline fun GlfwContext.applyHints() {
    glfwWindowHint(GLFW_CLIENT_API, clientApi)
    glfwWindowHint(GLFW_RESIZABLE, if(resizable) GLFW_TRUE else GLFW_FALSE)
}

/**
 * Returns a new instance of [VkApplicationInfo] allocated with [VkApplicationInfo.calloc] and apply the
 * [transformer] callback. This function is marked to use the [ExperimentalContracts] API to improve the
 * compatibility.
 */
@OptIn(ExperimentalContracts::class)
inline fun vulkanApplicationInfo(name: String, transformer: VkApplicationInfo.() -> Unit = {}): VkApplicationInfo {
    return VkApplicationInfo.calloc()
        .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
        .pApplicationName(+name)
        .apply(transformer)
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

/**
 * Returns a new instance of [VkInstanceCreateInfo] allocated with [VkInstanceCreateInfo.calloc] and apply the
 * [transformer] callback. This function is marked to use the [ExperimentalContracts] API to improve the
 * compatibility.
 */
@OptIn(ExperimentalContracts::class)
inline fun vulkanInstanceCreateInfo(
    applicationInfo: VkApplicationInfo?,
    transformer: VkInstanceCreateInfo.() -> Unit
): VkInstanceCreateInfo {
    return VkInstanceCreateInfo.calloc()
        .sType(VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO)
        .pApplicationInfo(applicationInfo)
        .apply(transformer)
}

/**
 * Translates a Vulkan `VkResult` value to a String describing the result.
 *
 * @param result the `VkResult` value
 * @return the result description
 *
 * https://github.com/LWJGL/lwjgl3-demos/blob/6a113ceb5011ce68e8336d3f2e6c46db03f52250/src/org/lwjgl/demo/vulkan/VKUtil.java#L184
 */
fun translateVulkanResult(result: Int): String = when(result) {
    VK_SUCCESS -> "Command successfully completed."
    VK_NOT_READY -> "A fence or query has not yet completed."
    VK_TIMEOUT -> "A wait operation has not completed in the specified time."
    VK_EVENT_SET -> "An event is signaled."
    VK_EVENT_RESET -> "An event is unsignaled."
    VK_INCOMPLETE -> "A return array was too small for the result."
    VK_SUBOPTIMAL_KHR -> "A swapchain no longer matches the surface properties exactly, but can still be used to present to the surface successfully."
    VK_ERROR_OUT_OF_HOST_MEMORY -> "A host memory allocation has failed."
    VK_ERROR_OUT_OF_DEVICE_MEMORY -> "A device memory allocation has failed."
    VK_ERROR_INITIALIZATION_FAILED -> "Initialization of an object could not be completed for implementation-specific reasons."
    VK_ERROR_DEVICE_LOST -> "The logical or physical device has been lost."
    VK_ERROR_MEMORY_MAP_FAILED -> "Mapping of a memory object has failed."
    VK_ERROR_LAYER_NOT_PRESENT -> "A requested layer is not present or could not be loaded."
    VK_ERROR_EXTENSION_NOT_PRESENT -> "A requested extension is not supported."
    VK_ERROR_FEATURE_NOT_PRESENT -> "A requested feature is not supported."
    VK_ERROR_INCOMPATIBLE_DRIVER -> "The requested version of Vulkan is not supported by the driver or is otherwise incompatible for implementation-specific reasons."
    VK_ERROR_TOO_MANY_OBJECTS -> "Too many objects of the type have already been created."
    VK_ERROR_FORMAT_NOT_SUPPORTED -> "A requested format is not supported on this device."
    VK_ERROR_SURFACE_LOST_KHR -> "A surface is no longer available."
    VK_ERROR_NATIVE_WINDOW_IN_USE_KHR -> "The requested window is already connected to a VkSurfaceKHR, or to some other non-Vulkan API."
    VK_ERROR_OUT_OF_DATE_KHR ->
        "A surface has changed in such a way that it is no longer compatible with the swapchain, and further presentation" +
            "requests using the swapchain will fail. Applications must query the new surface properties and recreate their" +
            "swapchain if they wish to continue presenting to the surface."
    VK_ERROR_INCOMPATIBLE_DISPLAY_KHR -> "The display used by a swapchain does not use the same presentable image layout," +
        "or is incompatible in a way that prevents sharing an image."
    VK_ERROR_VALIDATION_FAILED_EXT -> "A validation layer found an error."
    else -> String.format("%s [%d]", "Unknown", Integer.valueOf(result))
}
