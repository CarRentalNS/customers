package si.fri.rso.samples.customers.api.v1.resources;

import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rso.samples.customers.api.v1.dtos.UploadImageResponse;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.services.beans.CustomersBean;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Log
@ApplicationScoped
@Path("/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomersResource {

    @Inject
    private CustomersBean customersBean;

    @Context
    protected UriInfo uriInfo;

    @GET
    public Response getCustomers() {

        List<Customer> customers = customersBean.getCustomers();

        return Response.ok(customers).build();
    }

    @GET
    @Path("/filtered")
    public Response getCustomersFiltered() {

        List<Customer> customers;

        customers = customersBean.getCustomersFilter(uriInfo);

        return Response.status(Response.Status.OK).entity(customers).build();
    }

    @GET
    @Path("/{customerId}")
    public Response getCustomer(@PathParam("customerId") Integer customerId) {

        Customer customer = customersBean.getCustomer(customerId);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(customer).build();
    }

    @POST
    public Response createCustomer(Customer customer) {

        if ((customer.getFirstName() == null || customer.getFirstName().isEmpty()) || (customer.getLastName() == null
                || customer.getLastName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            customer = customersBean.createCustomer(customer);
        }

        if (customer.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(customer).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(customer).build();
        }
    }

    @PUT
    @Path("{customerId}")
    public Response putZavarovanec(@PathParam("customerId") String customerId, Customer customer) {

        customer = customersBean.putCustomer(customerId, customer);

        if (customer == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } else {
            if (customer.getId() != null)
                return Response.status(Response.Status.OK).entity(customer).build();
            else
                return Response.status(Response.Status.NOT_MODIFIED).build();
        }
    }

    @DELETE
    @Path("{customerId}")
    public Response deleteCustomer(@PathParam("customerId") Integer customerId) {

        boolean deleted = customersBean.deleteCustomer(customerId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST
    @Path("/{id}/image")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public Response uploadImage(InputStream uploadedInputStream) {

        byte[] bytes = new byte[0];
//        try (uploadedInputStream) {
//            bytes = uploadedInputStream.readAllBytes();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        UploadImageResponse uploadImageResponse = new UploadImageResponse();

        Integer numberOfFaces = customersBean.countFacesOnImage(bytes);
        uploadImageResponse.setNumberOfFaces(numberOfFaces);

        if (numberOfFaces != 1) {
            uploadImageResponse.setMessage("Image must contain one face.");
            return Response.status(Response.Status.BAD_REQUEST).entity(uploadImageResponse).build();

        }

        List<String> detectedCelebrities = customersBean.checkForCelebrities(bytes);

        if (!detectedCelebrities.isEmpty()) {
            uploadImageResponse.setMessage("Image must not contain celebrities. Detected celebrities: "
                    + detectedCelebrities.stream().collect(Collectors.joining(", ")));
            return Response.status(Response.Status.BAD_REQUEST).entity(uploadImageResponse).build();
        }

        uploadImageResponse.setMessage("Success.");
        return Response.status(Response.Status.CREATED).entity(uploadImageResponse).build();
    }
}
