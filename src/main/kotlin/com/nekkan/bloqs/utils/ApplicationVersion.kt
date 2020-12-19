package com.nekkan.bloqs.utils

data class ApplicationVersion(val major: Int, val minor: Int, val patch: Int) {

    override fun toString(): String {
        return "$major.$minor.$patch"
    }

    override fun equals(other: Any?): Boolean {
        return other is ApplicationVersion && other.major == major && other.minor == minor && other.patch == patch
    }

    companion object {
        val zero = ApplicationVersion(0, 0, 0)
    }

}
