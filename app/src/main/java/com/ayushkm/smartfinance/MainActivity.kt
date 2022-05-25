package com.ayushkm.smartfinance

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ayushkm.smartfinance.model.Transaction
import com.ayushkm.smartfinance.model.TransactionRepository.subscribeToTransactionUpdates
import com.ayushkm.smartfinance.model.TransactionRepository.transactionList


class MainActivity : AppCompatActivity() {
    private lateinit var usernameTextView: TextView
    private lateinit var addTransactionButton: Button
    private lateinit var transactionsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        subscribeToTransactionUpdates()

        usernameTextView = findViewById(R.id.tv_user_name)
        addTransactionButton = findViewById(R.id.btn_add_transaction)
        transactionsRecyclerView = findViewById(R.id.rv_transactions)

        intent.extras?.let {
            usernameTextView.text = getString(R.string.welcome_string, it.getString("USER_NAME"))
        }

        addTransactionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, TransactionActivity::class.java))
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

        transactionList.observe(this, transactionObserver)
        // endregion recycler view stuff
    }
}