package com.blackmidori.familyexpenses.services

import com.blackmidori.familyexpenses.Session
import com.blackmidori.familyexpenses.core.http.HttpClient
import com.blackmidori.familyexpenses.models.AppUserTokens
import com.blackmidori.familyexpenses.repositories.AuthRepository
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.minutes

class TokensService(
    val tokens: AppUserTokens = Session.appUser.tokens,
    val authRepository: AuthRepository,
) {

    fun getUpdatedAccessToken(): Result<String> {
        if (tokens.accessTokenExpirationDateTime.minus(1.minutes) > Clock.System.now()) {
            return Result.success(tokens.accessToken)
        }else{
            val tokensResult = authRepository.renewTokens(tokens.refreshToken)
            if(tokensResult.isSuccess){
                return Result.success(tokensResult.getOrThrow().accessToken)
            }else{
                return Result.failure(tokensResult.exceptionOrNull()!!)
            }
        }
    }
}