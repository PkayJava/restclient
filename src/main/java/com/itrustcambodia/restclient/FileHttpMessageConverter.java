package com.itrustcambodia.restclient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

public class FileHttpMessageConverter implements HttpMessageConverter<File> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        return File.class.equals(clazz);
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        return File.class.equals(clazz);
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return Collections.singletonList(MediaType.ALL);
    }

    @Override
    public File read(Class<? extends File> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        HttpHeaders httpHeaders = inputMessage.getHeaders();

        String filename = String.valueOf(System.currentTimeMillis());
        if (httpHeaders.get("Content-Disposition") != null && httpHeaders.get("Content-Disposition").size() > 0) {
            String contentDisposition = httpHeaders.get("Content-Disposition").get(0);
            if (contentDisposition != null && !"".equals(contentDisposition)) {
                if (contentDisposition.indexOf("=") > -1) {
                    filename = contentDisposition.substring(contentDisposition.indexOf("=") + 1);
                    if (filename.charAt(0) == '"') {
                        filename = filename.substring(1);
                    }
                    if (filename.endsWith("\"")) {
                        filename = filename.substring(0, filename.length() - 1);
                    }
                }
            }
        }
        File file = new File(FileUtils.getTempDirectoryPath(), filename);
        FileOutputStream outputStream = FileUtils.openOutputStream(file);
        IOUtils.copyLarge(inputMessage.getBody(), outputStream);
        IOUtils.closeQuietly(outputStream);
        return file;
    }

    @Override
    public void write(File t, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        IOUtils.copyLarge(FileUtils.openInputStream(t), outputMessage.getBody());
    }

}