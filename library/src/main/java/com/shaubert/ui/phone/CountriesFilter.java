package com.shaubert.ui.phone;


public interface CountriesFilter {

    /**
     * Filter for countries
     * @param country Country
     * @return false to skip, true to accept
     */
    boolean filter(Country country);
}
