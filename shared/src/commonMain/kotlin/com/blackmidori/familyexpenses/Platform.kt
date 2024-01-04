package com.blackmidori.familyexpenses

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform