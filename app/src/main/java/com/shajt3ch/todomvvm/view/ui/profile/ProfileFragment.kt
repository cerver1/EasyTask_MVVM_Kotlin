package com.shajt3ch.todomvvm.view.ui.profile

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide

import com.shajt3ch.todomvvm.R
import com.shajt3ch.todomvvm.viewmodel.profile.ProfileViewModel
import kotlinx.android.synthetic.main.profile_fragment.*

class ProfileFragment : Fragment() {

    companion object {
        const val TAG = "ProfileFragment"
        fun newInstance() = ProfileFragment()
    }

    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.profile_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

        observer()

    }

    private fun observer() {
        viewModel.loading.observe(viewLifecycleOwner, Observer {
            pb_profile.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.getUserProfile().observe(viewLifecycleOwner, Observer {
            profileName.text = it.name
            profileEmail.text = it.email
            profileBio.text = it.bio

            Log.e(TAG, "glide on ")

            Glide.with(this)
                .load(it.profileImage)
                .circleCrop()
                .into(profileImage)

            Log.e(TAG, "glide off ")

        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.profile_edit_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_edit_profile ->

                Log.e(TAG, "Edit Clicked ")

        }

        return false
        //return super.onOptionsItemSelected(item)

    }

}
