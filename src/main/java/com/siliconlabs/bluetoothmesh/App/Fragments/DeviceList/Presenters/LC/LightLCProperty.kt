/*
 * Copyright © 2019 Silicon Labs, http://www.silabs.com. All rights reserved.
 */

package com.siliconlabs.bluetoothmesh.App.Fragments.DeviceList.Presenters.LC

import androidx.annotation.VisibleForTesting
import com.siliconlabs.bluetoothmesh.App.Utils.Converters
import com.siliconlabs.bluetoothmesh.App.Utils.toByteArray
import kotlin.math.pow

enum class LightLCProperty(val id: Int, val characteristic: Characteristic) {
    AmbientLuxLevelOn(0x002B, Characteristic.Illuminance),
    AmbientLuxLevelProlong(0x002C, Characteristic.Illuminance),
    AmbientLuxLevelStandby(0x002D, Characteristic.Illuminance),
    LightnessOn(0x002E, Characteristic.PerceivedLightness),
    LightnessProlong(0x002F, Characteristic.PerceivedLightness),
    LightnessStandby(0x0030, Characteristic.PerceivedLightness),
    RegulatorAccuracy(0x0031, Characteristic.Percentage8),
    RegulatorKid(0x0032, Characteristic.Coefficient),
    RegulatorKiu(0x0033, Characteristic.Coefficient),
    RegulatorKpd(0x0034, Characteristic.Coefficient),
    RegulatorKpu(0x0035, Characteristic.Coefficient),
    TimeFade(0x0036, Characteristic.TimeMillisecond24),
    TimeFadeOn(0x0037, Characteristic.TimeMillisecond24),
    TimeFadeStandbyAuto(0x0038, Characteristic.TimeMillisecond24),
    TimeFadeStandbyManual(0x0039, Characteristic.TimeMillisecond24),
    TimeOccupancyDelay(0x003A, Characteristic.TimeMillisecond24),
    TimeProlong(0x003B, Characteristic.TimeMillisecond24),
    TimeRunOn(0x003C, Characteristic.TimeMillisecond24);

    enum class Characteristic(val range: IntRange? = null, val factor: Int = 1, val bytes: Int) {
        Illuminance(0..16777214, factor = 100, bytes = 3),
        PerceivedLightness(0..65535, bytes = 2),
        Percentage8(0..200, factor = 2, bytes = 1),
        Coefficient(bytes = 4),
        TimeMillisecond24(0..16777214, factor = 1000, bytes = 3);

        val min get() = range?.first?.toFloat()?.div(factor)
        val max get() = range?.last?.toFloat()?.div(factor)
    }

    private fun checkRange(value: Int) {
        if (value !in characteristic.range ?: return) {
            throw LightLCPropertyValueRangeException()
        }
    }

    @Throws(LightLCPropertyValueRangeException::class)
    fun convertToByteArray(data: String): ByteArray {
        return when (characteristic) {
            Characteristic.Illuminance,
            Characteristic.Percentage8,
            Characteristic.TimeMillisecond24,
            Characteristic.PerceivedLightness -> {
                val value = convertToNormalized(data)
                checkRange(value)
                value.toByteArray(characteristic.bytes)
            }
            Characteristic.Coefficient -> {
                val value = data.toFloat()
                Converters.convertFloatToByteArray(value)
            }
        }
    }

    @VisibleForTesting
    fun convertToNormalized(data: String): Int {
        val commaIndex = data.indexOf('.')
        val numerator = (if (commaIndex < 0) data else data.removeRange(commaIndex, commaIndex + 1)).toLong()
        val denominator = when (commaIndex) {
            -1 -> 1
            0 -> 10
            else -> 10f.pow(data.length - 1 - commaIndex).toInt()
        }
        return (numerator * characteristic.factor / denominator).toInt()
    }

    fun convertToValue(data: ByteArray): String {
        return when (this.characteristic) {
            Characteristic.Illuminance -> {
                val value = Converters.convertUint24ToInt(data, 0) / 100f
                value.toString()
            }
            Characteristic.PerceivedLightness -> {
                Converters.convertUint16ToInt(data, 0).toString()
            }
            Characteristic.Percentage8 -> {
                val value = Converters.convertUint8ToInt(data, 0)
                if (value == 255) "(Not known)" else (value / 2f).toString()
            }
            Characteristic.Coefficient -> {
                Converters.convertByteArrayToFloat(data).toString()
            }
            Characteristic.TimeMillisecond24 -> {
                val value = Converters.convertUint24ToInt(data, 0) / 1000f
                value.toString()
            }
        }
    }

    class LightLCPropertyValueRangeException : Exception()
}
