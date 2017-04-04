package com.magic.api.commons;

import org.apache.commons.httpclient.methods.multipart.PartBase;

import java.io.IOException;
import java.io.OutputStream;

public class ByteArrayPart extends PartBase {
    private byte[] mData;

    private String mName;

    public ByteArrayPart(byte[] data, String name, String type) {
        super(name, type, "UTF8", "binary");
        mName = name;
        mData = data;
    }
    public ByteArrayPart(byte[] data, String name, String fileName, String type) {
        super(name, type, "UTF8", "binary");
        mName = fileName;
        mData = data;
    }

    protected void sendData(OutputStream out) throws IOException {
        out.write(mData);
    }

    protected long lengthOfData() throws IOException {
        return mData.length;
    }

    public byte[] getmData() {
        return mData;
    }

    protected void sendDispositionHeader(OutputStream out)
            throws IOException {
        super.sendDispositionHeader(out);
        StringBuilder buf = new StringBuilder();
        buf.append("; filename=\"").append(mName).append("\"");
        out.write(buf.toString().getBytes());
    }
}
