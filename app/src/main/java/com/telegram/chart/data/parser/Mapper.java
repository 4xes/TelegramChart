package com.telegram.chart.data.parser;

public interface Mapper<From, To> {
    From map(To obj) throws Throwable;
}