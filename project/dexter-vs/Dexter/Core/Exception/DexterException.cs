using System;

public class DexterException : Exception
{
    public DexterException(String message) : base(message)
    {

    }

    public DexterException(String message, Exception innerException) : base(message, innerException)
    {

    }
}
