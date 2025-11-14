package com.moneydance.modules.features.yahooqt.TASE;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Objects;

public class SecurityPrice implements Comparable<SecurityPrice> {

  public static final class ByDate implements Comparator<SecurityPrice>, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public int compare(SecurityPrice p1, SecurityPrice p2) {
      return p1.date.compareTo(p2.date);
    }
  }

  private LocalDate date;
  private long value;

  public SecurityPrice() {
  }

  public SecurityPrice(LocalDate date, long value) {
    this.date = date;
    this.value = value;
  }

  public LocalDate getDate() {
    return date;
  }

  public long getValue() {
    return value;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public void setValue(long value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.format("%tF: %,10.2f", date, value);

  }
}