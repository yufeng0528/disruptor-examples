package com.mcba.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.mcba.disruptor.eventhandler.PrintCarEventHandler;
import com.mcba.disruptor.eventhandler.SetColorCarEventHandler;
import com.mcba.disruptor.eventhandler.SetPowerCarEventHandler;
import com.mcba.disruptor.eventhandler.SetWheelCarEventHandler;

/**
 * Created by Hichame EL KHALFI on 09/06/2015.
 */
public class ExampleMain {

    @SuppressWarnings("unchecked")
	public static void main(String[] args) {
        // 1 - Executor that will be used to construct new threads for consumers
    	// https://stackoverflow.com/questions/8309735/disruptor-eventhandlers-not-invoked
        ExecutorService executorService = Executors.newCachedThreadPool();

        // 2 - Specify the size of the ring buffer, must be power of 2.
        int bufferSize = 64;

        // 3 - initialize the Disruptor object
        Disruptor<CarEvent> disruptor = new Disruptor<CarEvent>(new CarEventFactory(), bufferSize, executorService,
        		ProducerType.SINGLE, new YieldingWaitStrategy());//java 8 flavor

        //http://blog.csdn.net/u011499747/article/details/78384292
        EventHandler<CarEvent> setColor = new SetColorCarEventHandler();
        EventHandler<CarEvent> setPower = new SetPowerCarEventHandler();
        EventHandler<CarEvent> setWheel = new SetWheelCarEventHandler();
        EventHandler<CarEvent> print = new PrintCarEventHandler();
        
        disruptor.handleEventsWith(setColor, setWheel);
        disruptor.after(setColor, setWheel).then(setPower).then(print);
//        disruptor.after(setColor).then(print);
//        disruptor.after(setWheel, setPower).then(print);

        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        final RingBuffer<CarEvent> ringBuffer = disruptor.getRingBuffer();

        for (long l = 0; l < 1; l++) {
            // 1 - get next car sequence (sequence is internal ringbuffer counter)
            long seq = ringBuffer.next();

            //2 - get CarEvent object based on it sequence.
            CarEvent carEvent = ringBuffer.get(seq);

            //3 - set the payload (Car) in the CarEvent.
            carEvent.set(new Car());

            //4 - publish the event using it sequence.
            ringBuffer.publish(seq);
        }
        
    }
}
