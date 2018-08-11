package exercicio;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ChaveCriptografia
{
	static Logger log = LoggerFactory.getLogger(ChaveCriptografia.class);
			
	private SecretKey aesKey;

	public ChaveCriptografia()	{

		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
		    kgen.init(128);
		    aesKey = kgen.generateKey();
			
		} catch (Exception e) {
			log.error("EXCEPTION", e);
		}
	}
	
	public SecretKey getKey() {
		return aesKey;
	}
}
