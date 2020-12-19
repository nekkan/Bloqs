package com.nekkan.bloqs.utils

data class ApplicationVersion(val major: Int, val minor: Int, val patch: Int) {

    override fun toString(): String {
        return "$major.$minor.$patch"
    }

    companion object {
        val zero = ApplicationVersion(0, 0, 0)
    }

}
