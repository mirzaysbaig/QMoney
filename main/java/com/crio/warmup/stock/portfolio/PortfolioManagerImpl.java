
package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.crio.warmup.stock.quotes.StockQuotesService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.springframework.web.client.RestTemplate;
/// this is used in module 4 for factory design pattern so now client can use it 
// public class PortfolioManagerImpl implements PortfolioManager { // for module 5



//   RestTemplate restTemplate=new RestTemplate();
//   // Caution: Do not delete or modify the constructor, or else your build will break!
//   // This is absolutely necessary for backward compatibility
//   protected PortfolioManagerImpl(RestTemplate restTemplate) { // constructor important 

public class PortfolioManagerImpl implements PortfolioManager {

  private static String token ="909365543cc8586027f4495af2d61139cb227848";
  private RestTemplate restTemplate;
 private StockQuotesService stockQuotesService;

  // Caution: Do not delete or modify the constructor, or else your build will break!
  // This is absolutely necessary for backward compatibility
  @Deprecated
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }
  protected PortfolioManagerImpl(StockQuotesService stockQuotesServices){
    this.stockQuotesService= stockQuotesServices;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // 1. Now we want to convert our code into a module, so we will not call it from main anymore.
  //    Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  //    into #calculateAnnualizedReturn function here and ensure it follows the method signature.
  // 2. Logic to read Json file and convert them into Objects will not be required further as our
  //    clients will take care of it, going forward.

  // Note:
  // Make sure to exercise the tests inside PortfolioManagerTest using command below:
  // ./gradlew test --tests PortfolioManagerTest

  //CHECKSTYLE:OFF
  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF


  // for module 4 we have implemented this here  
  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades,
      LocalDate endDate) throws JsonProcessingException, StockQuoteServiceException
       {
    // TODO Auto-generated method stub
    List<AnnualizedReturn> annulaizedReturns= new ArrayList<>();
    for(PortfolioTrade trades:portfolioTrades){
       List<Candle> candles=getStockQuote(trades.getSymbol(), trades.getPurchaseDate(),endDate);
         Double buy_value=candles.get(0).getOpen();
         Double sell_value=candles.get(candles.size()-1).getClose();
         Double totalReturns= (double)(sell_value-buy_value)/buy_value;
      
        // to get no of days 
        long noOfDays=ChronoUnit.DAYS.between(trades.getPurchaseDate(), endDate);
        Double years=(double)noOfDays/(double)365;
        Double annualized_returns=Math.pow(1+totalReturns, 1/years)-1; 
        // mapping with annualizedreturn class
        AnnualizedReturn annual=new AnnualizedReturn(trades.getSymbol(), annualized_returns, totalReturns);
        annulaizedReturns.add(annual);
    }
    // we also has to sort it out in descending order using get comparator function above 
       Collections.sort(annulaizedReturns,getComparator());
       return annulaizedReturns;
  }

  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
     throws JsonProcessingException, StockQuoteServiceException {
      // direct access // module 6 using factory
      List<Candle> candles= stockQuotesService.getStockQuote(symbol,from,to);
      return candles;
      // gone to module 6 for implementation
  //   String url=buildUri(symbol,from,to);
  //   RestTemplate restTemplate=new RestTemplate();
  //   ObjectMapper objectMapper=new ObjectMapper();
  //   Candle[] candles= restTemplate.getForObject(url, TiingoCandle[].class);//responsetype=TingoCandle where it maps data from api)
  //   List<Candle> candles1=Arrays.asList(candles);
  //   return candles1;
  }
  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo third-party APIs to a separate function.
  //  Remember to fill out the buildUri function and use that.

// code gone to module 6 quotes tingoservice
  // protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
  //      String uriTemplate = "https://api.tiingo.com/tiingo/daily/"+symbol+ "/prices?"
  //           + "startDate="+startDate+"&endDate="+endDate+"&token="+token;
  //     return uriTemplate; // it gives the url ;
  // }


 


  // ¶TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Modify the function #getStockQuote and start delegating to calls to
  //  stockQuoteService provided via newly added constructor of the class.
  //  You also have a liberty to completely get rid of that function itself, however, make sure
  //  that you do not delete the #getStockQuote function.

}
