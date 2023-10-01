package com.example.assignment_ocean.data


class DataClass {
    var dataTitle: String? = null
    var dataDesc: String? = null
    var dataPriority: String? = null
    var dataImage: String? = null
    var dataNum: String? = null
    var dataSize: String? = null
    var dataRange: String? = null

    constructor(dataTitle: String?, dataDesc: String?, dataPriority: String?, dataImage: String?, dataNum: String?, dataSize: String?, dataRange: String?){
        this.dataTitle = dataTitle
        this.dataDesc = dataDesc
        this.dataPriority = dataPriority
        this.dataImage = dataImage
        this.dataNum = dataNum
        this.dataSize = dataSize
        this.dataRange = dataRange

    }

    constructor(){

    }

}

