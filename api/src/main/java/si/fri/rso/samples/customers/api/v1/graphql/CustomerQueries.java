package si.fri.rso.samples.customers.api.v1.graphql;

import com.kumuluz.ee.graphql.annotations.GraphQLClass;
import com.kumuluz.ee.graphql.classes.Filter;
import com.kumuluz.ee.graphql.classes.Pagination;
import com.kumuluz.ee.graphql.classes.PaginationWrapper;
import com.kumuluz.ee.graphql.classes.Sort;
import com.kumuluz.ee.graphql.utils.GraphQLUtils;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import si.fri.rso.samples.customers.models.entities.Customer;
import si.fri.rso.samples.customers.services.beans.CustomersBean;

import javax.inject.Inject;
import javax.persistence.EntityManager;

@GraphQLClass
public class CustomerQueries {

    @Inject
    private CustomersBean customersBean;

    @GraphQLQuery
    public PaginationWrapper<Customer> allCustomers(@GraphQLArgument(name = "pagination") Pagination pagination,
                                                    @GraphQLArgument(name = "sort") Sort sort,
                                                    @GraphQLArgument(name = "filter") Filter filter) {
        return GraphQLUtils.process(customersBean.getCustomers(), pagination, sort, filter);
    }

    @GraphQLQuery
    public Customer getCustomer(@GraphQLArgument(name = "id") Integer id) {
        return customersBean.getCustomer(id);
    }

}
