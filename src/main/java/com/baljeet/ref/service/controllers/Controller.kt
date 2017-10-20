package com.baljeet.ref.service.controllers

import com.baljeet.ref.service.service.DealService
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.net.MediaType.JSON_UTF_8
import spark.Spark.*
import spark.servlet.SparkApplication
import javax.inject.Inject

class Response (
    var code: Int = 200,
    var msg: String? = "OK"){

    companion object {
        fun failure(msg: String): Response {
            return Response(500, msg)
        }
    }
}

class DealController @Inject constructor(var dealService: DealService) : SparkApplication {

    private val mapper = ObjectMapper()
    private val JSON = JSON_UTF_8.toString()


    override fun init() {
        path("/api") {

            get("/hello") { _, _ -> "Hello World!" }

            get("/deal/:id", JSON) { req, resp ->
                resp.type(JSON)
                val deal = dealService.getDeal(req.params(":id").toInt())
                mapper.writeValueAsString(deal)
            }
        }

        internalServerError { _, res ->
            res.type(JSON_UTF_8.toString())
            mapper.writeValueAsString(Response.failure("Server error while handling Request"))
        }
    }
}