package com.example.riley.androidfoodhub.Model;

import java.util.List;

/**
 * Created by riley on 12/23/2017.
 */

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
