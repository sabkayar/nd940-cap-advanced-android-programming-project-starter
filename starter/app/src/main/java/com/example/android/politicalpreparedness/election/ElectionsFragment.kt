package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionClickListener
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.showSnackBar

class ElectionsFragment : Fragment() {

    //TODO: DONE Declare ViewModel

    private val viewModel: ElectionsViewModel by lazy {
        val database = ElectionDatabase.getInstance(requireContext())
        ViewModelProvider(this, ElectionsViewModelFactory(database.electionDao)
        ).get(ElectionsViewModel::class.java)
    }
    lateinit var binding: FragmentElectionBinding
    lateinit var navController: NavController

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: DONE  Add ViewModel values and create ViewModel

        //TODO: DONE Add binding values
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.electionViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.populateElections()

        //TODO: Link elections to voter info

        //TODO: DONE Initiate recycler adapters

        //TODO: DONE Populate recycler adapters

        //TODO: DONE Refresh adapters when fragment loads

        val electionListAdapter = ElectionListAdapter(ElectionClickListener {
            viewModel.onElectionItemClicked(it)
        })

        val savedElectionListAdapter = ElectionListAdapter(ElectionClickListener {
            viewModel.onElectionItemClicked(it)
        })
        binding.upcomingElectionRecyclerView.adapter = electionListAdapter
        binding.savedElectionsRecyclerView.adapter = savedElectionListAdapter

        navController=this.findNavController()

        viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            electionListAdapter.submitList(it)
        })

        viewModel.savedElections.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                savedElectionListAdapter.submitList(it)
            }else{
                savedElectionListAdapter.submitList(mutableListOf())
            }
        })

        viewModel.navigateToVoterInfo.observe(viewLifecycleOwner, Observer {
            it?.let {
                navController.navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(it.id, it.division))
                viewModel.doneNavigatingToVoterInfo()
            }
        })

        viewModel.showPrompt.observe(viewLifecycleOwner, Observer {
            requireActivity().showSnackBar(it, binding.root)
        })
        return binding.root

    }


}