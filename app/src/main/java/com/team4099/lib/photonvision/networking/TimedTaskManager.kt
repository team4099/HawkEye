package com.team4099.lib.photonvision.networking

import java.util.concurrent.*


class TimedTaskManager {
    object Singleton {
        val instance: TimedTaskManager = TimedTaskManager()
    }


    private class CaughtThreadFactory : ThreadFactory {
        override fun newThread(r: Runnable): Thread {
            val thread = defaultThreadFactory.newThread(r)
//            thread.uncaughtExceptionHandler = UncaughtExceptionHandler { t: Thread?, e: Throwable? -> logger.error("TimedTask threw uncaught exception!", e) }
            return thread
        }

        companion object {
            private val defaultThreadFactory = Executors.defaultThreadFactory()
        }
    }

    private val timedTaskExecutorPool: ScheduledExecutorService = ScheduledThreadPoolExecutor(2, CaughtThreadFactory())
    private val activeTasks = ConcurrentHashMap<String, Future<*>?>()
    fun addTask(identifier: String, runnable: Runnable?, millisInterval: Long) {
        if (!activeTasks.containsKey(identifier)) {
            val future = timedTaskExecutorPool.scheduleAtFixedRate(
                    runnable, 0, millisInterval, TimeUnit.MILLISECONDS)
            activeTasks[identifier] = future
        }
    }

    fun addTask(
            identifier: String, runnable: Runnable?, millisStartDelay: Long, millisInterval: Long) {
        if (!activeTasks.containsKey(identifier)) {
            val future = timedTaskExecutorPool.scheduleAtFixedRate(
                    runnable, millisStartDelay, millisInterval, TimeUnit.MILLISECONDS)
            activeTasks[identifier] = future
        }
    }

    fun addOneShotTask(runnable: Runnable?, millisStartDelay: Long) {
        timedTaskExecutorPool.schedule(runnable, millisStartDelay, TimeUnit.MILLISECONDS)
    }

    fun cancelTask(identifier: String) {
        val future = activeTasks.getOrDefault(identifier, null)
        if (future != null) {
            future.cancel(true)
            activeTasks.remove(identifier)
        }
    }

}