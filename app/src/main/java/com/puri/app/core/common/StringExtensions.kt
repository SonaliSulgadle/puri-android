package com.puri.app.core.common

private val KOREAN_CITY_NAMES = listOf(
    "서울", "Seoul", "부산", "Busan", "인천", "Incheon",
    "대구", "Daegu", "대전", "Daejeon", "광주", "Gwangju",
    "울산", "Ulsan", "수원", "Suwon", "성남", "Seongnam",
    "고양", "Goyang", "용인", "Yongin", "제주", "Jeju",
    "강릉", "Gangneung", "춘천", "Chuncheon", "전주", "Jeonju",
    "경기도", "Gyeonggi", "강원", "Gangwon", "충청", "Chungcheong",
    "전라", "Jeolla", "경상", "Gyeongsang"
)

private val DISTRICT_SUFFIX_PATTERN = Regex("[가-힣]+(구|군|시|동|읍|면)\\b")
private val ENGLISH_DISTRICT_PATTERN = Regex(
    "\\b\\w+-(gu|gun|si|dong|eup|myeon)\\b",
    RegexOption.IGNORE_CASE
)

/**
 * Detects whether a string contains a location reference —
 * a Korean city/province name, a district-style suffix (구/시/동 etc.),
 * or the romanized equivalent (-gu, -si, -dong etc.)
 *
 * Used to decide whether the prompt should apply location-specific
 * rules instead of the Seoul default.
 */
fun String.containsLocationHint(): Boolean {
    val text = this.trim()
    if (text.isBlank()) return false

    val hasCityName = KOREAN_CITY_NAMES.any { text.contains(it, ignoreCase = true) }
    val hasKoreanSuffix = DISTRICT_SUFFIX_PATTERN.containsMatchIn(text)
    val hasEnglishSuffix = ENGLISH_DISTRICT_PATTERN.containsMatchIn(text)

    return hasCityName || hasKoreanSuffix || hasEnglishSuffix
}