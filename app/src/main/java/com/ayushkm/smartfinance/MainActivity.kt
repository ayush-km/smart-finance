package com.ayushkm.smartfinance

import TransactionType
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayushkm.smartfinance.model.ExpenditureCategory
import com.ayushkm.smartfinance.model.IncomeCategory
import com.ayushkm.smartfinance.model.Transaction
import com.ayushkm.smartfinance.model.TransactionRepository


class MainActivity : AppCompatActivity() {
    private val transactionRepository = TransactionRepository()

    private lateinit var uploadDataButton: Button
    private lateinit var transactionsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        transactionRepository.subscribeToTransactionUpdates()


        uploadDataButton = findViewById(R.id.btn_upload_data)
        transactionsRecyclerView = findViewById(R.id.rv_transactions)

        uploadDataButton.setOnClickListener {
            transactionRepository.addTransaction(
                Transaction(
                    TransactionType.EXPENDITURE,
                    34,
                    ExpenditureCategory.MISC.toString(),
                    "Nestle Juice"
                ),
                this@MainActivity
            )
            transactionRepository.addTransaction(
                Transaction(
                    TransactionType.INCOME,
                    55,
                    IncomeCategory.SALARY.toString(),
                    "Just The Monthly Pay"
                ),
                this@MainActivity
            )
        }


        // region recycler view stuff
        transactionsRecyclerView.layoutManager = LinearLayoutManager(this)
        val transactionAdapter = TransactionAdapter {
            Intent(this@MainActivity, TransactionActivity::class.java).also { intent ->
                intent.putExtra("TRANSACTION", it)
                startActivity(intent)
            }
        }
        transactionsRecyclerView.adapter = transactionAdapter

        val transactionObserver = Observer<List<Transaction>> {
            transactionAdapter.submitList(it)
        }

        transactionRepository.transactionList.observe(this, transactionObserver)
        // endregion recycler view stuff
    }
}