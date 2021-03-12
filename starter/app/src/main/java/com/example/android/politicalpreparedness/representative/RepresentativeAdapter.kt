package com.example.android.politicalpreparedness.representative

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.CHANNEL
import com.example.android.politicalpreparedness.databinding.ListItemRepresentativeBinding
import com.example.android.politicalpreparedness.representative.model.Representative

class RepresentativeAdapter(val viewModel: RepresentativeViewModel) : ListAdapter<Representative, ViewHolder>(RepresentativeDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(viewModel, getItem(position))
    }
}

class ViewHolder(val binding: ListItemRepresentativeBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(viewModel: RepresentativeViewModel, representative: Representative?) {
        binding.viewModel = viewModel
        binding.representative = representative
        binding.executePendingBindings()
    }

    companion object {
        fun from(parent: ViewGroup): ViewHolder {
            val binding = ListItemRepresentativeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }
    }
}

class RepresentativeDiffUtil : DiffUtil.ItemCallback<Representative>() {
    override fun areItemsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Representative, newItem: Representative): Boolean {
        return oldItem.official.name == newItem.official.name
    }

}
