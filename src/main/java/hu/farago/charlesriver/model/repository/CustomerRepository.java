package hu.farago.charlesriver.model.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hu.farago.charlesriver.model.entity.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);
}