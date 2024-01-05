package com.blackmidori.familyexpenses.models

import kotlinx.datetime.Instant

class AppUserTokens (val accessTokenExpirationDateTime: Instant, val accessToken: String, val refreshToken: String)