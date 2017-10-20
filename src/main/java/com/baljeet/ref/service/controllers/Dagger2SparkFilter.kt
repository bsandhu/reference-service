package com.baljeet.ref.service.controllers

import com.baljeet.ref.service.ServiceRefImpl
import spark.servlet.SparkApplication
import spark.servlet.SparkFilter
import java.lang.IllegalArgumentException
import javax.servlet.FilterConfig
import javax.servlet.ServletException

/**
 * Add hook to the Spark filter to inject Dagger2 provided controllers
 */
class Dagger2SparkFilter : SparkFilter() {

    var config: FilterConfig? = null

    @Throws(ServletException::class)
    override fun init(filterConfig: FilterConfig) {
        this.config = filterConfig
        super.init(filterConfig)
    }

    override fun getApplication(applicationClassName: String?): SparkApplication {
        val serviceRefImpl = this.config!!.servletContext.getAttribute("dagger") as ServiceRefImpl

        when (applicationClassName) {
            DealController::class.java.name -> {return serviceRefImpl.dealController()}
            else -> throw IllegalArgumentException("Could not find a Controller named $applicationClassName")
        }
    }

}