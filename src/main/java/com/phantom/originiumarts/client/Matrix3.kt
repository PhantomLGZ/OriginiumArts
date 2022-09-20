package com.phantom.originiumarts.client

import com.mojang.math.Vector3f
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

data class Matrix3(
    val m00: Double, val m01: Double, val m02: Double,
    val m10: Double, val m11: Double, val m12: Double,
    val m20: Double, val m21: Double, val m22: Double
) {
    fun row(index: Int): DoubleArray = when (index) {
        0 -> doubleArrayOf(m00, m01, m02)
        1 -> doubleArrayOf(m10, m11, m12)
        2 -> doubleArrayOf(m20, m21, m22)
        else -> doubleArrayOf()
    }

    fun column(index: Int) = when (index) {
        0 -> doubleArrayOf(m00, m10, m20)
        1 -> doubleArrayOf(m01, m11, m21)
        2 -> doubleArrayOf(m02, m12, m22)
        else -> doubleArrayOf()
    }
}

fun createByAlpha(alpha: Double): Matrix3 {
    return Matrix3(
        1.0, 0.0, 0.0,
        0.0, cos(alpha), -sin(alpha),
        0.0, sin(alpha), cos(alpha)
    )
}

fun createByBeta(beta: Double): Matrix3 {
    return Matrix3(
        cos(beta), 0.0, sin(beta),
        0.0, 1.0, 0.0,
        -sin(beta), 0.0, cos(beta)
    )
}

fun createByGamma(gamma: Double): Matrix3 {
    return Matrix3(
        cos(gamma), -sin(gamma), 0.0,
        sin(gamma), cos(gamma), 0.0,
        0.0, 0.0, 1.0
    )
}

fun Matrix3.mul(matrix: Matrix3): Matrix3 {
    return Matrix3(
        this.row(0).mul(matrix.column(0)), this.row(0).mul(matrix.column(1)), this.row(0).mul(matrix.column(2)),
        this.row(1).mul(matrix.column(0)), this.row(1).mul(matrix.column(1)), this.row(1).mul(matrix.column(2)),
        this.row(2).mul(matrix.column(0)), this.row(2).mul(matrix.column(1)), this.row(2).mul(matrix.column(2))
    )
}

fun DoubleArray.mul(array: DoubleArray): Double {
    return this[0] * array[0] + this[1] * array[1] + this[2] * array[2]
}

fun Vector3f.mul(matrix: Matrix3): Vector3f {
    val array = doubleArrayOf(this.x().toDouble(), this.y().toDouble(), this.z().toDouble())
    return Vector3f(
        matrix.row(0).mul(array).toFloat(),
        matrix.row(1).mul(array).toFloat(),
        matrix.row(2).mul(array).toFloat()
    )
}

fun Vec3.mul(matrix: Matrix3): Vec3 {
    val array = doubleArrayOf(this.x(), this.y(), this.z())
    return Vec3(
        matrix.row(0).mul(array),
        matrix.row(1).mul(array),
        matrix.row(2).mul(array)
    )
}