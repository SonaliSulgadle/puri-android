package com.puri.app.domain.model

// domain/model/AddressResult.kt
data class AddressResult(
    val original: String,
    val addressType: AddressType,
    val normalized: String,       // searchable road address
    val shortForm: String,       // for Naver Map search
    val locationDetail: String?,      // floor, unit, building name — preserved separately
    val confidence: AddressConfidence,
    val note: String?,
    val naverMapAppUrl: String,  // nmap:// deep link
    val naverMapWebUrl: String   // https://map.naver.com fallback
)

enum class AddressType(val label: String) {
    JIBEON("지번 → 도로명"),
    ROAD_NAME("도로명 ✓"),
    ENGLISH("영문 → 한국어"),
    BUILDING_NAME("건물명 → 주소"),
    INCOMPLETE("불완전")
}

enum class AddressConfidence {
    HIGH, MEDIUM, LOW
}