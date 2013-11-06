package com.itrustcambodia.restclient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class StringBufferHttpMessageConverter implements HttpMessageConverter<StringBuffer> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return StringBuffer.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return StringBuffer.class.equals(clazz);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.ALL);
    }

    @Override
    public StringBuffer read(Class<? extends StringBuffer> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        StringBuffer body = new StringBuffer();
        body.append(IOUtils.toString(inputMessage.getBody()));
        return body;
    }

    @Override
    public void write(StringBuffer t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        if (t != null) {
            IOUtils.write(t.toString(), outputMessage.getBody());
        }
    }

}
