package com.telegram.chart.data;

public interface Mapper<From, To> {
    From map(To obj);
}