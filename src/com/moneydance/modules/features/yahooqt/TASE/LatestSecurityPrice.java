package com.moneydance.modules.features.yahooqt.TASE;

import java.time.LocalDate;

public class LatestSecurityPrice extends SecurityPrice {
  private long high;
  private long low;
  private long volume;

  public LatestSecurityPrice() {
  }

  public LatestSecurityPrice(LocalDate date, long price) {
    super(date, price);
  }

  public LatestSecurityPrice(LocalDate date, long price, long high, long low, long volume) {
    super(date, price);
    this.high = high;
    this.low = low;
    this.volume = volume;
  }

  public long getHigh() {
    return high;
  }

  public long getLow() {
    return low;

  }

  public long getVolume() {
    return volume;
  }

  public void setHigh(long high) {
    this.high = high;
  }

  public void setLow(long low) {
    this.low = low;
  }

  public void setVolume(long volume) {
    this.volume = volume;
  }
}