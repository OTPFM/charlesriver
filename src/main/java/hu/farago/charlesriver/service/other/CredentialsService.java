package hu.farago.charlesriver.service.other;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import hu.farago.charlesriver.model.dto.Credentials;

@Service
public class CredentialsService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CredentialsService.class);
	
	private static Credentials SINGLETON = new Credentials(); // bad practice, store this data in the DB later...
	
	public void saveCredentials(Credentials cred) {
		LOGGER.info("Credentials stored to the session.");
		this.SINGLETON = cred;
	}
	
	public Credentials getCredentials() {
		return SINGLETON;
	}

}
