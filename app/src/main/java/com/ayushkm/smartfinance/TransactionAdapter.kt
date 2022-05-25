package com.ayushkm.smartfinance

import TransactionType.EXPENDITURE
import TransactionType.INCOME
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ayushkm.smartfinance.model.Transaction

class TransactionViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    private val amountTextView = view.findViewById<TextView>(R.id.tv_amount)
    private val categoryTextView = view.findViewById<TextView>(R.id.tv_category)

    internal fun bind(transaction: Transaction) {
        val color: Int = when (transaction.type) {
            INCOME -> ContextCompat.getColor(
                view.context,
                R.color.income
            )
            EXPENDITURE -> ContextCompat.getColor(
                view.context,
                R.color.expenditure
            )
        }
        amountTextView.setTextColor(color)
        amountTextView.text = view.context.getString(R.string.amount, transaction.amount)
        categoryTextView.text = transaction.category
    }
}

class TransactionAdapter(
    private val clickHandler: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionViewHolder>(DIFF_CONFIG) {
    companion object {
        val DIFF_CONFIG = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.transaction_item_view, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
        holder.itemView.setOnClickListener { clickHandler(transaction) }
    }
}
