@file:JvmMultifileClass
@file:JvmName("VulkanApi")

package com.nekkan.bloqs.vulkan

import com.nekkan.bloqs.context.GlfwContext
import com.nekkan.bloqs.utils.ApplicationVersion
import org.lwjgl.glfw.GLFW
import org.lwjgl.vulkan.VK11.*
import org.lwjgl.vulkan.VkApplicationInfo
import org.lwjgl.vulkan.VkDeviceQueueCreateInfo
import org.lwjgl.vulkan.VkInstanceCreateInfo
import kotlin.contracts.ExperimentalContracts

/**
 * @return A [Int] Vulkan representation of the receiver [ApplicationVersion].
 */
inline val ApplicationVersion.vulkanVersion: Int
    get() = VK_MAKE_VERSION(major, minor, patch)

/**
 * Apply both [GlfwContext.clientApi] and [GlfwContext.resizable] to the GLFW window hints.
 */
inline fun GlfwContext.applyHints() {
    GLFW.glfwWindowHint(GLFW.GLFW_CLIENT_API, clientApi)
    GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, if(resizable) GLFW.GLFW_TRUE else GLFW.GLFW_FALSE)
}

/**
 * Returns a new instance of [VkApplicationInfo] allocated with [VkApplicationInfo.calloc] and apply the
 * [transformer] callback. This function is marked to use the [ExperimentalContracts] API to improve the
 * compatibility. The instance must be explicitly freed.
 */
@OptIn(ExperimentalContracts::class)
inline fun vulkanApplicationInfo(name: String, transformer: VkApplicationInfo.() -> Unit = {}): VkApplicationInfo {
    return VkApplicationInfo.calloc()
        .sType(VK_STRUCTURE_TYPE_APPLICATION_INFO)
        .pApplicationName(+name)
        .apply(transformer)
}

/**
 * Returns a new instance of [VkInstanceCreateInfo] allocated with [VkInstanceCreateInfo.calloc] and apply the
 * [transformer] callback. This function is marked to use the [ExperimentalContracts] API to improve the
 * compatibility. The instance must be explicitly freed.
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
 * Returns a new instance of [VkDeviceQueueCreateInfo] allocated with [VkDeviceQueueCreateInfo.calloc] and
 * apply the [transformer] callback. This function is marked to use the [ExperimentalContracts] API to improve
 * the compatibility. The instance must be explicitly freed.
 */
@OptIn(ExperimentalContracts::class)
inline fun vulkanDeviceQueueCreateInfo(
    queueFamilyIndex: Int,
    transformer: VkDeviceQueueCreateInfo.() -> Unit = {}
): VkDeviceQueueCreateInfo {
    return VkDeviceQueueCreateInfo.calloc()
        .sType(VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO)
        .queueFamilyIndex(queueFamilyIndex)
        .apply(transformer)
}
