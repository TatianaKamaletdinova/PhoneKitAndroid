package ru.kamal.country_phone_kit.util.data

import android.content.res.Resources
import ru.kamal.country_phone_kit.api.model.Country
import ru.kamal.country_phone_kit.util.ifNotNull

typealias CodeCountry = String

internal object CountriesRepository {

    private const val CODE_RU = "RU"

    /** Мапа с ключом кода страны и самой страной для быстрого поиска страны по ее коду.
     * Если коды нескольких стран одинаковые, страны перетираются, и это ок. **/
    @Volatile
    private var codesMap: MutableMap<CodeCountry, Country>? = null

    /** Список со всеми станами мира. **/
    @Volatile
    private var countriesArray: List<Country>? = null

    @Volatile
    private var defaultCountry: Country = Country(
        code = "7",
        shortname = "RU",
        name = "Russian Federation",
        isoCode = "643",
        phoneFormat = "000 000 00 00",
        maxPhoneLength = 10
    )

    fun getCodeCountiesMap(resources: Resources): Map<CodeCountry, Country> {
        return codesMap ?: synchronized(this) { loadCountries(resources).first }
    }

    fun getCountries(resources: Resources): List<Country> =
        countriesArray ?: synchronized(this) { loadCountries(resources).second }

    private fun loadCountries(resources: Resources): Triple<Map<CodeCountry, Country>, List<Country>, Country> =
        synchronized(this) {
            ifNotNull(codesMap, countriesArray) { codes, countries ->
                Triple(codes, countries, defaultCountry)
            } ?: run {
                try {
                    val codes = HashMap<CodeCountry, Country>()
                    var defCountry: Country = defaultCountry
                    val countries: MutableList<Country> = mutableListOf()

                    resources.assets.open("countries.txt")
                        .bufferedReader()
                        .useLines { line ->
                            line.forEach { str ->
                                val args = str.split(";").toTypedArray()

                                val countryWithCode = Country(
                                    name = args[1],
                                    code = args[0],
                                    shortname = args[2],
                                    phoneFormat = args[4],
                                    maxPhoneLength = args[5].takeIf { it.isNotBlank() }?.toInt(),
                                    isoCode = args[3]
                                )

                                codes[args[0]] = countryWithCode
                                countries.add(countryWithCode)
                                if (countryWithCode.shortname.uppercase() == CODE_RU) {
                                    defCountry = countryWithCode
                                }
                            }
                            defaultCountry = defCountry
                            updateCountriesArray(countries, defCountry)

                            codes[defCountry.code] = defCountry
                            codesMap = codes
                        }

                    Triple(codes, countries, defCountry)
                } catch (e: Throwable) {
                    initDefault()
                }
            }
        }

    private fun updateCountriesArray(
        countries: List<Country>?,
        defaultCountry: Country
    ): List<Country> {
        val allCountries = if (countries.isNullOrEmpty()) {
            listOf(defaultCountry)
        } else {
            countries.sortedBy { it.name }.toMutableList().apply {
                remove(defaultCountry)
                add(0, defaultCountry)
            }
        }
        countriesArray = allCountries
        return allCountries
    }

    private fun initDefault(): Triple<Map<CodeCountry, Country>, List<Country>, Country> {
        val codes = mutableMapOf(defaultCountry.code to defaultCountry)
        val countries = listOf(defaultCountry)

        codesMap = codes
        countriesArray = countries

        return Triple(codes, countries, defaultCountry)
    }
}