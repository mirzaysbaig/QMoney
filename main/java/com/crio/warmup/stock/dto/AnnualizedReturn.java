
package com.crio.warmup.stock.dto;
// in same way as total return we need class structure for functionality of annualized return

public class AnnualizedReturn implements Comparable<AnnualizedReturn> {

  private final String symbol;
  private final Double annualizedReturn;
  private final Double totalReturns;

  public AnnualizedReturn(String symbol, Double annualizedReturn, Double totalReturns) {
    this.symbol = symbol;
    this.annualizedReturn = annualizedReturn;
    this.totalReturns = totalReturns;
  }

  public String getSymbol() {
    return symbol;
  }

  public Double getAnnualizedReturn() {
    return annualizedReturn;
  }

  public Double getTotalReturns() {
    return totalReturns;
  }

  @Override
  public int compareTo(AnnualizedReturn o) {
    // TODO Auto-generated method stub
    if(this.annualizedReturn>o.annualizedReturn){
      return -1;
    }
    else if(this.annualizedReturn<o.annualizedReturn){
      return 1;
    }
    else
    return 0;
  }
}
