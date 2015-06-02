package com.mcba.disruptor;

import com.lmax.disruptor.EventHandler;

/**
 * Created by Hichame EL KHALFI on 01/06/2015.
 */
public class CarEventPrintCarHandler implements EventHandler<CarEvent> {


    public void onEvent(CarEvent event, long sequence, boolean endOfBatch) throws Exception {

        System.out.println(event.get());

    }
}