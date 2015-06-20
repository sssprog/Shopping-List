package com.sssprog.shoppingliststandalone.api.parsemodels

import com.parse.ParseObject
import kotlin.properties.ReadWriteProperty

private class ParseProperty<T: Any> : ReadWriteProperty<ParseObject, T> {
    override fun get(thisRef: ParseObject, desc: PropertyMetadata): T {
        return thisRef.get(desc.name) as T
    }

    override fun set(thisRef: ParseObject, desc: PropertyMetadata, value: T) {
        thisRef.put(desc.name, value)
    }

}

public fun parseProperty<T: Any>(): ParseProperty<T> = ParseProperty<T>()
