package com.blackmidori.familyexpenses.android

import java.time.Instant
import java.time.OffsetDateTime

class AppUserTokens (val accessTokenExpirationDateTime: Instant, val accessToken: String, val refreshToken: String)