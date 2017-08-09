package hu.farago.charlesriver.service;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ComplianceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComplianceService.class);

	@Value("${crd.client.protocol}")
	private String protocol;

	@Value("${crd.client.hostname}")
	private String hostname;

	@Value("${crd.client.port}")
	private Integer port;

	@Value("${crd.username}")
	private String username;

	@Value("${crd.password}")
	private String password;

	@Value("${crd.account}")
	private String account;

	@PostConstruct
	public void postConstruct() throws Exception {
		LOGGER.info(
				String.format(
						"Properties initialized: %s, %s, %d, %s, %s, %s", 
						protocol, hostname, port, username, password, account));
	}

}
