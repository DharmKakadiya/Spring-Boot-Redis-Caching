package com.practice.Services;

import com.practice.Models.Customer;
import com.practice.Repositories.CustomerRepository;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@CacheConfig(cacheNames = "customerCache")
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Cacheable(cacheNames = "customers")
    @Override
    public List<Customer> getAll() {
        waitSomeTime();
        return this.customerRepository.findAll();
    }

    @CacheEvict(cacheNames = "customers", allEntries = true)
    @Override
    public Customer add(Customer customer) {
        return this.customerRepository.save(customer);
    }

    @CacheEvict(cacheNames = "customers", allEntries = true)
    @Override
    public Customer update(Customer customer) {
        Optional<Customer> optCustomer = this.customerRepository.findById(customer.getId());
        if (!optCustomer.isPresent())
            return null;
        Customer repCustomer = optCustomer.get();
        repCustomer.setName(customer.getName());
        repCustomer.setContactName(customer.getContactName());
        repCustomer.setAddress(customer.getAddress());
        repCustomer.setCity(customer.getCity());
        repCustomer.setPostalCode(customer.getPostalCode());
        repCustomer.setCountry(customer.getCountry());
        return this.customerRepository.save(repCustomer);
    }

    @Caching(evict = { @CacheEvict(cacheNames = "customer", key = "#id"),
            @CacheEvict(cacheNames = "customers", allEntries = true) })
    @Override
    public void delete(long id) {
        this.customerRepository.deleteById(id);
    }

    @Cacheable(cacheNames = "customer", key = "#id", unless = "#result == null")
    @Override
    public Customer getCustomerById(long id) {
        waitSomeTime();
        return this.customerRepository.findById(id).orElse(null);
    }

    private void waitSomeTime() {
        System.out.println("Long Wait Begin");
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Long Wait End");
    }

}