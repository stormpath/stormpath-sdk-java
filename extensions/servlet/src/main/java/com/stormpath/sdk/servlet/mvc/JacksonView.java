package com.stormpath.sdk.servlet.mvc;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stormpath.sdk.servlet.http.MediaType;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 1.0.RC8
 */
public class JacksonView implements View {

    private ObjectMapper objectMapper = new ObjectMapper();

    private JsonEncoding encoding = JsonEncoding.UTF8;

    private String contentType = MediaType.APPLICATION_JSON_VALUE;

    private boolean updateContentLength = false;

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean isUpdateContentLength() {
        return updateContentLength;
    }

    public void setUpdateContentLength(boolean updateContentLength) {
        this.updateContentLength = updateContentLength;
    }

    public JsonEncoding getEncoding() {
        return encoding;
    }

    public void setEncoding(JsonEncoding encoding) {
        this.encoding = encoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, ViewModel model) throws Exception {

        response.setContentType(getContentType());

        OutputStream stream = (this.updateContentLength ? createTemporaryOutputStream() : response.getOutputStream());

        Object value = finalizeContent(model, request);

        writeContent(stream, value);

        if (this.updateContentLength) {
            assert stream instanceof ByteArrayOutputStream;
            writeToResponse(response, (ByteArrayOutputStream) stream);
        }
    }

    /**
     * Write the given temporary OutputStream to the HTTP response.
     *
     * @param response current HTTP response
     * @param baos     the temporary OutputStream to write
     * @throws IOException if writing/flushing failed
     */
    protected void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
        // Write content length (determined via byte array).
        response.setContentLength(baos.size());

        // Flush byte array to servlet output stream.
        ServletOutputStream out = response.getOutputStream();
        baos.writeTo(out);
        out.flush();
    }

    @SuppressWarnings("UnusedParameters") //template method
    protected Object finalizeContent(ViewModel model, HttpServletRequest request) {
        return model.getModel();
    }

    protected void writeContent(OutputStream stream, Object content) throws IOException {

        JsonGenerator generator = this.objectMapper.getFactory().createGenerator(stream, this.encoding);

        this.objectMapper.writeValue(generator, content);

        generator.flush();
    }

    private OutputStream createTemporaryOutputStream() {
        return new ByteArrayOutputStream(4096);
    }
}
