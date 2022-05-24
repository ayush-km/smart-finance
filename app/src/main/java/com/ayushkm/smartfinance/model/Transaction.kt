package com.ayushkm.smartfinance.model

import TransactionType
import com.google.firebase.Timestamp
import java.io.Serializable
import java.util.*

data class Transaction(
    val type: TransactionType = TransactionType.EXPENDITURE,
    val amount: Int = 0,
    val category: String = ExpenditureCategory.FOOD.name,
    val description: String = "",
    val timestamp: Date = Timestamp.now().toDate()
) : Serializable


