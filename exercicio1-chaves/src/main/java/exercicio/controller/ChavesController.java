package exercicio.controller;

import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import exercicio.AsymetricKeys;

@RestController
public class ChavesController {
	
	@Autowired
	private AsymetricKeys keys;
	
	@GetMapping("/key/public")
	public ResponseEntity<String> keyPublic() throws Exception
	{
		String resultado = Base64.getEncoder().encodeToString(keys.getPublicBytes());

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}
}
