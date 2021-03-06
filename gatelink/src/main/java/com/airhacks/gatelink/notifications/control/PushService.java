
package com.airhacks.gatelink.notifications.control;

import com.airhacks.gatelink.Control;
import com.airhacks.gatelink.log.boundary.Tracer;
import java.io.ByteArrayInputStream;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author airhacks.com
 */
@Control
public class PushService {

    private Client client;

    @Inject
    Tracer tracer;

    @PostConstruct
    public void initializeClient() {
        this.client = ClientBuilder.newClient();
    }

    public Response send(String endpoint, String salt, String ephemeralPublicKey, String vapidPublicKey, String jsonWebSignature, byte[] encryptedContent) {
        tracer.log("endpoint", endpoint);
        tracer.log("salt", salt);
        tracer.log("ephemeralPublicKey", ephemeralPublicKey);
        tracer.log("vapidPublicKey", vapidPublicKey);
        tracer.log("jsonWebSignature", jsonWebSignature);
        tracer.log("encryptedContent", encryptedContent);

        Response response = this.client.target(endpoint).
                request().
                header("TTL", "2419200").
                header("Content-Encoding", "aesgcm").
                header("Encryption", "salt=" + salt).
                header("Authorization", "WebPush " + jsonWebSignature).
                header("Crypto-Key", "dh=" + ephemeralPublicKey + ";p256ecdsa=" + vapidPublicKey).
                post(Entity.entity(new ByteArrayInputStream(encryptedContent), MediaType.APPLICATION_OCTET_STREAM));

        return response;
    }


}
