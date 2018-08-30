using System;

public class DexterRuntimeException : SystemException
{
    public DexterRuntimeException(String message) : base(message)
    {

    }

    public DexterRuntimeException(String message, Exception innerException) : base(message, innerException)
    {

    }
}

