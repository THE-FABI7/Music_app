package com.example.applemusic.Models;

import java.util.List;

public class Root {

    private int resultCount = 0;
    private List<Result> results = null;
    public int getResultCount() {
        return resultCount;
    }
    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }
    public List<Result> getResults() {
        return results;
    }
    public void setResults(List<Result> results) {
        this.results = results;
    }
}
