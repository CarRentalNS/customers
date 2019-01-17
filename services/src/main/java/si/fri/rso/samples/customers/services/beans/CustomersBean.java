package si.fri.rso.samples.customers.services.beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.metrics.annotation.Timed;
import si.fri.rso.samples.customers.models.dtos.Order;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.services.clients.AmazonRekognitionClient;
import si.fri.rso.samples.customers.services.configuration.AppProperties;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@ApplicationScoped
public class CustomersBean {

    private Logger log = Logger.getLogger(CustomersBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private CustomersBean customersBean;

    @Inject
    private AmazonRekognitionClient amazonRekognitionClient;

    private Client httpClient;

    @Inject
    @DiscoverService("orders")
    private Optional<String> baseUrl;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
       // baseUrl = "http://159.122.187.177:31465"; // only for demonstration
    }


    public List<Customer> getCustomers() {

        TypedQuery<Customer> query = em.createNamedQuery("Customer.getAll", Customer.class);

        return query.getResultList();

    }

    public List<Customer> getCustomersFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Customer.class, queryParameters);
    }

    public Customer getCustomer(Integer customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer == null) {
            throw new NotFoundException();
        }

        List<Order> orders = customersBean.getOrders(customerId);
        customer.setOrders(orders);

        return customer;
    }

    public Customer createCustomer(Customer customer) {

        try {
            beginTx();
            em.persist(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public Customer putCustomer(String customerId, Customer customer) {

        Customer c = em.find(Customer.class, customerId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            customer.setId(c.getId());
            customer = em.merge(customer);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return customer;
    }

    public boolean deleteCustomer(Integer customerId) {

        Customer customer = em.find(Customer.class, customerId);

        if (customer != null) {
            try {
                beginTx();
                em.remove(customer);
                commitTx();
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

    @Timed
    @CircuitBreaker(requestVolumeThreshold = 3)
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getOrdersFallback")
    public List<Order> getOrders(Integer customerId) {


        if (appProperties.isExternalServicesEnabled() && baseUrl.isPresent()) {
           /* try {
                String json = getJSONResponse("GET", baseUrl.get());
                ObjectMapper mapper = new ObjectMapper();

                Order driverId = mapper.readValue(json, Order.class);

                return httpClient
                        .target(baseUrl.get() + "/v1/orders?where=customerId:EQ:" + customerId)
                        .request().get(new GenericType<List<Order>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                throw new InternalServerErrorException(e);
            }*/
          // try {
               System.out.println(" URL is "+baseUrl);

               //String json = getJSONResponse("GET", baseUrl.get());
              // ObjectMapper mapper = new ObjectMapper();

              // Order driverId = mapper.readValue(json, Order.class);
          // } catch (IOException e){
               return new ArrayList<>();
           }




        return null;

    }

    public List<Order> getOrdersFallback(Integer customerId) {

        return Collections.emptyList();

    }

    public Integer countFacesOnImage(byte[] image) {

        log.info("Detenting faces with Amazon Rekognition...");

        int nDetectedFaces = amazonRekognitionClient.countFaces(image);

        log.info("Amazon Rekognition: number of detected faces: " + nDetectedFaces);

        return nDetectedFaces;
    }

    public List<String> checkForCelebrities(byte[] image) {

        log.info("Recognising celebrities with Amazon Rekognition...");

        return amazonRekognitionClient.checkForCelebrities(image);


    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }

    public void loadOrder(Integer n) {


    }

    private static String getJSONResponse(String requestType, String fullUrl) {
        return getJSONResponse( requestType,  fullUrl, null);
    }

    private static String getJSONResponse(String requestType, String fullUrl, String json) {
        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = null;

            if ("GET".equals(requestType)) {
                HttpGet request = new HttpGet(fullUrl);
                response = httpClient.execute(request);

            } else if ("POST".equals(requestType)) {
                HttpPost request = new HttpPost(fullUrl);

                request.setEntity(new StringEntity(json));
                request.setHeader("Content-type", "application/json");
                request.setHeader("Accept", "application/json");

                response = httpClient.execute(request);

            } else {
                throw new InternalServerErrorException("Wrong request type:" + requestType);
            }

            int status = response.getStatusLine().getStatusCode();
            System.out.println("response code: " + status);
            if (status >= 200 && status < 300) {
                HttpEntity entity = response.getEntity();
                if (entity != null)
                    return EntityUtils.toString(entity);
            } else {
                String msg = "Remote server '" + fullUrl + "' is responded with status " + status + ".";
                System.out.println(msg);
                // todo logging
                throw new InternalServerErrorException(msg);
            }

        } catch (IOException e) {
            String msg = e.getClass().getName() + " occured: " + e.getMessage();
            // todo logging
            System.out.println(msg);
            throw new InternalServerErrorException(msg);
        }
        return "{}"; //empty json
    }
}
