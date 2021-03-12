package com.example.android.politicalpreparedness.representative

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.databinding.FragmentRepresentativeBinding
import com.example.android.politicalpreparedness.loadUrl
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.representative.model.Item

class RepresentativeFragment : Fragment() {

    private val viewModel: RepresentativeViewModel by lazy {
        ViewModelProvider(this).get(RepresentativeViewModel::class.java)
    }
    lateinit var binding: FragmentRepresentativeBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentRepresentativeBinding.inflate(inflater, container, false)
        binding.address = Address()
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


        viewModel.loadUrlIntent.observe(viewLifecycleOwner, Observer {
            requireActivity().loadUrl(it)
        })

        val adapter=RepresentativeAdapter(viewModel)
        binding.representativesRecyclerView.adapter=adapter
        viewModel.representatives.observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })

        return binding.root
    }
}