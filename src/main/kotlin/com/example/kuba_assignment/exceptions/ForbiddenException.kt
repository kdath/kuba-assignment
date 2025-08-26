package com.example.kuba_assignment.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus


@ResponseStatus(HttpStatus.FORBIDDEN)
class ForbiddenException(message: String? = null) : RuntimeException(message)
