package com.evervault;

import java.time.Instant;

public class RunClientSideTokenOptions {
    private Instant expiry;
     public RunClientSideTokenOptions(

             Instant expiry
     ) {
         this.expiry = expiry;
     }
}
