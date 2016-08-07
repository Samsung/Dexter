/**
 * Copyright (c) 2014 Samsung Electronics, Inc.,
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.samsung.sec.dexter.core.job;

import com.samsung.sec.dexter.core.config.IDexterStandaloneListener;
import com.samsung.sec.dexter.core.util.IDexterClient;
import com.samsung.sec.dexter.util.ThreadUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DexterJobFacade implements IDexterStandaloneListener {
    public static final long SLEEP_FOR_LOGIN = 60 * 60; // seconds => 1 hour
    public static final long ALLOWED_FREE_MEMORY_SIZE_FOR_JOBS = 30 * 1024 * 1024; // 50
                                                                                   // MB
    public static final int MAX_JOB_DELAY_COUNT = 100;

    private static final int INITIAL_DELAY_FOR_DELETING_LOG = 0;
    private static final int INITIAL_DELAY_FOR_SENDING_RESULT = 10;
    private static final int INITIAL_DELAY_FOR_MERGING_FILTER = 15;

    private int intervalSendingAnalysisResult = 5;
    private int intervalMergingFilter = 3;
    private int intervalDeletingResultLog = 60 * 60; // seconds => 1 hours

    private ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> sendingResultScheduledFuture;
    private ScheduledFuture<?> mergingFilterScheduledFuture;

    private IDexterClient client;

    public DexterJobFacade(final IDexterClient client) {
        assert client != null;

        this.client = client;
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
    }

    /*
     * private static class LazyHolder { private static final DexterJobFacade
     * INSTANCE = new DexterJobFacade(); }
     * 
     * public static DexterJobFacade getInstance() { return LazyHolder.INSTANCE;
     * }
     */

    public void startGeneralJobs() {
        createAndRunDeletingLogScheduledFuture();
    }

    public void startDexterServerJobs() {
        createAndRunSendingResultScheduledFuture();
        createAndRunMergeFilterScheduledFuture();
    }

    private void createAndRunDeletingLogScheduledFuture() {
        scheduledExecutorService.scheduleAtFixedRate(new DeleteResultLogJob(), INITIAL_DELAY_FOR_DELETING_LOG,
                intervalDeletingResultLog, TimeUnit.SECONDS);
    }

    private void createAndRunSendingResultScheduledFuture() {
        sendingResultScheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new SendResultJob(client),
                INITIAL_DELAY_FOR_SENDING_RESULT, intervalSendingAnalysisResult, TimeUnit.SECONDS);
    }

    private void createAndRunMergeFilterScheduledFuture() {
        mergingFilterScheduledFuture = scheduledExecutorService.scheduleAtFixedRate(new MergeFilterJob(client),
                INITIAL_DELAY_FOR_MERGING_FILTER, intervalMergingFilter, TimeUnit.SECONDS);
    }

    public void shutdownScheduleService() {
        scheduledExecutorService.shutdown();
    }

    public void cancelDexterServerJobs() {
        if (sendingResultScheduledFuture != null)
            sendingResultScheduledFuture.cancel(false);
        if (mergingFilterScheduledFuture != null)
            mergingFilterScheduledFuture.cancel(false);
    }

    public void resumeDexterServerJobs() {
        new Thread() {
            final static int MAX_TRY_COUNT = 30;

            public void run() {
                resumeSendResultFuture();
                resumeMergeFilterFuture();
            }

            private void resumeSendResultFuture() {
                if (sendingResultScheduledFuture == null) {
                    createAndRunSendingResultScheduledFuture();
                    return;
                }

                int tryCount = 0;
                while (tryCount++ < MAX_TRY_COUNT) {
                    if (sendingResultScheduledFuture.isDone()) {
                        createAndRunSendingResultScheduledFuture();
                        break;
                    }

                    ThreadUtil.sleepOneSecond();
                }
            }

            private void resumeMergeFilterFuture() {
                if (mergingFilterScheduledFuture == null) {
                    createAndRunMergeFilterScheduledFuture();
                    return;
                }

                int tryCount = 0;
                while (tryCount++ < MAX_TRY_COUNT) {
                    if (mergingFilterScheduledFuture.isDone()) {
                        createAndRunMergeFilterScheduledFuture();
                        break;
                    }

                    ThreadUtil.sleepOneSecond();
                }
            };
        }.start();
    }

    /**
     * @return the iNTERVAL_SEND_ANALYSIS_RESULT
     */
    public int getIntervalSendingAnalysisResult() {
        return intervalSendingAnalysisResult;
    }

    /**
     * @return the iNTERVAL_MERGE_FILTER
     */
    public int getIntervalMergingFilter() {
        return intervalMergingFilter;
    }

    public long getIntervalDeleteResultLog() {
        return intervalDeletingResultLog;
    }

    /**
     * @param interval
     * the iNTERVAL_SEND_ANALYSIS_RESULT to set
     */
    public void setIntervalSendingAnalysisResult(final int interval) {
        intervalSendingAnalysisResult = interval;
    }

    /**
     * @param interval
     * the iNTERVAL_MERGE_FILTER to set
     */
    public void setIntervalMergingFilter(final int interval) {
        intervalMergingFilter = interval;
    }

    @Override
    public void handleWhenStandaloneMode() {
        cancelDexterServerJobs();
    }

    @Override
    public void handleWhenNotStandaloneMode() {
        resumeDexterServerJobs();
    }
}
