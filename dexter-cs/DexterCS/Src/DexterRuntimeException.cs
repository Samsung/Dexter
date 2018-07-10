using System;
using System.Runtime.Serialization;

namespace DexterCS
{
    [Serializable]
    public class DexterRuntimeException : Exception, ISerializable
    {
        public DexterRuntimeException()
        {
        }

        public DexterRuntimeException(string message) : base(message)
        {
        }

        public DexterRuntimeException(string message, Exception innerException) : base(message, innerException)
        {
        }

        protected DexterRuntimeException(SerializationInfo info, StreamingContext context) : base(info, context)
        {
        }
        public override void GetObjectData(SerializationInfo info, StreamingContext context)
        {
            base.GetObjectData(info, context);
        }
    }
}