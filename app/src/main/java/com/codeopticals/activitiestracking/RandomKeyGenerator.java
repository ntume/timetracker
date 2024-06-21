package com.codeopticals.activitiestracking;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.SecureRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomKeyGenerator {

    // this class is used to generate random keys to id the activities in the database
    private int len = 24;


    // Method to generate a random alphanumeric password of a specific length
    @RequiresApi(api = Build.VERSION_CODES.N)
    public String getKey()
    {
        // ASCII range – alphanumeric (0-9, a-z, A-Z)
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        SecureRandom random = new SecureRandom();

        // each iteration of the loop randomly chooses a character from the given
        // ASCII range and appends it to the `StringBuilder` instance
        return IntStream.range(0, this.len)
                .map(i -> random.nextInt(chars.length()))
                .mapToObj(randomIndex -> String.valueOf(chars.charAt(randomIndex)))
                .collect(Collectors.joining());
    }

}
