package com.ayushkm.smartfinance

import TransactionType.EXPENDITURE
import TransactionType.INCOME
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.ayushkm.smartfinance.model.ExpenditureCategory
import com.ayushkm.smartfinance.model.IncomeCategory
import com.ayushkm.smartfinance.model.Transaction
import com.ayushkm.smartfinance.model.TransactionRepository.addTransaction
import com.ayushkm.smartfinance.model.TransactionRepository.deleteTransaction
import com.ayushkm.smartfinance.model.TransactionRepository.updateTransaction

class TransactionActivity : AppCompatActivity() {
    private lateinit var typeRadioGroup: RadioGroup
    private lateinit var categorySpinner: Spinner
    private lateinit var amountEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        typeRadioGroup = findViewById(R.id.rg_type)
        categorySpinner = findViewById(R.id.spn_categories)
        amountEditText = findViewById(R.id.et_amount)
        descriptionEditText = findViewById(R.id.et_description)
        saveButton = findViewById(R.id.btn_save)
        deleteButton = findViewById(R.id.btn_delete)
        deleteButton.visibility = View.GONE

        var passedTransaction: Transaction? = null

        intent.extras?.let {
            passedTransaction = it.getSerializable("TRANSACTION") as Transaction
            initializeUIFromExtras(passedTransaction!!)
            setDeleteButtonClickHandler(passedTransaction!!)
        } ?: updateSpinner(
            ExpenditureCategory.values().map { it.toString() }.toTypedArray()
        )

        typeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val values: Array<String> = when (checkedId) {
                R.id.rb_expense -> ExpenditureCategory.values().map { it.toString() }.toTypedArray()
                else -> IncomeCategory.values().map { it.toString() }.toTypedArray()
            }
            updateSpinner(values)
        }


        saveButton.setOnClickListener {
            val type = when (typeRadioGroup.checkedRadioButtonId) {
                R.id.rb_expense -> EXPENDITURE
                else -> INCOME
            }
            val category = categorySpinner.selectedItem.toString()
            val amtString = amountEditText.text.toString()
            val amount = if (amtString.isNotBlank()) amtString.toInt() else 0
            val descString = descriptionEditText.text.toString()
            val description = if (descString.isNotBlank()) descString else ""



            when (saveButton.text.toString()) {
                "Update Transaction" -> {
                    val oldTransaction = passedTransaction
                    val newTransaction = Transaction(
                        type = type,
                        amount = amount,
                        category = category,
                        description = description,
                    )
                    updateTransaction(
                        oldTransaction = oldTransaction!!,
                        newTransaction = newTransaction,
                        this@TransactionActivity
                    )
                }
                else -> {
                    val transaction = Transaction(
                        type = type,
                        amount = amount,
                        category = category,
                        description = description
                    )
                    addTransaction(transaction, this@TransactionActivity)
                }
            }
            finish()
        }


    }

    private fun setDeleteButtonClickHandler(transaction: Transaction) {
        deleteButton.setOnClickListener {
            deleteTransaction(transaction, this@TransactionActivity)
            finish()
        }
    }

    private fun initializeUIFromExtras(transaction: Transaction) {
        val type = transaction.type
        val typedRadioButtonID: Int
        val category = transaction.category
        val amount = transaction.amount
        val description = transaction.description

        val categories: Array<String> = when (type) {
            EXPENDITURE -> {
                typedRadioButtonID = R.id.rb_expense
                ExpenditureCategory.values().map { it.toString() }.toTypedArray()
            }
            INCOME -> {
                typedRadioButtonID = R.id.rb_income
                IncomeCategory.values().map { it.toString() }.toTypedArray()
            }
        }
        typeRadioGroup.check(typedRadioButtonID)
        updateSpinner(categories, category)
        amountEditText.setText(amount.toString(), TextView.BufferType.EDITABLE)
        descriptionEditText.setText(description, TextView.BufferType.EDITABLE)
        saveButton.text = "Update Transaction"

        deleteButton.visibility = View.VISIBLE
    }

    private fun updateSpinner(values: Array<String>, selectedCategory: String = "") {
        categorySpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item, values
        )
        categorySpinner.setSelection(values.indexOf(selectedCategory), true)
    }
}