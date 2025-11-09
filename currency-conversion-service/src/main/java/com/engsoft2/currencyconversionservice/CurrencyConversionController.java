package com.engsoft2.currencyconversionservice;

import java.math.BigDecimal;

import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CurrencyConversionController {
    private CurrencyExchangeProxy proxy;
    private RabbitTemplate rabbitTemplate;
    private FanoutExchange fanout;

    public CurrencyConversionController(CurrencyExchangeProxy proxy, RabbitTemplate template, FanoutExchange fanout) {
        this.proxy = proxy;
        this.rabbitTemplate = template;
        this.fanout = fanout;
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(@PathVariable String from, @PathVariable String to,
            @PathVariable BigDecimal quantity) {
        CurrencyConversion currencyConversion = proxy.retrieveExchangeValue(from, to);
        HistoryDTO dto = new HistoryDTO(from, to);
        rabbitTemplate.convertAndSend(fanout.getName(),"",dto);
        return new CurrencyConversion(
                currencyConversion.getId(),
                from, to, quantity,
                currencyConversion.getConversionMultiple(),
                quantity.multiply(currencyConversion.getConversionMultiple()),
                currencyConversion.getEnvironment() + " " + "feign");
    }

}
