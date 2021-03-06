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
		sb.append("\nAlerts Generated: ").append(result.getAlertCount());
		sb.append("\nWarnings Generated: ").append(result.getWarningCount());
		sb.append("\nData Exceptions Generated: ").append(result.getDataExceptionCount());
		return sb;
	}

	private StringBuffer prepareResult(CPLResultWrapper wrapper) {
		StringBuffer sb = prepareResult(wrapper.getResult());
		ViolationBean[] violations = wrapper.getViolations();
		sb.append("\nNumber of Violations: ").append(violations.length);
		for (ViolationBean bean : violations) {
			sb.append("\n * ");
			sb.append(bean.toString());
		}
		TestBean[] tests = wrapper.getTests();
		sb.append("\nNumber of Tests: ").append(tests.length);
		for (TestBean bean : tests) {
			sb.append("\n * ");
			sb.append(bean.toString());
		}
		return sb;
	}

}
