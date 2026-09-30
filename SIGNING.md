# Windows publisher identity

The release workflow is ready for Authenticode signing. Unsigned community builds run normally, but Microsoft Defender SmartScreen displays **Unknown publisher** until the installer carries a trusted certificate.

To enable signing, obtain a code-signing certificate from a trusted certificate authority and add these GitHub Actions repository secrets:

- WINDOWS_CERTIFICATE_BASE64: Base64-encoded PFX/PKCS#12 certificate
- WINDOWS_CERTIFICATE_PASSWORD: password protecting the PFX

The Windows packaging script then signs the installer with SHA-256 and a trusted timestamp. An EV certificate usually establishes SmartScreen reputation immediately; an organization-validated standard certificate displays the verified publisher but may need time to build download reputation.

Never commit the certificate or its password to the repository.
