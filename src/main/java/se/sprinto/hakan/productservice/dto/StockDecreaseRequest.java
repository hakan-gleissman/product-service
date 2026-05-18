package se.sprinto.hakan.productservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class StockDecreaseRequest {

    @Valid
    @NotEmpty
    private List<StockDecreaseItemRequest> items;

    public List<StockDecreaseItemRequest> getItems() {
        return items;
    }

    public void setItems(List<StockDecreaseItemRequest> items) {
        this.items = items;
    }
}
