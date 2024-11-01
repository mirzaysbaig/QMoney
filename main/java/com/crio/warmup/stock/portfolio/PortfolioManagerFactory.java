
package com.crio.warmup.stock.portfolio;

import com.crio.warmup.stock.quotes.StockQuoteServiceFactory;
import com.crio.warmup.stock.quotes.StockQuotesService;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerFactory {

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Implement the method to return new instance of PortfolioManager.
  //  Remember, pass along the RestTemplate argument that is provided to the new instance.
@Deprecated // it terms for outdated and warn the developers 
  public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {
  PortfolioManager instance=null;
  // portfoliomanagerimpl constructor take resttemplate as parameter 
  // we make object of portfoliomanageripl with refernce od interface 
  instance=new PortfolioManagerImpl(restTemplate);
     return instance;// thus we can get instance from it which is of Portfolio Manger type as client  calling it with rest template instance 
  }

  // public static PortfolioManager getPortfolioManager(RestTemplate restTemplate) {

  // }

  // TODO: CRIO_TASK_MODULE_ADDITIONAL_REFACTOR
  //  Implement the method to return new instance of PortfolioManager.
  //  Steps:
  //    1. Create appropriate instance of StoockQuoteService using StockQuoteServiceFactory and then
  //       use the same instance of StockQuoteService to create the instance of PortfolioManager.
  //    2. Mark the earlier constructor of PortfolioManager as @Deprecated.
  //    3. Make sure all of the tests pass by using the gradle command below:
  //       ./gradlew test --tests PortfolioManagerFactory


   public static PortfolioManager getPortfolioManager(String provider,
     RestTemplate restTemplate) {
      StockQuotesService stockQuotesService=StockQuoteServiceFactory.getService(provider,restTemplate);
      // now we got the service object from factory of type stockQuotesService;
      PortfolioManager instance=null;
      instance=new PortfolioManagerImpl(stockQuotesService);

     return instance;
   }

}
