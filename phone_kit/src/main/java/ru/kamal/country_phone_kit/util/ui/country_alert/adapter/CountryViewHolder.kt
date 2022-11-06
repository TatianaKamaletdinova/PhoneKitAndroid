package ru.kamal.country_phone_kit.util.ui.country_alert.adapter

import androidx.recyclerview.widget.RecyclerView
import ru.kamal.country_phone_kit.R
import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.databinding.ItemCountryBinding
import ru.kamal.country_phone_kit.util.getFlag
import ru.kamal.country_phone_kit.util.withUnit

internal class CountryViewHolder(
    private val binding: ItemCountryBinding,
    private val action: (account: Country) -> Unit,
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(country: Country) = withUnit(binding) {
        flag.text = getFlag(shortName = country.shortname, isAddArrow = false)
        this.country.text = country.name

        code.setTextColor(code.resources.getColor(R.color.color_accent))
        code.text = code.context.getString(R.string.common_plus, country.code)

        root.setOnClickListener { action(country) }
    }
}