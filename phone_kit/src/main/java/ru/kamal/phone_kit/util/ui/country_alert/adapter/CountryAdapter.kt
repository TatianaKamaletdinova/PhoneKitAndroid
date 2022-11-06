package ru.kamal.phone_kit.util.ui.country_alert.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.kamal.phone_kit.api.model.Country
import ru.kamal.phone_kit.databinding.ItemCountryBinding

internal class CountryAdapter(
    private val action: (account: Country) -> Unit,
) : ListAdapter<Country, CountryViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCountryBinding.inflate(inflater, parent, false)
        return CountryViewHolder(binding, action)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private class DiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(old: Country, new: Country): Boolean =
            old.shortname == new.shortname

        override fun areContentsTheSame(old: Country, new: Country) =
            old == new
    }
}