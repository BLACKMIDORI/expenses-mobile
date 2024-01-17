package com.blackmidori.familyexpenses.models

import kotlinx.datetime.Instant

class ChargeAssociation(val id: String, val creationDateTime: Instant, val name: String, val expense: Expense)