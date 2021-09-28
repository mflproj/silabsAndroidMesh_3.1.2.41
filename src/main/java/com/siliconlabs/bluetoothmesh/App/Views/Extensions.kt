/*
 * Copyright © 2020 Silicon Labs, http://www.silabs.com. All rights reserved.
 */
package com.siliconlabs.bluetoothmesh.App.Views

import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.Spinner

fun View.makeVisibleIf(condition: Boolean) {
    visibility = if (condition) View.VISIBLE else View.GONE
}

fun Spinner.setOnItemSelectedListenerOnViewCreated(onItemSelected: (position: Int) -> Unit) = post {
    onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            onItemSelected.invoke(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }
    }
}