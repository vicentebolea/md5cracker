package PasswordCracker;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PasswordCrackerMain {
  public static void main(String args[]) {
    if (args.length < 4) {
      System.out.println("Usage: PasswordCrackerMain numThreads passwordLength isEarlyTermination encryptedPassword");
      return;
    }

    int numThreads = Integer.parseInt(args[0]);
    int passwordLength = Integer.parseInt(args[1]);
    boolean isEarlyTermination = Boolean.parseBoolean(args[2]);
    String encryptedPassword = args[3];

    // If you want to know the ExecutorService,
    // refer to site; https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
    ExecutorService workerPool = Executors.newFixedThreadPool(numThreads);
    PasswordFuture passwordFuture = new PasswordFuture();
    PasswordCrackerConsts consts = new PasswordCrackerConsts(numThreads, passwordLength, encryptedPassword);

    /*
     * Create PasswordCrackerTask and use executor service to run in a separate thread
     */
    for (int i = 0; i < numThreads; i++) {
      workerPool.submit(new PasswordCrackerTask(i, isEarlyTermination, consts, passwordFuture));
    }

    String passwd = null;
    try {
     passwd = passwordFuture.get();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      workerPool.shutdown();
    }

    // Print out the final result
    System.out.println("20175319");
    System.out.println(numThreads);
    System.out.println(passwordLength);
    System.out.println(String.valueOf(isEarlyTermination));
    System.out.println(encryptedPassword);
    System.out.println(passwd);
  }
}

/**
 * A {@code Future} represents the result of an asynchronous
 * computation.  Methods are provided to check if the computation is
 * complete, to wait for its completion, and to retrieve the result of
 * the computation.  The result can only be retrieved using method
 * {@code get} when the computation has completed, blocking if
 * necessary until it is ready.  Cancellation is performed by the
 * {@code cancel} method.  Additional methods are provided to
 * determine if the task completed normally or was cancelled. Once a
 * computation has completed, the computation cannot be cancelled.
 * If you would like to use a {@code Future} for the sake
 * of cancellability but not provide a usable result, you can
 * declare types of the form {@code Future<?>} and
 * return {@code null} as a result of the underlying task.
 **/
// If you want to know the Future class, refer to site; https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/Future.html

// Complete this class using a lock and the condition variable
class PasswordFuture implements Future<String> {
    String result;
    Lock lock = new ReentrantLock();
    Condition resultSet = lock.newCondition(); // refer to Condition and Lock class in javadoc

    /*  ### set ###
     *  set the result and send signal to thread waiting for the result
     */
    public void set(String result) {
      lock.lock();
      this.result = result;
      resultSet.signal();
      lock.unlock();
    }

    /*  ### get ###
     *  if result is ready, return it.
     *  if not, wait on the conditional variable.
     */
    @Override
    public String get() throws InterruptedException, ExecutionException {
      lock.lock();
      try {
        if (!isDone()) {
          // No routine to catch the InterruptedException, out of the scope of 
          // this assignment
          resultSet.await();
        }
      } finally {
        lock.unlock();
      }

      return result;
    }
    /*  ### isDone ###
     *  returns true if result is set
     */
    @Override
    public boolean isDone() {
      return (result != null);
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }
    @Override
    public boolean isCancelled() {
        return false;
    }
    @Override
    public String get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        // no need to implement this. We don't use this...
        return null;
    }
}


