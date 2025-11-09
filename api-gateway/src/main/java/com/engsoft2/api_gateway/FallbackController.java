package com.engsoft2.api_gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @GetMapping("/currency-conversion")
    public ResponseEntity<String> currencyConversionFallback() {
        return ResponseEntity.internalServerError().body("Serviços temporariamente indisponíveis. Tente novamente.");
        //Implementação mais adequada seria retornar dados em cache de
        //uma requisição anterior com sucesso
    }
}
