package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.loadUrl
import com.example.android.politicalpreparedness.setProgressBarToVisible
import com.example.android.politicalpreparedness.showSnackBar

class VoterInfoFragment : Fragment() {


    private val viewModel: VoterInfoViewModel by lazy {
        val database = ElectionDatabase.getInstance(requireContext())
        ViewModelProvider(this, VoterInfoViewModelFactory(database.electionDao))
                .get(VoterInfoViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        //TODO: DONE Add ViewModel values and create ViewModel

        //TODO: DONE Add binding values
        val binding = FragmentVoterInfoBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val args = VoterInfoFragmentArgs.fromBundle(arguments!!)
        requireActivity().setProgressBarToVisible(true)
        viewModel.populateVoterInfo(args.argElectionId, args.argDivision)

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer {
            binding.voterInfo = it
            requireActivity().setProgressBarToVisible(false)
        })

        viewModel.loadUrlIntent.observe(viewLifecycleOwner, Observer {
            it?.let {
                requireActivity().loadUrl(it)
                viewModel.doneLoadingUrlIntent()
            }
        })

        viewModel.showPrompt.observe(viewLifecycleOwner, Observer {
            requireActivity().showSnackBar(it, binding.root)
            findNavController().popBackStack()
            requireActivity().setProgressBarToVisible(false)
        })

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO:  DONE Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        viewModel.onSaveButtonClicked.observe(viewLifecycleOwner, Observer {
            it?.let {
                findNavController().popBackStack()
            }
        })

        viewModel.isElectionAvailableLocally.observe(viewLifecycleOwner, Observer {
            it?.let { availableLocally ->
                binding.buttonSaveElection.apply {
                    text = if (availableLocally) getString(R.string.un_follow_election)
                    else getString(R.string.follow_election)
                }
            }
        })

        return binding.root

    }

    //TODO: DONE Create method to load URL intents


}

