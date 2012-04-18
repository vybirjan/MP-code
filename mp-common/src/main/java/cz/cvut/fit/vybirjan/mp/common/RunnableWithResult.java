package cz.cvut.fit.vybirjan.mp.common;

/**
 * Helper class for runnables which returns value
 * 
 * @author Jan Vybíral
 * 
 * @param <T>
 *            Type of result
 */
public abstract class RunnableWithResult<T> implements Runnable {

	private volatile T result = null;
	private volatile boolean finished = false;
	private final Object notifier = new Object();

	@Override
	public void run() {
		finished = false;
		try {
			result = runWithResult();
		} finally {
			finished = true;
			synchronized (notifier) {
				notifier.notifyAll();
			}
		}
	};

	protected abstract T runWithResult();

	/**
	 * Returns value returned from runnable
	 * 
	 * @throws IllegalStateException
	 *             When called before runnable finished
	 * @return Returned value
	 * @see RunnableWithResult#isFinished()
	 */
	public T getResult() {
		if (!finished) {
			throw new IllegalStateException("Runnable did not finish yet");
		}
		return result;
	}

	/**
	 * Whether runnable finished
	 * 
	 * @return True if runnable finished and result is available, false
	 *         otherwise
	 */
	public boolean isFinished() {
		return finished;
	}

	/**
	 * Waits for thread to finish and returns value. Blocks until result is
	 * available.
	 * 
	 * @return Result from runnable
	 * @throws InterruptedException
	 */
	public T waitFor() throws InterruptedException {
		if (finished) {
			return getResult();
		} else {
			synchronized (notifier) {
				if (finished) {
					return getResult();
				} else {
					notifier.wait();
					return getResult();
				}
			}
		}
	}
}
