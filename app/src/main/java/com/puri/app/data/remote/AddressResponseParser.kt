package com.puri.app.data.remote

import com.puri.app.domain.model.AddressConfidence
import com.puri.app.domain.model.AddressResult
import com.puri.app.domain.model.AddressType
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressResponseParser @Inject constructor() {

    fun parse(rawResponse: String, originalInput: String): AddressResult? {
        val lines = rawResponse.lines().map { it.trim() }.filter { it.isNotBlank() }
        val typeRaw = extractField(lines, "TYPE")
        val normalized = extractField(lines, "NORMALIZED") ?: return null
        val shortForm = extractField(lines, "SHORT")
        val locationDetail = extractField(lines, "DETAIL")
            ?.takeIf { it.uppercase() != "NONE" && it.isNotBlank() }
        val confidenceRaw = extractField(lines, "CONFIDENCE")
        val note = extractField(lines, "NOTE")
            ?.takeIf { it.uppercase() != "NONE" && it.isNotBlank() }

        if (normalized.isBlank()) return null

        val displayShort = shortForm?.takeIf { it.isNotBlank() } ?: normalized
        val encoded = URLEncoder.encode(displayShort, "UTF-8")

        return AddressResult(
            original = originalInput,
            addressType = parseType(typeRaw),
            normalized = normalized,
            shortForm = displayShort,
            locationDetail = locationDetail,
            confidence = parseConfidence(confidenceRaw),
            note = note,
            naverMapAppUrl = "nmap://search?query=$encoded&appname=com.puri.app",
            naverMapWebUrl = "https://map.naver.com/p/search/$encoded"
        )
    }

    private fun extractField(lines: List<String>, key: String): String? =
        lines.firstOrNull { line ->
            line.startsWith("$key:", ignoreCase = true)
        }?.let { line ->
            val colonIndex = line.indexOf(':')
            if (colonIndex < 0) null
            else line.substring(colonIndex + 1).trim().takeIf { it.isNotBlank() }
        }

    private fun parseType(raw: String?): AddressType = when {
        raw == null -> AddressType.INCOMPLETE
        raw.contains("지번") -> AddressType.JIBEON
        raw.contains("도로명") -> AddressType.ROAD_NAME
        raw.contains("영문") -> AddressType.ENGLISH
        raw.contains("건물명") -> AddressType.BUILDING_NAME
        else -> AddressType.INCOMPLETE
    }

    private fun parseConfidence(raw: String?): AddressConfidence = when {
        raw == null -> AddressConfidence.LOW
        raw.equals("HIGH", ignoreCase = true) -> AddressConfidence.HIGH
        raw.equals("MEDIUM", ignoreCase = true) -> AddressConfidence.MEDIUM
        else -> AddressConfidence.LOW
    }
}