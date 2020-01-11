package com.peixotop.pontointeligente

data class Response<T> (
        val errors: ArrayList<String> = arrayListOf(),
        var data: T? = null
)