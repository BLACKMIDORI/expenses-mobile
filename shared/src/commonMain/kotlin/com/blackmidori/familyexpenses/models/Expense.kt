package com.blackmidori.familyexpenses.models

import kotlinx.datetime.Instant

class Expense(val id: String, val creationDateTime: Instant, val name: String)