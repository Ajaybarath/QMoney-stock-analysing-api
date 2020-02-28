
package com.crio.warmup.stock.dto;

public class AnnualizedReturn {

  private final transient String symbol;
  private final transient Double yearlyReturns;
  private final transient Double totalReturns;

  public AnnualizedReturn(String symbol, Double yearlyReturns, Double totalReturns) {
    this.symbol = symbol;
    this.yearlyReturns = yearlyReturns;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return yearlyReturns;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }
}
