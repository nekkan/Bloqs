package com.nekkan.bloqs.context

data class ContextHolder(
    val window: WindowContext,
    val vulkan: VulkanContext,
    val glfw: GlfwContext
)
