@file:JvmName("Bloqs")

package com.nekkan.bloqs

import com.nekkan.bloqs.context.GlfwContext
import com.nekkan.bloqs.context.VulkanContext
import com.nekkan.bloqs.context.WindowContext
import com.nekkan.bloqs.utils.ApplicationVersion
import com.nekkan.bloqs.vulkan.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import mu.KotlinLogging
import org.lwjgl.PointerBuffer
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions
import org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported
import org.lwjgl.system.Configuration.DEBUG
import org.lwjgl.system.MemoryUtil.*
import org.lwjgl.vulkan.EXTDebugReport.VK_EXT_DEBUG_REPORT_EXTENSION_NAME
import org.lwjgl.vulkan.VK11.*

/**
 * A constant applied to each context that needs the application name.
 */
const val BLOQS_APPLICATION_NAME = "Bloqs"

/**
 * A constant wrapping a hint, usually provided in [GlfwContext] that returns whether the created window
 * should be resizable or not.
 */
private const val WINDOW_RESIZABLE = false

/**
 * Returns the [CoroutineScope] wrapping the created context. A [coroutineScope] is used to launch
 * coroutines. Learn more: https://kotlinlang.org/docs/reference/coroutines-overview.html
 */
internal val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

/**
 * According to the [Vulkan Tutorial](https://vulkan-tutorial.com) website, validation layers are optional
 * components that hook into Vulkan function calls to apply additional operations, such as:
 * * Checking the values of parameters against the specification to detect misuse
 * * Tracking creation and destruction of objects to find resource leaks
 * * Checking thread safety by tracking the threads that calls originate from
 * * Logging every call and its parameters to the standard output
 * * Tracing Vulkan calls for profiling and replaying
 */
private val validationLayers = if(DEBUG[false])
    listOf("VK_LAYER_LUNARG_standard_validation")
else
    emptyList()

@get:JvmName("getLogger")
val Bloqs = KotlinLogging.logger("com.nekkan.bloqs.Bloqs")

private const val WIDTH = 800

private const val HEIGHT = 600

fun main() {
    /**
     * The version applied to the Vulkan context and the application information. It will be not used
     * because it is not necessary.
     */
    val version = ApplicationVersion.zero

    /**
     * Attempts to initialize the GLFW library using a [static import from LWJGL3][glfwInit]. This single
     * line of code consists in thrown an [IllegalStateException] if the initialization attempt fails.
     */
    check(glfwInit()) { "Cannot initialize the GLFW library." }

    /**
     * Attempts to find the [Vulkan] loader using a [static import from LWJGL3][glfwVulkanSupported]. This
     * single line of code consists in thrown an [IllegalStateException] if it is not found.
     */
    check(glfwVulkanSupported()) { "GLFW failed to find the Vulkan loader." }

    val vulkanContext = VulkanContext(BLOQS_APPLICATION_NAME, version, "", version, VK_API_VERSION_1_1)
    val windowContext = WindowContext(BLOQS_APPLICATION_NAME, WIDTH, HEIGHT)

    /**
     * Returns all required instance extensions or throw an [IllegalStateException] if not found at all.
     */
    val requiredExtensions = checkNotNull(glfwGetRequiredInstanceExtensions()) {
        "Failed to find the required extensions."
    }

    /**
     * We need to tell Vulkan what extensions we would like to use. Those include the platform-dependent
     * required extension given by GLFW to use. We also add the debug extension so that validation layers will
     * and related features will be able to log messages to us.
     */
    val debugReportExtension = +VK_EXT_DEBUG_REPORT_EXTENSION_NAME
    val pointerEnabledExtensionNames = memAllocPointer(requiredExtensions.remaining() + 1)
        .apply { put(debugReportExtension) }
        .flip()

    /**
     * General information about our [Vulkan] application, such as the name, the [Vulkan] version we are
     * targeting, the used engine, etc.
     */
    val vulkanApplicationInfo = vulkanApplicationInfo(BLOQS_APPLICATION_NAME) {
        pEngineName(+vulkanContext.engineName)
        versionNonZero(vulkanContext.applicationVersion)
        engineVersionNonZero(vulkanContext.engineVersion)
        apiVersion(vulkanContext.apiVersion)
    }

    /**
     * Generate a pointer based in the [validationLayers] list. Those layers will check whether we make any
     * mistakes in using the Vulkan API through the debug extension.
     */
    val pointerEnabledLayerNames = memAllocPointer(validationLayers.size)
        .apply {
            validationLayers.forEach { put(+it) }
        }
        .flip()

    /**
     * [Vulkan] uses many structures when creating something. This ensures that every information is available
     * internally and allows for easier validation and immutability.
     */
    val vulkanCreateInfo = vulkanInstanceCreateInfo(vulkanApplicationInfo) {
        ppEnabledExtensionNames(pointerEnabledExtensionNames)
        ppEnabledLayerNames(pointerEnabledLayerNames)
    }

    /**
     * Create a [PointerBuffer] to hold the [Vulkan] instance or throws an [IllegalStateException] if the
     * operation was not succeeded.
     */
    val pointerVulkan = memAllocPointer(1)
    vulkanCheck { vkCreateInstance(vulkanCreateInfo, null, pointerVulkan) }

    // Oriented-object instance wrapper around the long handle.
    val vulkan = Vulkan(pointerVulkan[0], vulkanCreateInfo)
    val (physicalDevice, queueFamilyIndex) = vulkan.findPhysicalDevice()

    /**
     * The creation of a logical device involves specifying a bunch of details in structs again, of which the
     * first one will be VkDeviceQueueCreateInfo. This structure describes the number of queues we want for a
     * single queue family. Right now we're only interested in a queue with graphics capabilities.
     */
    val pointerQueuePriorities = memAllocFloat(1).put(1f)
    val deviceQueueCreateInfo = vulkanDeviceQueueCreateInfoBuffer(queueFamilyIndex) {
        pQueuePriorities(pointerQueuePriorities)
    }

    /**
     * After selecting a physical device to use we need to set up a logical device to interface with it. The
     * logical device creation process is similar to the instance creation process and describes the features
     * we want to use. We also need to specify which queues to create now that we've queried which queue
     * families are available.
     */
    val deviceCreateInfo = vulkanDeviceCreateInfo(deviceQueueCreateInfo) {
        ppEnabledLayerNames(pointerEnabledLayerNames)
        pNext(NULL)
    }

    /**
     * That's it, we're now ready to instantiate the logical device with a call to the appropriately named
     * `vkCreateDevice` function.
     */
    val pointerDevice = memAllocPointer(1)
    val deviceHandle = vulkanCheck("logical device") {
        vkCreateDevice(physicalDevice, deviceCreateInfo, null, pointerDevice)
    }
    val device = pointerDevice[0]

    // Free memory by deallocating everything used.
    free(pointerVulkan, pointerDevice, pointerEnabledExtensionNames, pointerEnabledLayerNames, deviceQueueCreateInfo)
    free(
        debugReportExtension,
        vulkanApplicationInfo.pApplicationName(),
        vulkanApplicationInfo.pEngineName(),
        pointerQueuePriorities
    )
    free(vulkanCreateInfo, vulkanApplicationInfo, deviceCreateInfo)

    /**
     * Apply default window hints to the context to avoid incompatibility. and create a [GlfwContext] by
     * providing the [WINDOW_RESIZABLE] and [GLFW_NO_API] constants and apply apply them to the GLFW window
     * hints.
     */
    val glfwContext = GlfwContext(GLFW_NO_API, WINDOW_RESIZABLE)
    glfwDefaultWindowHints()
    glfwContext.applyHints()

    val window = glfwCreateWindow(windowContext.width, windowContext.height, windowContext.title, NULL, NULL)
    while(!glfwWindowShouldClose(window)) {
        glfwPollEvents()
    }
}
