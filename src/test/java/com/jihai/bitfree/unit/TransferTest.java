package com.jihai.bitfree.unit;

import com.jihai.bitfree.demo.TransferDemo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TransferTest extends AppTest {

    @Autowired
    private TransferDemo transferDemo;

    @Test
    public void test() throws InterruptedException {
        transferDemo.transfer(1L, 100);
    }
}
