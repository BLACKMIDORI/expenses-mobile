package com.blackmidori.familyexpenses

import com.blackmidori.familyexpenses.models.AppUser
import com.blackmidori.familyexpenses.models.AppUserTokens
import kotlinx.datetime.Instant

class Session {
    companion object {
        var appUser: AppUser = AppUser(
            "uninitialized", AppUserTokens(
                Instant.DISTANT_PAST,
                "uninitialized",
                "uninitialized"
            )
        );
    }
}