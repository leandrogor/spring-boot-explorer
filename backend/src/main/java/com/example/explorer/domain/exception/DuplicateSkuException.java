package com.example.explorer.domain.exception;

public class DuplicateSkuException extends BusinessException {
    private final String sku;

    public DuplicateSkuException(String sku) {
        super("DUPLICATE_SKU", String.format("Duplicate SKU found: %s", sku));
        this.sku = sku;
    }

    public String getSku() {
        return sku;
    }
}
