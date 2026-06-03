package com.abbie.fast_tray.models

// Generic class agar bisa dipakai untuk semua endpoint
data class ApiResponse<T>(
    val data: T
)