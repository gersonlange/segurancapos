package exercicio;

import java.io.File;
import java.nio.file.Files;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.springframework.stereotype.Component;

@Component
public class AsymetricKeys {
	
	private Cipher cipher;
	
	public AsymetricKeys() throws NoSuchAlgorithmException, NoSuchPaddingException {
		cipher = Cipher.getInstance("RSA");
	}
	
	public byte[] getPrivateBytes() throws Exception {
		return Files.readAllBytes(new File(GenerateKeys.PRIVATEKEY_FILE).toPath());
	};
	
	public PrivateKey getPrivate() throws Exception {
		byte[] keyBytes = getPrivateBytes();
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePrivate(spec);
	}
	
	public byte[] getPublicBytes() throws Exception {
		return Files.readAllBytes(new File(GenerateKeys.PUBLICKEY_FILE).toPath());
	}
	
	public PublicKey getPublic() throws Exception {
		byte[] keyBytes = getPublicBytes();
		return getPublic(keyBytes);		
	}
	
	public static PublicKey getPublic(byte[] keyBytes) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	public byte[] getEncripta(byte[] texto) throws Exception
	{
        cipher.init(Cipher.ENCRYPT_MODE, getPublic());
        
        return cipher.doFinal(texto);
	}
	
	public byte[] getDecrypt(byte[] msg) throws InvalidKeyException, Exception 
	{
		this.cipher.init(Cipher.DECRYPT_MODE, getPrivate());
		return cipher.doFinal(msg);
	}
}
