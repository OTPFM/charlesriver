package hu.farago.charlesriver.service.aspect;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;

@Aspect
@Component
public class LoginAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAspect.class);

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
		LOGGER.info(String.format("Properties initialized: %s, %s, %d, %s, %s, %s", protocol, hostname, port, username,
				password, account));
	}

	@SuppressWarnings("deprecation")
	@Around("execution(* hu.farago.charlesriver.service.*Service.*(..))")
	public Object aroundSampleCreation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		Object ret = null;
		ClientSession existingSession = (ClientSession) proceedingJoinPoint.getArgs()[0];
		if (existingSession != null) {
			ret = proceedingJoinPoint.proceed();
		} else {

			ClientSession clientSession = new ClientSession(protocol, hostname, port);
			clientSession.setGzipRequestEnabled(false);
			clientSession.setGzipResponseEnabled(false);

			Object[] otherAttributes = proceedingJoinPoint.getArgs();
			otherAttributes = Arrays.copyOfRange(otherAttributes, 1, otherAttributes.length);

			try {
				clientSession.logon(username, password);
				Object[] allAttributes = ArrayUtils.addAll(new Object[] { clientSession }, otherAttributes);
				ret = proceedingJoinPoint.proceed(allAttributes);
			} catch (ServiceException e) {
				LOGGER.error("Error message received from server: " + e.getFaultString());
			} catch (TransportException e) {
				LOGGER.error("Error communicating with server: " + e.getLocalizedMessage());
			} finally {
				clientSession.logout();
			}

		}
		return ret;
	}

}
