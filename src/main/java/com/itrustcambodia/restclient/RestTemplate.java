package com.itrustcambodia.restclient;

import java.io.IOException;

import org.apache.http.auth.AuthSchemeProvider;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.methods.CloseableHttpResponse;
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

public class RestTemplate {

    private CloseableHttpClient client;

    private HttpClientContext context;

    public RestTemplate() {
        this.context = HttpClientContext.create();
        this.client = HttpClients.createDefault();
    }

    public RestTemplate(String userAgent) {
        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setUserAgent(userAgent);
        this.client = httpClientBuilder.build();
        this.context = HttpClientContext.create();
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
        context.setCredentialsProvider(credential);

        Registry<AuthSchemeProvider> registry = registryBuilder.build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setDefaultAuthSchemeRegistry(registry);
        this.client = httpClientBuilder.build();
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
        context.setCredentialsProvider(credential);

        Registry<AuthSchemeProvider> registry = registryBuilder.build();

        HttpClientBuilder httpClientBuilder = HttpClients.custom();
        httpClientBuilder.setDefaultAuthSchemeRegistry(registry);
        this.client = httpClientBuilder.build();
    }

    public CloseableHttpResponse execute(HttpUriRequest request) throws IOException, ClientProtocolException {
        return this.client.execute(request, this.context);
    }

    public void destroy() throws Exception {
        this.context = null;
        this.client.close();
    }

}