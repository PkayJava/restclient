package com.itrustcambodia.restclient;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RestTemplateFactory implements FactoryBean<RestTemplate>, InitializingBean, DisposableBean {

    private RestTemplate restTemplate;

    private String username;

    private String password;

    private String userAgent;

    private boolean digest;

    private String realm;

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public void destroy() throws Exception {
        this.restTemplate = null;
        this.username = null;
        this.password = null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PoolingClientConnectionManager threadSafeClientConnManager = new PoolingClientConnectionManager();
        ClientHttpRequestFactory requestFactory = null;
        HttpParams httpParams = new BasicHttpParams();

        List<Header> headers = new ArrayList<Header>();

        if (this.userAgent != null && !"".equals(this.userAgent)) {
            headers.add(new BasicHeader("User-Agent", this.userAgent));
        }

        httpParams.setParameter(ClientPNames.DEFAULT_HEADERS, headers);

        DefaultHttpClient httpClient = new DefaultHttpClient(threadSafeClientConnManager, httpParams);

        if (!org.apache.commons.lang3.StringUtils.isEmpty(username) && !org.apache.commons.lang3.StringUtils.isEmpty(password)) {
            Credentials credentials = new UsernamePasswordCredentials(username, password);
            if (this.digest) {
                httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, this.realm, AuthPolicy.DIGEST), credentials);
            } else {
                httpClient.getCredentialsProvider().setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, this.realm, AuthPolicy.BASIC), credentials);
            }
        }
        requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        restTemplate = new RestTemplate(requestFactory);
        restTemplate.getMessageConverters().add(new FileHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringBufferHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
    }

    @Override
    public RestTemplate getObject() throws Exception {
        return restTemplate;
    }

    @Override
    public Class<RestTemplate> getObjectType() {
        return RestTemplate.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDigest() {
        return digest;
    }

    public void setDigest(boolean digest) {
        this.digest = digest;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }
}