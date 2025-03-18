package com.plcoding.bookpedia.core.presentation

import com.plcoding.bookpedia.core.domain.DataError
import com.plcoding.bookpedia.resources.Res
import com.plcoding.bookpedia.resources.error_disk_full
import com.plcoding.bookpedia.resources.error_no_internet
import com.plcoding.bookpedia.resources.error_request_timeout
import com.plcoding.bookpedia.resources.error_serialization
import com.plcoding.bookpedia.resources.error_too_many_requests
import com.plcoding.bookpedia.resources.error_unknown

fun DataError.toUiText(): UiText {

    val stringRes = when(this){
        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.UNKNOWN -> Res.string.error_unknown
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.SERVER -> Res.string.error_unknown
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization
        DataError.Remote.UNKNOWN -> Res.string.error_unknown

    }

    return UiText.StringResourceId(stringRes)

}