package com.chen.entity;

public class Algorithm {
    private int id;
    private String algorithm_name;
    private String algorithm_code;
    private int state;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAlgorithm_name() {
        return algorithm_name;
    }

    public void setAlgorithm_name(String algorithm_name) {
        this.algorithm_name = algorithm_name;
    }

    public String getAlgorithm_code() {
        return algorithm_code;
    }

    public void setAlgorithm_code(String algorithm_code) {
        this.algorithm_code = algorithm_code;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
