package com.phantom.originiumarts.client.gui.widget

import net.minecraft.client.gui.components.AbstractSliderButton
import net.minecraft.network.chat.TranslatableComponent
import kotlin.math.roundToInt

class CapabilitySliderButton(
    x: Int,
    y: Int,
    width: Int = 200,
    height: Int = 20,
    private val baseMsg: TranslatableComponent,
    initValue: Int,
    private val maxValue: Double = 100.0,
    private val minValue: Double = 0.0,
    val applyValueFun: (value: Int) -> Unit
) : AbstractSliderButton(x, y, width, height, baseMsg.copy(), (initValue - minValue) / (maxValue - minValue)) {

    private var preValue = initValue

    init {
        updateMessage()
    }

    override fun updateMessage() {
        message = baseMsg.copy().append(": ${value.linearInterpolation()}")
    }

    override fun applyValue() {
        value.linearInterpolation().let {
            if (preValue != it) {
                applyValueFun(value.linearInterpolation())
                preValue = it
            }
        }
    }

    private fun Double.linearInterpolation(): Int =
        (minValue + this * (maxValue - minValue)).roundToInt()

}