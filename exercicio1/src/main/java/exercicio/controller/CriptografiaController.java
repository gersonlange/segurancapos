package exercicio.controller;

import java.math.BigInteger;
import java.net.URI;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import exercicio.ChaveCriptografia;
import exercicio.model.Arquivo;
import exercicio.model.Dados;

@RestController
public class CriptografiaController {
	
	static Logger log = LoggerFactory.getLogger(CriptografiaController.class);
	
	private static final String ALGORITHM = "AES";
	
	@PostMapping("/encripta")
	public ResponseEntity<Dados> encripta(
			@RequestBody String valor) throws Exception
	{
		byte[] bytes = new ChaveCriptografia().getKey().getEncoded();

		SecretKeySpec secretKey = new SecretKeySpec(bytes, "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

		String resultado = Base64.getEncoder().encodeToString(cipher.doFinal(valor.getBytes("UTF-8")) );
		
		
		
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, getPublicKey());
		
		Dados dados = new Dados();
		dados.setChave(Base64.getEncoder().encodeToString(cipher.doFinal(bytes)));
		dados.setDados(resultado);
	
		log.info(resultado);

		//grava chave no servidor
		String md5 = getMd5(resultado);

		Arquivo arquivo = new Arquivo();
		arquivo.setChave(dados.getChave());
		arquivo.setMd5(md5);

		RestTemplate rest = new RestTemplate();
		ResponseEntity<?> post = rest.postForEntity(
				new URI("http://localhost:8081/chave/set"),
				arquivo, Arquivo.class);
		
		log.info("chave_set: " + post.getStatusCode());
		
		//
		
		return new ResponseEntity<>(dados, HttpStatus.OK);
	}
	
	@PostMapping("/decripta")
	public ResponseEntity<String> decripta(
			@RequestBody String valor) throws Exception
	{
		String md5 = getMd5(valor);
		
		//par de chaves temporario
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		keyGen.initialize(1024);
		
		KeyPair pair = keyGen.generateKeyPair();
		PrivateKey privateKey = pair.getPrivate();
		PublicKey publicKey = pair.getPublic();

		String pKey = Base64.getUrlEncoder().encodeToString(publicKey.getEncoded()).replaceAll("\n", "");

		//buscando chave do rest
		RestTemplate rest = new RestTemplate();
		log.info("http://localhost:8081/chave/get/"+md5+"/"+pKey);
		String key = rest.getForObject("http://localhost:8081/chave/get/"+md5+"/"+pKey, String.class);
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, privateKey);

		byte[] chave = cipher.doFinal(Base64.getDecoder().decode(key));


		//decripta
		byte[] bdados = Base64.getDecoder().decode(valor.getBytes("UTF-8"));
		
		SecretKeySpec secretKey = new SecretKeySpec(chave, ALGORITHM);
        cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

	    byte[] bdecry = cipher.doFinal(bdados);

		String resultado = new String(bdecry);
		
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}
	
	private PublicKey getPublicKey() throws InvalidKeySpecException, NoSuchAlgorithmException
	{
		RestTemplate rest = new RestTemplate();
		String key = rest.getForObject("http://localhost:8081/key/public", String.class);

		
		byte[] keyBytes = Base64.getDecoder().decode(key);
		X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		return kf.generatePublic(spec);
	}
	
	private String getMd5(String s) throws NoSuchAlgorithmException
	{
		MessageDigest m = MessageDigest.getInstance("MD5");
		m.update(s.getBytes());
		return new BigInteger(1,m.digest()).toString(16);
	}

}
