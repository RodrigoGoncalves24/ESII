package com.engsoft2.currencyexchangeservice;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
public class CurrencyExchangeController {
    private Logger logger = LoggerFactory.getLogger(CurrencyExchangeController.class);
    private CurrencyExchangeRepository repository;
    private Environment environment;

    public CurrencyExchangeController(CurrencyExchangeRepository repository, Environment environment) {
        this.repository = repository;
        this.environment = environment;
    }

    @GetMapping("/currency-exchange/from/{from}/to/{to}")
    public CurrencyExchange retrieveExchangeValue(@PathVariable String from, @PathVariable String to) {
        logger.info("retrieveExchangeValue called with from {} to {}", from, to);
        CurrencyExchange currencyExchange = repository.findByFromAndTo(from, to);
        if (currencyExchange == null) {
            throw new ResourceNotFoundException("From " + from + "To " + to + " not found");
        }
        String port = environment.getProperty("local.server.port");
        currencyExchange.setEnvironment(port);
        return currencyExchange;
    }

    @PostMapping("/currency-exchange/rates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyExchange> createExchangeRate(@RequestBody CurrencyExchange rateData) {
        CurrencyExchange existingRate = repository.findByFromAndTo(rateData.getFrom(), rateData.getTo());

        if (existingRate != null) {
            return new ResponseEntity<>(null, HttpStatus.CONFLICT);
        }

        CurrencyExchange savedRate = repository.save(rateData);
        return new ResponseEntity<>(savedRate, HttpStatus.CREATED);
    }

    @PutMapping("/currency-exchange/rates/from/{from}/to/{to}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CurrencyExchange> updateExchangeRate(
            @PathVariable String from,
            @PathVariable String to,
            @RequestBody CurrencyExchange rateData) {
        
        CurrencyExchange rateToUpdate = repository.findByFromAndTo(from, to);
        if (rateToUpdate == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        rateToUpdate.setConversionMultiple(rateData.getConversionMultiple());
        
        CurrencyExchange updatedRate = repository.save(rateToUpdate);

        return new ResponseEntity<>(updatedRate, HttpStatus.OK);
    }

}
