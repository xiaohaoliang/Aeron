/*
 * Copyright 2017 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.aeron.driver;

import java.net.InetSocketAddress;

/**
 * Strategy for applying congestion control to determine the receiverWindowLength of the Status Messages
 */
public interface CongestionControl extends AutoCloseable
{
    /**
     * Polled by {@link Receiver} to determine when to initiate an RTT measurement to a Sender.
     *
     * @param now in nanoseconds
     * @return true for should measure RTT now or false for no measurement
     */
    boolean shouldMeasureRtt(long now);

    /**
     * Called by {@link Receiver} on reception of an RTT Measurement.
     *
     * @param now        in nanoseconds
     * @param rttInNanos to the Sender
     * @param srcAddress of the Sender
     */
    void onRttMeasurement(long now, long rttInNanos, InetSocketAddress srcAddress);

    /**
     * Called by {@link DriverConductor} upon execution of {@link PublicationImage#trackRebuild(long, long)} to
     * pass on current status.
     *
     * The return value must be packed using {@link CongestionControlUtil#packOutcome(int, boolean)}.
     *
     * @param now                     in nanoseconds
     * @param newConsumptionPosition  of the Subscribers
     * @param lastSmPosition          of the image
     * @param hwmPosition             of the image
     * @param startingRebuildPosition of the rebuild
     * @param endingRebuildPosition   of the rebuild
     * @param lossOccurred            during rebuild
     * @return outcome of congestion control calculation containing window length and whether to force sending an SM.
     */
    long onTrackRebuild(
        long now,
        long newConsumptionPosition,
        long lastSmPosition,
        long hwmPosition,
        long startingRebuildPosition,
        long endingRebuildPosition,
        boolean lossOccurred);

    /**
     * Called by {@link DriverConductor} to initialise window length for a new {@link PublicationImage}.
     *
     * @return new image window length
     */
    int initialWindowLength();

    void close();
}
