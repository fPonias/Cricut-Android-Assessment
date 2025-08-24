package com.cricut.androidassessment.util

class Subscribable<T>(default:T) {
    private val subscribers = mutableSetOf<(from: T, to: T)->Unit>()

    fun subscribe(callback: (from: T, to: T) -> Unit) {
        subscribers.add(callback)
    }

    fun notify(from: T = value) {
        for (cb in subscribers) {
            cb(from, value)
        }
    }

    var value:T = default
        set(value) {
            val from = field
            field = value
            notify(from)
        }
}