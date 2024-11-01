
package com.crio.warmup.stock;


import com.crio.warmup.stock.dto.*;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.crio.warmup.stock.portfolio.PortfolioManager;
import com.crio.warmup.stock.portfolio.PortfolioManagerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.management.RuntimeErrorException;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.web.client.RestTemplate;


public class PortfolioManagerApplication {

 
  private static String token ="909365543cc8586027f4495af2d61139cb227848";
  public static String getToken(){
    return token;
  }
  // module 1,2
  public static List<String> mainReadFile(String[] args) throws IOException, URISyntaxException {
    List<PortfolioTrade> trades=readTradesFromJson(args[0]); // reading json data from file at args[0], for this first we get file than read the data 
    // now we got converted in java mapping and stored in trades array of class type PortfolioTrade
    // for(PortfolioTrade t:trades){// to print all data
    //     System.out.println(t.toString());// this function to print every details in t pojo file
    // }
    // to get just symbol 
    List<String> symbols=new ArrayList<String>();
    for(PortfolioTrade t:trades){
           symbols.add(t.getSymbol());
    }
    return symbols;
  }





  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(
        Thread.currentThread().getContextClassLoader().getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }


  

  public static List<String> debugOutputs() {

    //  String valueOfArgument0 = "trades.json";
    //  String resultOfResolveFilePathArgs0 = "/home/crio-user/workspace/mirzab-ug21-ec-nitp-ac-ME_QMONEY_V2/qmoney/bin/main/trades.json";
    //  String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@6150c3ec";
    //  String functionNameFromTestFileInStackTrace = "mainreadFile";
    //  String lineNumberFromTestFileInStackTrace = "29:1";
     String valueOfArgument0 = "trades.json";
     String resultOfResolveFilePathArgs0 = "trades.json";
     String toStringOfObjectMapper = "ObjectMapper";//"com.fasterxml.jackson.databind.ObjectMapper@6150c3ec";
     String functionNameFromTestFileInStackTrace = "mainReadFile";
     String lineNumberFromTestFileInStackTrace = "29:1";

    return Arrays.asList(new String[]{valueOfArgument0, resultOfResolveFilePathArgs0,
        toStringOfObjectMapper, functionNameFromTestFileInStackTrace,
        lineNumberFromTestFileInStackTrace});
  }


  // Note: module 3
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  public static List<String> mainReadQuotes(String[] args) throws IOException, URISyntaxException {
    List<PortfolioTrade> trades=readTradesFromJson(args[0]);
    RestTemplate restTemplate=new RestTemplate();
    // end date is args[1] but it is in LOcaldate format to convert to  string format to convert it  in java we use parse 
    LocalDate enddate=LocalDate.parse(args[1]);
    // we just need for closing date for list of candles to sort for total returns Dto
   List<TotalReturnsDto> totalReturnsDtos=new ArrayList<>();
    for(PortfolioTrade trade:trades){
        String url=prepareUrl(trade, enddate,token);
        // mapping the called api with the help of resttemplate and deseriliaze it
       // to map as tingocandle and to stor in cande which is interface oof it 
        Candle[] candles= restTemplate.getForObject(url, TiingoCandle[].class);//responsetype=TingoCandle where it maps data from api)
        // to sort for totalreturn dtos we stored first the values of total return dtos 
        // for total return we just need the closingprice of the last date which is store in the last of the api is stored in candles list
        totalReturnsDtos.add(new TotalReturnsDto(trade.getSymbol(),candles[candles.length-1].getClose()));
    }
    // as we got the toatal return dto now to sort
    Collections.sort(totalReturnsDtos);
    // now to return the sorted results of symbols as there date from above
    List<String> result=new ArrayList<>();
    for(TotalReturnsDto dto:totalReturnsDtos){
      result.add(dto.getSymbol());
    }
    return result;
    // now the sorted with end date trade is r eturned 
  }

  // TODO:
  //  After refactor, make sure that the tests pass by using these two commands
  //  ./gradlew test --tests PortfolioManagerApplicationTest.readTradesFromJson
  //  ./gradlew test --tests PortfolioManagerApplicationTest.mainReadFile
  public static List<PortfolioTrade> readTradesFromJson(String filename) throws IOException, URISyntaxException {
    File file= resolveFileFromResources(filename);// as args[0]==trades.json file from this we r getting file url which is converted in file type in resolve function
    ObjectMapper mapper=getObjectMapper();// serialize and then read the data in valid type
    List<PortfolioTrade> trades=Arrays.asList(mapper.readValue(file,PortfolioTrade[].class));
    return trades;
  }


  // TODO:
  //  Build the Url using given parameters and use this function in your code to cann the API.
  public static String prepareUrl(PortfolioTrade trade, LocalDate endDate, String token) {
     String url="https://api.tiingo.com/tiingo/daily/"+trade.getSymbol() + "/prices?startDate=" +trade.getPurchaseDate()+ "&endDate="+endDate+"&token="+token;
     return url;// url for calling api of different symbol from tingo 
  }



  // TODO:
  //  Ensure all tests are passing using below command
  //  ./gradlew test --tests ModuleThreeRefactorTest
 public static Double getOpeningPriceOnStartDate(List<Candle> candles) {
     return candles.get(0).getOpen();
  }


  public static Double getClosingPriceOnEndDate(List<Candle> candles) {
     return candles.get(candles.size()-1).getClose();
  }


  public static List<Candle> fetchCandles(PortfolioTrade trade, LocalDate endDate, String token) {
    RestTemplate restTemplate=new RestTemplate();
    String url=prepareUrl(trade, endDate,token);
    // mapping the called api with the help of resttemplate and deseriliaze it
   // to map as tingocandle and to stor in cande which is interface oof it 
    Candle[] candles= restTemplate.getForObject(url, TiingoCandle[].class);//responsetype=TingoCandle where it maps data from api)
    List<Candle> candles1=Arrays.asList(candles);
     return candles1;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args)
      throws IOException, URISyntaxException {
    List<PortfolioTrade> trades=readTradesFromJson(args[0]);
    // end date is args[1] but it is in LOcaldate format to convert to  string format to convert it  in java we use parse 
    LocalDate endDate=LocalDate.parse(args[1]);
    List<AnnualizedReturn> annualizedReturns =new ArrayList<>();
    for(PortfolioTrade trade:trades){
    List<Candle> candles=fetchCandles(trade, endDate, token);
    Double openingprice=getOpeningPriceOnStartDate(candles);
    Double closingprice=getClosingPriceOnEndDate(candles);
    AnnualizedReturn result=calculateAnnualizedReturns(endDate, trade, openingprice, closingprice);
    annualizedReturns.add(result);
    }
    // now to sort the symbol based on the descending order of annulaized return
    Collections.sort(annualizedReturns);
    return annualizedReturns;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  //  Return the populated list of AnnualizedReturn for all stocks.
  //  Annualized returns should be calculated in two steps:
  //   1. Calculate totalReturn = (sell_value - buy_value) / buy_value.
  //      1.1 Store the same as totalReturns
  //   2. Calculate extrapolated annualized returns by scaling the same in years span.
  //      The formula is:
  //      annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  //      2.1 Store the same as annualized_returns
  //  Test the same using below specified command. The build should be successful.
  //     ./gradlew test --tests PortfolioManagerApplicationTest.testCalculateAnnualizedReturn
  
  // in module 4 we shift this code to portfoliomanager implemantaion to improve the readeablity using factory design pattern 
  // so that client can  use it from application side not we can use from main 
  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate,
      PortfolioTrade trade, Double buyPrice, Double sellPrice) {
        if(endDate.isBefore(trade.getPurchaseDate())){
            throw new RuntimeException("Enddate cannot be before purchase date");
        }
      // we have purchase date in trade class from where we can get the purchase date 
      Double buy_value=buyPrice*trade.getQuantity();
      Double sell_value=sellPrice*trade.getQuantity();
      Double totalReturns= (double)(sell_value-buy_value)/buy_value;
      
      // to get no of days 
      long noOfDays=ChronoUnit.DAYS.between(trade.getPurchaseDate(), endDate);
  
      Double years=(double)noOfDays/(double)365;
      Double annualized_returns=Math.pow(1+totalReturns, 1/years)-1; 
      System.out.println(annualized_returns);
      return new AnnualizedReturn(trade.getSymbol(),annualized_returns, totalReturns);
  }


  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Once you are done with the implementation inside PortfolioManagerImpl and
  //  PortfolioManagerFactory, create PortfolioManager using PortfolioManagerFactory.
  //  Refer to the code from previous modules to get the List<PortfolioTrades> and endDate, and
  //  call the newly implemented method in PortfolioManager to calculate the annualized returns.

  // Note:
  // Remember to confirm that you are getting same results for annualized returns as in Module 3.
  private static String readFileAsString(String file) throws IOException { // reading file as string 
   // byte[] byteArray = Files.readAllBytes(file.toPath());
    Path path=Paths.get(file);
    String contents = Files.readString(path,StandardCharsets.UTF_8);
    return contents;
  }

  public static List<AnnualizedReturn> mainCalculateReturnsAfterRefactor(String[] args)
      throws Exception {
       String file = args[0];
       LocalDate endDate = LocalDate.parse(args[1]);
       // file is passed as string so we have to implement that logic whic is 
       //List<PortfolioTrade> portfolioTrades=readTradesFromJson(filename);
      String contents =readFileAsString(file);
      ObjectMapper objectMapper = getObjectMapper();
      RestTemplate restTemplate = new RestTemplate();

      PortfolioTrade[] portfolioTrades = objectMapper.readValue(contents, PortfolioTrade[].class);
      PortfolioManager portfolioManager=PortfolioManagerFactory.getPortfolioManager(restTemplate);
       return portfolioManager.calculateAnnualizedReturn(Arrays.asList(portfolioTrades), endDate);
  }


  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    // printJsonObject(mainReadFile(args));  //this is for previous module 1 

    //printJsonObject(mainReadQuotes(args));// for module 2 stage 2
    //printJsonObject(mainCalculateSingleReturn(args)); // for module 3

    printJsonObject(mainCalculateReturnsAfterRefactor(args)); // for module 4
  }
}

