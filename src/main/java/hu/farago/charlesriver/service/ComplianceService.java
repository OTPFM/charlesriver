package hu.farago.charlesriver.service;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.crd.beans.TestBean;
import com.crd.beans.ViolationBean;
import com.crd.client.ClientSession;
import com.crd.client.ServiceException;
import com.crd.client.TransportException;
import com.crd.compliancesvc.Compliance;
import com.crd.compliancesvc.processor.CPLResult;
import com.crd.compliancesvc.processor.CPLResultWrapper;

import hu.farago.charlesriver.model.dto.Check;

@Service
public class ComplianceService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComplianceService.class);

	public String checkCompliance(ClientSession clientSession, Check check) throws ServiceException, TransportException {
		Compliance compliance = clientSession.getCompliance();
		compliance.setReferenceRequestIdHeader(UUID.randomUUID().toString());

		CPLResultWrapper result = compliance.runComplianceReturningWrapper(
				check.getExtId(), 
				check.getComplianceType(),
				check.getBatchType(), 
				check.getId(), 
				check.isDetails());
		
		String resultStr = prepareResult(result).toString();
		LOGGER.info(resultStr);
		
		return resultStr;
	}

	private StringBuffer prepareResult(CPLResult result) {
		StringBuffer sb = new StringBuffer();
		sb.append("Compliance run ");
		sb.append(result.isSuccess() ? "successful." : "failed.");
		sb.append(" Alerts Generated: ").append(result.getAlertCount()).append(" ");
		sb.append(" Warnings Generated: ").append(result.getWarningCount()).append(" ");
		sb.append(" Data Exceptions Generated: ").append(result.getDataExceptionCount()).append(" ");
		return sb;
	}

	private StringBuffer prepareResult(CPLResultWrapper wrapper) {
		StringBuffer sb = prepareResult(wrapper.getResult());
		ViolationBean[] violations = wrapper.getViolations();
		sb.append("  Number of Violations : ").append(violations.length).append("  ");
		TestBean[] tests = wrapper.getTests();
		sb.append("Number of Tests : ").append(tests.length);
		return sb;
	}

}
