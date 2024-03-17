package com.blackmidori.expenses

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform