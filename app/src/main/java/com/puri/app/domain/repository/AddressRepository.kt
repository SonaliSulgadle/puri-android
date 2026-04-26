package com.puri.app.domain.repository

import com.puri.app.core.common.Resource
import com.puri.app.domain.model.AddressResult

interface AddressRepository {
    suspend fun convertAddress(rawAddress: String): Resource<AddressResult>
}