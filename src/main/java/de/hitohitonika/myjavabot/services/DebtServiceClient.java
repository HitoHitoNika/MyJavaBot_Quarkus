package de.hitohitonika.myjavabot.services;

import de.hitohitonika.myjavabot.data.PaymentInfo;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.math.BigDecimal;
import java.util.List;

@RegisterRestClient(configKey = "debt-api")
@Path("/paymentinfo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface DebtServiceClient {

    @GET
    Uni<List<PaymentInfo>> getPaymentInfos();

    @GET
    @Path("/user")
    Uni<PaymentInfo> getPaymentInfoByName(@QueryParam("name") String name);

    @POST
    @Path("/pay")
    Uni<PaymentInfo> payDebt(@QueryParam("name") String name, @QueryParam("amount") BigDecimal amount);

    @PUT
    @Path("/user")
    Uni<Void> updatePaymentInfo(@QueryParam("name") String name);
}
