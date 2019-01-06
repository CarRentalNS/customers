package si.fri.rso.samples.orders.api.v1.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;

import javax.inject.Inject;

@GraphQLClass
public class CustomerMutations {

    @Inject
    private CustomersBean customersBean;

    @GraphQLMutation
    public Customer addCustomer(@GraphQLArgument(name = "customer") Customer customer) {
        customersBean.createCustomer(customer);
        return customer;
    }

    @GraphQLMutation
    public DeleteResponse deleteCustomer(@GraphQLArgument(name = "id") Integer id) {
        return new DeleteResponse(customersBean.deleteCustomer(id));
    }

}
