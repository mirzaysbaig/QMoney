
package com.crio.warmup.stock.quotes;

import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.exception.StockQuoteServiceException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.springframework.web.client.RestTemplate;

public class TiingoService implements StockQuotesService {


  private RestTemplate restTemplate;
  private static String token ="909365543cc8586027f4495af2d61139cb227848";

  protected TiingoService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  @Override
  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to)
      throws JsonProcessingException,StockQuoteServiceException {
    // TODO Auto-generated method stub
    if(from.compareTo(to)>=0){ // if startdate is after or at enddate;
      throw new RuntimeException();
    }
    List<Candle> stockStartToEnd =new ArrayList<>();
    try{
    // here parsing is not done of date 
    String url=buildUri(symbol,from,to);
   // RestTemplate restTemplate=new RestTemplate(); this we already got from above
   // so not to use this it will fail
    ObjectMapper objectMapper=getObjectMapper();
    String apiresponse= restTemplate.getForObject(url, String.class);//responsetype=TingoCandle where it maps data from api but can give wrong for dates as it cannot map local date 
    Candle[] candles=objectMapper.readValue(apiresponse,TiingoCandle[].class);
    stockStartToEnd=Arrays.asList(candles);
    // if(candles!=null) return Arrays.asList(candles); // we can throw null pointer exception
    // //else return Collections.emptyList();
    //  else return Arrays.asList(new TiingoCandle[0]);
    //List<Candle> candles1=Arrays.asList(candles);
    //return candles1;
    }
    catch(NullPointerException e){
      throw new StockQuoteServiceException("Error Occurred on Requesting response from tiingo",e.getCause());
    }
    return stockStartToEnd;
  
  }
  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement getStockQuote method below that was also declared in the interface.

  // Note:
  // 1. You can move the code from PortfolioManagerImpl#getStockQuote inside newly created method.
  // 2. Run the tests using command below and make sure it passes.
  //    ./gradlew test --tests TiingoServiceTest


  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Write a method to create appropriate url to call the Tiingo API.
  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
    //uri template to be in string format so startdate enddate all of that if some error maty rise
    String uriTemplate = String.format("https://api.tiingo.com/tiingo/daily/%s"
        + "/prices?startDate=%s&endDate=%s&token=%s",symbol,startDate,endDate,token);
   return uriTemplate; // it gives the url ;
}




  // TODO: CRIO_TASK_MODULE_EXCEPTIONS
  //  1. Update the method signature to match the signature change in the interface.
  //     Start throwing new StockQuoteServiceException when you get some invalid response from
  //     Tiingo, or if Tiingo returns empty results for whatever reason, or you encounter
  //     a runtime exception during Json parsing.
  //  2. Make sure that the exception propagates all the way from
  //     PortfolioManager#calculateAnnualisedReturns so that the external user's of our API
  //     are able to explicitly handle this exception upfront.

  //CHECKSTYLE:OFF


}
