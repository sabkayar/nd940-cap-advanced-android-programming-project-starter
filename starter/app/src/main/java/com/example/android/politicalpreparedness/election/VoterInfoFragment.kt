package com.example.android.politicalpreparedness.election

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.showSnackBar
import com.example.android.politicalpreparedness.showToast
import kotlinx.coroutines.launch

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
        viewModel.populateVoterInfo(args.argElectionId, args.argDivision)

        viewModel.voterInfo.observe(viewLifecycleOwner, Observer {
            binding.voterInfo=it
        })

        viewModel.loadUrlIntent.observe(viewLifecycleOwner, Observer {
            loadUrl(it)
        })

        viewModel.showToast.observe(viewLifecycleOwner, Observer {
            requireActivity().showSnackBar(it,binding.root)
            findNavController().popBackStack()
        })

        //TODO: Populate voter info -- hide views without provided data.
        /**
        Hint: You will need to ensure proper data is provided from previous fragment.
         */


        //TODO:  DONE Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks

        return binding.root

    }

    //TODO: DONE Create method to load URL intents

   private fun loadUrl(intent: Intent?) {
        intent?.let {
            startActivity(intent)
            return
        }
        Toast.makeText(requireActivity(),"Information not available",Toast.LENGTH_SHORT).show()
    }

}

