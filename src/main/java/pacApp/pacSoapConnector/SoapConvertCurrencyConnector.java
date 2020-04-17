package pacApp.pacSoapConnector;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.core.SoapActionCallback;

import com.consumingwebservice.wsdl.AuthHeader;
import com.consumingwebservice.wsdl.ConvertCurrenyRequest;
import com.consumingwebservice.wsdl.GetCurrencyCodesRequest;
import com.consumingwebservice.wsdl.ObjectFactory;
import com.consumingwebservice.wsdl.ResponseOfListOfString;
import com.consumingwebservice.wsdl.ResponseOfString;

import pacApp.pacSoapConnector.context.SoapMarshaller;

@Service
public class SoapConvertCurrencyConnector extends WebServiceGatewaySupport {
	
	private final String SOAP_SERVICE_URL = "http://localhost:50923/Service1.svc/soap";
	
	class CURRENCY_SERVICE_ENDPOINTS{
		public static final String serviceUrl = "http://localhost:50923/Service1.svc/soap";
		public static final String getCurrencyCodesUrl = "http://tempuri.org/IService1/GetCurrencyCodes";
		public static final String convertCurrencyUrl = "http://tempuri.org/IService1/ConvertCurrency";
	}
	
	private void setupMarshaller() {
		getWebServiceTemplate().setMarshaller(new SoapMarshaller().marshaller());
		getWebServiceTemplate().setUnmarshaller(new SoapMarshaller().marshaller());		
	}
		
	public List<String> getCurrencyCodesResponse() {
		try {
			setupMarshaller();
			GetCurrencyCodesRequest gccr = new GetCurrencyCodesRequest(); 
			ResponseOfListOfString responseList = (ResponseOfListOfString)getWebServiceTemplate()
			        .marshalSendAndReceive(CURRENCY_SERVICE_ENDPOINTS.serviceUrl, gccr,
				            new SoapActionCallback(CURRENCY_SERVICE_ENDPOINTS.getCurrencyCodesUrl));		
			System.out.println("Result: "+responseList.getReturnValue().getValue().getString());
			return responseList.getReturnValue().getValue().getString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String convertCurrency(String value, String toCurrency) {
		setupMarshaller();
		ObjectFactory factory = new ObjectFactory();
		ConvertCurrenyRequest ccr = new ConvertCurrenyRequest();
		ccr.setAutHeader(factory.createConvertCurrenyRequestAutHeader(getRequetsHeader(factory)));
		ccr.setCcValue(factory.createConvertCurrenyRequestCcValue(value));
		ccr.setToCurrency(factory.createConvertCurrenyRequestToCurrency(toCurrency));		
		ResponseOfString response = (ResponseOfString)getWebServiceTemplate()
				 .marshalSendAndReceive(CURRENCY_SERVICE_ENDPOINTS.serviceUrl, ccr,
						  new SoapActionCallback(CURRENCY_SERVICE_ENDPOINTS.convertCurrencyUrl));
		System.out.println(response.getReturnValue().getValue());
		return response.getReturnValue().getValue();
	}
	

	private AuthHeader getRequetsHeader(ObjectFactory factory) {
		AuthHeader authH = new AuthHeader();
		final String username = "Admin";
		final String password = "pa$$w0rd";// todo encrypt		
		authH.setUsername(factory.createAuthHeaderUsername(username)); 
		authH.setPassword(factory.createAuthHeaderPassword(password));
		return authH;
	}
}
