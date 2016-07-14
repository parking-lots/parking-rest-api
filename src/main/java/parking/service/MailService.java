package parking.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

@Component
public class MailService {

    public final static HTTPBasicAuthFilter httpBasicAuthFilter = new HTTPBasicAuthFilter("api", "key-9b2644c1e5b2f81c9722b50c9b079433");
    public final static String resourceDomain = "https://api.mailgun.net/v3/" + "parkinger.net" + "/messages";

    public ClientResponse sendEmail(String emailTo, String subject, String message) {
        Client client = Client.create();

        client.addFilter(httpBasicAuthFilter);
        WebResource webResource = client.resource(resourceDomain);

        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("from", "Parkinger team <info@parkinger.net>");
        formData.field("to", emailTo);
        formData.field("subject", subject);
        formData.field("html", message);

        return webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).
                post(ClientResponse.class, formData);
    }
}

