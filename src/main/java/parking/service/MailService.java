package parking.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.multipart.FormDataMultiPart;

import javax.ws.rs.core.MediaType;

public class MailService {

    public static ClientResponse sendEmail(String emailTo, String subject, String message) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api",
                "key-9b2644c1e5b2f81c9722b50c9b079433"));
        WebResource webResource =
                client.resource("https://api.mailgun.net/v3/parkinger.net" +
                        "/messages");

        FormDataMultiPart formData = new FormDataMultiPart();
        formData.field("from", "Parkinger team <info@parkinger.net>");
        formData.field("to", emailTo);
        formData.field("subject", subject);
        formData.field("html", message);

        return webResource.type(MediaType.MULTIPART_FORM_DATA_TYPE).
                post(ClientResponse.class, formData);
    }

}

