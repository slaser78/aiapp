package scott.mil

import grails.core.GrailsApplication
import org.apache.hc.client5.http.classic.methods.HttpDelete
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.classic.methods.HttpPut
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.client5.http.socket.ConnectionSocketFactory
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.config.Registry
import org.apache.hc.core5.http.config.RegistryBuilder
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.http.conn.socket.PlainConnectionSocketFactory

import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.SecureRandom
import java.security.cert.X509Certificate

class ElasticService {
    GrailsApplication grailsApplication


    def postRest(String suffix, String query) {
        def json = ""
        def uri = grailsApplication.config.getProperty('elastic', String.class) + suffix
        String apiKey = grailsApplication.config.getProperty('apikey', String.class)
        SSLContext sslContext = getSslContext()
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build()
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(registry))
                .build()
        HttpPost httpPost = new HttpPost(uri)
        httpPost.addHeader("Content-Type", "application/json")
        httpPost.addHeader("Authorization", "ApiKey ${apiKey}")
        StringEntity entity = new StringEntity(query)
        httpPost.setEntity(entity)
        try {
            HttpClientContext clientContext = HttpClientContext.create()
            httpClient.execute(httpPost, clientContext, response -> {
                json = EntityUtils.toString(response.getEntity())
                log.warn ( "JSON: ${json}")
            })
        } catch (e) {
            log.error("Elastic Post Error: " + e.getMessage())
        } finally {
            httpClient.close()
        }
        return json
    }
    def putRest (String suffix, def query) {
        def json = ""
        String apiKey = grailsApplication.config.getProperty('apikey', String.class)
        def uri = grailsApplication.config.getProperty('elastic', String.class) + suffix
        SSLContext sslContext = getSslContext()
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build()
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(registry))
                .build()
        HttpPut httpPut = new HttpPut(uri)
        httpPut.addHeader("Content-Type", "application/json")
        httpPut.addHeader("Authorization", "ApiKey ${apiKey}")
        StringEntity entity = new StringEntity(query.toString())
        httpPut.setEntity(entity)
        try {
            HttpClientContext clientContext = HttpClientContext.create()
            httpClient.execute(httpPut, clientContext, response -> {
                json = EntityUtils.toString(response.getEntity())
                log.warn ( "JSON: ${json}")
            })
        } catch (e) {
            log.error ("Elastic Put Error: " + e.getMessage())
        } finally {
            httpClient.close()
        }
    }

    def getRest (String suffix, def query) {
        def json = ""
        String apiKey = grailsApplication.config.getProperty('apikey', String.class)
        def uri = grailsApplication.config.getProperty('elastic', String.class) + suffix
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build()
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(registry))
                .build()
        HttpGet httpGet = new HttpGet(uri)
        httpGet.addHeader("Content-Type", "application/json")
        httpGet.addHeader("Authorization", "ApiKey ${apiKey}")
        StringEntity entity = new StringEntity(query.toString())
        httpGet.setEntity(entity)
        try {
            HttpClientContext clientContext = HttpClientContext.create()
            httpClient.execute(httpGet, clientContext, response -> {
                json = EntityUtils.toString(response.getEntity())
                log.warn ( "JSON: ${json}")
            })
        } catch (e) {
            log.error ("Elastic Get Error: " + e.getMessage())
        } finally {
            httpClient.close()
        }
    }

    def deleteRest (String suffix, def query) {
        def json = ""
        String apiKey = grailsApplication.config.getProperty('apikey', String.class)
        def uri = grailsApplication.config.getProperty('elastic', String.class) + suffix
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory> create()
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build()
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setConnectionManager(new PoolingHttpClientConnectionManager(registry))
                .build()
        HttpDelete httpDelete = new HttpDelete(uri)
        httpDelete.addHeader("Content-Type", "application/json")
        httpDelete.addHeader("Authorization", "ApiKey ${apiKey}")
        StringEntity entity = new StringEntity(query.toString())
        httpDelete.setEntity(entity)
        try {
            HttpClientContext clientContext = HttpClientContext.create()
            httpClient.execute(httpDelete, clientContext, response -> {
                json = EntityUtils.toString(response.getEntity())
                log.warn ( "JSON: ${json}")
            })
        } catch (e) {
            log.error ("Elastic Delete Error: " + e.getMessage())
        } finally {
            httpClient.close()
        }
    }



    def getSslContext() {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    X509Certificate[] getAcceptedIssuers() {
                        return null
                    }
                    void checkClientTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                    void checkServerTrusted(
                            X509Certificate[] certs, String authType) {
                    }
                }
        }
        SSLContext sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, trustAllCerts, new SecureRandom())
        return sslContext
    }

    def convertToText() {

    }
}