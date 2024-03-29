package com.example.demo.Utilies;

import java.util.Arrays;

public final class ByteArrayWrapper
{
    private final byte[] data;

    public byte[] getBytes() {
        return data;
    }

    public ByteArrayWrapper(byte[] data)
    {
        if (data == null)
        {
            throw new NullPointerException();
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ByteArrayWrapper))
        {
            return false;
        }
        return Arrays.equals(data, ((ByteArrayWrapper)other).data);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }

    @Override
    public String toString() {
        return Utilies.BytesToHexString(data);
    }
}
