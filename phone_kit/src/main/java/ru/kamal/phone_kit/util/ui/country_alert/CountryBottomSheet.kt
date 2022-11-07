package ru.kamal.phone_kit.util.ui.country_alert

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.util.data.CountriesRepository
import ru.kamal.phone_kit.util.launchSafeIgnoreError
import ru.kamal.phone_kit.util.ui.country_alert.adapter.CountryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import ru.kamal.country_phone_kit.R

internal class CountryBottomSheet(
    private val context: Context,
    private val updateSelectedCountry: (Country) -> Unit
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var jobCountrySearch: Job? = null
    private val countriesList: List<Country> = CountriesRepository.getCountries(context.resources)


    fun showDialog() {
        val dialog = BottomSheetDialog(context)
        dialog.setOnShowListener {
            val bottomSheet: FrameLayout = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet) ?: return@setOnShowListener
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            showFullScreenBottomSheet(bottomSheet)

            bottomSheet.setBackgroundResource(android.R.color.transparent)
            expandBottomSheet(bottomSheetBehavior)
        }
        @SuppressLint("InflateParams")
        val dialogView = LayoutInflater.from(context).inflate(R.layout.alert_country, null)
        val countries = dialogView.findViewById<RecyclerView>(R.id.countryList)
        val searchView = dialogView.findViewById<SearchView>(R.id.searchView)

        val countryAdapter = CountryAdapter(
            action = {
                jobCountrySearch?.cancel()
                updateSelectedCountry(it)
                dialog.dismiss()
            })

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                jobCountrySearch = coroutineScope.launchSafeIgnoreError {
                    countryAdapter.submitList(findCountry(query))
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                jobCountrySearch = coroutineScope.launchSafeIgnoreError {
                    countryAdapter.submitList(findCountry(newText))
                }
                return false
            }
        })

        countries.apply {
            adapter = countryAdapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            countryAdapter.submitList(countriesList)
        }

        dialog.setContentView(dialogView)
        dialog.show()
    }

    private fun showFullScreenBottomSheet(bottomSheet: FrameLayout) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = Resources.getSystem().displayMetrics.heightPixels
        bottomSheet.layoutParams = layoutParams
    }

    private fun expandBottomSheet(bottomSheetBehavior: BottomSheetBehavior<FrameLayout>) {
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private suspend fun findCountry(query: String?): List<Country> =
        withContext(Dispatchers.Default) {
            val search = query?.uppercase()

            val result = if (query?.isBlank() == true) {
                countriesList
            } else {
                countriesList.filter {
                    it.name.uppercase().startsWith(search.orEmpty()) ||
                            it.code.uppercase().startsWith(search.orEmpty())
                }
            }
            result
        }
}