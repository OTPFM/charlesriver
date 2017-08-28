package hu.farago.charlesriver.service.aspect;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;

import hu.farago.charlesriver.model.dto.Credentials;
import hu.farago.charlesriver.service.other.CredentialsService;

@Aspect
@Component
public class LoginAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginAspect.class);

	@Autowired
	private CredentialsService credentialService;

	@SuppressWarnings("deprecation")
	@Around("execution(* hu.farago.charlesriver.service.*Service.*(..))")
	public Object aroundSampleCreation(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

		LOGGER.info("Logon to CRD service");
		Object ret = null;
		ClientSession existingSession = (ClientSession) proceedingJoinPoint.getArgs()[0];
		if (existingSession != null) {
			ret = proceedingJoinPoint.proceed();
		} else {

			Credentials cred = credentialService.getCredentials();
			ClientSession clientSession = new ClientSession(cred.protocol, cred.hostname, cred.port);
			clientSession.setGzipRequestEnabled(false);
			clientSession.setGzipResponseEnabled(false);

			Object[] otherAttributes = proceedingJoinPoint.getArgs();
			otherAttributes = Arrays.copyOfRange(otherAttributes, 1, otherAttributes.length);

			try {
				clientSession.logon(cred.username, cred.password);
				LOGGER.info("Successful logon");
				Object[] allAttributes = ArrayUtils.addAll(new Object[] { clientSession }, otherAttributes);
				ret = proceedingJoinPoint.proceed(allAttributes);
			} catch (ServiceException e) {
				LOGGER.error("Error message received from server: " + e.getMessage());
				throw e;
			} catch (TransportException e) {
				LOGGER.error("Error communicating with server: " + e.getMessage());
				throw e;
			} catch (Exception e) {
				LOGGER.error("Other error: " + e.getMessage());
				throw e;
			} finally {
				clientSession.logout();
				LOGGER.info("Successful logout");
			}

		}
		return ret;
	}

}
