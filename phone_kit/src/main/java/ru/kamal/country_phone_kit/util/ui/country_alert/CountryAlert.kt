package ru.kamal.country_phone_kit.util.ui.country_alert

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.kamal.country_phone_kit.R
import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.data.CountriesRepository
import ru.kamal.country_phone_kit.util.launchSafeIgnoreError
import ru.kamal.country_phone_kit.util.ui.country_alert.adapter.CountryAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext

internal class CountryAlert(
    private val context: Context,
    private val updateSelectedCountry: (Country) -> Unit
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private var jobCountrySearch: Job? = null
    private val countriesList: List<Country> = CountriesRepository.getCountries(context.resources)

    fun showPickDialog() {
        val dialog = AlertDialog.Builder(context).create()
        val dialogView =
            LayoutInflater.from(context).inflate(R.layout.alert_country, null)
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
        dialog.setView(dialogView)
        dialog.show()
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