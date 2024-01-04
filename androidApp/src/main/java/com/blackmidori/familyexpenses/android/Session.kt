package com.blackmidori.familyexpenses.android

import java.time.Instant
import java.time.OffsetDateTime

class Session {
    companion object {
        var appUser: AppUser = AppUser(
            "uninitialized", AppUserTokens(
                Instant.MIN,
                "uninitialized",
                "uninitialized"
            )
        );
    }
}