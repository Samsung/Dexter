namespace DexterCS
{
    public interface IDexterHomeListener
    {
        void HandleDexterHomeChanged(string oldPath, string newPath);
    }
}