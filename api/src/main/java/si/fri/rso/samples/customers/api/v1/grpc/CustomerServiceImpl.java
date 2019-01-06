package si.fri.rso.samples.customers.api.v1.grpc;

import com.kumuluz.ee.grpc.annotations.GrpcService;
import io.grpc.stub.StreamObserver;
import si.fri.rso.samples.customers.api.grpc.CustomerGrpc;
import si.fri.rso.samples.customers.api.grpc.CustomerService;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.services.beans.CustomersBean;

import javax.enterprise.inject.spi.CDI;

@GrpcService
public class CustomerServiceImpl extends CustomerGrpc.CustomerImplBase {

    private CustomersBean customersBean;

    @Override
    public void getCustomer(CustomerService.CustomerRequest request,
                            StreamObserver<CustomerService.CustomerResponse> responseObserver) {

        customersBean = CDI.current().select(CustomersBean.class).get();
        Customer customer = customersBean.getCustomer(request.getId());
        CustomerService.CustomerResponse response;

        if (customer != null) {
            CustomerService.CustomerResponse.Builder builder = CustomerService.CustomerResponse.newBuilder()
                    .setId(customer.getId())
                    .setFirstName(customer.getFirstName())
                    .setLastName(customer.getLastName());

            customer.getOrders().stream().forEach(order -> {
                builder.addOrders(CustomerService.Order.newBuilder()
                        .setId(order.getId())
                        .setTitle(order.getTitle()))
                        .build();
            });

            response = builder.build();
            responseObserver.onNext(response);

        }

        responseObserver.onCompleted();

    }
}
