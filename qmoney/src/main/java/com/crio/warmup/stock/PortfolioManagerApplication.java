package com.crio.warmup.stock;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
import com.crio.warmup.stock.log.UncaughtExceptionHandler;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.logging.log4j.ThreadContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerApplication {

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Read the json file provided in the argument[0]. The file will be avilable in
  // the classpath.
  // 1. Use #resolveFileFromResources to get actual file from classpath.
  // 2. parse the json file using ObjectMapper provided with #getObjectMapper,
  // and extract symbols provided in every trade.
  // return the list of all symbols in the same order as provided in json.
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json"
  // Make sure that it prints below String on the console -
  // ["AAPL","MSFT","GOOGL"]
  // Now, run
  // ./gradlew build and make sure that the build passes successfully
  // There can be few unused imports, you will need to fix them to make the build
  // pass.

  public static List<String> mainReadFile(String[] args)
      throws URISyntaxException, JsonParseException, JsonMappingException, IOException {

    ObjectMapper objectMapper = getObjectMapper();
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] trade = objectMapper.readValue(file, PortfolioTrade[].class);

    List<String> list = new ArrayList<>();

    for (int i = 0; i < trade.length; i++) {
      PortfolioTrade t = trade[i];
      list.add(t.getSymbol());
    }

    return list;
  }

  private static void printJsonObject(Object object) throws IOException {
    Logger logger = Logger.getLogger(PortfolioManagerApplication.class.getCanonicalName());
    ObjectMapper mapper = new ObjectMapper();
    logger.info(mapper.writeValueAsString(object));
  }

  private static File resolveFileFromResources(String filename) throws URISyntaxException {
    return Paths.get(Thread.currentThread().getContextClassLoader()
      .getResource(filename).toURI()).toFile();
  }

  private static ObjectMapper getObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    return objectMapper;
  }

  // TODO: CRIO_TASK_MODULE_JSON_PARSING
  // Follow the instructions provided in the task documentation and fill up the
  // correct values for
  // the variables provided. First value is provided for your reference.
  // A. Put a breakpoint on the first line inside mainReadFile() which says
  // return Collections.emptyList();
  // B. Then Debug the test #mainReadFile provided in
  // PortfoliomanagerApplicationTest.java
  // following the instructions to run the test.
  // Once you are able to run the test, perform following tasks and record the
  // output as a
  // String in the function below.
  // Use this link to see how to evaluate expressions -
  // https://code.visualstudio.com/docs/editor/debugging#_data-inspection
  // 1. evaluate the value of "args[0]" and set the value
  // to the variable named valueOfArgument0
  // (This is implemented for your
  // reference.)
  // 2. In the same window, evaluate the value of expression
  // below and set it
  // to resultOfResolveFilePathArgs0
  // expression ==> resolveFileFromResources(args[0])
  // 3. In the same window, evaluate the value of expression below and set it
  // to toStringOfObjectMapper.
  // You might see some garbage numbers in the output. Dont worry, its expected.
  // expression ==> getObjectMapper().toString()
  // 4. Now Go to the debug window and open stack trace. Put the name of the
  // function you see at
  // second place from top to variable functionNameFromTestFileInStackTrace
  // 5. In the same window, you will see the line number of the function in the
  // stack trace window.
  // assign the same to lineNumberFromTestFileInStackTrace
  // Once you are done with above, just run the corresponding test and
  // make sure its working as expected. use below command to do the same.
  // ./gradlew test --tests PortfolioManagerApplicationTest.testDebugValues

  public static List<String> debugOutputs() {

    String valueOfArgument0 = "trades.json";
    String h = "/home/crio-user/workspace/ajaybarath3-ME_QMONEY/qmoney/bin";
    String resultOfResolveFilePathArgs0 = h + "/main/trades.json";
    String toStringOfObjectMapper = "com.fasterxml.jackson.databind.ObjectMapper@2e4b8173";
    String functionNameFromTestFileInStackTrace = "mainReadFile";
    String lineNumberFromTestFileInStackTrace = "22";

    return Arrays.asList(new String[] { valueOfArgument0, resultOfResolveFilePathArgs0, 
        toStringOfObjectMapper,
        functionNameFromTestFileInStackTrace, lineNumberFromTestFileInStackTrace });
  }

  // TODO: CRIO_TASK_MODULE_REST_API
  // Copy the relavent code from #mainReadFile to parse the Json into
  // PortfolioTrade list.
  // Now That you have the list of PortfolioTrade already populated in module#1
  // For each stock symbol in the portfolio trades,
  // Call Tiingo api
  // (https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=&endDate=&token=)
  // with
  // 1. ticker = symbol in portfolio_trade
  // 2. startDate = purchaseDate in portfolio_trade.
  // 3. endDate = args[1]
  // Use RestTemplate#getForObject in order to call the API,
  // and deserialize the results in List<Candle>
  // Note - You may have to register on Tiingo to get the api_token.
  // Please refer the the module documentation for the steps.
  // Find out the closing price of the stock on the end_date and
  // return the list of all symbols in ascending order by its close value on
  // endDate
  // Test the function using gradle commands below
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // And make sure that its printing correct results.


  public static List<String> mainReadQuotes(String[] args)
      throws URISyntaxException, JsonParseException, JsonMappingException, IOException {

    ObjectMapper objectMapper = getObjectMapper();
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] trade = objectMapper.readValue(file, PortfolioTrade[].class);
    List<Candle> updatedList = new ArrayList<>();

    for (int i = 0; i < trade.length; i++) {
      String sym = trade[i].getSymbol();
      String purdate = "" + trade[i].getPurchaseDate();
      String url = "https://api.tiingo.com/tiingo/daily/" + sym + "/prices?startDate=" + purdate + "&endDate=" + args[1]
          + "&token=ec0bf0f7256f7b9cd787dca3311a12f44da9c875";

      List<TiingoCandle> data = fetchAnnualData(url);
      data.get(0).setSymbol(sym);
      data.get(1).setSymbol(sym);
      updatedList.add(data.get(1));

    }
    Collections.sort(updatedList, new Comparator<Candle>() {
      public int compare(Candle s1, Candle s2) {
        return s1.getClose().compareTo(s2.getClose());
      }
    });

    List<String> slist = new ArrayList<>();

    for (int i = 0; i < updatedList.size(); i++) {
      slist.add(updatedList.get(i).getSymbol());
    }

    return slist;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // Copy the relevant code from #mainReadQuotes to parse the Json into
  // PortfolioTrade list and
  // Get the latest quotes from TIingo.
  // Now That you have the list of PortfolioTrade And their data,
  // With this data, Calculate annualized returns for the stocks provided in the
  // Json
  // Below are the values to be considered for calculations.
  // buy_price = open_price on purchase_date and sell_value = close_price on
  // end_date
  // startDate and endDate are already calculated in module2
  // using the function you just wrote #calculateAnnualizedReturns
  // Return the list of AnnualizedReturns sorted by annualizedReturns in
  // descending order.
  // use gralde command like below to test your code
  // ./gradlew run --args="trades.json 2020-01-01"
  // ./gradlew run --args="trades.json 2019-07-01"
  // ./gradlew run --args="trades.json 2019-12-03"
  // where trades.json is your json file

  public static List<TiingoCandle> fetchAnnualData(String url) {

    RestTemplate restTemplate = new RestTemplate();
    List<TiingoCandle> list = new ArrayList<>();

    ResponseEntity<TiingoCandle[]> response = restTemplate.getForEntity(url, TiingoCandle[].class);
    TiingoCandle[] candles = response.getBody();
    if (candles != null) {
      list.add(candles[0]);
      list.add(candles[candles.length - 1]);
      return list;
    }

    return null;
  }

  public static List<AnnualizedReturn> mainCalculateSingleReturn(String[] args) throws IOException, 
      URISyntaxException {

    ObjectMapper objectMapper = getObjectMapper();
    File file = resolveFileFromResources(args[0]);
    PortfolioTrade[] trade = objectMapper.readValue(file, PortfolioTrade[].class);

    List<AnnualizedReturn> annualList = new ArrayList<>();

    for (int i = 0; i < trade.length; i++) {
      PortfolioTrade startTrade = trade[i];
      String sym = startTrade.getSymbol();
      String purdate = "" + startTrade.getPurchaseDate();
      String url = "https://api.tiingo.com/tiingo/daily/" + sym + "/prices?startDate=" + purdate + "&endDate=" + args[1]
          + "&token=ec0bf0f7256f7b9cd787dca3311a12f44da9c875";

      List<TiingoCandle> data = fetchAnnualData(url);
      data.get(0).setSymbol(sym);
      data.get(1).setSymbol(sym);

      Candle end = data.get(1);

      LocalDate endDate = end.getDate();
      Double buyPrice = data.get(0).getOpen();
      Double sellPrice = end.getClose();

      AnnualizedReturn areturn = calculateAnnualizedReturns(endDate, startTrade, 
          buyPrice, sellPrice);

      annualList.add(areturn);

    }

    Collections.sort(annualList, new Comparator<AnnualizedReturn>() {
      public int compare(AnnualizedReturn s1, AnnualizedReturn s2) {
        return s1.getAnnualizedReturn().compareTo(s2.getAnnualizedReturn());
      }
    });

    Collections.reverse(annualList);
    
    return annualList;
  }

  // TODO: CRIO_TASK_MODULE_CALCULATIONS
  // annualized returns should be calculated in two steps -
  // 1. Calculate totalReturn = (sell_value - buy_value) / buy_value
  // Store the same as totalReturns
  // 2. calculate extrapolated annualized returns by scaling the same in years
  // span. The formula is
  // annualized_returns = (1 + total_returns) ^ (1 / total_num_years) - 1
  // Store the same as annualized_returns
  // return the populated list of AnnualizedReturn for all stocks,
  // Test the same using below specified command. The build should be successful
  // ./gradlew test --tests
  // PortfolioManagerApplicationTest.testCalculateAnnualizedReturn

  public static AnnualizedReturn calculateAnnualizedReturns(LocalDate endDate, 
      PortfolioTrade trade, Double buyPrice,
      Double sellPrice) {

    LocalDate startDate = trade.getPurchaseDate();
    double period = (double) ChronoUnit.DAYS.between(startDate, endDate);

    Double duration = 365 / period;
    Double totalret = (sellPrice - buyPrice) / buyPrice;
    totalret += 1;
    Double annualizedreturns = Math.pow(totalret, duration) - 1;
    return new AnnualizedReturn(trade.getSymbol(), annualizedreturns, totalret);

    
  }

  public static void main(String[] args) throws Exception {
    Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
    ThreadContext.put("runId", UUID.randomUUID().toString());

    printJsonObject(mainCalculateSingleReturn(args));

  }
}
