package com.blackmidori.familyexpenses.models

import kotlinx.datetime.Instant

class PayerPaymentWeight(val id: String, val creationDateTime: Instant, val weight: Double, val payer: Payer)