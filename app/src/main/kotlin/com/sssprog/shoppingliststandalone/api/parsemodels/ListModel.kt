package com.sssprog.shoppingliststandalone.api.parsemodels

import com.parse.ParseClassName
import com.parse.ParseObject
import com.parse.ParseQuery

@ParseClassName("List")
public class ListModel : ParseObject() {

    public var name: String by parseProperty()

    companion object {
        public fun query(): ParseQuery<ListModel> = ParseQuery.getQuery(javaClass<ListModel>())
    }

}
