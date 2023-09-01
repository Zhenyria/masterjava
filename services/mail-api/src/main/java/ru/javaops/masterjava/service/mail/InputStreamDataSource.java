package ru.javaops.masterjava.service.mail;

import lombok.AllArgsConstructor;

import javax.activation.DataSource;
import java.io.InputStream;
import java.io.OutputStream;

@AllArgsConstructor
public class InputStreamDataSource implements DataSource {
    private final InputStream inputStream;

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getContentType() {
        return "*/*";
    }

    @Override
    public String getName() {
        return "InputStreamDataSource";
    }
}
