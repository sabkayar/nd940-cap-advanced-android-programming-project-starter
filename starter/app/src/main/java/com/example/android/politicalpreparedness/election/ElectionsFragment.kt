package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding

class ElectionsFragment: Fragment() {

    //TODO: DONE Declare ViewModel

    private val viewModel: ElectionsViewModel by lazy {
        val database= ElectionDatabase.getInstance(requireContext())
        ViewModelProvider(this, ElectionsViewModelFactory(database.electionDao)
        ).get(ElectionsViewModel::class.java)
    }
    lateinit var binding:FragmentElectionBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_election,container,false)
        binding.electionViewModel=viewModel
        binding.lifecycleOwner=this

        viewModel.populateElectionsFromNetwork()
        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters
        return binding.root

    }

    //TODO: Refresh adapters when fragment loads

}