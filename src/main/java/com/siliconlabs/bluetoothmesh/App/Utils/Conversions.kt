/*
 * Copyright © 2021 Silicon Labs, http://www.silabs.com. All rights reserved.
 */

package com.siliconlabs.bluetoothmesh.App.Utils

import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * e.g. "ffFF00" -> {-1, -1, 0}
 * @receiver hex string
 */
fun String.hexToByteArray(): ByteArray = Converters.hexToByteArray(this)

fun Int.toByteArray(bytes: Int): ByteArray = ByteBuffer.allocate(4)
        .order(ByteOrder.LITTLE_ENDIAN)
        .putInt(this)
        .array()
        .copyOf(bytes.coerceAtMost(4))
