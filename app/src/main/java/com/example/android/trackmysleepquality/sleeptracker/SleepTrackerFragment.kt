/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabase
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.databinding.FragmentSleepTrackerBinding
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepDetailFragment
import com.example.android.trackmysleepquality.sleeptracker.SleepTrackerFragmentDirections.actionSleepTrackerFragmentToSleepQualityFragment
import com.google.android.material.snackbar.Snackbar

/**
 * A fragment with buttons to record start and end times for sleep, which are saved in
 * a database. Cumulative data is displayed in a simple scrollable TextView.
 * (Because we have not learned about RecyclerView yet.)
 */
class SleepTrackerFragment : Fragment() {

    private lateinit var sleepTrackerViewModel: SleepTrackerViewModel

    private val adapter by lazy { SleepNightAdapter(adapterListener) }

    private val adapterListener = SleepNightListener { nightId ->
        sleepTrackerViewModel.onSleepNightClicked(nightId)
    }

    private val gridManager by lazy { GridLayoutManager(requireActivity(), 3) }

    private val navigateToSleepQualityObserver by lazy {
        Observer<Long> { nightId ->
            nightId?.let { navigateToSleepQualityFragment(it) }
        }
    }

    private val navigateToSleepDataQualityObserver by lazy {
        Observer<Long> { nightId ->
            nightId?.let { navigateToSleepDataQualityFragment(it) }
        }
    }

    private val showSnackBarEvent by lazy {
        Observer<Boolean> {
            if (it == true)
                presentSnackBarEvent()
        }
    }

    private val showSleepNightItems by lazy {
        Observer<List<SleepNight>> { items ->
            items?.let { adapter.addHeaderAndSubmitList(it) }
        }
    }

    /**
     * Called when the Fragment is ready to display content to the screen.
     *
     * This function uses DataBindingUtil to inflate R.layout.fragment_sleep_quality.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Get a reference to the binding object and inflate the fragment views.
        val binding: FragmentSleepTrackerBinding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_sleep_tracker, container, false)

        val application = requireNotNull(this.activity).application

        val dataSource = SleepDatabase.getInstance(application).sleepDatabaseDao

        val viewModelFactory = SleepTrackerViewModelFactory(dataSource, application)

        sleepTrackerViewModel = ViewModelProvider(this, viewModelFactory)
                .get(SleepTrackerViewModel::class.java)

        binding.lifecycleOwner = this
        binding.sleepTrackerViewModel = sleepTrackerViewModel
        binding.sleepList.layoutManager = gridManager
        binding.sleepList.adapter = adapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        setObservers()
    }

    override fun onStop() {
        super.onStop()
        removeObservers()
    }

    private fun setObservers() {
        with(sleepTrackerViewModel) {
            navigateToSleepQuality.observe(this@SleepTrackerFragment, navigateToSleepQualityObserver)
            navigateToSleepDataQuality.observe(this@SleepTrackerFragment, navigateToSleepDataQualityObserver)
            showSnackbarEvent.observe(this@SleepTrackerFragment, showSnackBarEvent)
            nights.observe(this@SleepTrackerFragment, showSleepNightItems)
        }
    }

    private fun removeObservers() {
        with(sleepTrackerViewModel) {
            navigateToSleepQuality.removeObserver(navigateToSleepQualityObserver)
            navigateToSleepDataQuality.removeObserver(navigateToSleepDataQualityObserver)
            showSnackbarEvent.removeObserver(showSnackBarEvent)
            nights.removeObserver(showSleepNightItems)
        }
    }

    private fun navigateToSleepQualityFragment(nightId: Long) {
        findNavController().navigate(
                actionSleepTrackerFragmentToSleepQualityFragment(nightId))

        sleepTrackerViewModel.onSleepQualityNavigated()
    }

    private fun navigateToSleepDataQualityFragment(nightId: Long) {
        findNavController().navigate(
                actionSleepTrackerFragmentToSleepDetailFragment(nightId))

        sleepTrackerViewModel.onSleepDataQualityNavigated()
    }

    private fun presentSnackBarEvent() {
        Snackbar.make(
                requireActivity().findViewById(android.R.id.content),
                getString(R.string.cleared_message),
                Snackbar.LENGTH_SHORT
        ).show()
        sleepTrackerViewModel.doneShowingSnackbar()
    }
}
