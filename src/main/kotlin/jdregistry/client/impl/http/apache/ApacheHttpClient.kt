package jdregistry.client.impl.http.apache

import jdregistry.client.http.IHttpGetClient
import jdregistry.client.http.IHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
import java.io.ByteArrayOutputStream
import java.net.URI

/**
 * Test class for implementing [IHttpGetClient].
 *
 * @author Lukas Zimmermann
 * @since 0.0.1
 *
 */
class ApacheHttpClient : IHttpGetClient {

    private val client = HttpClientBuilder.create().build()

    override fun get(uri: URI, authorization: String?): IHttpResponse {

        // Execute HTTP GET, read all required attributes from the response and close
        val (outstream, statusCode, authenticate) = client.execute(HttpGet(uri)).use { response ->

            val outstream = ByteArrayOutputStream()
            response.entity.writeTo(outstream)

            Triple(
                    outstream,
                    response.statusLine.statusCode,
                    response.getHeaders("Www-Authenticate").map { it.value })
        }
        outstream.use { it.flush() }

        return object : IHttpResponse {

            override val authenticate = authenticate
            override val body = outstream.toString()
            override val statusCode = statusCode
        }
    }
}
