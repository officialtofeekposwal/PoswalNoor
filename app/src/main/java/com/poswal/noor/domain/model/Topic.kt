package com.poswal.noor.domain.model

enum class Topic(val key: String) {
    NAMAZ("namaz"),
    SABR("sabr"),
    AKHLAQ("akhlaq"),
    PARENTS("parents"),
    DUA("dua"),
    AKHIRAH("akhirah"),
    ILM("ilm"),
    CHARITY("charity"),
    FAMILY("family");

    companion object {
        val ALL_KEYS: Set<String> = entries.map { it.key }.toSet()
        fun fromKey(key: String): Topic? = entries.find { it.key == key }
    }
}
