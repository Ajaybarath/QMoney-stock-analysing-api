package com.crio.warmup.stock.portfolio;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.SECONDS;

import com.crio.warmup.stock.dto.AnnualizedReturn;
import com.crio.warmup.stock.dto.Candle;
import com.crio.warmup.stock.dto.PortfolioTrade;
import com.crio.warmup.stock.dto.TiingoCandle;
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

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class PortfolioManagerImpl implements PortfolioManager {



  private transient RestTemplate restTemplate;

  // Caution: Do not delete or modify the constructor, or else your build will
  // break!
  // This is absolutely necessary for backward compatibility
  protected PortfolioManagerImpl(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }


  //TODO: CRIO_TASK_MODULE_REFACTOR
  // Now we want to convert our code into a module, so we will not call it from main anymore.
  // Copy your code from Module#3 PortfolioManagerApplication#calculateAnnualizedReturn
  // into #calculateAnnualizedReturn function here and make sure that it
  // follows the method signature.
  // Logic to read Json file and convert them into Objects will not be required further as our
  // clients will take care of it, going forward.
  // Test your code using Junits provided.
  // Make sure that all of the tests inside PortfolioManagerTest using command below -
  // ./gradlew test --tests PortfolioManagerTest
  // This will guard you against any regressions.
  // run ./gradlew build in order to test yout code, and make sure that
  // the tests and static code quality pass.

  //CHECKSTYLE:OFF




  private Comparator<AnnualizedReturn> getComparator() {
    return Comparator.comparing(AnnualizedReturn::getAnnualizedReturn).reversed();
  }

  //CHECKSTYLE:OFF

  // TODO: CRIO_TASK_MODULE_REFACTOR
  //  Extract the logic to call Tiingo thirdparty APIs to a separate function.
  //  It should be split into fto parts.
  //  Part#1 - Prepare the Url to call Tiingo based on a template constant,
  //  by replacing the placeholders.
  //  Constant should look like
  //  https://api.tiingo.com/tiingo/daily/<ticker>/prices?startDate=?&endDate=?&token=?
  //  Where ? are replaced with something similar to <ticker> and then actual url produced by
  //  replacing the placeholders with actual parameters.


  public List<Candle> getStockQuote(String symbol, LocalDate from, LocalDate to) {

    String url = buildUri(symbol, from, to);

    List<Candle> list = new ArrayList<>();

    ResponseEntity<TiingoCandle[]> response = restTemplate.getForEntity(url, TiingoCandle[].class);
    TiingoCandle[] candles = response.getBody();
    if (candles != null) {
      list.add(0, candles[0]);
      list.add(1, candles[candles.length - 1]);
      return list;
    }
    
    
    return null;
  }

  protected String buildUri(String symbol, LocalDate startDate, LocalDate endDate) {
       String uriTemplate = "https://api.tiingo.com/tiingo/daily/" + symbol + "/prices?"
            + "startDate=" + startDate + "&endDate=" + endDate + "&token=ec0bf0f7256f7b9cd787dca3311a12f44da9c875";

    return uriTemplate;
  }

  @Override
  public List<AnnualizedReturn> calculateAnnualizedReturn(List<PortfolioTrade> portfolioTrades, LocalDate endDate) {
    
    List<AnnualizedReturn> annualizedReturns = new ArrayList<>();

    for (int i = 0; i < portfolioTrades.size(); i++){

      PortfolioTrade trade = portfolioTrades.get(i);

      LocalDate startDate = trade.getPurchaseDate();

      List<Candle> data = getStockQuote(trade.getSymbol(), startDate, endDate);
      
      Candle end = data.get(2);

      Double buyPrice = data.get(0).getOpen();
      Double sellPrice = end.getClose();

      double period = (double) ChronoUnit.DAYS.between(startDate, endDate);

      Double duration = 365 / period;
      Double totalret = (sellPrice - buyPrice) / buyPrice;
      totalret += 1;
      Double annualreturns = Math.pow(totalret, duration) - 1;
      annualizedReturns.add(new AnnualizedReturn(trade.getSymbol(), annualreturns, totalret));

    }
    
    Collections.sort(annualizedReturns, new Comparator<AnnualizedReturn>() {
      public int compare(AnnualizedReturn s1, AnnualizedReturn s2) {
        return s1.getAnnualizedReturn().compareTo(s2.getAnnualizedReturn());
      }
    });

    Collections.reverse(annualizedReturns);

    return annualizedReturns;
  }

  // private static List<Candle> fetchAnnualData(String url) {

  //   RestTemplate restTemplate = new RestTemplate();
  //   List<Candle> list = new ArrayList<>();

  //   ResponseEntity<TiingoCandle[]> response = restTemplate.getForEntity(url, TiingoCandle[].class);
  //   TiingoCandle[] candles = response.getBody();
  //   if (candles != null) {
  //     list.add(candles[0]);
  //     list.add(candles[candles.length - 1]);
  //     return list;
  //   }

  //   return null;
  // }

}
