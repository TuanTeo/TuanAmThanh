package com.dev.tuanteo.tuanamthanh.object;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ArtistTest {

    @Before
    public void setUp() {
    }

    @Test
    public void whenCreateArtist_shouldHaveValue() {
        Artist artist = new Artist();
        artist.setName("tuan");

        Assert.assertEquals("tuan", artist.getName());
    }
}