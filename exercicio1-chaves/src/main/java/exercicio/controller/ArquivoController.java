package exercicio.controller;

import java.security.InvalidKeyException;
import java.util.Base64;
import java.util.HashMap;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import exercicio.AsymetricKeys;
import exercicio.model.Arquivo;

@RestController
public class ArquivoController {
	static Logger log = LoggerFactory.getLogger(ArquivoController.class);

	private static final HashMap<String, byte[]> chaves = new HashMap<>();
	
	@PostMapping("/chave/set")
	public ResponseEntity<String> chave(
			@RequestBody Arquivo arquivo
		) throws Exception 
	{
		byte[] bchave = Base64.getDecoder().decode(arquivo.getChave().getBytes("UTF-8"));

		byte[] chave = new AsymetricKeys().getDecrypt(bchave);

		chaves.put(arquivo.getMd5(), chave);

		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/chave/get/{md5}/{publicKey}")
	public ResponseEntity<String> arquivoChave(
			@PathVariable("md5") String md5,
			@PathVariable("publicKey") String publicKey
		) throws InvalidKeyException, Exception
	{
		log.info("Arquivo: " + md5);
		
		if ( ! chaves.containsKey(md5) )
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		
		byte[] chave  = chaves.get(md5);

		log.info("Arquivo: " + md5 + " " + new String(chave));

		
		byte[] keyPublic = Base64.getUrlDecoder().decode(publicKey.getBytes("UTF-8"));

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, AsymetricKeys.getPublic(keyPublic));
		
		String resultado = Base64.getEncoder().encodeToString(cipher.doFinal(chave));
		
		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}
}
