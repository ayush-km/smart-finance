package com.ayushkm.smartfinance.model

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException

const val TAG = "TransactionRepository"

object TransactionRepository {
    private val transactionCollectionReference = Firebase.firestore.collection("transactions")
    private val _transactionList = MutableLiveData<List<Transaction>>()
    val transactionList: LiveData<List<Transaction>> = _transactionList

    fun addTransaction(transaction: Transaction, context: Context) =
        CoroutineScope(Dispatchers.IO).launch {
            try {
                transactionCollectionReference.add(transaction).await()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    fun deleteTransaction(transaction: Transaction, context: Context) =
        CoroutineScope(Dispatchers.IO).launch {

        }


    fun updateTransaction(
        oldTransaction: Transaction,
        newTransaction: Transaction,
        context: Context
    ) =
        CoroutineScope(Dispatchers.IO).launch {
            val transactionQuery = transactionCollectionReference
                .whereEqualTo("amount", oldTransaction.amount)
                .whereEqualTo("type", oldTransaction.type)
                .whereEqualTo("category", oldTransaction.category)
                .whereEqualTo("description", oldTransaction.description)
                .get()
                .await()

            if (transactionQuery.documents.isNotEmpty()) {
                transactionQuery.forEach {
                    transactionCollectionReference.document(it.id)
                        .set(newTransaction, SetOptions.merge()).await()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "No Such Transaction Found!", Toast.LENGTH_SHORT).show()
                }
            }
        }

    fun subscribeToTransactionUpdates() {
        transactionCollectionReference
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { querySnapshot, error ->
                error?.let {
                    Log.e(TAG, it.message.toString())
                    return@addSnapshotListener
                }

                querySnapshot?.let {
                    val transactions = mutableListOf<Transaction>()
                    it.documents.forEach { documentSnapshot ->
                        documentSnapshot.toObject<Transaction>()?.let { transaction ->
                            transactions.add(transaction)
                        }
                    }

                    _transactionList.value = transactions
                }
            }
    }
}