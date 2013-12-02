package com.itrustcambodia.restclient;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.impl.auth.BasicSchemeFactory;
import org.apache.http.impl.auth.DigestSchemeFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class RestTemplate {

    public static final String APPLICATION_FORM_URLENCODED = "application/x-www-form-urlencoded";

    public final static String APPLICATION_JSON = "application/json";

    private CloseableHttpClient client;

    private HttpClientContext context;

    private Gson gson;

    public RestTemplate() {
        this.context = HttpClientContext.create();
        this.client = HttpClients.createDefault();
        this.gson = new Gson();
    }

    public RestTemplate(String userAgent) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setUserAgent(userAgent);
        this.client = httpClientBuilder.build();
        this.context = HttpClientContext.create();
        this.gson = new Gson();
    }

    public RestTemplate(String username, String password) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(username)) {
            throw new NullPointerException("username can not be null or empty");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            throw new NullPointerException("password can not be null or empty");
        }
        RegistryBuilder<AuthSchemeProvider> registryBuilder = RegistryBuilder.<AuthSchemeProvider> create();

        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, null, AuthSchemes.BASIC);
        registryBuilder.register(AuthSchemes.BASIC, new BasicSchemeFactory());

        CredentialsProvider credential = new BasicCredentialsProvider();
        credential.setCredentials(authScope, new UsernamePasswordCredentials(username, password));
        this.context = HttpClientContext.create();
        this.context.setCredentialsProvider(credential);

        Registry<AuthSchemeProvider> registry = registryBuilder.build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setDefaultAuthSchemeRegistry(registry);
        this.client = httpClientBuilder.build();
        this.gson = new Gson();
    }

    public RestTemplate(String username, String password, String realm) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(username)) {
            throw new NullPointerException("username can not be null or empty");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            throw new NullPointerException("password can not be null or empty");
        }
        if (org.apache.commons.lang3.StringUtils.isEmpty(realm)) {
            throw new NullPointerException("realm can not be null or empty");
        }
        RegistryBuilder<AuthSchemeProvider> registryBuilder = RegistryBuilder.<AuthSchemeProvider> create();

        AuthScope authScope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, realm, AuthSchemes.DIGEST);
        registryBuilder.register(AuthSchemes.DIGEST, new DigestSchemeFactory());

        CredentialsProvider credential = new BasicCredentialsProvider();
        credential.setCredentials(authScope, new UsernamePasswordCredentials(username, password));
        this.context = HttpClientContext.create();
        this.context.setCredentialsProvider(credential);

        Registry<AuthSchemeProvider> registry = registryBuilder.build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setDefaultAuthSchemeRegistry(registry);
        this.client = httpClientBuilder.build();
        this.gson = new Gson();
    }

    public <T> ResponseEntity<T> executeAndReturn(HttpUriRequest request, Class<T> responseType) {
        CloseableHttpResponse response = null;
        try {
            response = doExecute(request);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        if (response != null) {
            HttpEntity httpEntity = response.getEntity();
            try {
                String content = EntityUtils.toString(httpEntity, "UTF-8");
                return new ResponseEntity<T>(response, this.gson.fromJson(content, responseType));
            } catch (IOException e) {
            } finally {
                IOUtils.closeQuietly(response);
            }
        }
        return new ResponseEntity<T>(HttpStatus.SC_SERVICE_UNAVAILABLE);
    }

    public ResponseEntity<byte[]> executeAndReturn(HttpUriRequest request) {
        CloseableHttpResponse response = null;
        try {
            response = doExecute(request);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        if (response != null) {
            HttpEntity httpEntity = response.getEntity();
            try {
                return new ResponseEntity<byte[]>(response, EntityUtils.toByteArray(httpEntity));
            } catch (IOException e) {
            } finally {
                IOUtils.closeQuietly(response);
            }
        }
        return new ResponseEntity<byte[]>(HttpStatus.SC_SERVICE_UNAVAILABLE);
    }

    public ResponseEntity<byte[]> executeAndReturn(HttpUriRequest request, String contentType) {
        request.setHeader("Content-Type", contentType);
        return executeAndReturn(request);
    }

    public ResponseEntity<byte[]> executeAndReturn(HttpUriRequest request, String contentType, HttpEntity entity) {
        request.setHeader("Content-Type", contentType);
        if (request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return executeAndReturn(request);
    }

    public <T> ResponseEntity<T> executeAndReturn(HttpUriRequest request, Class<T> responseType, String contentType) {
        request.setHeader("Content-Type", contentType);
        return executeAndReturn(request, responseType);
    }

    public <T> ResponseEntity<T> executeAndReturn(HttpUriRequest request, Class<T> responseType, String contentType, HttpEntity entity) {
        request.setHeader("Content-Type", contentType);
        if (request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return executeAndReturn(request, responseType);
    }

    public ResponseEntity<Void> execute(HttpUriRequest request) {
        CloseableHttpResponse response = null;
        try {
            response = doExecute(request);
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }
        if (response != null) {
            HttpEntity httpEntity = response.getEntity();
            try {
                EntityUtils.consume(httpEntity);
                return new ResponseEntity<Void>(response);
            } catch (IOException e) {
            } finally {
                IOUtils.closeQuietly(response);
            }
        }
        return new ResponseEntity<Void>(HttpStatus.SC_SERVICE_UNAVAILABLE);
    }

    public ResponseEntity<Void> execute(HttpUriRequest request, HttpEntity entity) {
        if (request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return execute(request);
    }

    public ResponseEntity<Void> execute(HttpUriRequest request, String contentType, HttpEntity entity) {
        request.addHeader("Content-Type", contentType);
        if (request instanceof HttpEntityEnclosingRequestBase) {
            ((HttpEntityEnclosingRequestBase) request).setEntity(entity);
        }
        return execute(request);
    }

    public CloseableHttpResponse doExecute(HttpUriRequest request) throws IOException, ClientProtocolException {
        return this.client.execute(request, this.context);
    }

    public void destroy() {
        this.context = null;
        if (this.client != null) {
            IOUtils.closeQuietly(this.client);
        }
    }

}